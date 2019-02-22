package com.example.hanan.nim_gp.Game;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hanan.nim_gp.AccountActivity.view_accountActivity;
import com.example.hanan.nim_gp.DeviceList.DeviceListActivity;
import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;
import com.example.hanan.nim_gp.Training.TrainingInformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SelectGameActivity extends AppCompatActivity implements View.OnClickListener {

    private final int SCORE_LEVEL_ONE = 100;
    private final int SCORE_LEVEL_TWO = 200;
    private final int SCORE_LEVEL_THREE = 300;
    private final int SCORE_LEVEL_FOUR = 400;


    private ImageView gameLevel1_iv;
    private ImageView gameLevel2_iv;
    private ImageView gameLevel3_iv;
    private ImageView gameLevel4_iv;


    private ProgressDialog progressDialog;

    private int mSelectdGameLevel;
    private long mPlyaerScore = 200;
    private int mHigherAvalableLevel = 2;
    private String mPlayerEmail = "hanooon300@gmail.com";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_level);

        initElements();
        getIntentValue();
        getPlayerScore();



    }

    private void getIntentValue() {


        Intent intent = getIntent();
        if( intent != null && intent.hasExtra("trainingInformationsArray")) {
            ArrayList<TrainingInformation> trainingInformationArrayList = (ArrayList<TrainingInformation>) intent.getSerializableExtra("trainingInformationsArray");

        }
    }

    private void initElements() {

        gameLevel1_iv = findViewById(R.id.carLevel1_iv);
        gameLevel1_iv.setOnClickListener(this);

        gameLevel2_iv = findViewById(R.id.carLevel2_iv);
        gameLevel2_iv.setOnClickListener(this);

        gameLevel3_iv = findViewById(R.id.carLevel3_iv);
        gameLevel3_iv.setOnClickListener(this);

        gameLevel4_iv = findViewById(R.id.carLevel4_iv);
        gameLevel4_iv.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

    }

    private void getPlayerScore() {

        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Players");
        rootRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    getData(dataSnapshot);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
                System.out.println("problem to read value");
                Toast.makeText(SelectGameActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }

        });
    }

    private void getData(DataSnapshot dataSnapshot) {

        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            String email = (String) postSnapshot.child("email").getValue();


            if(email.equals(mPlayerEmail)) {
                mPlyaerScore = (Long) postSnapshot.child("score").getValue();
                displayAvailableLevel();
            }
        }
    }

    private void displayAvailableLevel() {

        progressDialog.dismiss();

        if(mPlyaerScore >= SCORE_LEVEL_ONE)
            findViewById(R.id.lockLevel1).setVisibility(View.GONE);

        if(mPlyaerScore >= SCORE_LEVEL_TWO)
            findViewById(R.id.lockLevel2).setVisibility(View.GONE);

        if(mPlyaerScore >= SCORE_LEVEL_THREE)
            findViewById(R.id.lockLevel3).setVisibility(View.GONE);

        if(mPlyaerScore >= SCORE_LEVEL_FOUR)
            findViewById(R.id.lockLevel4).setVisibility(View.GONE);




        mHigherAvalableLevel = (int)(mPlyaerScore/100);

    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.carLevel1_iv:
                if(mHigherAvalableLevel >= 1)
                    mSelectdGameLevel = 1;
                break;
            case R.id.carLevel2_iv:
                if(mHigherAvalableLevel >= 2)
                    mSelectdGameLevel = 2;
                break;
            case R.id.carLevel3_iv:
                if(mHigherAvalableLevel >= 3)
                    mSelectdGameLevel = 3;
                break;
            case R.id.carLevel4_iv:
                if(mHigherAvalableLevel >= 4)
                    mSelectdGameLevel = 4;
                break;

        }

        Toast.makeText(SelectGameActivity.this,"click :"+String.valueOf(mSelectdGameLevel),Toast.LENGTH_SHORT).show();

        goTo(DeviceListActivity.class);


    }

    private void goTo(Class nextClass) {

        Context context = SelectGameActivity.this;

        Intent intent = new Intent(context,nextClass);
        startActivity(intent);
    }

}
