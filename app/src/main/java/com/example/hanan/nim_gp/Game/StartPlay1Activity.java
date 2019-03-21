package com.example.hanan.nim_gp.Game;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.neeuro.NativeNSBPlugin.NativeNSBInterface;

import com.example.hanan.nim_gp.R;

import java.util.Timer;
import java.util.TimerTask;

import me.aflak.bluetooth.Bluetooth;

//import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.ROBOT_ADDRESS_OF_SELECTED_DEVICE;
import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.ROBOT_ADDRESS_OF_SELECTED_DEVICE;
import static com.example.hanan.nim_gp.Game.ConnectionWithRobotCarActivity.CONNECTED_DEVICE_INTENT;
import static com.example.hanan.nim_gp.Game.SelectGameLevelActivity.SELECTED_GAME_LEVEL_INTENT;
import static com.example.hanan.nim_gp.Game.control_modeActivity.CONTROL_MODE_GAME_INTENT;


public class StartPlay1Activity extends AppCompatActivity {


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

    private TextView mMsg_tv;
    /*Robot**/
    private int mConnectedDeviceIndex;
    private BluetoothDevice mConnectedDevice ;
    private Timer timer;
    private TimerTask timerTask;
    private ProgressDialog progressDialog;
    private int mPlayCounter;
    private String mSelectedRobotDeviceAddress;

    private long GAME_TIME = 100000;


    /*test data*/
    private TextView relax_tv;
    private TextView focus_tv;


    private void initElements(){

        setPlayCallBack();
//        mConnectedDeviceIndex = -1;
        mMsg_tv = findViewById(R.id.msg);

        initTastData();


    }

    private void initTastData() {

        relax_tv = findViewById(R.id.relax);
        focus_tv = findViewById(R.id.focus);
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
        initBluetoothForRobot();
        initializeSenzeBandBasic();
        sbDelegate.setTextView(mMsg_tv);
        sbDelegate.setControlRobotBluetooth(bluetooth);
        sbDelegate.setSelectedRobotAddress(mSelectedRobotDeviceAddress);
        setTestDataTextView();
        checkOfEndPlayTimer();


    }

    private void setTestDataTextView() {

        sbDelegate.setRelaxTextView(relax_tv);
        sbDelegate.setFocusTextView(focus_tv);
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

        if(intent.hasExtra(ROBOT_ADDRESS_OF_SELECTED_DEVICE))
            mSelectedRobotDeviceAddress = intent.getStringExtra(ROBOT_ADDRESS_OF_SELECTED_DEVICE);

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

    private void checkOfEndPlayTimer(){

        sbDelegate.setEnded(false);

        timer = new Timer();
        initTask();
        timer.schedule(timerTask,GAME_TIME);

    }

    private void initTask(){

        timerTask = new TimerTask() {
            @Override
            public void run() {
                endPlay();

            }
        };
    }



    public Bluetooth getBluetooth(){

        return bluetooth;
    }


    public TextView getmMsg_tv(){

        return mMsg_tv;
    }



    private void endPlay() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                sbDelegate.setEnded(true);

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





}
