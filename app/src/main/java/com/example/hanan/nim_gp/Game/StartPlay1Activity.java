package com.example.hanan.nim_gp.Game;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanan.nim_gp.GameOver.CompletedActivity;
import com.example.hanan.nim_gp.MainActivity;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.neeuro.NativeNSBPlugin.NativeNSBInterface;

import com.example.hanan.nim_gp.R;

import java.util.Timer;
import java.util.TimerTask;

import me.aflak.bluetooth.Bluetooth;

//import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.ROBOT_ADDRESS_OF_SELECTED_DEVICE;
import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.HEADSET_ADDRESS_OF_SELECTED_DEVICE;
import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.NEEURO_ADDRESS_OF_SELECTED_DEVICE;
import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.ROBOT_ADDRESS_OF_SELECTED_DEVICE;
import static com.example.hanan.nim_gp.Game.ConnectionWithRobotCarActivity.CONNECTED_DEVICE_INTENT;
import static com.example.hanan.nim_gp.Game.SelectGameLevelActivity.SELECTED_GAME_LEVEL_INTENT;
import static com.example.hanan.nim_gp.Game.control_modeActivity.CONTROL_MODE_GAME_INTENT;


public class StartPlay1Activity extends AppCompatActivity implements View.OnClickListener {


    public static final String SELECTED_GAME_LEVEL_INTENT = "SELECTED_GAME_LEVEL_INTENT" ;
    public static final String CONTROL_MODE_GAME_INTENT ="controlMode";
    public static final String CONTROL_GAME_INTENT ="gameMode";


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

    private long GAME_TIME = 200000;
//    private long GAME_TIME = 100000;



    private Button mStart_bt;
    private TextView mGameStartCounter;
    private ImageView mFullScreen;
    /*test data*/
    private TextView relax_tv;
    private TextView focus_tv;

    private TextView message;
    private Button quit;
    String controlType;


    private TextView mTextFeild;
    private CountDownTimer countDownTimer;
    private final long startTime = 5000;
    private final long interval = 1 * 1000;
    private boolean timerHasStarted = false;
    private String mHeadsetAddress;
    private Context mContext;

    /**/
    private TextView mScore_tv;
    private ConstraintLayout mCompleted_l;


    private void initElements(){

//        mConnectedDeviceIndex = -1;
        mMsg_tv = findViewById(R.id.msg);

        mStart_bt = findViewById(R.id.start_bt);
        mStart_bt.setOnClickListener(this);

        mGameStartCounter = findViewById(R.id.count);
        mFullScreen = findViewById(R.id.full_screen);

        mTextFeild = findViewById(R.id.count);
        countDownTimer = new StartPlay1Activity.MyCountDownTimer(startTime, interval);

        quit = findViewById(R.id.quit);
        quit.setOnClickListener(this);

        message = findViewById(R.id.controlMode);

        mCompleted_l = findViewById(R.id.completed_layout);
        mScore_tv = findViewById(R.id.textViewScore);

        mContext = StartPlay1Activity.this;

        setPlayCallBack();
        setMessage();
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
        setContentView(R.layout.activity_play_game);

        initElements();
        getFormIntent();
        initBluetoothForRobot();
        initializeSenzeBandBasic();
        setDataToSbDelegate();
        setTestDataTextView();
//        checkOfEndPlayTimer();




    }

    private void setDataToSbDelegate() {

        sbDelegate.setTextView(mMsg_tv);
        sbDelegate.setControlRobotBluetooth(bluetooth);
        sbDelegate.setSelectedRobotAddress(mSelectedRobotDeviceAddress);
        sbDelegate.setComplatedLayout(mCompleted_l);
        sbDelegate.setScoreTextView(mScore_tv);
        sbDelegate.setStarted(false);
        sbDelegate.setEnded(false);


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

        if(intent.hasExtra(NEEURO_ADDRESS_OF_SELECTED_DEVICE))
            mHeadsetAddress = intent.getStringExtra(NEEURO_ADDRESS_OF_SELECTED_DEVICE);

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

                                        NativeNSBInterface.getInstance().disconnectBT(mHeadsetAddress);

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

    private void startGame() {

        mStart_bt.setVisibility(View.GONE);
        mFullScreen.setVisibility(View.GONE);
        mGameStartCounter.setVisibility(View.VISIBLE);
        countDownStart();
    }



    private void setMessage() {

        Typeface message_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        message.setTypeface(message_font);


        if (mCcontrolModeNumber == FOCUS_NUMBER){
            message.setText("Focus To Win");
        }
        if (mCcontrolModeNumber == RELAX_NUMBER){
            message.setText("Relax To Win");}

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.start_bt:
                startGame();
                break;
            case R.id.quit:
                DeleteMessage();
                break;
        }

    }


    private void countDownStart() {
        countDownTimer.start();
        timerHasStarted = true;
        mTextFeild.setText("GO!");

    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            mTextFeild.setText("GO!");
            if(mTextFeild.getText().equals("GO!")){
                message.setVisibility(View.VISIBLE);
                quit.setVisibility(View.VISIBLE);
                mTextFeild.setVisibility(View.GONE);

                sbDelegate.setStarted(true);
                checkOfEndPlayTimer();

            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTextFeild.setText("  "+millisUntilFinished / 1000);

        }

    }

    private void DeleteMessage(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("QUIT");
        builder.setMessage("Do You Want to Quit Current Game?");
        builder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NativeNSBInterface.getInstance().disconnectBT(mHeadsetAddress);
                        startActivity(new Intent(StartPlay1Activity.this, MainActivity.class));

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void goToComplatedGame(){

        Context context = this;
        Class complatedClass = CompletedActivity.class;

        Intent intent = new Intent(context,complatedClass);
        startActivity(intent);
    }







    public Context getContext(){

        return mContext;
    }
}
