package com.example.hanan.nim_gp.Game;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hanan.nim_gp.ManageDevices.Device;
import com.example.hanan.nim_gp.R;

import java.util.ArrayList;


public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mLayoutInflater;
    private ArrayList mDevices;
    private int  mViewResourceId;
    private ArrayList<Device> mSevedDeviceList;
    private TextView deviceName;
    private TextView deviceAdress;

    public DeviceListAdapter(Context context, int tvResourceId, ArrayList devices){
        super(context, tvResourceId,devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        if(mDevices.get(position) instanceof BluetoothDevice){

            initElements(convertView);

           BluetoothDevice device = (BluetoothDevice) mDevices.get(position);

            if (deviceName != null) {
                deviceName.setText(device.getName());
            }
            if (deviceAdress != null) {
                deviceAdress.setText(device.getAddress());
            }

            displayNameForExitsDevice(device);
        }
        else {
           Device device = (Device) mDevices.get(position);

        if (device != null) {
            initElements(convertView);

            if (deviceName != null) {
                deviceName.setText(device.getName());
            }
            if (deviceAdress != null) {
                deviceAdress.setText(device.getAddress());
            }

            displayNameForExitsDevice(device);
        }
        }

        return convertView;
    }

    private void initElements(View convertView){
         deviceName = convertView.findViewById(R.id.tvDeviceName);
         deviceAdress =  convertView.findViewById(R.id.tvDeviceAddress);

    }

    private void displayNameForExitsDevice(BluetoothDevice newDevice) {

        if(mSevedDeviceList == null)
            return;

        for(Object device: mSevedDeviceList){

            if(((Device)device).getAddress().equals(newDevice.getAddress())){
                deviceName.setText(((Device) device).getName());
            }

        }
    }

    private void displayNameForExitsDevice(Device newDevice) {

        if(mSevedDeviceList == null)
            return;

        for(Object device: mSevedDeviceList){

            if(((Device)device).getAddress().equals(newDevice.getAddress())){
                deviceName.setText(((Device) device).getName());
            }

        }
    }

    public void setSavedDeviceList(ArrayList devices) {

        mSevedDeviceList = devices;
    }
}
