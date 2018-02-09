package eu.input.cnit.ct.inputvstb.layout;


import android.os.Bundle;
import android.support.annotation.Nullable;
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
import eu.input.cnit.ct.inputvstb.adapters.recordings.RecordingAdapter;
import eu.input.cnit.ct.inputvstb.models.VSTBChannel;
import eu.input.cnit.ct.inputvstb.models.VSTBProvider;
import eu.input.cnit.ct.inputvstb.utils.DividerItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {



    private static final String CHANNELS = "channel_list";


    private String mRecordedListString;

    private RecyclerView mRecordingsList;
    private TextView mPlaceHolder;
    private List<VSTBChannel> mRecordings;
    private RecordingAdapter mAdapter;
    
    

    public RecordFragment() {
        // Required empty public constructor
    }

    public static RecordFragment newInstance(String recordings ) {
        Log.v(RecordFragment.class.getCanonicalName(),"newInstance("+recordings+")");
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();

        args.putString(CHANNELS, recordings);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {

            mRecordedListString = getArguments().getString(CHANNELS);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView =  inflater.inflate(R.layout.fragment_record, container, false);

        mRecordingsList=(RecyclerView) mView.findViewById(R.id.recyclerViewRecordings);
        mPlaceHolder=(TextView) mView.findViewById(R.id.textViewPlaceHolderRecordings);

        loadChannels();



        if(mRecordings.size()>0){
            Log.v(ChannelsFragment.class.getCanonicalName(),"There are channels");

            mAdapter=new RecordingAdapter(mRecordings,getActivity());



            mPlaceHolder.setVisibility(View.GONE);
            mRecordingsList.setVisibility(View.VISIBLE);


            mRecordingsList.setHasFixedSize(true);
            mRecordingsList.setAdapter(mAdapter);
            mRecordingsList.setLayoutManager(new LinearLayoutManager(getActivity()));


            mRecordingsList.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));



        }else{
            mRecordingsList.setVisibility(View.GONE);
        }


        return mView;
    }


    public boolean loadChannels(){

        try {

            mRecordings = new LinkedList<>();
            JSONArray mArray=new JSONArray(mRecordedListString);
            for(int i=0;i<mArray.length();i++){
                JSONObject mJSONChannel=mArray.getJSONObject(i);
                VSTBChannel c = new VSTBChannel().fromJSON(mJSONChannel);

                mRecordings.add(c);
            }

        } catch (JSONException e) {
            Log.e(RecordFragment.class.getCanonicalName(), "Error on JSON - loadChannels()");
            Log.e(RecordFragment.class.getCanonicalName(), e.getMessage());
            e.printStackTrace();
            return false;
        }



        return true;
    }

}
