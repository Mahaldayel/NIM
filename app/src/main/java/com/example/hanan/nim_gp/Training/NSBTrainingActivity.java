package com.example.hanan.nim_gp.Training;

import android.app.AlertDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanan.nim_gp.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.neeuro.NativeNSBPlugin.NativeNSBInterface;

import java.util.Timer;
import java.util.TimerTask;

import com.example.hanan.nim_gp.R;

import static com.example.hanan.nim_gp.Game.ConnectionWithHeadset.NEEURO_ADDRESS_OF_SELECTED_DEVICE;


public class NSBTrainingActivity extends AppCompatActivity implements View.OnClickListener
{

    public static final int TRAINING_TIME = 30000;

    public static final int TRAINING_MODE_FOCUS = 1;
    public static final int TRAINING_MODE_RELAX = 2;



    /**for test only**/
    private TextView focus;
    private TextView relaxation;
    private TextView battery;
    private TextView avg_foucs;
    private TextView max_focus;
    private TextView avg_relax;
    private TextView max_relax;

    /***/


    private ConstraintLayout mTraining_layout;
    private TextView mCurrentTrainingMode_tv;
    private Button mStartTraining_bt;
    private Button mTryAgain_bt;
    private Button mBack_bt;
    private Button mQuit_bt;
    private ImageView mTrainingCar_iv;
    private TextView mDesciption;
    private int mCurrentTrainingMode;

    private TimerTask timerTask;
    private Timer timer;

    private boolean mFinish = false;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private TrainingInformation mTainingInformation;

    private static Context mContext;


    /** training callback **/
    BeforeTrainingConnectingWithNeeruo.senzeBandDelegates sbDelegate ;
    BeforeTrainingConnectingWithNeeruo.scanCallBack scanCB ;
    BeforeTrainingConnectingWithNeeruo.connectionCallBack connectionCB ;
    BeforeTrainingConnectingWithNeeruo.NSBFunctionsCallBack nsbFunctionsCB ;
    private String mHeadsetAddress;
    private TextView mTrainingCounter;


    private void initElements(){

        initTestElements();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Players");
        firebaseAuth = FirebaseAuth.getInstance();

        mCurrentTrainingMode_tv = findViewById(R.id.training_mode_tv);
        Typeface font = Typeface.createFromAsset(getAssets(),  "fonts/Tondu_Beta.ttf");
        mCurrentTrainingMode_tv.setTypeface(font);


        mStartTraining_bt = findViewById(R.id.start_training_bt);
        mStartTraining_bt.setOnClickListener(this);

        mTryAgain_bt = findViewById(R.id.try_again_bt);
        mTryAgain_bt.setOnClickListener(this);

        mBack_bt = findViewById(R.id.back_bt);
        mBack_bt.setOnClickListener(this);

        mQuit_bt = findViewById(R.id.quit);
        mQuit_bt.setOnClickListener(this);

        mTraining_layout = findViewById(R.id.training_layout);

        mTrainingCar_iv = findViewById(R.id.training_car);

        mDesciption = findViewById(R.id.before_scanning_deception);
        mDesciption.setTypeface(font);
        mDesciption.setText("Wear your headset, you will be training on two modes the first one will be focus on pushing the car");

        mTrainingCounter = findViewById(R.id.training_counter);

        mContext = NSBTrainingActivity.this;

        initTrainingInformation();
        setTrainingCallBack();

    }

    private void initTrainingInformation() {

        mTainingInformation = new TrainingInformation();
        mTainingInformation.setPlayerEmail(firebaseAuth.getCurrentUser().getEmail());


    }

    private void initTestElements() {

        battery = findViewById(R.id.battery);

        focus = findViewById(R.id.focus);
        avg_foucs = findViewById(R.id.avg_focus);
        max_focus = findViewById(R.id.max_focus);

        relaxation = findViewById(R.id.relax);
        avg_relax = findViewById(R.id.avg_relax);
        max_relax = findViewById(R.id.max_relax);



    }

    private void setTrainingCallBack(){

        sbDelegate =  BeforeTrainingConnectingWithNeeruo.sbDelegate;
        scanCB = BeforeTrainingConnectingWithNeeruo.scanCB ;
        connectionCB =  BeforeTrainingConnectingWithNeeruo.connectionCB;
        nsbFunctionsCB = BeforeTrainingConnectingWithNeeruo.nsbFunctionsCB;

    }

