package com.example.hanan.nim_gp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanan.nim_gp.AccountActivity.view_accountActivity;
import com.example.hanan.nim_gp.leaders.LeadersActivity;

public class MainActivity extends AppCompatActivity {
private TextView account;
private ImageView LB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LB =(ImageView) findViewById(R.id.leaderboard_iv);
        LB.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent LB2= new Intent(MainActivity.this, LeadersActivity.class);
                startActivity(LB2);
            }
        });
        account=(TextView)findViewById(R.id.account_tv);
        account.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent LB2= new Intent(MainActivity.this, view_accountActivity.class);
                startActivity(LB2);
            }
        });}



}
