package com.example.hanan.nim_gp.Game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;


public class control_modeActivity extends AppCompatActivity implements View.OnClickListener  {
    private Button relax;
    private Button focus;
    private ImageView back;
    public static final String CONTROL_MODE_GAME_INTENT ="controlMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_control);
        initElements();


    }
    private void initElements(){
        relax=findViewById(R.id.relax);
        relax.setOnClickListener(this);
        focus=findViewById(R.id.focus);
        focus.setOnClickListener(this);
        back=findViewById(R.id.back);
        back.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        if(view ==relax){


            Intent intent = new Intent(control_modeActivity.this, SelectGameActivity.class);
            intent.putExtra(CONTROL_MODE_GAME_INTENT, "Relax");

            startActivity(intent);

        }
        if (view==focus){
            Intent intent = new Intent(control_modeActivity.this, SelectGameActivity.class);
            intent.putExtra(CONTROL_MODE_GAME_INTENT, "Focus");
            startActivity(intent);

        }
        if(view==back){   startActivity(new Intent(control_modeActivity.this, MainActivity.class));}
}}
