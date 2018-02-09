package eu.input.cnit.ct.inputvstb.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import eu.input.cnit.ct.inputvstb.managers.VSTBSyncManager;

/**
 * Created by gabriele on 22/06/17.
 */

public class VSTBChannel {


    public static String NAME = "name";
    public static String ID = "id";
    public static String URL = "url";
    public static String PROVIDER_ID = "idContentProvider";

    private String name;
    private String url;
    private Integer id;
    private Integer provider_id;



    public VSTBChannel(){}

    public VSTBChannel(String name, String url, Integer id, Integer provider_id) {
        this.name = name;
        this.url = url;
        this.id = id;
        this.provider_id = provider_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(Integer provider_id) {
        this.provider_id = provider_id;
    }

    public JSONObject toJSON() {
        JSONObject jsonProvider = new JSONObject();
        try {
            jsonProvider.put(URL, this.url);
            jsonProvider.put(NAME, this.name);
            jsonProvider.put(PROVIDER_ID, this.provider_id);
            jsonProvider.put(ID, this.id.intValue());




        } catch (JSONException e) {
            Log.e(VSTBChannel.class.getCanonicalName(), "Error on JSON Marshalling");
            Log.e(VSTBChannel.class.getCanonicalName(), e.getMessage());
        }


        return jsonProvider;
    }


    public VSTBChannel fromJSON(JSONObject mProviderJSON){

        try {
            this.setName(mProviderJSON.getString(NAME));
            this.setProvider_id(mProviderJSON.getInt(PROVIDER_ID));
            this.setUrl(mProviderJSON.getString(URL));
            this.setId(mProviderJSON.getInt(ID));



        } catch (JSONException e) {
            Log.e(VSTBChannel.class.getCanonicalName(), "Error on JSON Creation");
            Log.e(VSTBChannel.class.getCanonicalName(), e.getMessage());
        }
        return this;
    }


}
