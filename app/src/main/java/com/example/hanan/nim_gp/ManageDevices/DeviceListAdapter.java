package com.example.hanan.nim_gp.ManageDevices;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
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
    private int mSelectedDeviceIndex;

    public DeviceListAdapter(Context context, int tvResourceId, ArrayList<Device> devices,int selectedDeviceIndex){
        super(context, tvResourceId,devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
        mSelectedDeviceIndex = selectedDeviceIndex;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        Device device = mDevices.get(position);

        if (device != null) {
            TextView deviceName = convertView.findViewById(R.id.tvDeviceName);
            TextView deviceAdress =  convertView.findViewById(R.id.tvDeviceAddress);
            TextView deviceType = convertView.findViewById(R.id.tvDeviceType);
            ImageView selectedDeviceBackground = convertView.findViewById(R.id.select_device_background);

            if (deviceName != null)
                deviceName.setText(device.getName());

            if (deviceAdress != null)
                deviceAdress.setText(device.getAddress());

            if(deviceType != null)
                deviceType.setText(device.getType().name());

            if(mSelectedDeviceIndex == position)
                selectedDeviceBackground.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

}
