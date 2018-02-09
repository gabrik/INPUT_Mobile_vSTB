package eu.input.cnit.ct.inputvstb.adapters.dlna_media;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import eu.input.cnit.ct.inputvstb.R;
import eu.input.cnit.ct.inputvstb.adapters.dlna_media.view_holder.ViewHolder;
import eu.input.cnit.ct.inputvstb.models.VSTBChannel;
import eu.input.cnit.ct.inputvstb.models.VSTBDLNADevice;
import eu.input.cnit.ct.inputvstb.models.VSTBProvider;

/**
 * Created by gabriele on 05/06/17.
 */

public class DLNAMediaAdapter extends ArrayAdapter<VSTBChannel> {

    private List<VSTBChannel> mChannels;
    private Activity mActivity;

    private VSTBChannel selected = null;
    private int deviceSelected = -1;

    private ViewHolder vh;

    private int position;

    private ProgressDialog mLoadingDialog;


    public DLNAMediaAdapter(Activity mActivity, List<VSTBChannel> mChannels) {
        super(mActivity,R.layout.spinner_item_layout_dlna_media,mChannels);
        this.mChannels = mChannels;
        this.mActivity=mActivity;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position,convertView,parent);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.i(DLNAMediaAdapter.class.getCanonicalName(),"getView("+position+")");
        final VSTBChannel channel = mChannels.get(position);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        View holder =  inflater.inflate(R.layout.spinner_item_layout_dlna_media,parent,false);

        //LinearLayout rootView = (LinearLayout) holder.findViewById(R.id.deviceLinearLayo);
        //RelativeLayout nestedView = (RelativeLayout) holder.findViewById(R.id.deviceRelativeLayo);
        TextView mDeviceNameView=(TextView) holder.findViewById(R.id.textViewMediaName);

        //holder.buildViewHolder(holder);

        mDeviceNameView.setText(channel.getName());

        return holder;

        //return super.getView(position, convertView, parent);

    }


    public void add(VSTBChannel channel,int position){
        mChannels.add(position,channel);

    }


    public void remove(VSTBChannel c){
        int p = mChannels.indexOf(c);
        mChannels.remove(c);

    }


    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }






    @Override
    public long getItemId(int position) {
        return position;
    }


}
