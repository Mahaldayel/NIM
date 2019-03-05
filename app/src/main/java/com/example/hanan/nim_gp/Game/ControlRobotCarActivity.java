package com.example.hanan.nim_gp.Game;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hanan.nim_gp.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DeviceCallback;

import static com.example.hanan.nim_gp.Game.ConnectionWithRobotCarActivity.CONNECTED_DEVICE_INTENT;
import static com.example.hanan.nim_gp.Game.SelectGameLevelActivity.SELECTED_GAME_LEVEL_INTENT;


public class ControlRobotCarActivity extends AppCompatActivity implements View.OnClickListener {

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
    private int mPlayCounter;
    private float SignalsAvreg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_connection);


        initElements();
        getConnectedDeviceIndexFromIntent();
        ConnectToRobot();
        checkOfConnectionToRobotTimer();

    }


    private void initElements(){


        bluetooth = new Bluetooth(this);
        bluetooth.enable();

        send_bt = findViewById(R.id.send_bt);
        send_bt.setOnClickListener(this);

        sendNumber_et = findViewById(R.id.sendNumber);

        mConnectedDeviceIndex = -1;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("ConnectToRobot");
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
            ConnectToRobot();
        else
            if(bluetooth.isConnected())
                bluetooth.send(msg);

    }

    private void ConnectToRobot() {


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


    private void checkOfConnectToRobot(){



        if(bluetooth.getBluetoothAdapter() != null && !bluetooth.isConnected())
            ConnectToRobot();

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

                if(bluetooth.isConnected())
                    startPlay();
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

    private void checkOfConnectionToRobotTimer(){

        timer = new Timer();
        initTask();
        timer.schedule(timerTask,20,1000);

    }

    private void initTask(){

        timerTask = new TimerTask() {
            @Override
            public void run() {
                checkOfConnectToRobot();

            }
        };
    }


    private void startPlay() {

        Log.e("hanan", "in : startPlay  " + SignalsAvreg);

        mPlayCounter = 0;
        SignalsAvreg = 0.445f;

        timer = new Timer();
        initPlayTask();
        timer.schedule(timerTask,100);


    }

    private void initPlayTask() {

        timerTask = new TimerTask() {
            @Override
            public void run() {

                moveCar();

                if( mPlayCounter == 100){
                    // TODO end game
                    endPlay();
                    timer.cancel();
                    timerTask.cancel();
                }

                Log.e("hanan", "in : initPlayTask  " + mPlayCounter);
                mPlayCounter ++;
            }
        };


    }

    public void moveCar() {

        if(bluetooth != null) {
            if (bluetooth.getBluetoothAdapter() != null && bluetooth.isConnected()) {
                bluetooth.send(String.valueOf(String.valueOf(1)));

                Log.e("hanan", "in : sendToRobot  " + msg);

            }
        }

    }


    private void endPlay() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ControlRobotCarActivity.this);
                // set title
                alertDialogBuilder.setTitle("End");
                // set dialog message
                alertDialogBuilder
                        .setMessage("The game is over ")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int which) {

                                    }
                                });


                AlertDialog alertDialog = alertDialogBuilder.create();
                try {

                    alertDialog.show();
                }
                catch (WindowManager.BadTokenException e) {
                    //use a log message
                }



            }
        });
    }



}
