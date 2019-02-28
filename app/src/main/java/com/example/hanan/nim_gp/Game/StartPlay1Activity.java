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
import android.view.WindowManager;

import com.neeuro.NativeNSBPlugin.NativeNSBInterface;

import com.example.hanan.nim_gp.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DeviceCallback;

import static com.example.hanan.nim_gp.Game.ConnectionWithRobotCarActivity.CONNECTED_DEVICE_INTENT;
import static com.example.hanan.nim_gp.Game.SelectGameActivity.SELECTED_GAME_LEVEL_INTENT;
import static com.example.hanan.nim_gp.Game.control_modeActivity.CONTROL_MODE_GAME_INTENT;


public class StartPlay1Activity extends AppCompatActivity {











//
//  ConnectionWithHeadset.senzeBandDelegates sbDelegate ;
//ConnectionWithHeadset.scanCallBack scanCB ;
//   ConnectionWithHeadset.connectionCallBack connectionCB ;
//    ConnectionWithHeadset.NSBFunctionsCallBack nsbFunctionsCB ;
//
//
//    private void initElements(){
//
//
//
//
//        setPlayCallBack();
//
//    }
//
//
//
//
//
//    private void setPlayCallBack(){
//
//        sbDelegate = ConnectionWithHeadset.sbDelegate;
//        scanCB = ConnectionWithHeadset.scanCB ;
//        connectionCB = ConnectionWithHeadset.connectionCB;
//        nsbFunctionsCB = ConnectionWithHeadset.nsbFunctionsCB;
//
//    }
//
//    public void initializeSenzeBandBasic()
//    {
//        NativeNSBInterface.getInstance().initializeNSB(getApplicationContext(),this,nsbFunctionsCB,scanCB,connectionCB,sbDelegate);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_start_play1);
//
//
//        initElements();
//        initializeSenzeBandBasic();
//
//
//
//
//    }
//


    public static final int RELAX_NUMBER = 1;
    public static final int FOCUS_NUMBER = 2;

    public static final int END_GAME_TIME = 100;


    private Bluetooth bluetooth ;


    ConnectionWithHeadset.senzeBandDelegates sbDelegate ;
    ConnectionWithHeadset.scanCallBack scanCB ;
    ConnectionWithHeadset.connectionCallBack connectionCB ;
    ConnectionWithHeadset.NSBFunctionsCallBack nsbFunctionsCB ;


    private int mSelectedGameLevel;
    private int mCcontrolModeNumber;

    /*Robot**/
    private int mConnectedDeviceIndex;
    private BluetoothDevice mConnectedDevice ;
    private Timer timer;
    private TimerTask timerTask;
    private ProgressDialog progressDialog;
    private int mPlayCounter;
    private float SignalsAvreg;


    private void initElements(){



        setPlayCallBack();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connect To Robot");
        progressDialog.show();

        mConnectedDeviceIndex = -1;


    }

    private void initBluetoothForRobot() {

        bluetooth = new Bluetooth(this);
        bluetooth.enable();

    }


    private void setPlayCallBack(){

        sbDelegate = ConnectionWithHeadset.sbDelegate;
        scanCB = ConnectionWithHeadset.scanCB ;
        connectionCB = ConnectionWithHeadset.connectionCB;
        nsbFunctionsCB = ConnectionWithHeadset.nsbFunctionsCB;

    }

    public void initializeSenzeBandBasic()
    {
        NativeNSBInterface.getInstance().initializeNSB(getApplicationContext(),this,nsbFunctionsCB,scanCB,connectionCB,sbDelegate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_play1);

        initElements();
        getFormIntent();
        initializeSenzeBandBasic();

        initBluetoothForRobot();
        ConnectToRobot();
        checkOfConnectionToRobotTimer();


    }

    private void getFormIntent(){

        Intent intent = getIntent();

        if(intent.hasExtra(CONTROL_MODE_GAME_INTENT)){
            mCcontrolModeNumber = intent.getIntExtra(CONTROL_MODE_GAME_INTENT,0);

        }

        if(intent.hasExtra(CONNECTED_DEVICE_INTENT))
            mConnectedDeviceIndex = intent.getIntExtra(CONNECTED_DEVICE_INTENT,-1);


        if(intent.hasExtra(SELECTED_GAME_LEVEL_INTENT))
            mSelectedGameLevel = intent.getIntExtra(SELECTED_GAME_LEVEL_INTENT,0);


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



    /**Robot Car**/

    private void checkOfConnectionToRobotTimer(){

        timer = new Timer();
        initTask();
        timer.schedule(timerTask,20,100);

    }

    private void initTask(){

        timerTask = new TimerTask() {
            @Override
            public void run() {
                checkOfConnectToRobot();

            }
        };
    }

    private void checkOfConnectToRobot(){



        if(bluetooth.getBluetoothAdapter() != null && !bluetooth.isConnected())
            ConnectToRobot();

        bluetooth.setDeviceCallback(new DeviceCallback() {
            @Override public void onDeviceConnected(BluetoothDevice device) {
                timer.cancel();
                hander.sendEmptyMessage(1);

            }
            @Override public void onDeviceDisconnected(BluetoothDevice device, String message) {}
            @Override public void onMessage(final String message) {
                //TODO get score from robot
                hander.sendEmptyMessage(0);

            }
            @Override public void onError(String message) {}
            @Override public void onConnectError(BluetoothDevice device, String message) {

            }
        });

        Log.e("hanan", "out : startPlay  " + SignalsAvreg);

        if(bluetooth.isConnected()){
            sbDelegate.setControlRobotBluetooth(bluetooth,this);
            sbDelegate.startPlay();
//            startPlay();
            Log.e("hanan", "in : startPlay  " + SignalsAvreg);

        }

    }

    private void startPlay() {

        Log.e("hanan", "in : startPlay  " + SignalsAvreg);

        mPlayCounter = 0;
        SignalsAvreg = 0.445f;

        timer = new Timer();
        initPlayTask();
        timer.schedule(timerTask,0,100);


    }

    private void initPlayTask() {

        timerTask = new TimerTask() {
            @Override
            public void run() {

                checkOFPlayerSignle();

                if( mPlayCounter == END_GAME_TIME){
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

    private void checkOFPlayerSignle() {

        float temp = sbDelegate.getRelax();
        Log.e("hanan", "in : checkOFPlayerSignle  " + temp);

        if(temp > SignalsAvreg)
            sbDelegate.sendToRobot(String.valueOf((int)Math.ceil(temp/SignalsAvreg)));
    }

    private void endPlay() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        StartPlay1Activity.this);
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


    private void ConnectToRobot() {


        if(bluetooth.getBluetoothAdapter() != null){
            List<BluetoothDevice> device = bluetooth.getPairedDevices();


            if(mConnectedDeviceIndex < 0)
                getFormIntent();
            else
                mConnectedDevice = device.get(mConnectedDeviceIndex);

            if(!bluetooth.isConnected() && mConnectedDevice != null)
                bluetooth.connectToDevice(mConnectedDevice);

        }
    }


    Handler hander = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    //TODO display score
                    break;
                case 1:
                    progressDialog.dismiss();

                    break;

            }
        }};







}
