package eu.input.cnit.ct.inputvstb.adapters.dlna_devices;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import eu.input.cnit.ct.inputvstb.R;
import eu.input.cnit.ct.inputvstb.activity.PlayActivity;
import eu.input.cnit.ct.inputvstb.activity.RemoteActivity;
import eu.input.cnit.ct.inputvstb.adapters.dlna_devices.view_holder.ViewHolder;
import eu.input.cnit.ct.inputvstb.managers.VSTBSyncManager;
import eu.input.cnit.ct.inputvstb.models.VSTBDLNADevice;
import eu.input.cnit.ct.inputvstb.models.VSTBProvider;
import eu.input.cnit.ct.inputvstb.utils.OnDataSended;

/**
 * Created by gabriele on 05/06/17.
 */

public class DLNAAdapter extends ArrayAdapter<VSTBDLNADevice> {

    private List<VSTBDLNADevice> mDevices;
    private Activity mActivity;

    private VSTBProvider selected = null;
    private int deviceSelected = -1;

    private ViewHolder vh;

    private int position;

    private ProgressDialog mLoadingDialog;


    public DLNAAdapter(Activity mActivity, List<VSTBDLNADevice> mDevices) {
        super(mActivity,R.layout.spinner_item_layout_dlna_devices,mDevices);
        this.mDevices = mDevices;
        this.mActivity=mActivity;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position,convertView,parent);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.i(DLNAAdapter.class.getCanonicalName(),"getView("+position+")");
        final VSTBDLNADevice device = mDevices.get(position);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        View holder =  inflater.inflate(R.layout.spinner_item_layout_dlna_devices,parent,false);

        //LinearLayout rootView = (LinearLayout) holder.findViewById(R.id.deviceLinearLayo);
        //RelativeLayout nestedView = (RelativeLayout) holder.findViewById(R.id.deviceRelativeLayo);
        TextView mDeviceNameView=(TextView) holder.findViewById(R.id.textViewDeviceName);

        //holder.buildViewHolder(holder);

        mDeviceNameView.setText(device.getName());

        return holder;

        //return super.getView(position, convertView, parent);

    }


    public void add(VSTBDLNADevice device,int position){
        mDevices.add(position,device);

    }


    public void remove(VSTBDLNADevice c){
        int p = mDevices.indexOf(c);
        mDevices.remove(c);

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
