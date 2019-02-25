package com.example.hanan.nim_gp.AccountActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccount extends AppCompatActivity implements View.OnClickListener {

    TextView DeleteButton;
    DatabaseReference database;
    ProgressDialog progressDialog;
    Boolean Deleted = false;
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        DeleteMessage();




        database = FirebaseDatabase.getInstance().getReference().child("Players");

        DeleteButton = findViewById(R.id.DeleteAccount);

        DeleteButton.setOnClickListener(this);

    }


    private void DeleteFromSystem() {



        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Players").child(currentUser.getUid());

        rootRef.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                DeleteSuccefully();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                DeleteUnsuccefully(e);
            }
        });

    }




    private void DeleteSuccefully(){

        Toast.makeText(DeleteAccount.this,"Your account has been deleted succefully!",Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        startActivity(new Intent(DeleteAccount.this, FirstPage.class));

    }

    private void DeleteUnsuccefully(Exception e){

        Toast.makeText(DeleteAccount.this,"Could not delete your account!",Toast.LENGTH_SHORT).show();

    }
    private void DeletePlayer(String id){
        DatabaseReference dplayer = FirebaseDatabase.getInstance().getReference("Players").child(id);
        dplayer.removeValue();

    }

    private void DeleteMessage(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirmation message");
        builder.setMessage(
                "Are you sure you want to delete your account?");
        builder.setPositiveButton("YES, i'm sure",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.show();//
                        currentUser.delete();
                        DeleteFromSystem();;

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent LB2= new Intent(DeleteAccount.this, view_accountActivity.class);
                startActivity(LB2);
                progressDialog.dismiss();
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onClick(View v) {
        if(v == DeleteButton){
            DeleteMessage();

        }}
}
