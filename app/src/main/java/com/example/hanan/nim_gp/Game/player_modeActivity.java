package com.example.hanan.nim_gp.Game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hanan.nim_gp.R;

public class player_modeActivity extends AppCompatActivity implements View.OnClickListener  {
    private Button single;
    private Button multi;
    private ImageView back;
    private String controlType ;
    private String gameLEVEL;
    public static final String SELECTED_GAME_LEVEL_INTENT = "SELECTED_GAME_LEVEL_INTENT" ;
    public static final String CONTROL_MODE_GAME_INTENT ="controlMode";
    public static final String CONTROL_GAME_INTENT ="gameMode";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_mode);
        Intent intent = getIntent();
        controlType = intent.getStringExtra(CONTROL_MODE_GAME_INTENT);
        gameLEVEL= intent.getStringExtra(SELECTED_GAME_LEVEL_INTENT);

        initElements();


    }
    private void initElements(){
        single=findViewById(R.id.single);
        single.setOnClickListener(this);
        multi=findViewById(R.id.multi);
        multi.setOnClickListener(this);
        back=findViewById(R.id.back);
        back.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        if(view ==single){
            Intent intent = new Intent(player_modeActivity.this, ConnectionWithRobotCarActivity.class);
            intent.putExtra(CONTROL_MODE_GAME_INTENT,controlType);
            intent.putExtra(SELECTED_GAME_LEVEL_INTENT,gameLEVEL);
            intent.putExtra(CONTROL_GAME_INTENT, "Single");
            startActivity(intent);
        }
        if (view==multi){
            Intent intent = new Intent(player_modeActivity.this, ConnectionWithRobotCarActivity.class);
            intent.putExtra(CONTROL_MODE_GAME_INTENT,controlType);
            intent.putExtra(SELECTED_GAME_LEVEL_INTENT,gameLEVEL);
            intent.putExtra(CONTROL_GAME_INTENT, "MultiPlayer");
            startActivity(intent);
        }
        if(view==back){   startActivity(new Intent(player_modeActivity.this, control_modeActivity.class));}
    }}

