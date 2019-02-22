package com.example.hanan.nim_gp.DeviceList;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DeviceCallback;
import me.aflak.bluetooth.DiscoveryCallback;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;

import static com.example.hanan.nim_gp.Game.SelectGameActivity.SELECTED_GAME_LEVEL_INTENT;


public class DeviceListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener ,View.OnClickListener {


    private static final int REQUEST_ENABLE_BT = 1111;
    public static final String CONNECTED_DEVICE_INTENT = "connected device index";

    // you must have bluetooth permissions before calling the constructor
    Bluetooth bluetooth ;
    BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> mPairedDevices;
    private List<BluetoothDevice> mNewDevices;

    public DeviceListAdapter mNewDeviceListAdapter;
    private ProgressDialog progressDialog;


    private ListView mLvNewDevices;

    private Timer timer;
    private TimerTask timerTask;

    private BluetoothDevice mConnectedDevice ;

    private Button mScan_bt;
    private Button mBack_bt;

    private Context mContext;

    private int mSelectedGameLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_device_list);
        initElements();
        getSelectedLevelFromIntent();
        check();

    }

    private void getSelectedLevelFromIntent() {

        Intent intent = getIntent();

        if(intent.hasExtra(SELECTED_GAME_LEVEL_INTENT))
            mSelectedGameLevel = intent.getIntExtra(SELECTED_GAME_LEVEL_INTENT,0);
    }

    private void initElements(){
        bluetooth = new Bluetooth(this);

        mLvNewDevices = findViewById(R.id.lvNewDevices);
        mLvNewDevices.setOnItemClickListener(this);

        mNewDevices = new ArrayList<>();
        mPairedDevices = new ArrayList<>();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        mScan_bt = findViewById(R.id.button_scan);
        mScan_bt.setOnClickListener(this);

        mBack_bt = findViewById(R.id.button_back);
        mBack_bt.setOnClickListener(this);

        mContext = DeviceListActivity.this;

        progressDialog = new ProgressDialog(this);


    }


    @Override
    protected void onStart() {
        super.onStart();
        bluetooth.onStart();
        bluetooth.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.onStop();
    }


    private void scan(){

        progressDialogShow("Scanning ...");

        if(!bluetooth.isEnabled()){
            bluetooth.isEnabled();
            scan();
        }else{
            displayNewDevice();
            mPairedDevices =  bluetooth.getPairedDevices();

        }


    }



    private void displayNewDevice(){

        bluetooth.startScanning();

        bluetooth.setDiscoveryCallback(new DiscoveryCallback() {
            @Override public void onDiscoveryStarted() {
            }
            @Override public void onDiscoveryFinished() {}
            @Override public void onDeviceFound(BluetoothDevice device) {
                progressDialog.dismiss();
                addNewDeviceToListView(device);


            }
            @Override public void onDevicePaired(BluetoothDevice device) {
                connectPairedDevice(device);

            }
            @Override public void onDeviceUnpaired(BluetoothDevice device) {}
            @Override public void onError(String message) {}
        });

    }

    private void connectPairedDevice(BluetoothDevice device) {
        mPairedDevices.add(device);
        bluetooth.connectToDevice(mConnectedDevice);
        connected();
    }

    private void addNewDeviceToListView(BluetoothDevice device) {
        mNewDevices.add(device);
        mNewDeviceListAdapter = new DeviceListAdapter(DeviceListActivity.this, R.layout.device_adapter_view,(ArrayList<BluetoothDevice>) mNewDevices);
        mLvNewDevices.setAdapter(mNewDeviceListAdapter);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        if (adapterView == mLvNewDevices){


            pairedAndConnectClickedDevice(i);


        }



    }

    private void pairedAndConnectClickedDevice(int i) {

        mConnectedDevice = mNewDevices.get(i);
        progressDialogShow("Connecting ...");
        bluetooth.pair(mConnectedDevice);
        bluetooth.connectToDevice(mConnectedDevice);
    }

    private void progressDialogShow(String msg) {

        progressDialog.setMessage(msg);
        progressDialog.show();
    }


    private void connected(){

        bluetooth.setDeviceCallback(new DeviceCallback() {
            @Override public void onDeviceConnected(BluetoothDevice device) {
                timerTask.cancel();
                goToPullAndPush();

            }
            @Override public void onDeviceDisconnected(BluetoothDevice device, String message) {}
            @Override public void onMessage(String message) {}
            @Override public void onError(String message) {}
            @Override public void onConnectError(BluetoothDevice device, String message) {

                displayErrorMessage(message);
            }
        });
    }

    private void displayErrorMessage(String message) {

        if(message.contains("read failed"))
           message = "the device not available";


        final String msg = message;


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(mContext);
                }
                builder.setTitle("Error")
                        .setMessage(msg)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {

        if(view == mScan_bt)
            scan();

        else if(view == mBack_bt){

            goTo(MainActivity.class);

        }


    }

    private void goTo(Class nextClass) {

        Context context = DeviceListActivity.this;

        Intent intent = new Intent(context,nextClass);
        startActivity(intent);

    }

    private void goToPullAndPush(){


        progressDialog.dismiss();

        Context context = DeviceListActivity.this;
        Class PullAndPushClass = AfterConnectionActivity.class;

        Intent intent = new Intent(context,PullAndPushClass);
        int index = mPairedDevices.indexOf(mConnectedDevice);
        intent.putExtra(CONNECTED_DEVICE_INTENT,index);
        intent.putExtra(SELECTED_GAME_LEVEL_INTENT, mSelectedGameLevel);

        startActivity(intent);

    }



    private void check(){

        timer = new Timer();
        initTask();
        timer.schedule(timerTask,10);

    }

    private void initTask(){

        timerTask = new TimerTask() {
            @Override
            public void run() {
                connected();

            }
        };
    }



}
