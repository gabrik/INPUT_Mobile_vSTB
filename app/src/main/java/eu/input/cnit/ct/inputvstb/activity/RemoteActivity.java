package eu.input.cnit.ct.inputvstb.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import eu.input.cnit.ct.inputvstb.InputVSTB;
import eu.input.cnit.ct.inputvstb.R;
import eu.input.cnit.ct.inputvstb.adapters.dlna_devices.DLNAAdapter;
import eu.input.cnit.ct.inputvstb.managers.VSTBSyncManager;
import eu.input.cnit.ct.inputvstb.models.VSTBDLNADevice;
import eu.input.cnit.ct.inputvstb.models.VSTBProvider;
import eu.input.cnit.ct.inputvstb.utils.OnDataSended;

public class RemoteActivity extends AppCompatActivity implements View.OnClickListener,OnDataSended, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {


    public static final String EXTRA = "channel_info";
    public static final String EXTRA_DEVICE = "device";


    private VSTBProvider mChannel;
    private int mDevice;

    private boolean playedFirstTime=false;

    private Button mStopButton,mPauseButton,mPlayButton,mRescanButton;
    private Switch mMuteSwitch;
    private SeekBar mVolumeSeekBar;
    private Spinner mSpinnerDevices;


    private LinkedList<VSTBDLNADevice> mDLNADevices =null;
    private ArrayAdapter<VSTBDLNADevice> adapter;


    private VSTBDLNADevice selected = null;

