package com.example.hanan.nim_gp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hanan.nim_gp.AccountActivity.FirstPage;
import com.example.hanan.nim_gp.AccountActivity.ViewAccountActivity;
import com.example.hanan.nim_gp.Game.control_modeActivity;
import com.example.hanan.nim_gp.Game.play;
import com.example.hanan.nim_gp.GameOver.CompletedActivity;
import com.example.hanan.nim_gp.Training.BeforeTrainingConnectingWithNeeruo;
import com.example.hanan.nim_gp.leaders.LeadersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    FirebaseUser CurrentPlayer = FirebaseAuth.getInstance().getCurrentUser();
    String CurrentplayeId = CurrentPlayer.getUid();
    DatabaseReference refrence= FirebaseDatabase.getInstance().getReference().child("PlayersGameInfo");
    private Button buttonPlay;
    private Button buttonAccount;
    private Button buttonTraining;
    private Button buttonLeaders;
    private Button signout;
    private TextView ScoreView;
    private String Score;
    long currentTime, currentTime2;
    Handler hand = new Handler();
   // TextView ti;
    int i =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getScore();
        initElemens();
    }

    private void getScore(){

        refrence.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {

                    if(child.getKey().equals(CurrentplayeId)) {

                        Score = child.child("score").getValue().toString();
                            System.out.println("************************");
                        System.out.println(Score);
                        ScoreView.setText(Score);

                    }

                }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                   }



        });

    }


        private void initElemens(){

            buttonAccount = findViewById(R.id.buttonAccount);
            buttonAccount.setOnClickListener(this);

            buttonLeaders = findViewById(R.id.buttonLeaders);
            buttonLeaders.setOnClickListener(this);

            buttonPlay = findViewById(R.id.buttonPlay);
            buttonPlay.setOnClickListener(this);

            buttonTraining = findViewById(R.id.buttonTraining);
            buttonTraining.setOnClickListener(this);

            signout=findViewById(R.id.SignOut);
            signout.setOnClickListener(this);

            ScoreView=findViewById(R.id.score1);
           currentTime=  System.currentTimeMillis();
        }



    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.buttonPlay:
                 goTo(control_modeActivity.class);
                break;
            case R.id.buttonAccount:
                goTo(ViewAccountActivity.class);
                break;
            case R.id.buttonTraining:
                goTo(BeforeTrainingConnectingWithNeeruo.class);
                break;
            case R.id.buttonLeaders:
                goTo(LeadersActivity.class);
                break;
            case R.id.SignOut:
                FirebaseAuth.getInstance().signOut();
                goTo(FirstPage.class);
                break;




        }
    }

    private void goTo(Class nextClass){
        Context context = MainActivity.this;
        Intent intent = new Intent(context,nextClass);
        startActivity(intent);
    }



}
