package com.example.hanan.nim_gp.ManageDevices;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanan.nim_gp.R;

import java.util.ArrayList;


public class DeviceListAdapter extends ArrayAdapter {

    private LayoutInflater mLayoutInflater;
    private ArrayList<Device> mDevices;
    private int  mViewResourceId;
    private TextView deviceName;
    private TextView deviceAdress;
    private TextView deviceType;
    private TextView selectedDeviceBackground;

    private Context mContext;

    public DeviceListAdapter(Context context, int tvResourceId, ArrayList<Device> devices){
        super(context, tvResourceId,devices);
        mContext = context;
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        Device device = mDevices.get(position);

        if (device != null) {

            initElements(convertView);

            if (deviceName != null)
                deviceName.setText(device.getName());

            if (deviceAdress != null)
                deviceAdress.setText(device.getAddress());

            if(deviceType != null)
                deviceType.setText(device.getType().name());

            if(device.getSelected()) {
                selectedDeviceBackground.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }


    private void initElements(View convertView){

        deviceName = convertView.findViewById(R.id.tvDeviceName);
        deviceAdress =  convertView.findViewById(R.id.tvDeviceAddress);
        deviceType = convertView.findViewById(R.id.tvDeviceType);

        selectedDeviceBackground = convertView.findViewById(R.id.select_device_background);
        Typeface font = Typeface.createFromAsset(mContext.getAssets(),  "fonts/Tondu_Beta.ttf");
        selectedDeviceBackground.setTypeface(font);


    }




}
