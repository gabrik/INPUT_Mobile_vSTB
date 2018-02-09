package eu.input.cnit.ct.inputvstb.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import eu.input.cnit.ct.inputvstb.R;
import eu.input.cnit.ct.inputvstb.managers.VSTBSyncManager;
import eu.input.cnit.ct.inputvstb.models.VSTBChannel;
import eu.input.cnit.ct.inputvstb.models.VSTBProvider;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, PlaybackControlView.VisibilityListener {


    public static final String EXTRA = "channel_info";
    public static final String EXTRA_DEVICE = "device";
    public static final String EXTRA_TYPE = "type";

    public static final int  TYPE_PROVIDER = 0;
    public static final int TYPE_CHANNEL = 1;

    private VideoView mVideoView;
    private TextView mProviderName, mShowName;
    private EditText mSourceTxt;
    private Button mPlayButton;
    private MediaPlayer mPlayer;


    private VSTBProvider mProvider;
    private VSTBChannel mChannel;
    private int mDevice;
    private int type;


    private SimpleExoPlayerView mExoPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private DataSource.Factory mediaDataSourceFactory;
    private Handler mMainHandler;
    private DefaultTrackSelector trackSelector;
    //private EventLogger mEventLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mProviderName = (TextView) findViewById(R.id.textViewProviderName);
        mShowName = (TextView) findViewById(R.id.textViewShowName);
        mSourceTxt = (EditText) findViewById(R.id.editTextSource);
        mPlayButton = (Button) findViewById(R.id.buttonPlay);
        mExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);


        try {

            Log.i(PlayActivity.class.getCanonicalName(), "Extra is: " + getIntent().getStringExtra(EXTRA));
            Log.i(PlayActivity.class.getCanonicalName(), "Extra Device is: " + getIntent().getIntExtra(EXTRA_DEVICE,-1));


            type=getIntent().getIntExtra(EXTRA_TYPE,-1);
            mDevice = getIntent().getIntExtra(EXTRA_DEVICE,-1);

            switch (type){
                case 0:
                    mProvider = new VSTBProvider().fromJSON(new JSONObject(getIntent().getStringExtra(EXTRA)));
                    mProviderName.setText(mProvider.getProviderName());
                    mShowName.setText(mProvider.getCurrentShowName());
                    mSourceTxt.setText(mProvider.getUrl());
                    Toast.makeText(this, "Channel URL " + mProvider.getUrl(), Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    mChannel = new VSTBChannel().fromJSON(new JSONObject(getIntent().getStringExtra(EXTRA)));
                    mProviderName.setText(mChannel.getName());
                    mShowName.setText(mChannel.getName());
                    mShowName.setVisibility(View.INVISIBLE);
                    mSourceTxt.setText(mChannel.getUrl());
                    Toast.makeText(this, "Channel URL " + mChannel.getUrl(), Toast.LENGTH_LONG).show();
                    break;
                default:
                    throw new JSONException("Extra type error");
            }













            mPlayer = new MediaPlayer();

            mediaDataSourceFactory = buildDataSourceFactory();
            mMainHandler = new Handler();

            //mVideoView=(VideoView)findViewById(R.id.videoView);

            trackSelector = new DefaultTrackSelector();


            

            mPlayButton.setOnClickListener(this);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getApplication(), trackSelector);

        } catch (JSONException e) {
            Log.e(PlayActivity.class.getCanonicalName(), "Error on JSON - loadChannels()");
            Log.e(PlayActivity.class.getCanonicalName(), e.getMessage());
            e.printStackTrace();

            Toast.makeText(this, "Error on getting channel information", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPlay:

                Uri vidUri = Uri.parse(mSourceTxt.getText().toString());

                mExoPlayerView.setControllerVisibilityListener(this);
                mExoPlayerView.requestFocus();
                MediaSource live = new ExtractorMediaSource(vidUri, mediaDataSourceFactory, new DefaultExtractorsFactory(), mMainHandler, null);

                mExoPlayer.prepare(live);

                mExoPlayerView.setPlayer(mExoPlayer);
                mExoPlayer.setPlayWhenReady(true);

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;

        }
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }


    private DataSource.Factory buildDataSourceFactory() {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        return new DefaultDataSourceFactory(PlayActivity.this, Util.getUserAgent(this, "ExoPlayerDemo"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mExoPlayer.release();
        switch (type){
            case 0:
                VSTBSyncManager.getInstance().stopContent(null, mProvider,mDevice);
                break;
            case 1:
            default:
                break;
        }

        //getWindow().(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }
}
