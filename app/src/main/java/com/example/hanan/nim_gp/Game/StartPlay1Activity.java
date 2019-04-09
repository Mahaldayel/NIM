package com.example.hanan.nim_gp.Game;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanan.nim_gp.Challenge.SendChallenge;
import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neeuro.NativeNSBPlugin.NativeNSBInterface;

import java.util.Timer;
import java.util.TimerTask;

import me.aflak.bluetooth.Bluetooth;

import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.LEVEL_ONE_TIME;
import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.LEVEL_TWO_TIME;
import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.NEEURO_ADDRESS_OF_SELECTED_DEVICE;
import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.ROBOT_ADDRESS_OF_SELECTED_DEVICE;
import static com.example.hanan.nim_gp.Game.ConnectionWithRobotCarActivity.CONNECTED_DEVICE_INTENT;
import static com.example.hanan.nim_gp.Game.ConnectionWithRobotCarActivity.Game_Score;
import static com.example.hanan.nim_gp.Game.SelectGameLevelActivity.SELECTED_GAME_LEVEL_INTENT;

//import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.ROBOT_ADDRESS_OF_SELECTED_DEVICE;


public class StartPlay1Activity extends AppCompatActivity implements View.OnClickListener {


    public static final String CONTROL_MODE_GAME_INTENT ="controlMode";
    public static final String CONTROL_GAME_INTENT ="gameMode";

    public static final int RELAX_NUMBER = 1;
    public static final int FOCUS_NUMBER = 2;

    private MediaPlayer mPlaymediaPlayer;

    private Button ChallengeButton;




    private Bluetooth bluetooth ;


    ConnectionWithHeadset.senzeBandDelegates sbDelegate ;
    ConnectionWithHeadset.scanCallBack scanCB ;
    ConnectionWithHeadset.connectionCallBack connectionCB ;
    ConnectionWithHeadset.NSBFunctionsCallBack nsbFunctionsCB ;



    private int mSelectedGameLevel;
    private String mCcontrolMode;

    private TextView mMsg_tv;
    /*Robot**/
    private int mConnectedDeviceIndex;
    private BluetoothDevice mConnectedDevice ;
    private Timer timer;
    private TimerTask timerTask;
    private ProgressDialog progressDialog;
    private int mPlayCounter;
    private String mSelectedRobotDeviceAddress;

    private long GAME_TIME = 40000;
//    private long GAME_TIME = 100000;



    private Button mStart_bt;
    private TextView mGameStartCounter;
    private ImageView mFullScreenOpacity;
    /*test data*/
    private TextView relax_tv;
    private TextView focus_tv;

    private TextView message;
    private Button quit;


    private TextView mTextFeild;
    private CountDownTimer countDownTimer;
    private final long startTime = 4000;
    private final long interval = 1 * 1000;
    private boolean timerHasStarted = false;
    private String mHeadsetAddress;
    private Context mContext = StartPlay1Activity.this;

    /**/
    private TextView mScore_c_tv;
    private TextView mScore_f_tv;
    private TextView mChallengeWin;
    private ConstraintLayout mCompleted_l;
    private ConstraintLayout mFailed_l;
    private ImageView mStarsImageView;
    private Button mLevelsBtnC;
    private Button mLevelsBtnF;
    private int mSavedScore;
    private TextView mPlayCounter_tv;
    private CountDownTimer playTimer;
    private String mScoreChallenge;
    private double mScore;