    public void initializeSenzeBandBasic()
    {
        NativeNSBInterface.getInstance().initializeNSB(getApplicationContext(),this,nsbFunctionsCB,scanCB,connectionCB,sbDelegate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);


        initElements();
        initializeSenzeBandBasic();
        getNureeAddressFormIntent();
        mCurrentTrainingMode = TRAINING_MODE_FOCUS;
        setTextView();
        prepareForFocusTraining();



    }

    private void getNureeAddressFormIntent() {

        Intent intent = getIntent();

        if(intent.hasExtra(NEEURO_ADDRESS_OF_SELECTED_DEVICE))
            mHeadsetAddress = intent.getStringExtra(NEEURO_ADDRESS_OF_SELECTED_DEVICE);
    }


    public Context getContext(){

        return mContext;
    }


    public void setTextView() {

        sbDelegate.pumpAttentionTextView(focus);
        sbDelegate.pumpMaxAttentionTextView(avg_foucs);
        sbDelegate.pumpAvgAttentionTextView(max_focus);

        sbDelegate.pumpRelaxationTextView(relaxation);
        sbDelegate.pumpMaxRelaxationTextView(avg_relax);
        sbDelegate.pumpAvgRelaxationTextView(max_relax);

        sbDelegate.pumpBatteryTextView(battery);


        connectionCB.setFinish(false);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.start_training_bt:
                if(mFinish)
                    if(checkOfFocusData()&& checkOfRelaxData())
                        saveTrainingInformationOnDatabase();
                    else
                        trainFailed();
                else
                    startTraining();
                break;
                case R.id.try_again_bt:
                prepareForFocusTraining();
                break;
            case R.id.back_bt:
                goTo(BeforeTrainingConnectingWithNeeruo.class);
                break;
            case R.id.quit:
                connectionCB.setFinish(true);
                NativeNSBInterface.getInstance().disconnectBT(mHeadsetAddress);
                goTo(MainActivity.class);
                break;
        }

    }

    private boolean checkOfFocusData() {
        if(mTainingInformation != null){

            if(mTainingInformation.getAvgFocus() == 0)
                return false;
            if(mTainingInformation.getMaxFocus() == 0)
                return false;

        }

        return true;
    }

    private boolean checkOfRelaxData() {

        if(mTainingInformation != null){

            if(mTainingInformation.getAvgRelax() == 0)
                return false;
            if(mTainingInformation.getMaxRelax() == 0)
                return false;

        }

        return true;
    }

    private void saveTrainingInformationOnDatabase() {

        String playerId = firebaseAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("TrainingInformation").child(playerId).setValue(mTainingInformation);

        goTo(MainActivity.class);
    }

    private void goTo(Class nextClass) {


        Context context = NSBTrainingActivity.this;

        Intent intent = new Intent(context,nextClass);
        NativeNSBInterface.getInstance().disconnectBT(mHeadsetAddress);

        startActivity(intent);


    }




    private void endTraining(){


        updateUi();

        if(mCurrentTrainingMode == TRAINING_MODE_FOCUS){

//            mTainingInformation = sbDelegate.endTrainFocus(mTainingInformation);
            mCurrentTrainingMode = TRAINING_MODE_RELAX;


        }else if(mCurrentTrainingMode == TRAINING_MODE_RELAX){
//            mTainingInformation = sbDelegate.endTrainRelax(mTainingInformation);
            mCurrentTrainingMode = TRAINING_MODE_FOCUS;
        }


    }

    private void updateUi() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mStartTraining_bt.setVisibility(View.VISIBLE);
                mTraining_layout.setVisibility(View.VISIBLE);

                if(mFinish)
                    mQuit_bt.setVisibility(View.GONE);

            }
        });
    }

    private void startTraining() {


        sbDelegate.clearLayout();
        startTrainingUiUpdate();

        if(mCurrentTrainingMode == TRAINING_MODE_FOCUS){

            startFocusTraining();
        }
        else if(mCurrentTrainingMode == TRAINING_MODE_RELAX) {

            startRelaxTraining();

        }

        checkTimeOftraining();


    }

    private void startTrainingUiUpdate() {

        mStartTraining_bt.setVisibility(View.GONE);
        mTraining_layout.setVisibility(View.GONE);
    }


    /***/
    private void prepareForRelaxTraining(){

        mStartTraining_bt.setBackground(getResources().getDrawable(R.drawable.next_bt));
        mDesciption.setText("Now, you will be calibrating on the second one is relax in order to pull the car.");

    }

    private void prepareForFocusTraining(){

        mFinish = false;
        mStartTraining_bt.setVisibility(View.VISIBLE);
        mTraining_layout.setVisibility(View.VISIBLE);
        mTryAgain_bt.setVisibility(View.GONE);
        mStartTraining_bt.setBackground(getResources().getDrawable(R.drawable.start_bt));
        mDesciption.setText("Wear your headset, you will be calibrating on two modes the first one will be focus on pushing the car");

    }

    /***/

    private void startRelaxTraining() {

        mFinish = true;
        mStartTraining_bt.setBackground(getResources().getDrawable(R.drawable.finish_bt));
        mTryAgain_bt.setVisibility(View.GONE);
        mDesciption.setText("You have completed the calibrating ");
        mCurrentTrainingMode_tv.setText("Relaxation Mode");
        moveBackword();

    }

    private void startFocusTraining() {

        mCurrentTrainingMode_tv.setText("Focus \n Mode");
        moveForward();
        prepareForRelaxTraining();
    }


    private void checkTimeOftraining(){

        timer = new Timer();
        initTask();
        timer.schedule(timerTask,TRAINING_TIME);
        displayCounter();

    }

    private void initTask() {

        timerTask = new TimerTask() {
            @Override
            public void run() {


                if(mCurrentTrainingMode == TRAINING_MODE_FOCUS){
                    mTainingInformation = sbDelegate.endTrainFocus(mTainingInformation);
                    if(checkOfFocusData())
                        trainSucceed();
                    else
                        trainFailed();
                }

                if(mCurrentTrainingMode == TRAINING_MODE_RELAX){
                    mTainingInformation = sbDelegate.endTrainRelax(mTainingInformation);
                    if(checkOfRelaxData())
                        trainSucceed();
                    else
                        trainFailed();

                    connectionCB.setFinish(true);

                }

                timer.cancel();
                    timerTask.cancel();

            }
        };


    }

    public void moveBackword(){
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_backward);
        mTrainingCar_iv.startAnimation(animation1);
    }

    public void moveForward(){
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_forward);
        mTrainingCar_iv.startAnimation(animation1);

    }


    public void trainSucceed() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        NSBTrainingActivity.this);
                // set title
                alertDialogBuilder.setTitle("Calibrating Succeeded");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Calibrating is successful. Accept this Calibrating?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int which) {

                                        endTraining();
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        startTraining();
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

    public void trainFailed() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        NSBTrainingActivity.this);
                // set title
                alertDialogBuilder.setTitle("Calibrating Failed");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Calibrating is Failed.\nPlease make sure yor are wearing your headset correctly ")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int which) {

                                        startTraining();
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
    /****/

    private void displayCounter(){

        final int[] i = {(TRAINING_TIME/1000)-2};
        CountDownTimer timer = new CountDownTimer(TRAINING_TIME,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // this method will be executed every second ( 1000 ms : the second parameter in the CountDownTimer constructor)

                mTrainingCounter.setText(String.valueOf(i[0]));
                i[0]--;

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

            }
        };
        timer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

//       NativeNSBInterface.getInstance().UnregisterUnbind();
    }


    private void displayDialog(){


        runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            mContext);
                    // set title
                    alertDialogBuilder.setTitle("Unable to receive your signals");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(" Unable to receive your signals from your headset. \n Please make sure your headset is working and has enough battery, and try reconnect again ")
                            .setCancelable(false)
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int which) {
                                            goTo(BeforeTrainingConnectingWithNeeruo.class);
                                        }
                                    });


                    AlertDialog alertDialog = alertDialogBuilder.create();
                    try {

                        alertDialog.show();
                    }
                    catch (WindowManager.BadTokenException e) { }

                }
            });


    }


}
