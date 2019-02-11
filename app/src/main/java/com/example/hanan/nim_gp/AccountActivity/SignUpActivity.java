package com.example.hanan.nim_gp.AccountActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.nim_gp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignup;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextUserName;
    private TextView textViewLocation;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private CountryPicker picker;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "AccountActivity.SignUpActivity";
    private static final int GALLERY_INTENT = 2;
    private ImageView backbtn;
    private Button setCountry;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private PlayerInformation player;
    boolean available=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        player=new PlayerInformation();

        //if the objects getcurrentuser method is not null
        //means user is already logged in
        /*if(firebaseAuth.getCurrentUser() != null){
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }*/


        //initializing views
        buttonSignup = (Button) findViewById(R.id.buttonSignup);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        backbtn = (ImageView) findViewById(R.id.backbtn);
        buttonSignup.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        setCountry = (Button) findViewById(R.id.setCountry);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDisplayDate = (TextView) findViewById(R.id.tvDate);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);



        //location
        picker = CountryPicker.newInstance("Select Country");  // dialog title
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {

                picker.dismiss();
                setCountry.setText(name);
                player.setCountyCode(code);


            }
        });


        //Date Picker START
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SignUpActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
                player.setBirthDate(date);
            }
        };
        //Date Picker END

    }


    private void registerUser(){

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String username= editTextUserName.getText().toString().trim();


        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(SignUpActivity.this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(SignUpActivity.this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(username)){
            //username is empty
            Toast.makeText(SignUpActivity.this,"Please enter username",Toast.LENGTH_LONG).show();
            return;
        }

        if(!checkUsernameAvailability()){
            Toast.makeText(SignUpActivity.this,"Please enter another username",Toast.LENGTH_LONG).show();
            return;
        }

        if(player.getBirthDate().equalsIgnoreCase("")){
            //birthDate is empty
            Toast.makeText(SignUpActivity.this,"Please enter your birth date",Toast.LENGTH_LONG).show();
            return;
        }

        if(player.getCountyCode().equalsIgnoreCase("")){
            //country  is empty
            Toast.makeText(SignUpActivity.this,"Select your country",Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();


        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            player.setEmail(editTextEmail.getText().toString().trim());
                            addPlayer();
                        }else{
                            //display some message here

                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            displayErrorMsg(errorCode);

                        }

                        progressDialog.dismiss();
                    }
                });

    }


    @Override
    public void onClick(View view){
        if(view == buttonSignup){
            registerUser();
        }

        if(view == backbtn){
            //open sign in activity
            startActivity(new Intent(this, FirstPage.class));

        }
    }


    public void openPicker(View view){
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == GALLERY_INTENT && resultCode==RESULT_OK) {
            Uri uri=data.getData();
            player.setPicURL(String.valueOf(data.getData()));
            StorageReference filePath = mStorage.child("Photos").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SignUpActivity.this,"DONE",Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }


    public boolean checkUsernameAvailability(){
        String username=editTextUserName.getText().toString();
        player.setUsername(username);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Players");

        //check if the username exist in Database or not
        mDatabase.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //the username exists
                        } else {
                            available = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
        return available;

    }


    public void addPlayer(){
            String playerId=firebaseAuth.getCurrentUser().getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Players").child(playerId).setValue(player);
            Toast.makeText(SignUpActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), UploadProfileImageActivity.class));
            finish();

    }


    public void displayErrorMsg(String errorCode){
        switch (errorCode) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(SignUpActivity.this, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(SignUpActivity.this, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(SignUpActivity.this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(SignUpActivity.this, "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                editTextEmail.setError("The email address is badly formatted.");
                editTextEmail.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(SignUpActivity.this, "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                editTextPassword.setError("password is incorrect ");
                editTextPassword.requestFocus();
                editTextPassword.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(SignUpActivity.this, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(SignUpActivity.this, "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(SignUpActivity.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(SignUpActivity.this, "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();
                editTextEmail.setError("The email address is already in use by another account.");
                editTextEmail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(SignUpActivity.this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(SignUpActivity.this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(SignUpActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(SignUpActivity.this, "There is no user record corresponding to this identifier. The user may have been deleted.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(SignUpActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(SignUpActivity.this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(SignUpActivity.this, "The given password is invalid.", Toast.LENGTH_LONG).show();
                editTextPassword.setError("The password is invalid it must 6 characters at least");
                editTextPassword.requestFocus();
                break;

        }
    }

}
