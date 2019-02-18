package com.example.hanan.nim_gp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.hanan.nim_gp.AccountActivity.view_accountActivity;

public class MainActivity extends AppCompatActivity {
private TextView account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        account=(TextView)findViewById(R.id.account_tv);
        account.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent LB2= new Intent(MainActivity.this, view_accountActivity.class);
                startActivity(LB2);
            }
        });    }



}
