package eu.input.cnit.ct.inputvstb.layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;

import eu.input.cnit.ct.inputvstb.InputVSTB;
import eu.input.cnit.ct.inputvstb.R;
import eu.input.cnit.ct.inputvstb.adapters.dlna_devices.DLNAAdapter;
import eu.input.cnit.ct.inputvstb.adapters.dlna_media.DLNAMediaAdapter;
import eu.input.cnit.ct.inputvstb.managers.VSTBSyncManager;
import eu.input.cnit.ct.inputvstb.models.VSTBChannel;
import eu.input.cnit.ct.inputvstb.models.VSTBDLNADevice;
import eu.input.cnit.ct.inputvstb.utils.OnDataSended;

public class RemoteFragment extends Fragment implements OnDataSended, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {


    private static final String DEVICES = "device_list";

    private String devices;
    private String all_media;

    private LinkedList<VSTBDLNADevice> mDevices;
    private LinkedList<VSTBChannel> mChannels;


    private Spinner mDeviceSpinner, mChannelsSpinner;
    private ImageButton mPlayButton, mPauseButton, mStopButton;
    private SeekBar mVolumeSeekBar;
    private Switch mMuteSwitch;
    private Button mStartButton, mRescanButton;

    private ArrayAdapter<VSTBDLNADevice> mDeviceAdapter;
    private ArrayAdapter<VSTBChannel> mChannelAdapter;

    private VSTBDLNADevice selected = null;
    private VSTBChannel selected_media = null;

    private boolean playedFirstTime = false;

    private ProgressDialog mLoadingDialog;

    public RemoteFragment() {
        // Required empty public constructor
    }


    public static RemoteFragment newInstance(String devices) {
        RemoteFragment fragment = new RemoteFragment();
        Bundle args = new Bundle();

        args.putString(DEVICES, devices);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {

            devices = getArguments().getString(DEVICES);
        }
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_remote, container, false);

        mDeviceSpinner = (Spinner) mView.findViewById(R.id.spinnerDevicesRemote);
        mChannelsSpinner = (Spinner) mView.findViewById(R.id.spinnerMediaRemote);
        mPlayButton = (ImageButton) mView.findViewById(R.id.buttonPlayRemote);
        mStopButton = (ImageButton) mView.findViewById(R.id.buttonStopRemote);
        mPauseButton = (ImageButton) mView.findViewById(R.id.buttonPauseRemote);
        mMuteSwitch = (Switch) mView.findViewById(R.id.switchMuteRemote);
        mVolumeSeekBar = (SeekBar) mView.findViewById(R.id.seekBarVolumeRemote);
        mStartButton = (Button) mView.findViewById(R.id.buttonStartRemote);
        mRescanButton = (Button) mView.findViewById(R.id.buttonRescanRemote);


