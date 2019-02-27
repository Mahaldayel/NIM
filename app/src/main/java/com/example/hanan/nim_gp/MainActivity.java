package com.example.hanan.nim_gp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanan.nim_gp.AccountActivity.view_accountActivity;
import com.example.hanan.nim_gp.Game.SelectGameActivity;
import com.example.hanan.nim_gp.Training.BeforeTrainingConnectingWithNeeruo;
import com.example.hanan.nim_gp.leaders.LeadersActivity;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    private TextView account_tv;
    private ImageView leaderBord_tv;
    private TextView play_tv;
    private TextView training_tv;

    private Button buttonPlay;
    private Button buttonAccount;
    private Button buttonTraining;
    private Button buttonLeaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initElemens();
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

        }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.buttonPlay:
                goTo(SelectGameActivity.class);
                break;
            case R.id.buttonAccount:
                goTo(view_accountActivity.class);
                break;
            case R.id.buttonTraining:
                goTo(BeforeTrainingConnectingWithNeeruo.class);
                break;
            case R.id.buttonLeaders:
                goTo(LeadersActivity.class);
                break;

        }
    }

    private void goTo(Class nextClass){
        Context context = MainActivity.this;
        Intent intent = new Intent(context,nextClass);
        startActivity(intent);
    }
}
