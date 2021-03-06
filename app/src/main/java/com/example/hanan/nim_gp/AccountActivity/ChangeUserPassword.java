package com.example.hanan.nim_gp.AccountActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeUserPassword extends AppCompatActivity implements View.OnClickListener{
    EditText editTextPassword,editTextRepeatPassword;
    private Button buttonupdate;
    private ImageView back;
    private static final int ValidPasswordSize = 8;
    private FirebaseUser user;


    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        initElement();



    }
    /**/
    private void initElement() {


        buttonupdate = findViewById(R.id.update_button);
        Typeface updateButton_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        buttonupdate.setTypeface(updateButton_font);


        dialog = new ProgressDialog(this);
        editTextPassword =  findViewById(R.id.editTextPassword);
        editTextRepeatPassword =  findViewById(R.id.editTextRepeatPassword);
        buttonupdate.setOnClickListener(this);
        back=findViewById(R.id.back);
        back.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    /**/
    @Override
    public void onClick(View v) {

        if(v == buttonupdate){
            change(v);

        }
        if(v==back){   startActivity(new Intent(ChangeUserPassword.this, ViewAccountActivity.class));}
    }


    /**/
    public void change(View v){
        String p1 = editTextPassword.getText().toString();
        String p2 =  editTextRepeatPassword.getText().toString();
        if(user!= null) {
            if (checkOfEmtiyInput(p1,p2))
            {
                dialog.setMessage("Changing password, Please wait!!");
                dialog.show();
                user.updatePassword(editTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(ChangeUserPassword.this, "Your password changed successfully! ", Toast.LENGTH_SHORT).show();
                            goToHome();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(ChangeUserPassword.this, "Your password could not change! ", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
        }
    }
    /**/
    private boolean checkOfEmtiyInput(String p1,String p2) {

        boolean isValid = true;

        /*
         * PASSWORD
         * */
        if(TextUtils.isEmpty(p1)){
            //Password is empty
            editTextPassword.setError("Please enter Password");
            //stopping the function execution further
            isValid = false;
        }else if(p1.length() < ValidPasswordSize){
            //Password is empty
            editTextPassword.setError("The Password is too short");
            //stopping the function execution further
            isValid = false;
        }

        if(TextUtils.isEmpty(p2)){
            //Password is empty
            editTextRepeatPassword.setError("Please enter Password");
            //stopping the function execution further
            isValid = false;
        }

        else if(p2.length() < ValidPasswordSize){
            //Password is empty
            editTextRepeatPassword.setError("The Password is too short");
            //stopping the function execution further
            isValid = false;
        }
        if(!p1.equals(p2)){

            editTextRepeatPassword.setError("The Password not match");
            isValid = false;


        }

        return isValid;
    }
    /**/
    private void goToHome(){

        Context context = ChangeUserPassword.this;
        Class homeClass = MainActivity.class;
        Intent intent = new Intent(context,homeClass);
        startActivity(intent);
    }




}

