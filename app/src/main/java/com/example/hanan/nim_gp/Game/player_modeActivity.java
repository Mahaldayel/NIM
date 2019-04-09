package com.example.hanan.nim_gp.Game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;

import static com.example.hanan.nim_gp.Game.SelectGameLevelActivity.SELECTED_GAME_LEVEL_INTENT;


public class player_modeActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button single;
    private Button multi;
    private ImageView back;
    private String controlType ;
    private int gameLEVEL;

    public static final String CONTROL_MODE_GAME_INTENT ="controlMode";
    public static final String CONTROL_GAME_INTENT ="gameMode";
    private Button mQuit_bt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_mode);
        Intent intent = getIntent();
        controlType = intent.getStringExtra(CONTROL_MODE_GAME_INTENT);
        gameLEVEL = intent.getIntExtra("SELECTED_GAME_LEVEL_INTENT",1);

        initElements();


    }
    private void initElements(){
        single=findViewById(R.id.single);
        single.setOnClickListener(this);
        multi=findViewById(R.id.multi);
        multi.setOnClickListener(this);
        back=findViewById(R.id.back);
        back.setOnClickListener(this);

        mQuit_bt = findViewById(R.id.quit_bt);
        mQuit_bt.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        if(view ==single){
            Intent intent = new Intent(player_modeActivity.this, ConnectionWithRobotCarActivity.class);
            intent.putExtra(CONTROL_MODE_GAME_INTENT,controlType);
            intent.putExtra(SELECTED_GAME_LEVEL_INTENT,getIntent().getIntExtra(SELECTED_GAME_LEVEL_INTENT,1));
            intent.putExtra(CONTROL_GAME_INTENT, "Single");

            startActivity(intent);
        }
        if (view==multi){
            Intent intent = new Intent(player_modeActivity.this, ConnectionWithRobotCarActivity.class);
            intent.putExtra(CONTROL_MODE_GAME_INTENT,controlType);
            intent.putExtra(SELECTED_GAME_LEVEL_INTENT,getIntent().getIntExtra(SELECTED_GAME_LEVEL_INTENT,1));
            intent.putExtra(CONTROL_GAME_INTENT, "MultiPlayer");


            startActivity(intent);
        }
        if(view==back){   startActivity(new Intent(player_modeActivity.this, SelectGameLevelActivity.class));}

        if(view == mQuit_bt){
            startActivity(new Intent( player_modeActivity.this, MainActivity.class));
        }

        }}