    private void initElements(){

//        mConnectedDeviceIndex = -1;
        mMsg_tv = findViewById(R.id.msg);

        mStart_bt = findViewById(R.id.start_bt);
        mStart_bt.setOnClickListener(this);

        mGameStartCounter = findViewById(R.id.count);
        mFullScreenOpacity = findViewById(R.id.full_screen_opacity);

        mTextFeild = findViewById(R.id.count);
        countDownTimer = new StartPlay1Activity.MyCountDownTimer(startTime, interval);

        quit = findViewById(R.id.quit);
        quit.setOnClickListener(this);

        message = findViewById(R.id.controlMode);

        mCompleted_l = findViewById(R.id.completed_layout);
        mFailed_l = findViewById(R.id.failed_layout);
        mScore_c_tv = findViewById(R.id.textViewScore);
        mScore_f_tv = findViewById(R.id.textViewScore_f);
        mStarsImageView =  findViewById(R.id.imageViewStars_f);
        mLevelsBtnC = findViewById(R.id.LevelsBtn_c);
        mLevelsBtnC.setOnClickListener(this);
        mLevelsBtnF = findViewById(R.id.LevelsBtn_f);
        mLevelsBtnF.setOnClickListener(this);

        mChallengeWin = findViewById(R.id.challengeWin);

        mPlayCounter_tv = findViewById(R.id.play_counter);


        mContext = StartPlay1Activity.this;

        ChallengeButton=findViewById(R.id.challengeButton);
        ChallengeButton.setOnClickListener(this);

        mPlaymediaPlayer =  MediaPlayer.create(this, R.raw.cat_sound);

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

        getFormIntent();
        initElements();
        getSavedScore();
        initBluetoothForRobot();
        initializeSenzeBandBasic();
        setTestDataTextView();




    }

    private void setDataToSbDelegate() {

        sbDelegate.setTextView(mMsg_tv);
        sbDelegate.setControlRobotBluetooth(bluetooth);
        sbDelegate.setSelectedRobotAddress(mSelectedRobotDeviceAddress);
        sbDelegate.setComplatedLayout(mCompleted_l);
        sbDelegate.setFaildLayout(mFailed_l);
        sbDelegate.setScoreCTextView(mScore_c_tv);
        sbDelegate.setScoreFTextView(mScore_f_tv);
        sbDelegate.setFullScreenOpacity(mFullScreenOpacity);
        sbDelegate.setStarsImageView(mStarsImageView);
        sbDelegate.setSavedScore(mSavedScore);
        sbDelegate.setStarted(false);
        sbDelegate.setEnded(false);
        sbDelegate.setmSelectedGameLevel(mSelectedGameLevel);
        sbDelegate.setScoreChallenge(mScoreChallenge);
        sbDelegate.setChallengeWin(mChallengeWin);


    }

    private void setTestDataTextView() {

        sbDelegate.setRelaxTextView(relax_tv);
        sbDelegate.setFocusTextView(focus_tv);
    }

    private void getFormIntent(){

        Intent intent = getIntent();

        if(intent.hasExtra(CONTROL_MODE_GAME_INTENT)){
            mCcontrolMode = intent.getStringExtra(CONTROL_MODE_GAME_INTENT);

        }

        if(intent.hasExtra(CONNECTED_DEVICE_INTENT))
            mConnectedDeviceIndex = intent.getIntExtra(CONNECTED_DEVICE_INTENT,-1);

        if(intent.hasExtra(Game_Score)){
            mScoreChallenge = intent.getStringExtra(Game_Score);

        }

        if(intent.hasExtra(SELECTED_GAME_LEVEL_INTENT))
            mSelectedGameLevel = intent.getIntExtra(SELECTED_GAME_LEVEL_INTENT,1);

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

    public Bluetooth getBluetooth(){

        return bluetooth;
    }


    public TextView getmMsg_tv(){

        return mMsg_tv;
    }




    private void startGame() {

        mStart_bt.setVisibility(View.GONE);
        mFullScreenOpacity.setVisibility(View.GONE);
        mGameStartCounter.setVisibility(View.VISIBLE);
        countDownStart();
    }



    private void setMessage() {

        Typeface message_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        message.setTypeface(message_font);


        if (mCcontrolMode.equals( "Focus")){
            message.setText("Focus To Win");
        }
        if (mCcontrolMode.equals( "Relax")){
            message.setText("Relax To Win");
        }

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
            case R.id.LevelsBtn_f:
            case R.id.LevelsBtn_c:
                goTo(SelectGameLevelActivity.class);
                break;
            case R.id.challengeButton:
                goTo(SendChallenge.class);
                break;

        }

    }

    private void goTo(Class nextClass) {

        Context context = this;
        Intent intent = new Intent(context,nextClass);
        intent.putExtra(CONTROL_MODE_GAME_INTENT,getIntent().getStringExtra(CONTROL_MODE_GAME_INTENT));
        intent.putExtra(SELECTED_GAME_LEVEL_INTENT,getIntent().getIntExtra(SELECTED_GAME_LEVEL_INTENT,1));

        intent.putExtra(Game_Score, String.valueOf(sbDelegate.getScore()));
        intent.putExtra(CONTROL_GAME_INTENT,getIntent().getStringExtra(CONTROL_GAME_INTENT));
        startActivity(intent);

    }


