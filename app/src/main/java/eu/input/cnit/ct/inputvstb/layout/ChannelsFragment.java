package eu.input.cnit.ct.inputvstb.layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import eu.input.cnit.ct.inputvstb.InputVSTB;
import eu.input.cnit.ct.inputvstb.R;
import eu.input.cnit.ct.inputvstb.adapters.channels.ChannelAdapter;
import eu.input.cnit.ct.inputvstb.models.VSTBProvider;
import eu.input.cnit.ct.inputvstb.utils.DividerItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link ChannelsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChannelsFragment extends Fragment {

    private static final String CHANNELS = "channel_list";


    private String mChannelsListString;





    private RecyclerView mChannelList;
    private TextView mPlaceHolder;
    private List<VSTBProvider> mChannels;
    private ChannelAdapter mAdapter;



    public ChannelsFragment() {
        // Required empty public constructor
    }


    public static ChannelsFragment newInstance( String p) {
        Log.v(ChannelsFragment.class.getCanonicalName(),"newInstance("+p+")");
        ChannelsFragment fragment = new ChannelsFragment();
        Bundle args = new Bundle();

        args.putString(CHANNELS, p);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mChannelsListString = getArguments().getString(CHANNELS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_channels, container, false);

        mChannelList=(RecyclerView) mView.findViewById(R.id.recyclerViewChannels);
        mPlaceHolder=(TextView) mView.findViewById(R.id.textViewPlaceHolder);

        loadChannels();



        if(mChannels.size()>0){
            Log.v(ChannelsFragment.class.getCanonicalName(),"There are channels");

            mAdapter=new ChannelAdapter(mChannels,getActivity());



            mPlaceHolder.setVisibility(View.GONE);
            mChannelList.setVisibility(View.VISIBLE);


            mChannelList.setHasFixedSize(true);
            mChannelList.setAdapter(mAdapter);
            mChannelList.setLayoutManager(new LinearLayoutManager(getActivity()));


            mChannelList.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));



        }else{
            mChannelList.setVisibility(View.GONE);
        }



        return mView;
    }




    public boolean loadChannels(){

        try {

            mChannels = new LinkedList<>();
            JSONArray mArray=new JSONArray(mChannelsListString);
            for(int i=0;i<mArray.length();i++){
                JSONObject mJSONChannel=mArray.getJSONObject(i);
                VSTBProvider c = new VSTBProvider().fromJSON(mJSONChannel);
                c.setLogo(InputVSTB.logo_map.get(c.getId()));
                mChannels.add(c);
            }

        } catch (JSONException e) {
            Log.e(ChannelsFragment.class.getCanonicalName(), "Error on JSON - loadChannels()");
            Log.e(ChannelsFragment.class.getCanonicalName(), e.getMessage());
            e.printStackTrace();
            return false;
        }



        return true;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}