    private ProgressDialog mLoadingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);

        try {

            VSTBSyncManager.getInstance().getDLNADevices(this, InputVSTB.ip_vdi,VSTBSyncManager.NO_SCAN);
            mLoadingDialog=ProgressDialog.show(this, "","Loading. Please wait...", true);

            Log.i(RemoteActivity.class.getCanonicalName(), "Extra is: " + getIntent().getStringExtra(EXTRA));
            Log.i(RemoteActivity.class.getCanonicalName(), "Extra Device is: " + getIntent().getIntExtra(EXTRA_DEVICE,-1));
            mChannel = new VSTBProvider().fromJSON(new JSONObject(getIntent().getStringExtra(EXTRA)));

            mDevice = getIntent().getIntExtra(EXTRA_DEVICE,-1);

            mSpinnerDevices = (Spinner) findViewById(R.id.spinnerDevices);
            mPlayButton = (Button) findViewById(R.id.buttonPlay);
            mStopButton = (Button) findViewById(R.id.buttonStop);
            mPauseButton = (Button) findViewById(R.id.buttonPause);
            mRescanButton = (Button) findViewById(R.id.buttonRescan);


            mMuteSwitch = (Switch) findViewById(R.id.switchMute);
            mVolumeSeekBar = (SeekBar) findViewById(R.id.seekBarVolume);


            mStopButton.setOnClickListener(this);
            mPlayButton.setOnClickListener(this);
            mSpinnerDevices.setOnItemSelectedListener(this);
            mPauseButton.setOnClickListener(this);
            mMuteSwitch.setOnCheckedChangeListener(this);
            mVolumeSeekBar.setOnSeekBarChangeListener(this);
            mSpinnerDevices.setOnItemSelectedListener(this);
            mRescanButton.setOnClickListener(this);




        } catch (JSONException e) {
            Log.e(RemoteActivity.class.getCanonicalName(), "Error on JSON - loadChannels()");
            Log.e(RemoteActivity.class.getCanonicalName(), e.getMessage());
            e.printStackTrace();

            Toast.makeText(this, "Error on getting channel information", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }

    }

    @Override
    public void onClick(View v) {

        if(selected!=null){
            switch (v.getId()){
                case R.id.buttonStop:

                    VSTBSyncManager.getInstance().remoteDMCControl(this,mChannel,VSTBSyncManager.STOP,selected.getUuid(),0,false,"");
                    playedFirstTime=false;
                    mPlayButton.setText("Start");
                    //VSTBSyncManager.getInstance().stopContent(null,mChannel,mDevice);



                    break;
                case R.id.buttonPause:
                    VSTBSyncManager.getInstance().remoteDMCControl(this,mChannel,VSTBSyncManager.PAUSE,selected.getUuid(),0,false,"");
                    if(!playedFirstTime){
                        playedFirstTime=!playedFirstTime;
                        mPlayButton.setText("Play");
                    }
                    break;
                case R.id.buttonPlay:
                    if(!playedFirstTime)
                        VSTBSyncManager.getInstance().remoteDMCControl(this,mChannel,VSTBSyncManager.START,selected.getUuid(),0,false,"");
                    else
                        VSTBSyncManager.getInstance().remoteDMCControl(this,mChannel,VSTBSyncManager.PLAY,selected.getUuid(),0,false,"");
                    break;
                case R.id.buttonRescan:
                    VSTBSyncManager.getInstance().getDLNADevices(this, InputVSTB.ip_vdi,VSTBSyncManager.SCAN);
                    mLoadingDialog=ProgressDialog.show(this, "","Loading. Please wait...", true);
                    break;
                default:
                    break;
            }
        } else{
            switch (v.getId()){
                case R.id.buttonRescan:
                    VSTBSyncManager.getInstance().getDLNADevices(this, InputVSTB.ip_vdi,VSTBSyncManager.SCAN);
                    mLoadingDialog=ProgressDialog.show(this, "","Loading. Please wait...", true);
                    break;
                default:
                    Toast.makeText(this,"Please select a device!",Toast.LENGTH_LONG).show();
                    break;
            }



             }


    }


    private void updateDevices(){
        if(mDLNADevices!=null){
            adapter = new DLNAAdapter(this, mDLNADevices);

            mSpinnerDevices.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VSTBSyncManager.getInstance().stopContent(null,mChannel,mDevice);
    }

    @Override
    public void onDataSended(Object result) {
        if(result instanceof String){
            Toast.makeText(this,"Call result is " + (String)result,Toast.LENGTH_LONG).show();

        }
        if (result instanceof LinkedList && ((LinkedList) result).size()>0) {
            Object o = ((LinkedList) result).get(0);
            if (o instanceof VSTBDLNADevice) {
                mLoadingDialog.dismiss();
                final LinkedList<VSTBDLNADevice> devices = (LinkedList<VSTBDLNADevice>) result;
                mDLNADevices = new LinkedList<>();

                updateDevices();
                for(VSTBDLNADevice d : devices){
                    if (d.getType().compareTo(VSTBDLNADevice.DLNA_Renderer)==0)
                        mDLNADevices.add(d);
                }

                updateDevices();
                /*
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, mDLNADevices);
                mSpinnerDevices.setAdapter(adapter);
                adapter.notifyDataSetChanged();*/



        }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //try {
            selected = mDLNADevices.get(position);

            //selected.fromJSON(new JSONObject(mDLNADevices.get(position)));
        //} catch (JSONException e) {
        //    Log.e(RemoteActivity.class.getCanonicalName(), "Error on JSON - onItemSelected()");
        //    Log.e(RemoteActivity.class.getCanonicalName(), e.getMessage());
        //    e.printStackTrace();

        //    Toast.makeText(this, "Error on getting channel information", Toast.LENGTH_LONG).show();
        //    Intent intent = new Intent(this, MainActivity.class);
        //    startActivity(intent);
        //}


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(selected!=null){
            switch (buttonView.getId()){
            case R.id.switchMute:
                VSTBSyncManager.getInstance().remoteDMCControl(this,mChannel,VSTBSyncManager.MUTE,selected.getUuid(),0,isChecked,"");
                break;
            default:
                break;
        }
        } else
            Toast.makeText(this,"Please select a device!",Toast.LENGTH_LONG).show();


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(selected!=null){
            switch (seekBar.getId()){
                case R.id.seekBarVolume:
                    VSTBSyncManager.getInstance().remoteDMCControl(this,mChannel,VSTBSyncManager.VOL,selected.getUuid(),progress,false,"");
                    break;

            }
        } else
            Toast.makeText(this,"Please select a device!",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
