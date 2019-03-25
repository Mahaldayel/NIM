package com.example.hanan.nim_gp.Game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;
import com.example.hanan.nim_gp.Training.BeforeTrainingConnectingWithNeeruo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class control_modeActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button relax;
    private Button focus;
    private ImageView back;
    public static final String CONTROL_MODE_GAME_INTENT ="controlMode";
    DatabaseReference refrence= FirebaseDatabase.getInstance().getReference().child("TrainingInformation");
    FirebaseUser CurrentPlayer = FirebaseAuth.getInstance().getCurrentUser();
    String CurrentplayeId = CurrentPlayer.getUid();
    ProgressDialog message;


    boolean shaz=true;
    private Button mQuit_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_control);
        checkAvalibiltyOfTrainingInformation();
        initElements();


    }


    private void initElements(){

        relax=findViewById(R.id.relax);
        relax.setOnClickListener(this);
        focus=findViewById(R.id.focus);
        focus.setOnClickListener(this);
        back=findViewById(R.id.back);
        back.setOnClickListener(this);

        mQuit_bt = findViewById(R.id.quit_bt);
        mQuit_bt.setOnClickListener(this);

    }

    private void checkAvalibiltyOfTrainingInformation(){
        refrence.child(CurrentplayeId).addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Nothing happen
                }
                else {
                    shaz=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });


    }

    private void CreateMessage(){

        if(message==null){
            message= new ProgressDialog(control_modeActivity.this);
            message.setMessage("Please Wait..");
            message.setIndeterminate(false);
            message.setCancelable(true);
            message.show();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirmation message");
        builder.setMessage(
                "You didn't calibrate, calibrate to start the fun");
        builder.setPositiveButton("Go to calibration",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        message.show();//
                        Intent TrainingPage= new Intent(control_modeActivity.this, BeforeTrainingConnectingWithNeeruo.class);
                        startActivity(TrainingPage);
                        message.dismiss();
                        return;

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent HomePage= new Intent(control_modeActivity.this, MainActivity.class);
                startActivity(HomePage);

                message.dismiss();
                return;
            }
        });



        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void onClick(View view) {

        if(view==relax || view==focus){


           if(shaz==false){
                CreateMessage();
                return;
           }
           if(view ==relax){
               Intent intent = new Intent(control_modeActivity.this,SelectGameLevelActivity.class);
               intent.putExtra(CONTROL_MODE_GAME_INTENT, "Relax");
               startActivity(intent);
           }

           if (view==focus){
               Intent intent = new Intent(control_modeActivity.this, SelectGameLevelActivity.class);
               intent.putExtra(CONTROL_MODE_GAME_INTENT, "Focus");
               startActivity(intent);
           }



        }

        if(view == mQuit_bt || view == back ){
            startActivity(new Intent(control_modeActivity.this, MainActivity.class));
        }


    }}
