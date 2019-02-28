package com.example.hanan.nim_gp.Game;

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

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DeviceCallback;
import me.aflak.bluetooth.DiscoveryCallback;

import static com.example.hanan.nim_gp.Game.SelectGameActivity.SELECTED_GAME_LEVEL_INTENT;
import static com.example.hanan.nim_gp.Game.control_modeActivity.CONTROL_MODE_GAME_INTENT;


public class ConnectionWithRobotCarActivity extends AppCompatActivity implements AdapterView.OnItemClickListener ,View.OnClickListener {


    private static final int REQUEST_ENABLE_BT = 1111;
    public static final String CONNECTED_DEVICE_INTENT = "connected device index";

    public static final int ROBOT_TYPER = 1;

    // you must have bluetooth permissions before calling the constructor
    private Bluetooth bluetooth ;
    private BluetoothAdapter mBluetoothAdapter;

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
    private String mControlMode;

    private int mSelectedGameLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_list);


        initElements();
        getControlModeFromIntent();
        getSelectedLevelFromIntent();
        check();


    }

    private void getControlModeFromIntent() {

        Intent intent = getIntent();
        if(intent.hasExtra(CONTROL_MODE_GAME_INTENT)){
           mControlMode = intent.getStringExtra(CONTROL_MODE_GAME_INTENT);
        }
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

        mContext = ConnectionWithRobotCarActivity.this;

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

                if ( (!mNewDevices.contains(device)) && device.getType() == ROBOT_TYPER )
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
        mNewDeviceListAdapter = new DeviceListAdapter(ConnectionWithRobotCarActivity.this, R.layout.device_adapter_view,(ArrayList<BluetoothDevice>) mNewDevices);
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
                goToNextActivity();


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

            goTo(player_modeActivity.class);

        }


    }

    private void goTo(Class nextClass) {

        Context context = ConnectionWithRobotCarActivity.this;

        Intent intent = new Intent(context,nextClass);
        startActivity(intent);

    }

    private void goToNextActivity(){


        progressDialog.dismiss();

//        bluetooth.unpair(mConnectedDevice);
        Context context = ConnectionWithRobotCarActivity.this;
        Class nextClass = ConnectionWithHeadset.class;

        Intent intent = new Intent(context,nextClass);
        int index = mPairedDevices.indexOf(mConnectedDevice);
        intent.putExtra(CONNECTED_DEVICE_INTENT,index);
        intent.putExtra(SELECTED_GAME_LEVEL_INTENT, mSelectedGameLevel);
        intent.putExtra(CONTROL_MODE_GAME_INTENT,mControlMode);

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
