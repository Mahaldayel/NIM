package com.example.hanan.nim_gp.AccountActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.hanan.nim_gp.R;
import com.google.firebase.auth.FirebaseAuth;

public class FirstPage extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSignIn;
    private Button buttonSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);

        buttonSignIn.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);

        //
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

    }


    @Override
    public void onClick(View view){
        if(view == buttonSignUp){
            startActivity(new Intent(this, SignUpActivity.class));

        }

        if(view == buttonSignIn){
            //open sign in activity
            startActivity(new Intent(this, SignInActivity.class));

        }
    }
}
