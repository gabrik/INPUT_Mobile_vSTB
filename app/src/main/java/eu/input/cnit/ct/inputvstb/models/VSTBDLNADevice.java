package eu.input.cnit.ct.inputvstb.models;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by gabriele on 05/07/17.
 */

public class VSTBDLNADevice implements Comparable{

    public final static String DLNA_Renderer="MediaRenderer";
    public final static String DLNA_Server="MediaServer";

    public final static String UUID = "uuid";
    public final static String NAME = "name";
    public final static String TYPE = "type";

    private String uuid;
    private String name;
    private String type;


    public VSTBDLNADevice() {
    }

    public VSTBDLNADevice(String uuid, String name, String type) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return this.uuid.compareTo(((VSTBDLNADevice) o).getUuid());
    }


    public JSONObject toJSON() {
        JSONObject jsonProvider = new JSONObject();
        try {
            jsonProvider.put(TYPE, this.type);
            jsonProvider.put(NAME, this.name);
            jsonProvider.put(UUID, this.uuid);

        } catch (JSONException e) {
            Log.e(VSTBDLNADevice.class.getCanonicalName(), "Error on JSON Marshalling");
            Log.e(VSTBDLNADevice.class.getCanonicalName(), e.getMessage());
        }


        return jsonProvider;
    }


    public VSTBDLNADevice fromJSON(JSONObject mProviderJSON){

        try {
            this.setName(mProviderJSON.getString(NAME));
            this.setType(mProviderJSON.getString(TYPE));
            this.setUuid(mProviderJSON.getString(UUID));


        } catch (JSONException e) {
            Log.e(VSTBDLNADevice.class.getCanonicalName(), "Error on JSON Creation");
            Log.e(VSTBDLNADevice.class.getCanonicalName(), e.getMessage());
        }
        return this;
    }
}
