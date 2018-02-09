package eu.input.cnit.ct.inputvstb.adapters.channels.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import eu.input.cnit.ct.inputvstb.R;

/**
 * Created by gabriele on 05/06/17.
 */

public class ViewHolder extends RecyclerView.ViewHolder {


    public TextView mProviderNameView;
    public TextView mShowNameView;
    public ImageView mProviderLogoView;
    public ImageView mPlayButtonView;

    public LinearLayout rootView;
    public RelativeLayout nestedView;



    public ViewHolder(View itemView) {

        super(itemView);


        rootView = (LinearLayout) itemView.findViewById(R.id.providerLinearLayo);
        nestedView = (RelativeLayout) itemView.findViewById(R.id.providerRelativeLayo);

        mProviderLogoView = (ImageView) itemView.findViewById(R.id.imageViewProviderLogo);
        mPlayButtonView = (ImageView) itemView.findViewById(R.id.imageViewPlay);
        mProviderNameView = (TextView) itemView.findViewById(R.id.textViewProviderName);
        mShowNameView = (TextView) itemView.findViewById(R.id.textViewCurrentShow);





    }



}
