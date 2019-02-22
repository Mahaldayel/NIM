package com.example.hanan.nim_gp.DeviceList;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.example.hanan.nim_gp.R;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DeviceCallback;

import static com.example.hanan.nim_gp.DeviceList.DeviceListActivity.CONNECTED_DEVICE_INTENT;
import static com.example.hanan.nim_gp.Game.SelectGameActivity.SELECTED_GAME_LEVEL_INTENT;


public class AfterConnectionActivity extends AppCompatActivity implements View.OnClickListener {

    Bluetooth bluetooth ;

    private Button send_bt;
    private TextView receviedMsg_tv;
    private EditText sendNumber_et;
    private String msg = "0";

    private int mConnectedDeviceIndex;

    private BluetoothDevice mConnectedDevice ;

    private ProgressDialog progressDialog;

    private String receviedMsg;

    private Timer timer;
    private TimerTask timerTask;

    private int mSelectedGameLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_connection);


        initElements();
        getConnectedDeviceIndexFromIntent();
        Connect();
        check();
//        checkOfConnect();
    }


    private void initElements(){


        bluetooth = new Bluetooth(this);
        bluetooth.enable();

        send_bt = findViewById(R.id.send_bt);
        send_bt.setOnClickListener(this);

        sendNumber_et = findViewById(R.id.sendNumber);

        mConnectedDeviceIndex = -1;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connect");
        progressDialog.show();

        receviedMsg_tv = findViewById(R.id.receviedMsg);


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


    @Override
    public void onClick(View view) {


        if(view == send_bt)
            msg = sendNumber_et.getText().toString();


        if(bluetooth.getBluetoothAdapter() != null && !bluetooth.isConnected())
            Connect();
        else
            if(bluetooth.isConnected())
            bluetooth.send(msg);

    }

    private void Connect() {


        if(bluetooth.getBluetoothAdapter() != null){
            List<BluetoothDevice> device = bluetooth.getPairedDevices();


            if(mConnectedDeviceIndex < 0)
                getConnectedDeviceIndexFromIntent();

            mConnectedDevice = device.get(mConnectedDeviceIndex);

            if(!bluetooth.isConnected())
                bluetooth.connectToDevice(mConnectedDevice);

        }
    }

    private void getConnectedDeviceIndexFromIntent(){

        Intent intent = getIntent();
        if(intent.hasExtra(CONNECTED_DEVICE_INTENT))
            mConnectedDeviceIndex = intent.getIntExtra(CONNECTED_DEVICE_INTENT,-1);


        if(intent.hasExtra(SELECTED_GAME_LEVEL_INTENT))
            mSelectedGameLevel = intent.getIntExtra(SELECTED_GAME_LEVEL_INTENT,0);
    }


    private void checkOfConnect(){



                if(bluetooth.getBluetoothAdapter() != null && !bluetooth.isConnected())
                    Connect();

                bluetooth.setDeviceCallback(new DeviceCallback() {
                    @Override public void onDeviceConnected(BluetoothDevice device) {
                        hander.sendEmptyMessage(1);
                        timer.cancel();
                    }
                    @Override public void onDeviceDisconnected(BluetoothDevice device, String message) {}
                    @Override public void onMessage(final String message) {
                        receviedMsg = message;
                        hander.sendEmptyMessage(0);

                    }
                    @Override public void onError(String message) {}
                    @Override public void onConnectError(BluetoothDevice device, String message) {

                    }
                });
            }






    Handler hander = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    receviedMsg_tv.setText(receviedMsg);
                    break;
                    case 1:
                        progressDialog.dismiss();
                        break;

            }
        }};

    private void check(){

        timer = new Timer();
        initTask();
        timer.schedule(timerTask,20,100);

    }

    private void initTask(){

        timerTask = new TimerTask() {
            @Override
            public void run() {
                checkOfConnect();

            }
        };
    }

}