        mRescanButton.setOnClickListener(this);
        mStartButton.setOnClickListener(this);
        mDeviceSpinner.setOnItemSelectedListener(this);
        mChannelsSpinner.setOnItemSelectedListener(this);
        mMuteSwitch.setOnCheckedChangeListener(this);
        mVolumeSeekBar.setOnSeekBarChangeListener(this);
        mPlayButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);


        updateDevices();
        VSTBSyncManager.getInstance().getDMCContents(this, InputVSTB.ip_vdi, VSTBSyncManager.ALL, VSTBSyncManager.NO_SCAN);
        mLoadingDialog = ProgressDialog.show(getActivity(), "", "Loading DLNA Media. Please wait...", true);


        return mView;
    }


    private void updateDevices() {

        mDevices = new LinkedList<>();
        try {
            JSONArray dlnaDevicesJSON = new JSONArray(devices);
            for (int i = 0; i < dlnaDevicesJSON.length(); i++) {
                VSTBDLNADevice d = new VSTBDLNADevice().fromJSON(dlnaDevicesJSON.getJSONObject(i));
                if (d.getType().compareTo(VSTBDLNADevice.DLNA_Renderer) == 0) mDevices.add(d);
            }

            if (mDevices != null) {
                mDeviceAdapter = new DLNAAdapter(getActivity(), mDevices);

                mDeviceSpinner.setAdapter(mDeviceAdapter);
                mDeviceAdapter.notifyDataSetChanged();
            }


        } catch (JSONException e) {
            Log.e(RecordFragment.class.getCanonicalName(), e.getMessage());
            e.printStackTrace();
        }


        if (mLoadingDialog != null) mLoadingDialog.dismiss();


    }


    private void updateMedia() {
        mChannels = new LinkedList<>();
        try {
            JSONArray dlnaMediaJSON = new JSONArray(all_media);
            for (int i = 0; i < dlnaMediaJSON.length(); i++) {
                mChannels.add(new VSTBChannel().fromJSON(dlnaMediaJSON.getJSONObject(i)));

            }

            if (mChannels != null) {
                mChannelAdapter = new DLNAMediaAdapter(getActivity(), mChannels);

                mChannelsSpinner.setAdapter(mChannelAdapter);
                mChannelAdapter.notifyDataSetChanged();
            }


        } catch (JSONException e) {
            Log.e(RecordFragment.class.getCanonicalName(), e.getMessage());
            e.printStackTrace();
        }
        mLoadingDialog.dismiss();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void onDataSended(Object result) {
        if (result instanceof LinkedList && ((LinkedList) result).size() > 0) {
            Object o = ((LinkedList) result).get(0);
            if (o instanceof VSTBChannel) {

                final LinkedList<VSTBChannel> channels = (LinkedList<VSTBChannel>) result;
                if (channels.size() > 0) {


                    JSONArray recorderArray = new JSONArray();

                    for (VSTBChannel c : channels) {
                        recorderArray.put(c.toJSON());
                    }

                    all_media = recorderArray.toString();
                    updateMedia();
                }

            }
            if (o instanceof VSTBDLNADevice) {

                final LinkedList<VSTBDLNADevice> devicesResponse = (LinkedList<VSTBDLNADevice>) result;
                if (devicesResponse.size() > 0) {

                    JSONArray deviceArray = new JSONArray();

                    for (VSTBDLNADevice c : devicesResponse) {
                        deviceArray.put(c.toJSON());
                    }

                    devices = deviceArray.toString();

                    updateDevices();
                }
            }
        }

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (selected != null) {
            switch (buttonView.getId()) {
                case R.id.switchMuteRemote:
                    VSTBSyncManager.getInstance().remoteDMCControl(this, null, VSTBSyncManager.MUTE, selected.getUuid(), 0, isChecked, "");
                    break;
                default:
                    break;
            }
        } else
            Toast.makeText(getActivity(), "Please select a device!", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (selected != null) {
            switch (seekBar.getId()) {
                case R.id.seekBarVolumeRemote:
                    VSTBSyncManager.getInstance().remoteDMCControl(this, null, VSTBSyncManager.VOL, selected.getUuid(), progress, false, "");
                    break;

            }
        } else
            Toast.makeText(getActivity(), "Please select a device!", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {

        if (selected != null) {
            switch (v.getId()) {
                case R.id.buttonStopRemote:
                    VSTBSyncManager.getInstance().remoteDMCControl(this, null, VSTBSyncManager.STOP, selected.getUuid(), 0, false, "");
                    break;
                case R.id.buttonPauseRemote:
                    VSTBSyncManager.getInstance().remoteDMCControl(this, null, VSTBSyncManager.PAUSE, selected.getUuid(), 0, false, "");
                    break;
                case R.id.buttonPlayRemote:
                    VSTBSyncManager.getInstance().remoteDMCControl(this, null, VSTBSyncManager.PLAY, selected.getUuid(), 0, false, "");
                    break;
                case R.id.buttonStartRemote:
                    VSTBSyncManager.getInstance().remoteDMCControl(this, null, VSTBSyncManager.START, selected.getUuid(), 0, false, selected_media.getUrl());
                    break;
                default:
                    break;
            }
        }
        switch (v.getId()) {
            case R.id.buttonRescanRemote:
                VSTBSyncManager.getInstance().getDLNADevices(this, InputVSTB.ip_vdi, VSTBSyncManager.SCAN);
                VSTBSyncManager.getInstance().getDMCContents(this, InputVSTB.ip_vdi, VSTBSyncManager.ALL, VSTBSyncManager.NO_SCAN);
                mLoadingDialog = ProgressDialog.show(getActivity(), "", "Loading DLNA Media and Devices. Please wait...", true);
                break;
            default:
                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinnerDevicesRemote:
                selected = mDevices.get(position);
                break;
            case R.id.spinnerMediaRemote:
                selected_media = mChannels.get(position);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
