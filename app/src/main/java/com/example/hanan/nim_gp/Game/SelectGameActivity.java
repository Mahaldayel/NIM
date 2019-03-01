package com.example.hanan.nim_gp.Game;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SelectGameActivity extends AppCompatActivity implements View.OnClickListener {

    private final int SCORE_LEVEL_ONE = 0;
    private final int SCORE_LEVEL_TWO = 200;
    private final int SCORE_LEVEL_THREE = 300;
    private final int SCORE_LEVEL_FOUR = 400;

    public static final String SELECTED_GAME_LEVEL_INTENT = "SELECTED_GAME_LEVEL_INTENT" ;
    public static final String CONTROL_MODE_GAME_INTENT ="controlMode";




    private ImageView gameLevel1_iv;
    private ImageView gameLevel2_iv;
    private ImageView gameLevel3_iv;
    private ImageView gameLevel4_iv;

    private TextView mSelectGameTitle;
    private Button mBackButton;
    private Button mQuitButton;

    private ProgressDialog progressDialog;

    private int mSelectdGameLevel;
    private long mPlyaerScore ;
    private int mHigherAvalableLevel ;
    private String mPlayerEmail ;
    private String controlType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_level);
        Intent intent = getIntent();
        controlType = intent.getStringExtra(CONTROL_MODE_GAME_INTENT);
        initElements();
        getPlayerScore();

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

        mBackButton = findViewById(R.id.back_bt);
        mBackButton.setOnClickListener(this);

        mQuitButton = findViewById(R.id.quit_bt);
        mQuitButton.setOnClickListener(this);

        mSelectGameTitle = findViewById(R.id.selectGameLevel_tv);
        Typeface font = Typeface.createFromAsset(getAssets(),  "fonts/Tondu_Beta.ttf");
        mSelectGameTitle.setTypeface(font);


        progressDialog = new ProgressDialog(this);

       mPlayerEmail =  FirebaseAuth.getInstance().getCurrentUser().getEmail();

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

    private void checkAvailableLevel() {

        if(mSelectdGameLevel > mHigherAvalableLevel)
            showUnavailable();
        else {
            goTo(player_modeActivity.class);
        }
    }

    private void showUnavailable() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SelectGameActivity.this);
        // set title
        alertDialogBuilder.setTitle("Lock Level");
        // set dialog message
        alertDialogBuilder
                .setMessage("Sorry this level not available. \n You need to get more score to play on it. ")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int which) {

                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.carLevel1_iv:
                    mSelectdGameLevel = 0;
                break;
            case R.id.carLevel2_iv:
                    mSelectdGameLevel = 2;
                break;
            case R.id.carLevel3_iv:
                    mSelectdGameLevel = 3;
                break;
            case R.id.carLevel4_iv:
                    mSelectdGameLevel = 4;
                break;
            case R.id.back_bt:
                goTo(control_modeActivity.class);
                break;
            case R.id.quit_bt:
                goTo(MainActivity.class);

        }

        checkAvailableLevel();



    }

    private void goTo(Class nextClass) {

        Context context = SelectGameActivity.this;

        Intent intent = new Intent(context,nextClass);
        intent.putExtra(SELECTED_GAME_LEVEL_INTENT,mSelectdGameLevel);
        intent.putExtra(CONTROL_MODE_GAME_INTENT,controlType);
        startActivity(intent);
    }

}
