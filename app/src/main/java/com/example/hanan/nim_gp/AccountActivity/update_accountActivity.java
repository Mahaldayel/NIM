package com.example.hanan.nim_gp.AccountActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import com.google.firebase.database.ValueEventListener;
import com.haipq.android.flagkit.FlagImageView;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.util.HashMap;
import java.util.Objects;

public class update_accountActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECT_PICTURE = 0;
    String email,name,countryCode;
    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private TextView mTextViewCountry;
    private ImageView mTextViewPic;
    private Button mUpdateButton;
    private ImageView back;
    private CountryPicker picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        initElements();


//        ref =  database.getReference().child("players").child(temp.getUid());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Players").child("JF8mmf9m00VfHF3SbLKJ5xi1e3B3");

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
                Toast.makeText(update_accountActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }

        });
        picker = CountryPicker.newInstance("Select Country");  // dialog title
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {

                picker.dismiss();
                countryCode = code;


               FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference().child("Players").child("JF8mmf9m00VfHF3SbLKJ5xi1e3B3");

                myRef.child("countyCode").setValue(countryCode);

                mTextViewCountry.setText(name);



            }
        });}


    private void initElements(){


        mTextViewCountry=findViewById(R.id.countryCode);
        mTextViewName=findViewById(R.id.userName);
        mTextViewEmail=findViewById(R.id.email);
      mTextViewPic=findViewById(R.id.playerImage_iv);
        mUpdateButton=findViewById(R.id.update_button);
        back=findViewById(R.id.back);
        mUpdateButton.setOnClickListener(this);
        back.setOnClickListener(this);
        mTextViewPic.setOnClickListener(this);
        mTextViewCountry.setOnClickListener(this);


    }
    @Override
    public void onClick(View view) {
        if(view == mUpdateButton) {
            email = mTextViewEmail.getText().toString();
            countryCode = mTextViewCountry.getText().toString();
            name = mTextViewName.getText().toString();
            if(email.equals("")||countryCode.equals("")||name.equals("")){
                Toast.makeText(update_accountActivity.this, "empty field not accepted ", Toast.LENGTH_SHORT).show();
            }
            else{

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference().child("Players").child("JF8mmf9m00VfHF3SbLKJ5xi1e3B3");

            myRef.child("email").setValue(email);
           // myRef.child("countyCode").setValue(countryCode);
            myRef.child("username").setValue(name);
updateSuccessfully();

        }}
    if(view==back){
        startActivity(new Intent(update_accountActivity.this, view_accountActivity.class));}
    if(view==mTextViewPic){
        selectImage();
    }
    if (view==mTextViewCountry){
        openPicker();
    }
    }
    public void openPicker(){
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");

    }
    private void updateSuccessfully() {
        Toast.makeText(update_accountActivity.this, "The Information was Updated", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(update_accountActivity.this, view_accountActivity.class));}

    private void getData(DataSnapshot dataSnapshot) {

        countryCode = (String) dataSnapshot.child("countyCode").getValue();

        email = (String) dataSnapshot.child("email").getValue();
        name = (String) dataSnapshot.child("username").getValue();
////Just for now


            mTextViewEmail.setText(email);
            mTextViewCountry.setText(String.valueOf(countryCode));
            mTextViewName.setText(name);
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = getPath(data.getData());
            if(bitmap==null){
                Toast.makeText(update_accountActivity.this, "empty field not accepted ", Toast.LENGTH_SHORT).show();
            }
            else
            mTextViewPic.setImageBitmap(bitmap);
        }


    }

    private Bitmap getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        // cursor.close();
        // Convert file path into bitmap image using below line.
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        return bitmap;
    }

    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }



}


