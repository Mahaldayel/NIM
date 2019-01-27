package com.example.hanan.nim_gp.AccountActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.TextView;
import android.app.DatePickerDialog;
import java.util.Calendar;

import com.example.hanan.nim_gp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignup;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignin;
    private TextView textViewLocation;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private CountryPicker picker;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "AccountActivity.SignUpActivity";
    private Button setCountry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        //initializing views
        buttonSignup = (Button) findViewById(R.id.buttonSignup);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        setCountry = (Button) findViewById(R.id.setCountry);

        //location
        picker = CountryPicker.newInstance("Select Country");  // dialog title
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {

                picker.dismiss();
                setCountry.setText(name);

            }
        });



        //Date Picker START
        mDisplayDate = (TextView) findViewById(R.id.tvDate);

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
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };
        //Date Picker END

    }

    private void registerUser(){

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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
                            Toast.makeText(SignUpActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                        }else{
                            //display some message here
                            Toast.makeText(SignUpActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
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


        if(view == textViewSignin){
            //open sign in activity
        }

    }

    public void openPicker(View view){
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }

}
