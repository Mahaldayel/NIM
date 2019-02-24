package com.example.hanan.nim_gp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanan.nim_gp.AccountActivity.view_accountActivity;
import com.example.hanan.nim_gp.Game.SelectGameActivity;
import com.example.hanan.nim_gp.Training.BeforeTrainingConnectingWithNeeruo;
import com.example.hanan.nim_gp.Training.NSBTrainingActivity;
import com.example.hanan.nim_gp.leaders.LeadersActivity;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    private TextView account_tv;
    private ImageView leaderBord_tv;
    private TextView play_tv;
    private TextView training_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initElemens();
    }



        private void initElemens(){

            account_tv = findViewById(R.id.account_tv);
            account_tv.setOnClickListener(this);

            leaderBord_tv = findViewById(R.id.leaderboard_iv);
            leaderBord_tv.setOnClickListener(this);

            play_tv = findViewById(R.id.play_tv);
            play_tv.setOnClickListener(this);

            training_tv = findViewById(R.id.training_tv);
            training_tv.setOnClickListener(this);

        }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.play_tv:
                goTo(SelectGameActivity.class);
                break;
            case R.id.account_tv:
                goTo(view_accountActivity.class);
                break;
            case R.id.training_tv:
                goTo(BeforeTrainingConnectingWithNeeruo.class);
                break;
            case R.id.leaderboard_iv:
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
