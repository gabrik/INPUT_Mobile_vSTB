package eu.input.cnit.ct.inputvstb.adapters.dlna_devices.view_holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import eu.input.cnit.ct.inputvstb.R;

/**
 * Created by gabriele on 05/06/17.
 */

public class ViewHolder extends View {


    public TextView mDeviceNameView;


    public LinearLayout rootView;
    public RelativeLayout nestedView;

    public ViewHolder(Context context) {
        super(context);

    }



    public void buildViewHolder(View itemView){
        rootView = (LinearLayout) itemView.findViewById(R.id.deviceLinearLayo);
        nestedView = (RelativeLayout) itemView.findViewById(R.id.deviceRelativeLayo);
        mDeviceNameView=(TextView) itemView.findViewById(R.id.textViewDeviceName);

    }




}
