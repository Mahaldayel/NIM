package com.example.hanan.nim_gp.AccountActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hanan.nim_gp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

public class UpdateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    String email, oldName,name,countryCode, bDate,pic;
    private EditText mEditTextName ;
    private EditText mEditTextVEmail;
    private Button mTextViewCountry;
    private ImageView mTextViewPic;
    private Button mUpdateButton;
    private ImageView back;
    private CountryPicker picker;
    private EditText mDisplayDate;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    boolean  available ;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    StorageReference storageReference;
    FirebaseStorage storage;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private boolean availableIsUpdated;
    private Context mContext;

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
                Toast.makeText(UpdateAccountActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
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
                            UpdateAccountActivity.this,
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
                    bDate =date;

                }
            };
        }


    private void initElements(){
        mStorage = FirebaseStorage.getInstance().getReference();

        mEditTextVEmail = findViewById(R.id.email);
        Typeface playerEmail_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        mEditTextVEmail.setTypeface(playerEmail_font);

        mTextViewCountry = findViewById(R.id.countryCode);
        Typeface country_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        mTextViewCountry.setTypeface(country_font);

        mEditTextName = findViewById(R.id.userName);
        Typeface playerName_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        mEditTextName.setTypeface(playerName_font);

        mUpdateButton = findViewById(R.id.update_button);
        Typeface updateButton_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        mUpdateButton.setTypeface(updateButton_font);

        mDisplayDate =  findViewById(R.id.date_tv);
        Typeface playerBdate_font = Typeface.createFromAsset(getAssets(),  "fonts/Lalezar-Regular.ttf");
        mDisplayDate.setTypeface(playerBdate_font);

        mTextViewPic = findViewById(R.id.playerImage_iv);
        back = findViewById(R.id.back);

        mUpdateButton.setOnClickListener(this);
        back.setOnClickListener(this);
        mTextViewPic.setOnClickListener(this);
        mTextViewCountry.setOnClickListener(this);

        availableIsUpdated = false;
        mContext = this;


    }

    @Override
    public void onClick(View view) {
        if(view == mUpdateButton) {
            uploadImage();
            beforeUpdate();

           }
    if(view==back){
        startActivity(new Intent(UpdateAccountActivity.this, ViewAccountActivity.class));}
    if(view==mTextViewPic){
        //selectImage();
    }
    if (view==mTextViewCountry){
        openPicker();
    }
    }

    private void beforeUpdate(){

        getPlayerIntonationFields();
        if(!oldName.equals(name))
            checkUsernameAvailability(name,this);
        else
            update(false, name, email, bDate,false);
    }

    public boolean update(boolean checkUsernameAvailability, String nameStr, String emailStr, String bDateStr,boolean forTest) {

        if(!emailNotEmpty(emailStr) || !nameNotEmpty(nameStr)|| !birthDateNotEmpty(bDateStr) ){
            displayToast("empty field not accepted ",forTest);
            return false;
        }
        if(checkUsernameAvailability)
            if(getAvailableIsUpdated() && !getAvailable()){
                displayToast("This username is already taken, Please enter another one",forTest);
                return false;
        }

        updateFirebase(forTest);
        return true;

    }

    private void displayToast(String message,boolean forTest){

        if(forTest)
            return;

        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

    }

    public boolean emailNotEmpty(String emailStr) {

        if(emailStr.equals(""))
            return false;
        return true;
    }


    public boolean nameNotEmpty(String nameStr) {

        if(nameStr.equals(""))
            return false;
        return true;
    }

    public boolean birthDateNotEmpty(String bDateStr) {

        if(bDateStr.equals(""))
            return false;
        return true;
    }

    private void updateFirebase(boolean forTest) {

        if(forTest)
            return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String playeId = user.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Players").child(playeId);

        rootRef.child("email").setValue(email);
        rootRef.child("countyCode").setValue(countryCode);
        rootRef.child("username").setValue(name);
        rootRef.child("birthDate").setValue(bDate);
        updateSuccessfully();

    }



    public String getInformationFromField(EditText editText){

        return editText.getText().toString();
    }

    private void getPlayerIntonationFields() {

        email = getInformationFromField(mEditTextVEmail);
        name = getInformationFromField(mEditTextName);
        bDate = getInformationFromField(mDisplayDate);


    }

    public void openPicker(){
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");

    }
    private void updateSuccessfully() {
        Toast.makeText(UpdateAccountActivity.this, "The Information was Updated", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(UpdateAccountActivity.this, ViewAccountActivity.class));}

    private void getData(DataSnapshot dataSnapshot) {

        countryCode = (String) dataSnapshot.child("countyCode").getValue();
        bDate =(String)dataSnapshot.child("birthDate").getValue();
        email = (String) dataSnapshot.child("email").getValue();
        oldName = (String) dataSnapshot.child("username").getValue();
        pic = (String)dataSnapshot.child("picURL").getValue();
        Picasso.get().load(pic).into(mTextViewPic);
            mEditTextVEmail.setText(email);
            mTextViewCountry.setText(String.valueOf(countryCode));
            mEditTextName.setText(oldName);
            mDisplayDate.setText(bDate);
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
                            Toast.makeText(UpdateAccountActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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


    public void checkUsernameAvailability(String nameStr, Context context){

        FirebaseApp.initializeApp(context);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Players");

        //check if the username exist in Database or not
        database.orderByChild("username").equalTo(nameStr)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            setAvailable(false);

                            //the username exists
                        } else {
                            setAvailable(true);


                        }

                        setAvailableIsUpdatede(true);
                        update(true,name,email,bDate,false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });


    }

    public boolean getAvailable(){

        return available;
    }


    public void setAvailable(boolean available){

        this.available = available;
    }

    public void setAvailableIsUpdatede(boolean availableIsUpdated){

        this.availableIsUpdated = availableIsUpdated;
    }

    public boolean getAvailableIsUpdated(){

        return availableIsUpdated;
    }

    public  Activity getContext(){

        return UpdateAccountActivity.this;
    }


}


