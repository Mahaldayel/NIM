package com.example.hanan.nim_gp.AccountActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haipq.android.flagkit.FlagImageView;
import com.squareup.picasso.Picasso;

public class view_accountActivity extends AppCompatActivity implements View.OnClickListener {
    String email,Uname,countryCode,picUri,Bdate;
    Long score;
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
    private TextView deleteAccount;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        initElements();



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String playeId = user.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Players").child(playeId);

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
                System.out.println("problem to read value");
                Toast.makeText(view_accountActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }

        });

    }


        private void initElements(){
         //TO Tondu font
            TextView scorelable=(TextView) findViewById(R.id.scoreLabel_tv);
            Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Tondu_Beta.ttf");
            scorelable.setTypeface(custom_font);

            TextView countrylabel=(TextView) findViewById(R.id.levelLable_tv);
            Typeface country_font = Typeface.createFromAsset(getAssets(),  "fonts/Tondu_Beta.ttf");
            countrylabel.setTypeface(country_font);


            //TO Lalezar-Regular
            mTextViewName=findViewById(R.id.playerName_tv);
            Typeface playerName_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
            mTextViewName.setTypeface(playerName_font);

            mTextViewEmail=findViewById(R.id.email_tv);
            Typeface playerEmail_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
            mTextViewEmail.setTypeface(playerEmail_font);

            mTextViewScore=findViewById(R.id.score_tv) ;
            Typeface playerScore_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
            mTextViewScore.setTypeface(playerScore_font);

            password=findViewById(R.id.passwordLabel);
            Typeface playerPass_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
            password.setTypeface(playerPass_font);

            deleteAccount=findViewById(R.id.DeleteAccount);
            Typeface deletAccount_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
            deleteAccount.setTypeface(deletAccount_font);

            date=findViewById(R.id.date_tv);
            Typeface playerBdate_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
            date.setTypeface(playerBdate_font);
///////////////
            mUpdateButton=findViewById(R.id.update_button);
            mFlagImageViewCountry = (FlagImageView) findViewById(R.id.country_flagView);
            mImageViewSAFLag = findViewById(R.id.SA_FLAG);
            mImageViewPic=(ImageView)findViewById(R.id.playerImage_iv);
            back=findViewById(R.id.back);
            password.setOnClickListener(this);
            deleteAccount.setOnClickListener(this);
            mUpdateButton.setOnClickListener(this);
            back.setOnClickListener(this);



        }
    @Override
    public void onClick(View view) {
        if(view == mUpdateButton){
            startActivity(new Intent(view_accountActivity.this,update_accountActivity.class));
            }
            if(view==password){  startActivity(new Intent(view_accountActivity.this,changeUserPassword.class));
            }
            if(view==deleteAccount){
                startActivity(new Intent(view_accountActivity.this,DeleteAccount.class));
                 }
        if(view==back){   startActivity(new Intent(view_accountActivity.this, MainActivity.class));}}

    private void getData(DataSnapshot dataSnapshot) {

                        countryCode = (String) dataSnapshot.child("countyCode").getValue();
                        Bdate=(String) dataSnapshot.child("birthDate").getValue();
                        email = (String) dataSnapshot.child("email").getValue();
                         score = (Long) dataSnapshot.child("score").getValue();
                        Uname = (String) dataSnapshot.child("username").getValue();
                        picUri=(String)dataSnapshot.child("picURL").getValue();


            displayFlag();
            mTextViewEmail.setText(email);
            mTextViewScore.setText(String.valueOf(score));
            mTextViewName.setText(Uname);
            date.setText(Bdate);
           Picasso.get().load(picUri).into(mImageViewPic);
        progressDialog.dismiss();

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





