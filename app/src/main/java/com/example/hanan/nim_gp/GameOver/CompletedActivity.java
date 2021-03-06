package com.example.hanan.nim_gp.GameOver;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanan.nim_gp.Game.SelectGameLevelActivity;
import com.example.hanan.nim_gp.R;


public class CompletedActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewScore;
    private ImageView imageViewLevel;
    private Button LevelsBtn;
    private Button RestartBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);


        textViewScore = findViewById(R.id.textViewScore);
        imageViewLevel = findViewById(R.id.imageViewLevel);
        LevelsBtn = findViewById(R.id.LevelsBtn_c);
        RestartBtn = findViewById(R.id.RestartBtn_c);

        LevelsBtn.setOnClickListener(this);
        RestartBtn.setOnClickListener(this);

        //Changing fonts
        Typeface typeface=Typeface.createFromAsset(getAssets(), "fonts/Lalezar-Regular.ttf");
        textViewScore.setTypeface(typeface);
        textViewScore.setText("122");

        //set current score


        //set overall score

        //set level label


    }

    @Override
    public void onClick(View v) {

        if(v == LevelsBtn){
            startActivity(new Intent(this, SelectGameLevelActivity.class));
        }

        if(v == RestartBtn){

        }

    }
}