    private void countDownStart() {
        countDownTimer.start();
        timerHasStarted = true;
        mTextFeild.setText("GO!");

    }

    public void setCurrentScore(double mScore) {

        this.mScore = mScore;
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
//            if(mTextFeild.getText().equals("GO!")){
                message.setVisibility(View.VISIBLE);
                quit.setVisibility(View.VISIBLE);
                mTextFeild.setVisibility(View.GONE);
                sbDelegate.setStarted(true);

                if(mSelectedGameLevel == 1)
                    displayCounterLevelOne();
                else if (mSelectedGameLevel == 2)
                    displayCounterLevelTwo();

                mPlaymediaPlayer.start();
                mPlaymediaPlayer.setLooping(true);

//            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTextFeild.setText("  "+millisUntilFinished / 1000);

            if(millisUntilFinished / 1000 == 0)
                mTextFeild.setText("GO!");


        }

    }

    private void DeleteMessage(){

        if(!sbDelegate.getEnded()){

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
        }else {
            NativeNSBInterface.getInstance().disconnectBT(mHeadsetAddress);
            startActivity(new Intent(StartPlay1Activity.this, MainActivity.class));


        }

    }

    public Context getContext(){

        return mContext;
    }

    private void getSavedScore(){

        FirebaseUser CurrentPlayer = FirebaseAuth.getInstance().getCurrentUser();
        final String CurrentplayeId = CurrentPlayer.getUid();
        DatabaseReference refrence= FirebaseDatabase.getInstance().getReference().child("PlayersGameInfo");

        refrence.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {

                    if(child.getKey().equals(CurrentplayeId)) {

                        mSavedScore = Integer.parseInt(child.child("score").getValue().toString());

                        setDataToSbDelegate();


                    }

                }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }



        });

    }


    private void displayCounterLevelTwo(){


        final int[] i = {(LEVEL_TWO_TIME/1000)};
        playTimer = new CountDownTimer(LEVEL_TWO_TIME,1000) {


            @Override
            public void onTick(long millisUntilFinished) {
                // this method will be executed every second ( 1000 ms : the second parameter in the CountDownTimer constructor)

                if(!sbDelegate.getEnded()){

                    mPlayCounter_tv.setText(String.valueOf(0)+":"+String.valueOf(i[0]));

                    i[0]--;
                }

                else {
                    mPlaymediaPlayer.setLooping(false);
                    NativeNSBInterface.getInstance().disconnectBT(mHeadsetAddress);
                    playTimer.cancel();
                }

            }
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

            }
        };
        playTimer.start();

    }


    private void displayCounterLevelOne(){


        final int[] i = {(LEVEL_ONE_TIME/1000)};
        playTimer = new CountDownTimer(LEVEL_ONE_TIME,1000) {


            @Override
            public void onTick(long millisUntilFinished) {
                // this method will be executed every second ( 1000 ms : the second parameter in the CountDownTimer constructor)

                if(!sbDelegate.getEnded()){
                    if(i[0] == 240 ){

                        mPlayCounter_tv.setText(String.valueOf(i[0]/60)+":"+String.valueOf(i[0]-240));

                    } else if(i[0] < 240 && i[0] >= 180){

                        mPlayCounter_tv.setText(String.valueOf(i[0]/60)+":"+String.valueOf(i[0]-180));

                    } else if(i[0] < 180 && i[0] >= 120){
                        mPlayCounter_tv.setText(String.valueOf(i[0]/60)+":"+String.valueOf(i[0]-120));


                    } else if(i[0] <= 120 && i[0] > 60){

                        mPlayCounter_tv.setText(String.valueOf(i[0]/60)+":"+String.valueOf(i[0]-60));

                    }else if(i[0] <= 60 && i[0] > 0) {
                        mPlayCounter_tv.setText(String.valueOf(i[0]/60)+":"+String.valueOf(i[0]));

                    }
                    i[0]--;
                }

                else {
                    mPlaymediaPlayer.setLooping(false);
                    NativeNSBInterface.getInstance().disconnectBT(mHeadsetAddress);
                    playTimer.cancel();
                }

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

            }
        };
        playTimer.start();

    }

}
