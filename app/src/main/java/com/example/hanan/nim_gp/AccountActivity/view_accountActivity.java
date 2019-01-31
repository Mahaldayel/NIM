package com.example.hanan.nim_gp.AccountActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.haipq.android.flagkit.FlagImageView;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class view_accountActivity extends AppCompatActivity implements View.OnClickListener {
    String email,Uname,countryCode,picUri,Bdate;
    Long score;
//    private FirebaseAuth mAuth;
//    private FirebaseDatabase database;
//    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private TextView mTextViewName;
    private TextView date;
    private TextView mTextViewEmail;
    private ImageView mImageViewSAFLag;
    FlagImageView mFlagImageViewCountry;
    private TextView mTextViewScore;
    private ImageView mImageViewPic;

    private Button mUpdateButton;
    private ImageView back;
    private TextView password;

    private ProgressDialog progressDialog;
    private Map<String, Object> mPlayer;
    private String mId;
    //    private FirebaseFirestore db;
//    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        initElements();


//        ref =  database.getReference().child("players").child(temp.getUid());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Players").child("JF8mmf9m00VfHF3SbLKJ5xi1e3B3");
//        DatabaseReference itemsRef = rootRef.child("players");

        rootRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    getData(dataSnapshot);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("problem ");
                Toast.makeText(view_accountActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }

        });}


        private void initElements(){


            mFlagImageViewCountry = (FlagImageView) findViewById(R.id.country_flagView);
            mImageViewSAFLag = findViewById(R.id.SA_FLAG);
            mTextViewName=findViewById(R.id.playerName_tv);
            mTextViewEmail=findViewById(R.id.email_tv);
            mTextViewScore=findViewById(R.id.score_tv) ;
            mImageViewPic=(ImageView)findViewById(R.id.playerImage_iv);
            mUpdateButton=findViewById(R.id.update_button);
            password=findViewById(R.id.passwordLabel);
            back=findViewById(R.id.back);
            password.setOnClickListener(this);
            mUpdateButton.setOnClickListener(this);
            back.setOnClickListener(this);
            date=findViewById(R.id.date_tv);



        }
    @Override
    public void onClick(View view) {
        if(view == mUpdateButton){
            startActivity(new Intent(view_accountActivity.this,update_accountActivity.class));
            }
            if(view==password){  startActivity(new Intent(view_accountActivity.this,changeUserPassword.class));
            }
        if(view==back){   startActivity(new Intent(view_accountActivity.this, MainActivity.class));}}

    private void getData(DataSnapshot dataSnapshot) {

                        countryCode = (String) dataSnapshot.child("countyCode").getValue();
                        Bdate=(String) dataSnapshot.child("birthDate").getValue();
                        email = (String) dataSnapshot.child("email").getValue();
                         score = (Long) dataSnapshot.child("score").getValue();
                        Uname = (String) dataSnapshot.child("username").getValue();
                        picUri=(String)dataSnapshot.child("picURL").getValue();
////Just for now

        if(email.equals("arwaH@hotmail.com")) {

            displayFlag();
            mTextViewEmail.setText(email);
            mTextViewScore.setText(String.valueOf(score));
            mTextViewName.setText(Uname);
            date.setText(Bdate);
           Picasso.get().load(picUri).into(mImageViewPic);
           //Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTr2a7_Z5dQPXg-YUxdCwwVCx-dDJQk_jvBQhPu9WirCaNVWPOu").into(mImageViewPic);

        }
    }

    private void displayFlag() {

        if(!countryCode.equals( "SA")){
            mFlagImageViewCountry.setCountryCode(countryCode);
            mImageViewSAFLag.setVisibility(View.GONE);
            mFlagImageViewCountry.setVisibility(View.VISIBLE);
        }
        else {
            mImageViewSAFLag.setVisibility(View.VISIBLE);
            mFlagImageViewCountry.setVisibility(View.GONE);
        }
    }
}





