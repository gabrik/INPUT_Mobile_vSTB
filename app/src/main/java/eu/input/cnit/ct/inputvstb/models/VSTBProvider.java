package eu.input.cnit.ct.inputvstb.models;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gabriele on 05/06/17.
 */

public class VSTBProvider implements Cloneable{


    public static String NAME = "name";
    public static String ID = "id";
    public static String URL = "url";
    public static String SHOW = "show";
    public static String LOGO_URL = "logo_url";

    private String providerName;
    private String currentShowName;
    private Bitmap logo;

    private String url;
    private Integer id;
    private String logo_url;

    private List<VSTBChannel> mChannels;


    public VSTBProvider(){this.id=0;}

    public VSTBProvider(String providerName, String currentShowName, Bitmap logo, String url, Integer id) {
        this.providerName = providerName;
        this.currentShowName = currentShowName;
        this.logo = logo;
        this.url = url;
        this.id = id;
        mChannels=new LinkedList<>();
    }

    public VSTBProvider(String providerName, String currentShowName, String url, Integer id, String logo_url) {
        this.providerName = providerName;
        this.currentShowName = currentShowName;
        this.url = url;
        this.id = id;
        this.logo_url = logo_url;
        mChannels=new LinkedList<>();
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getCurrentShowName() {
        return currentShowName;
    }

    public void setCurrentShowName(String currentShowName) {
        this.currentShowName = currentShowName;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
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

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public JSONObject toJSON() {
        JSONObject jsonProvider = new JSONObject();
        try {
            jsonProvider.put(URL, this.url);
            jsonProvider.put(NAME, this.providerName);
            jsonProvider.put(SHOW, this.currentShowName);
            jsonProvider.put(ID, this.id.intValue());
            jsonProvider.put(LOGO_URL, this.logo_url);



        } catch (JSONException e) {
            Log.e(VSTBProvider.class.getCanonicalName(), "Error on JSON Marshalling");
            Log.e(VSTBProvider.class.getCanonicalName(), e.getMessage());
        }


        return jsonProvider;
    }


    public VSTBProvider fromJSON(JSONObject mProviderJSON){

        try {
            this.setProviderName(mProviderJSON.getString(NAME));
            this.setCurrentShowName(mProviderJSON.getString(SHOW));
            this.setUrl(mProviderJSON.getString(URL));
            this.setId(mProviderJSON.getInt(ID));
            this.setLogo_url(mProviderJSON.getString(LOGO_URL));


        } catch (JSONException e) {
            Log.e(VSTBProvider.class.getCanonicalName(), "Error on JSON Creation");
            Log.e(VSTBProvider.class.getCanonicalName(), e.getMessage());
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VSTBProvider)) return false;

        VSTBProvider that = (VSTBProvider) o;

        if (!getUrl().equals(that.getUrl())) return false;
        return getId().equals(that.getId());

    }

    @Override
    public int hashCode() {
        int result = getUrl().hashCode();
        result = 31 * result + getId().hashCode();
        return result;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    public VSTBProvider getCopy(){
        try {
            return (VSTBProvider)this.clone();
        } catch (CloneNotSupportedException e) {
            Log.e(VSTBProvider.class.getCanonicalName(), "Error on Cloning VSTBProvider");
            Log.e(VSTBProvider.class.getCanonicalName(), e.getMessage());
            return null;
        }

    }

    public List<VSTBChannel> getmChannels() {
        return mChannels;
    }

    public void setmChannels(List<VSTBChannel> mChannels) {
        this.mChannels = mChannels;
    }
}
