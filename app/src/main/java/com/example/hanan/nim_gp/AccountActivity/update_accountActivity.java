package com.example.hanan.nim_gp.AccountActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.nim_gp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.haipq.android.flagkit.FlagImageView;
import com.squareup.picasso.Picasso;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

public class update_accountActivity extends AppCompatActivity implements View.OnClickListener {
    String email,name1,name,countryCode,Bdate,pic;
    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private Button mTextViewCountry;
    private ImageView mTextViewPic;
    private Button mUpdateButton;
    private ImageView back;
    private CountryPicker picker;
    private TextView mDisplayDate;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    boolean  available ;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    StorageReference storageReference;
    FirebaseStorage storage;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        setContentView(R.layout.activity_update_account);

        initElements();
        // Pic
        mTextViewPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture") ,PICK_IMAGE_REQUEST);
            }
        });

//
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
                System.out.println("problem to read value ");
                Toast.makeText(update_accountActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }

        });
        picker = CountryPicker.newInstance("Select Country");  // dialog title
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {

                picker.dismiss();
                countryCode = code;
                mTextViewCountry.setText(name);
            }
    });

             mDisplayDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            update_accountActivity.this,
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
                    //Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                    String date = month + "/" + day + "/" + year;
                    mDisplayDate.setText(date);
                    Bdate=date;

                }
            };
        }


    private void initElements(){
        mStorage = FirebaseStorage.getInstance().getReference();

        mTextViewEmail=findViewById(R.id.email);
        Typeface playerEmail_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        mTextViewEmail.setTypeface(playerEmail_font);

        mTextViewCountry=findViewById(R.id.countryCode);
        Typeface country_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        mTextViewCountry.setTypeface(country_font);

        mTextViewName=findViewById(R.id.userName);
        Typeface playerName_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        mTextViewName.setTypeface(playerName_font);

        mUpdateButton=findViewById(R.id.update_button);
        Typeface updateButton_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        mUpdateButton.setTypeface(updateButton_font);

        mDisplayDate = (TextView) findViewById(R.id.date_tv);
        Typeface playerBdate_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        mDisplayDate.setTypeface(playerBdate_font);

        mTextViewPic=findViewById(R.id.playerImage_iv);
        back=findViewById(R.id.back);

        mUpdateButton.setOnClickListener(this);
        back.setOnClickListener(this);
        mTextViewPic.setOnClickListener(this);
        mTextViewCountry.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(view == mUpdateButton) {
            uploadImage();
            email = mTextViewEmail.getText().toString();
            name = mTextViewName.getText().toString();
            Bdate =mDisplayDate.getText().toString();
            if(email.equals("")||countryCode.equals("")||name.equals("")|| Bdate.equals("")){
                Toast.makeText(update_accountActivity.this, "empty field not accepted ", Toast.LENGTH_SHORT).show();
            }
            if(!name1.equals(name)){
                checkUsernameAvailability();
            if(!available){

                Toast.makeText(update_accountActivity.this,"This username is already taken, Please enter another one",Toast.LENGTH_LONG).show();
                return;}
            }
            else{

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String playeId = user.getUid();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Players").child(playeId);

            rootRef.child("email").setValue(email);
            rootRef.child("countyCode").setValue(countryCode);
            rootRef.child("username").setValue(name);
          //  rootRef.child("picURL").setValue(pic);
            rootRef.child("birthDate").setValue(Bdate);
            updateSuccessfully();

        }}
    if(view==back){
        startActivity(new Intent(update_accountActivity.this, view_accountActivity.class));}
    if(view==mTextViewPic){
        //selectImage();
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
        Bdate=(String)dataSnapshot.child("birthDate").getValue();
        email = (String) dataSnapshot.child("email").getValue();
        name1 = (String) dataSnapshot.child("username").getValue();
        pic=(String)dataSnapshot.child("picURL").getValue();
        Picasso.get().load(pic).into(mTextViewPic);
            mTextViewEmail.setText(email);
            mTextViewCountry.setText(String.valueOf(countryCode));
            mTextViewName.setText(name1);
            mDisplayDate.setText(Bdate);
        }
    private void uploadImage() {

        if(filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Please Wait...");
            progressDialog.show();

            final StorageReference ref=storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //String image = ref.getDownloadUrl().toString();
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String photoLink = uri.toString();
                                    //set the image for the player
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String playeId = user.getUid();
                                    DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("Players").child(playeId);
                                    updateData.child("picURL").setValue(photoLink);
                                 //   Toast.makeText(update_accountActivity.this, "UploadedImage", Toast.LENGTH_SHORT).show();
                                 //   startActivity(new Intent(getApplicationContext(), view_accountActivity.class));
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(update_accountActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
    @Override


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data != null && data.getData() != null){

            filePath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                mTextViewPic.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    public void checkUsernameAvailability(){



        mDatabase = FirebaseDatabase.getInstance().getReference().child("Players");

        //check if the username exist in Database or not
        mDatabase.orderByChild("username").equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            available =false;

                            //the username exists
                        } else {
                            available =true;

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });


    }


}


