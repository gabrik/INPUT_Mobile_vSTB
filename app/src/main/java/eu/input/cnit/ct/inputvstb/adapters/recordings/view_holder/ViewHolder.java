package eu.input.cnit.ct.inputvstb.adapters.recordings.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import eu.input.cnit.ct.inputvstb.R;

/**
 * Created by gabriele on 05/06/17.
 */

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView mRecordNameView;
    public ImageView mPlayButtonView;




    public ViewHolder(View itemView){

        super(itemView);
        mPlayButtonView = (ImageView) itemView.findViewById(R.id.imageViewPlayRecorded);
        mRecordNameView = (TextView) itemView.findViewById(R.id.textViewShowNameRecorded);
        }

    @Override
    public void onClick(View v){

        }


        public interface OnClickItemListener {
    }
}
