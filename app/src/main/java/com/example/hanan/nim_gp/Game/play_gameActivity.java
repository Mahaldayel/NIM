package com.example.hanan.nim_gp.Game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.hanan.nim_gp.R;

public class play_gameActivity extends AppCompatActivity {
private String controlType;
private String gameModeType;

    public static final String CONTROL_MODE_GAME_INTENT ="controlMode";
    public static final String CONTROL_GAME_INTENT ="gameMode";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        Intent intent = getIntent();
        /////JUST FOR TESTING
        controlType= intent.getStringExtra(CONTROL_MODE_GAME_INTENT);
        TextView controlMode = (TextView) findViewById(R.id.controlMode);
        controlMode.setText(controlType);

        Intent intent2 = getIntent();
         gameModeType = intent2.getStringExtra(CONTROL_GAME_INTENT);
        TextView gameMode = (TextView) findViewById(R.id.gameMode);
        gameMode.setText(gameModeType);
    }
}