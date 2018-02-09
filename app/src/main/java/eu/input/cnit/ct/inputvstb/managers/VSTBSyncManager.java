package eu.input.cnit.ct.inputvstb.managers;

import android.content.Context;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import eu.input.cnit.ct.inputvstb.InputVSTB;
import eu.input.cnit.ct.inputvstb.models.VSTBChannel;
import eu.input.cnit.ct.inputvstb.models.VSTBDLNADevice;
import eu.input.cnit.ct.inputvstb.models.VSTBProvider;
import eu.input.cnit.ct.inputvstb.utils.OnDataSended;

/**
 * Created by gabriele on 22/06/17.
 */

public class VSTBSyncManager {




    public static final int SMARTPHONE = 1;
    public static final int DLNA = 2;
    public static final int BOTH = 3;
    public static final int RECORD = 4;



    public static final int START = 1;
    public static final int PLAY = 2;
    public static final int STOP = 3;
    public static final int PAUSE = 4;
    public static final int VOL = 5;
    public static final int MUTE = 6;
    public static final int START_URL = 7;


    public static final int LIVE = 0;
    public static final int RECORDED = 1;
    public static final int ALL = 2;

    public static final int SCAN = 1;
    public static final int NO_SCAN=0;

    public static final int TIMEOUT = 4000;

    //URL Provider
    //http://<ip_vdi>:5000/gerprovider

    //URL PER PRENDERE VIDEO
    //http://<ip_vdi>:5000/getstream/<id_contenuto>


    //URL IMMAGINE CANALE/Provider
    //http://<ip_vdi>:5000/static/imagecp/<nome_canale_o_provider>.jpg

    private static final String BASEURL = "http://%s:5000";
    private static final String GET_PROVIDERS="/Virtualdecoderinterface/getProviders?mobile=1";
    private static final String GET_CHANNELS="/Virtualdecoderinterface/getChannels/%d?mobile=1";

    private static final String GET_IMAGE = "/static/imagecp/%s.jpg";
    private static final String GET_CONTENT = "/Virtualdecoderinterface/getStream/%d?device=%%d&mobile=1"; //{'url':<url>,'status':<stato>}
    private static final String STOP_CONTENT = "/Virtualdecoderinterface/stopStream/%d?device=%%d&mobile=1";
    private static final String REC_CONTENT = "/Virtualdecoderinterface/getStream/%d?device=4&mobile=1";

    private static final String DMC_ACTION = "/Virtualdecoderinterface/dmc/doAction?uuid=%s&id_content=%d&action=%d&volume=%d&value=%b&mobile=1&url=%s";
    private static final String DMC_GETDEVICES = "/Virtualdecoderinterface/dmc/getDevices?mobile=1&scan=%d";
    private static final String DMC_GETCONTENTS = "/Virtualdecoderinterface/dmc/getContents?mobile=1&recorded=%d&scan=%d";

    //mando uuid=uuid_player&id_content=&&d&action=%%d&mobile=1


    private static VSTBSyncManager mInstance;
    private Context mContext;


    //String.format("%d", 93); // prints 93

    private VSTBSyncManager(Context mContext) {
        this.mContext = mContext;
    }

    public static synchronized void initInstance(Context mCtx) {
        if (mInstance == null) {
            Log.i(VSTBSyncManager.class.getCanonicalName(), "initInstance(" + mCtx.getPackageName() + ")");
            mInstance = new VSTBSyncManager(mCtx);

        }

    }


    public static synchronized VSTBSyncManager getInstance() {
        return mInstance;
    }


