package eu.input.cnit.ct.inputvstb.adapters.channels;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import eu.input.cnit.ct.inputvstb.activity.PlayActivity;
import eu.input.cnit.ct.inputvstb.R;
import eu.input.cnit.ct.inputvstb.activity.RemoteActivity;
import eu.input.cnit.ct.inputvstb.adapters.channels.view_holder.ViewHolder;
import eu.input.cnit.ct.inputvstb.managers.VSTBSyncManager;
import eu.input.cnit.ct.inputvstb.models.VSTBProvider;
import eu.input.cnit.ct.inputvstb.utils.OnDataSended;

/**
 * Created by gabriele on 05/06/17.
 */

public class ChannelAdapter extends RecyclerView.Adapter<ViewHolder> implements PopupMenu.OnMenuItemClickListener,OnDataSended {

    private List<VSTBProvider> mChannels;
    private Context mContext;

    private VSTBProvider selected = null;
    private int deviceSelected = -1;

    private ViewHolder vh;

    private int position;

    private ProgressDialog mLoadingDialog;

    public ChannelAdapter(List<VSTBProvider> mChannels, Context mContext) {
        this.mChannels = mChannels;
        this.mContext = mContext;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(ChannelAdapter.class.getCanonicalName(),"onCreateViewHolder()");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout_channels,parent,false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int p) {
        Log.i(ChannelAdapter.class.getCanonicalName(),"onBindViewHolder("+p+")");
        final VSTBProvider channel = mChannels.get(p);

        holder.mShowNameView.setText(channel.getCurrentShowName());
        holder.mProviderNameView.setText(channel.getProviderName());
        holder.mProviderLogoView.setImageBitmap(channel.getLogo());

        holder.mPlayButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPosition(holder.getPosition());
                selected=channel;
                showPopup(v);
            }
        });



    }

    public void add(VSTBProvider channel,int position){
        mChannels.add(position,channel);
        notifyItemInserted(position);
    }


    public void remove(VSTBProvider c){
        int p = mChannels.indexOf(c);
        mChannels.remove(c);
        notifyItemRemoved(p);
    }


    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }



    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(mContext, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.actions_video_destination, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int s=mChannels.indexOf(selected);
        switch (item.getItemId()){
            case R.id.action_on_smartphone:
                Toast.makeText(mContext,"This will play on smartphone",Toast.LENGTH_LONG).show();
                VSTBSyncManager.getInstance().getContent(this, mChannels.get(s).getCopy(),VSTBSyncManager.SMARTPHONE);
                this.deviceSelected=VSTBSyncManager.SMARTPHONE;
                //intent.putExtra(PlayActivity.EXTRA,mRecordedChannels.get(s).toJSON().toString());
                //mContext.startActivity(intent);
                mLoadingDialog=ProgressDialog.show(mContext, "","Loading. Please wait...", true);
                return true;
            case R.id.action_on_dlna:
                Toast.makeText(mContext,"This will play on dlna devices",Toast.LENGTH_LONG).show();
                VSTBSyncManager.getInstance().getContent(this,mChannels.get(s).getCopy(),VSTBSyncManager.DLNA);
                this.deviceSelected=VSTBSyncManager.DLNA;
                mLoadingDialog=ProgressDialog.show(mContext, "","Loading. Please wait...", true);
                return true;
            case R.id.action_on_both:
                Toast.makeText(mContext,"This will play on both",Toast.LENGTH_LONG).show();
                VSTBSyncManager.getInstance().getContent(this,mChannels.get(s).getCopy(),VSTBSyncManager.BOTH);
                this.deviceSelected=VSTBSyncManager.BOTH;
                mLoadingDialog=ProgressDialog.show(mContext, "","Loading. Please wait...", true);
                return true;
            case R.id.action_record:
                Toast.makeText(mContext,"This will start recording on Edge Storage",Toast.LENGTH_LONG).show();
                VSTBSyncManager.getInstance().recContent(this,mChannels.get(s).getCopy(),VSTBSyncManager.RECORD);
                mLoadingDialog=ProgressDialog.show(mContext, "","Loading. Please wait...", true);
                this.deviceSelected=VSTBSyncManager.RECORD;
                return true;
            case R.id.action_close:
                return true;
            default:
                return false;


        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return mChannels.size();
    }

    @Override
    public void onDataSended(Object result) {
        mLoadingDialog.dismiss();
        if(result instanceof String){
            Toast.makeText(mContext,"Call result is " + (String)result,Toast.LENGTH_LONG).show();

        }
        if(result instanceof VSTBProvider){
            VSTBProvider c = (VSTBProvider) result;
            Intent intent = null;

            switch (this.deviceSelected){
                case VSTBSyncManager.BOTH:
                case VSTBSyncManager.SMARTPHONE:
                    intent = new Intent(mContext, PlayActivity.class);
                    intent.putExtra(PlayActivity.EXTRA_TYPE,PlayActivity.TYPE_PROVIDER);
                    break;
                case VSTBSyncManager.DLNA:
                    intent = new Intent(mContext, RemoteActivity.class);
                    break;
                case VSTBSyncManager.RECORD:
                    Toast.makeText(mContext,"Recording Started on Edge Acquirer",Toast.LENGTH_LONG).show();
                    return;
                default:
                    break;

            }

            if(deviceSelected!=VSTBSyncManager.RECORD){
                intent.putExtra(PlayActivity.EXTRA,c.toJSON().toString());
                intent.putExtra(PlayActivity.EXTRA_DEVICE,this.deviceSelected);
                mContext.startActivity(intent);
            }

        }
    }
}
