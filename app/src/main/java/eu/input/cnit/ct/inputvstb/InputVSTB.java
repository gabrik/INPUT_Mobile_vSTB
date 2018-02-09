package eu.input.cnit.ct.inputvstb;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.input.cnit.ct.inputvstb.activity.PlayActivity;
import eu.input.cnit.ct.inputvstb.managers.VSTBSyncManager;
import eu.input.cnit.ct.inputvstb.models.VSTBChannel;
import eu.input.cnit.ct.inputvstb.models.VSTBDLNADevice;
import eu.input.cnit.ct.inputvstb.models.VSTBProvider;
import eu.input.cnit.ct.inputvstb.utils.OnDataSended;

/**
 * Created by gabriele on 05/06/17.
 */

public class InputVSTB extends Application implements OnDataSended{

    public static String channels_list;
    public static String recorded_list;

    public static String device_list;

    public static Map<Integer,Bitmap> logo_map;

    public static List<VSTBProvider> mProviders;
    public static List<VSTBChannel> mRecordedChannels;



    public static String ip_vdi="";

    public static InputVSTB mInstance = null;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(InputVSTB.class.getCanonicalName(), "Application Inizialization!!");

        VSTBSyncManager.initInstance(InputVSTB.this);

        mInstance=this;

    }


        public void onDataSended(Object result) {
            if (result instanceof LinkedList && ((LinkedList) result).size() > 0) {
                Object o = ((LinkedList) result).get(0);
                if (o instanceof VSTBChannel) {

                    final LinkedList<VSTBChannel> channels = (LinkedList<VSTBChannel>) result;
                    if (channels.size() > 0) {


                        InputVSTB.mRecordedChannels = new LinkedList<>();
                        InputVSTB.mRecordedChannels.addAll(channels);

                        JSONArray recorderArray = new JSONArray();

                        for (VSTBChannel c : channels) {
                            recorderArray.put(c.toJSON());
                        }

                        recorded_list = recorderArray.toString();
                    }

                }
                if (o instanceof VSTBDLNADevice) {

                    final LinkedList<VSTBDLNADevice> devices = (LinkedList<VSTBDLNADevice>) result;
                    if (devices.size() > 0) {

                        JSONArray deviceArray = new JSONArray();

                        for (VSTBDLNADevice c : devices) {
                            deviceArray.put(c.toJSON());
                        }

                        device_list = deviceArray.toString();
                    }
                }
            }

        }
}