    public synchronized void getProviders(final OnDataSended callback,final String mVDIIP) {

        RequestQueue queue = Volley.newRequestQueue(mContext);

        final String url = String.format(BASEURL + GET_PROVIDERS, mVDIIP);

        Log.i(VSTBSyncManager.class.getCanonicalName(), "URL is:" + url);

        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is ok");
                        try {
                            JSONArray responseJSON = new JSONArray(response);

                            LinkedList<VSTBProvider> downloaderProviders=new LinkedList<>();

                            for(int i=0;i<responseJSON.length();i++){
                                JSONObject j = responseJSON.getJSONObject(i);
                                //String providerName, String currentShowName, String url, Integer id, String logo_url
                                downloaderProviders.add(new VSTBProvider(j.getString("name"),
                                        "",
                                        "",
                                        Integer.valueOf(j.getInt("idContentProvider")),
                                        String.format(BASEURL,mVDIIP)+j.getString("image")));


                            }
                                if (callback != null)
                                    callback.onDataSended(downloaderProviders);
                                Log.i(VSTBSyncManager.class.getCanonicalName(), "getProviders() Called");






                        } catch (JSONException e) {
                            Log.e(VSTBSyncManager.class.getCanonicalName(), e.getMessage());
                            e.printStackTrace();
                            if (callback != null)
                                callback.onDataSended(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(VSTBSyncManager.class.getCanonicalName(), "Response error: " + error.toString());
                        error.printStackTrace();
                        if (callback != null)
                            callback.onDataSended(null);
                    }
                }
        );
        getRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);
    }



    public synchronized void getChannels(final OnDataSended callback,final String mVDIIP,final Integer mProviderID){

        RequestQueue queue = Volley.newRequestQueue(mContext);

        final String url = String.format(BASEURL + GET_CHANNELS, mVDIIP,mProviderID);

        Log.i(VSTBSyncManager.class.getCanonicalName(), "URL is:" + url);


        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is ok");
                        try {
                            JSONArray responseJSON = new JSONArray(response);

                            LinkedList<VSTBChannel> downloadedChannles=new LinkedList<>();

                            for(int i=0;i<responseJSON.length();i++){
                                JSONObject j = responseJSON.getJSONObject(i);
                                //VSTBChannel(String name, String url, Integer id, Integer provider_id)
                                downloadedChannles.add(new VSTBChannel(j.getString("name"),
                                        String.format(BASEURL+GET_CONTENT,mVDIIP,mProviderID),
                                        j.getInt("idContent"),
                                        mProviderID));



                            }
                            if (callback != null)
                                callback.onDataSended(downloadedChannles);
                            Log.i(VSTBSyncManager.class.getCanonicalName(), "getChannels() Called");






                        } catch (JSONException e) {
                            Log.e(VSTBSyncManager.class.getCanonicalName(), e.getMessage());
                            e.printStackTrace();
                            if (callback != null)
                                callback.onDataSended(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(VSTBSyncManager.class.getCanonicalName(), "Response error: " + error.toString());
                        error.printStackTrace();
                        if (callback != null)
                            callback.onDataSended(null);
                    }
                }
        );
        getRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);

    }


    public synchronized void getContent(final OnDataSended callback,final VSTBProvider mChannel,final int mDevice){

        RequestQueue queue = Volley.newRequestQueue(mContext);



        final String url = String.format(mChannel.getUrl(), mDevice);



        Log.i(VSTBSyncManager.class.getCanonicalName(), "URL is:" + url);


        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is ok");
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is: " + response);
                        try {
                            JSONObject responseJSON = new JSONObject(response);


                            if(mDevice == SMARTPHONE || mDevice==BOTH)
                                mChannel.setUrl(responseJSON.getString("url"));



                            if (callback != null)
                                callback.onDataSended(mChannel);



                        } catch (JSONException e) {
                            Log.e(VSTBSyncManager.class.getCanonicalName(), e.getMessage());
                            e.printStackTrace();
                            if (callback != null)
                                callback.onDataSended(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(VSTBSyncManager.class.getCanonicalName(), "Response error: " + error.toString());
                        error.printStackTrace();
                        if (callback != null)
                            callback.onDataSended(null);
                    }
                }
        );


        getRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(getRequest);

    }


    public synchronized void recContent(final OnDataSended callback,final VSTBProvider mChannel,final int mDevice){

        RequestQueue queue = Volley.newRequestQueue(mContext);



        //final String url = String.format(REC_CONTENT,mChannel.getmChannels().get(0).getId());
        final String url = String.format(mChannel.getUrl(), mDevice);



        Log.i(VSTBSyncManager.class.getCanonicalName(), "URL is:" + url);


        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is ok");
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is: " + response);
                        try {
                            JSONObject responseJSON = new JSONObject(response);


                            if (callback != null){
                                if(mDevice == SMARTPHONE || mDevice==BOTH){
                                    mChannel.setUrl(responseJSON.getString("url"));
                                    callback.onDataSended(mChannel);
                                }

                                if(mDevice == RECORD){
                                    callback.onDataSended(response);
                                }

                            }











                        } catch (JSONException e) {
                            Log.e(VSTBSyncManager.class.getCanonicalName(), e.getMessage());
                            e.printStackTrace();
                            if (callback != null)
                                callback.onDataSended(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(VSTBSyncManager.class.getCanonicalName(), "Response error: " + error.toString());
                        error.printStackTrace();
                        if (callback != null)
                            callback.onDataSended(null);
                    }
                }
        );


        getRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(getRequest);

    }





    public synchronized void stopContent(final OnDataSended callback,final VSTBProvider mChannel,final int mDevice){

        RequestQueue queue = Volley.newRequestQueue(mContext);

        final String url =   String.format(String.format(BASEURL+STOP_CONTENT,InputVSTB.ip_vdi,mChannel.getId()), mDevice);


        Log.i(VSTBSyncManager.class.getCanonicalName(), "URL is:" + url);


        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is ok");
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is: " + response);
                        try {
                            JSONObject responseJSON = new JSONObject(response);



                            String res= responseJSON.getString("status");
                            if (callback != null)
                                callback.onDataSended(res);





                        } catch (JSONException e) {
                            Log.e(VSTBSyncManager.class.getCanonicalName(), e.getMessage());
                            e.printStackTrace();
                            if (callback != null)
                                callback.onDataSended(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(VSTBSyncManager.class.getCanonicalName(), "Response error: " + error.toString());
                        error.printStackTrace();
                        if (callback != null)
                            callback.onDataSended(null);
                    }
                }
        );
        getRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);

    }



    public synchronized void remoteDMCControl(final OnDataSended callback, final VSTBProvider mChannel, final int mAction, final String mRendererUUID,final int volume,final boolean value,final String url_media){

        RequestQueue queue = Volley.newRequestQueue(mContext);

        VSTBProvider c;
        if(mChannel!=null) c=mChannel;
        else c= new VSTBProvider();

        ////mando uuid=%s&id_content=%d&action=%d&mobile=1

        final String url =   String.format(String.format(BASEURL+DMC_ACTION,InputVSTB.ip_vdi,mRendererUUID,c.getId(),mAction,volume,value,url_media));

        Log.i(VSTBSyncManager.class.getCanonicalName(), "URL is:" + url);


        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is ok");
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is: " + response);
                        try {
                            JSONObject responseJSON = new JSONObject(response);



                            String res= responseJSON.getString("status");
                            if (callback != null)
                                callback.onDataSended(res);





                        } catch (JSONException e) {
                            Log.e(VSTBSyncManager.class.getCanonicalName(), e.getMessage());
                            e.printStackTrace();
                            if (callback != null)
                                callback.onDataSended(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(VSTBSyncManager.class.getCanonicalName(), "Response error: " + error.toString());
                        error.printStackTrace();
                        if (callback != null)
                            callback.onDataSended(null);
                    }
                }
        );
        getRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);

    }

    public synchronized void getDLNADevices(final OnDataSended callback,final String mVDIIP,final int scan){

        RequestQueue queue = Volley.newRequestQueue(mContext);

        final String url = String.format(BASEURL + DMC_GETDEVICES, mVDIIP,scan);

        Log.i(VSTBSyncManager.class.getCanonicalName(), "URL is:" + url);


        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is " + response);
                        try {
                            JSONArray responseJSON = new JSONArray(response);

                            LinkedList<VSTBDLNADevice> downloadedDevices=new LinkedList<>();

                            for(int i=0;i<responseJSON.length();i++){
                                JSONObject j = responseJSON.getJSONObject(i);
                                //public VSTBDLNADevice(String uuid, String name, String type)
                                downloadedDevices.add(new VSTBDLNADevice(j.getString(VSTBDLNADevice.UUID),
                                        j.getString(VSTBDLNADevice.NAME),
                                        j.getString(VSTBDLNADevice.TYPE)));



                            }
                            if (callback != null)
                                callback.onDataSended(downloadedDevices);
                            Log.i(VSTBSyncManager.class.getCanonicalName(), "getChannels() Called");






                        } catch (JSONException e) {
                            Log.e(VSTBSyncManager.class.getCanonicalName(), e.getMessage());
                            e.printStackTrace();
                            if (callback != null)
                                callback.onDataSended(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(VSTBSyncManager.class.getCanonicalName(), "Response error: " + error.toString());
                        error.printStackTrace();
                        if (callback != null)
                            callback.onDataSended(null);
                    }
                }
        );
        getRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);

    }



    public synchronized void getDMCContents(final OnDataSended callback, final String mVDIIP, final int type, final int scan){

        RequestQueue queue = Volley.newRequestQueue(mContext);

        final String url = String.format(BASEURL + DMC_GETCONTENTS, mVDIIP,type,scan);

        Log.i(VSTBSyncManager.class.getCanonicalName(), "URL is:" + url);


        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is ok");
                        Log.d(VSTBSyncManager.class.getCanonicalName(), "Response is: " + response);
                        try {
                            JSONArray responseJSON = new JSONArray(response);




                            LinkedList<VSTBChannel> mRecordedChannels = new LinkedList<>();


                            for(int j=0;j<responseJSON.length();j++){

                                JSONObject o = responseJSON.getJSONObject(j);


                                for(int i=0;i<o.getJSONArray("content").length();i++){
                                    JSONObject js = o.getJSONArray("content").getJSONObject(i);
                                    //VSTBChannel(String name, String url, Integer id, Integer provider_id)
                                    mRecordedChannels.add(new VSTBChannel(js.getString("name"),
                                            js.getString("url"),
                                            i,
                                            0));

                                }
                            }




                            if (callback != null)
                                callback.onDataSended(mRecordedChannels);
                            Log.i(VSTBSyncManager.class.getCanonicalName(), "getDMCContents() Called");






                        } catch (JSONException e) {
                            Log.e(VSTBSyncManager.class.getCanonicalName(), e.getMessage());
                            e.printStackTrace();
                            if (callback != null)
                                callback.onDataSended(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(VSTBSyncManager.class.getCanonicalName(), "Response error: " + error.toString());
                        error.printStackTrace();
                        if (callback != null)
                            callback.onDataSended(null);
                    }
                }
        );

        getRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);

    }

}
