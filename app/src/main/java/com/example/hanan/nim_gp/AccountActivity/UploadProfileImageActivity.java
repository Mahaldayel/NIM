package com.example.hanan.nim_gp.AccountActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class UploadProfileImageActivity extends AppCompatActivity {


    private Button buttonUpload,buttonSkip;
    private ImageView imageViewPic;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference mDatabase;
    FirebaseAuth firebaseAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_image);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonSkip = (Button) findViewById(R.id.buttonSkip);
        imageViewPic = (ImageView) findViewById(R.id.imageViewPic);


        imageViewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();

            }
        });

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDefaultImage();
            }
        });

    }

    private void uploadDefaultImage() {
        //set the image for the player
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String playeId = user.getUid();
        DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("Players").child(playeId);
        updateData.child("picURL").setValue("https://firebasestorage.googleapis.com/v0/b/nim1-2ba46.appspot.com/o/images%2Fdefault-level_number.png?alt=media&token=20c99135-29ce-4f60-8972-aaf4aff2b21a");
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

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
                                    Toast.makeText(UploadProfileImageActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadProfileImageActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void chooseImage(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture") ,PICK_IMAGE_REQUEST);

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data != null && data.getData() != null){

            filePath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imageViewPic.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


}
