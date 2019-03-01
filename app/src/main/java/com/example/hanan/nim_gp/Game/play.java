package com.example.hanan.nim_gp.Game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;

public class play extends AppCompatActivity implements View.OnClickListener{

    private TextView message;
    private Button quit;
    String controlType;

    public static final String SELECTED_GAME_LEVEL_INTENT = "SELECTED_GAME_LEVEL_INTENT" ;
    public static final String CONTROL_MODE_GAME_INTENT ="controlMode";
    public static final String CONTROL_GAME_INTENT ="gameMode";


    private TextView mTextFeild;
    private CountDownTimer countDownTimer;
    private final long startTime = 5000;
    private final long interval = 1 * 1000;
    private boolean timerHasStarted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        Intent intent = getIntent();
        controlType = intent.getStringExtra(CONTROL_MODE_GAME_INTENT);
        //gameLEVEL= intent.getStringExtra(SELECTED_GAME_LEVEL_INTENT);

        mTextFeild = findViewById(R.id.count);
        initElements();
        countDownStart();

    }
    private void initElements(){
        mTextFeild = findViewById(R.id.count);
        countDownTimer = new MyCountDownTimer(startTime, interval);

        quit=findViewById(R.id.quit);
        quit.setOnClickListener(this);

        message=findViewById(R.id.controlMode);
        Typeface message_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        message.setTypeface(message_font);
        if (controlType.equals("Focus")){
          message.setText("Focus To Win");
        }
        if (controlType.equals("Relax")){
            message.setText("Relax To Win");}

    }
        @Override
        public void onClick(View view) {
            if(view == quit){
                DeleteMessage();
    }}


    private void countDownStart() {
        countDownTimer.start();
        timerHasStarted = true;
        mTextFeild.setText("GO!");


    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            mTextFeild.setText("GO!");
            if(mTextFeild.getText().equals("GO!")){
            message.setVisibility(View.VISIBLE);
            quit.setVisibility(View.VISIBLE);
            mTextFeild.setVisibility(View.GONE);}
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTextFeild.setText("  "+millisUntilFinished / 1000);

        }

    }
    private void DeleteMessage(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("QUIT");
        builder.setMessage("Do You Want to Quit Current Game?");
        builder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(play.this, MainActivity.class));

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }}


