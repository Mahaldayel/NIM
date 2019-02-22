package com.example.hanan.nim_gp.Training;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanan.nim_gp.Game.SelectGameActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.neeuro.NativeNSBPlugin.NativeNSBInterface;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.example.hanan.nim_gp.R;



public class NSBTrainingActivity extends AppCompatActivity implements View.OnClickListener
{

    public static final int TRAINING_TIME = 30;

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

    private ArrayList<TrainingInformation> trainingInformationsArray;

    private ConstraintLayout mTraining_layout;
    private TextView mCurrentTrainingMode_tv;
    private Button mStartTraining_bt;
    private Button mTryAgain_bt;
    private ImageView mTrainingCar_iv;
    private TextView mDesciption;
    private int mCurrentTrainingMode;

    private TimerTask timerTask;
    private Timer timer;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;


    private void initElements(){

        initTestElements();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Players");
        firebaseAuth = FirebaseAuth.getInstance();

        mCurrentTrainingMode_tv = findViewById(R.id.training_mode_tv);

        mStartTraining_bt = findViewById(R.id.start_training_bt);
        mStartTraining_bt.setOnClickListener(this);

        mTryAgain_bt = findViewById(R.id.try_again_bt);
        mTryAgain_bt.setOnClickListener(this);

        mTraining_layout = findViewById(R.id.training_layout);

        mTrainingCar_iv = findViewById(R.id.training_car);

        mDesciption = findViewById(R.id.training_deception);
        mDesciption.setText("Wear your headset, you will be training on two modes the first one will be Focus on pushing the car");

        initTrainingArray();

    }

    private void initTrainingArray() {

        trainingInformationsArray = new ArrayList<>();

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


    BeforeTrainingConnectingWithNeeruo.senzeBandDelegates sbDelegate =  BeforeTrainingConnectingWithNeeruo.sbDelegate;
    BeforeTrainingConnectingWithNeeruo.scanCallBack scanCB = BeforeTrainingConnectingWithNeeruo.scanCB ;
    BeforeTrainingConnectingWithNeeruo.connectionCallBack connectionCB =  BeforeTrainingConnectingWithNeeruo.connectionCB;
    BeforeTrainingConnectingWithNeeruo.NSBFunctionsCallBack nsbFunctionsCB = BeforeTrainingConnectingWithNeeruo.nsbFunctionsCB;


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
        mCurrentTrainingMode = TRAINING_MODE_FOCUS;
        setTextView();


    }


    public void setTextView() {

        sbDelegate.pumpAttentionTextView(focus);
        sbDelegate.pumpMaxAttentionTextView(avg_foucs);
        sbDelegate.pumpAvgAttentionTextView(max_focus);

        sbDelegate.pumpRelaxationTextView(relaxation);
        sbDelegate.pumpMaxRelaxationTextView(avg_relax);
        sbDelegate.pumpAvgRelaxationTextView(max_relax);

        sbDelegate.pumpBatteryTextView(battery);
    }




    @Override
    public void onClick(View view) {

        if(view == mStartTraining_bt){

            if(mStartTraining_bt.getBackground().equals(getResources().getDrawable(R.drawable.finish_bt))){
                saveTrainingInformation();
                goToSelectGame();
            }else {

                startTraining();
            }

        }else if(mTryAgain_bt == view){
            startTraining();
        }


}

    private void saveTrainingInformation() {

//        String playerId = firebaseAuth.getCurrentUser().getUid();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("Players").child(playerId).setValue(player);
        goToSelectGame();

    }

    private void goToSelectGame() {


        Context context = NSBTrainingActivity.this;
        Class nextClass = SelectGameActivity.class;

        Intent intent = new Intent(context,nextClass);
        intent.putExtra("trainingInformationsArray",trainingInformationsArray);
        startActivity(intent);
    }

    private void endTraining(){

        TrainingInformation trainingInformation ;
        trainingInformation = sbDelegate.endTrain();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mStartTraining_bt.setVisibility(View.VISIBLE);
                mTraining_layout.setVisibility(View.VISIBLE);


                if(mCurrentTrainingMode == TRAINING_MODE_RELAX)
                    mTryAgain_bt.setVisibility(View.VISIBLE);
            }
        });

        if(mCurrentTrainingMode == TRAINING_MODE_FOCUS){

            trainingInformation.setTrainingType("Focus");
            mCurrentTrainingMode = TRAINING_MODE_RELAX;

        }else if(mCurrentTrainingMode == TRAINING_MODE_RELAX){

            trainingInformation.setTrainingType("Relax");
            mCurrentTrainingMode = TRAINING_MODE_FOCUS;

        }

        trainingInformationsArray.add(trainingInformation);
        sbDelegate.clearCount();



    }

    private void startTraining() {

        sbDelegate.setCountToZero();
        sbDelegate.clearLayout();
        mStartTraining_bt.setVisibility(View.GONE);
        mTraining_layout.setVisibility(View.GONE);

        if(mCurrentTrainingMode == TRAINING_MODE_FOCUS){
            startFocusTraining();
            mStartTraining_bt.setBackground(getResources().getDrawable(R.drawable.next_bt));
            mDesciption.setText("Now,  you will be training the second one is Relax in order to pull the car.");
        }
        else if(mCurrentTrainingMode == TRAINING_MODE_RELAX) {
            startRelaxTraining();
            mStartTraining_bt.setBackground(getResources().getDrawable(R.drawable.finish_bt));
            mDesciption.setVisibility(View.GONE);

        }

        checkTimeOftraining();


    }

    private void startRelaxTraining() {

        mCurrentTrainingMode_tv.setText("Relaxation Mode");
        moveBackword();

    }

    private void startFocusTraining() {

        mCurrentTrainingMode_tv.setText("Focus \n Mode");
        moveForward();
    }


    private void checkTimeOftraining(){

        timer = new Timer();
        initTask();
        timer.schedule(timerTask,0,100);
    }

    private void initTask() {

        timerTask = new TimerTask() {
            @Override
            public void run() {

                if(sbDelegate.getCounter() == TRAINING_TIME){
                    trainSucceed();
                    timer.cancel();
                    timerTask.cancel();
                }
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
                alertDialogBuilder.setTitle("Training Succeeded");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Training is successful. Accept this training?")
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
                alertDialog.show();
            }
        });
    }

}
