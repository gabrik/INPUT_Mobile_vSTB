package eu.input.cnit.ct.inputvstb.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.LinkedList;

import eu.input.cnit.ct.inputvstb.InputVSTB;
import eu.input.cnit.ct.inputvstb.R;
import eu.input.cnit.ct.inputvstb.managers.VSTBSyncManager;
import eu.input.cnit.ct.inputvstb.models.VSTBChannel;
import eu.input.cnit.ct.inputvstb.models.VSTBProvider;
import eu.input.cnit.ct.inputvstb.utils.OnDataSended;
import eu.input.cnit.ct.inputvstb.utils.Utility;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity implements OnDataSended {
    private final int SPLASH_DISPLAY_LENGTH = 2500;


    private Button mOkButton;
    private EditText mTxtIPVDI;
    private ProgressDialog mLoadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        mOkButton = (Button) findViewById(R.id.buttonOk);
        mTxtIPVDI = (EditText) findViewById(R.id.txtIPVDI);


        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputVSTB.ip_vdi = mTxtIPVDI.getText().toString();
                mLoadingDialog = ProgressDialog.show(SplashScreen.this, "Loading", "Loading. Please wait...", true);
                init();
            }
        });


    }


    public void getChannelsData(){
        for (VSTBProvider p : InputVSTB.mProviders){
            VSTBSyncManager.getInstance().getChannels(this,InputVSTB.ip_vdi,p.getId());
        }
    }

    public void init() {


        VSTBSyncManager.getInstance().getProviders(this, InputVSTB.ip_vdi);
        VSTBSyncManager.getInstance().getDMCContents(InputVSTB.mInstance,InputVSTB.ip_vdi,VSTBSyncManager.RECORDED,VSTBSyncManager.SCAN);
        VSTBSyncManager.getInstance().getDLNADevices(InputVSTB.mInstance, InputVSTB.ip_vdi,VSTBSyncManager.SCAN);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                /* Create an Intent that will start the Menu-Activity. */
//                Initialize.execute("");
//            }
//        }, SPLASH_DISPLAY_LENGTH);


    }


    @Override
    public void onDataSended(Object result) {

        if (result != null) {
            if (result instanceof LinkedList && ((LinkedList)result).size()>0 ) {
                Object o = ((LinkedList) result).get(0);
                if (o instanceof VSTBProvider) {
                    final LinkedList<VSTBProvider> providers = (LinkedList<VSTBProvider>) result;


                    final AsyncTask<String, Integer, String> Initialize = new AsyncTask<String, Integer, String>() {


                        @Override
                        protected String doInBackground(String... strings) {


                            JSONArray mArray = new JSONArray();
                            InputVSTB.logo_map = new HashMap<>();

                            for (VSTBProvider p : providers) {
                                InputVSTB.logo_map.put(p.getId(), Utility.getBitmapFromURL(p.getLogo_url()));
                                mArray.put(p.toJSON());
                            }


                            Log.v(SplashScreen.class.getCanonicalName(), "Providers are: " + mArray.toString());

                            InputVSTB.channels_list = mArray.toString();
                            InputVSTB.mProviders=providers;

                            return null;
                        }

                        @Override
                        protected void onPostExecute(String s) {

                            getChannelsData();
                            //Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                            //SplashScreen.this.startActivity(mainIntent);
                        }
                    };


                    Initialize.execute("");


                }
                if (o instanceof VSTBChannel){

                    final LinkedList<VSTBChannel> channels = (LinkedList<VSTBChannel>) result;
                    if (channels.size()>0){



                        int pos = InputVSTB.mProviders.indexOf(new VSTBProvider("","",null,"",channels.get(0).getProvider_id()));
                        VSTBProvider p = InputVSTB.mProviders.get(pos);
                        p.setUrl(channels.get(0).getUrl());
                        p.setCurrentShowName(channels.get(0).getName());
                        p.getmChannels().addAll(channels);


                        InputVSTB.mProviders.remove(pos);
                        InputVSTB.mProviders.add(p);

                    }


                    JSONArray mArray = new JSONArray();
                    for (VSTBProvider p : InputVSTB.mProviders) {

                        mArray.put(p.toJSON());
                    }


                    Log.v(SplashScreen.class.getCanonicalName(), "Channels are: " + mArray.toString());

                    InputVSTB.channels_list = mArray.toString();

                    mLoadingDialog.dismiss();
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    SplashScreen.this.startActivity(mainIntent);


                }
            }
        } else{
            mLoadingDialog.dismiss();
            Toast.makeText(SplashScreen.this,"Error on accessing VDI",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }
}
