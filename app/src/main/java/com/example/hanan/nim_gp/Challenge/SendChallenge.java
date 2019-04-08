package com.example.hanan.nim_gp.Challenge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SendChallenge extends AppCompatActivity implements View.OnClickListener {
    ArrayList<String> UserNames =new ArrayList<String>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Players");;
    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
    FirebaseUser CurrentPlayer = FirebaseAuth.getInstance().getCurrentUser();
    String CurrentplayeId = CurrentPlayer.getUid();
    public String CurrentPlayerUserName,ReciverUname,SenderPic,GameControlMode,GameLevel,GameMode;
    Button Send,cancle;
    EditText TextField;
    boolean avalibalty,challengeyourself,shaz=false;
    ProgressDialog message;
    GameInfo GameSetUp;
    public static final String SELECTED_GAME_LEVEL_INTENT = "SELECTED_GAME_LEVEL_INTENT" ;
    public static final String CONTROL_MODE_GAME_INTENT ="controlMode";
    public static final String CONTROL_GAME_INTENT ="gameMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_challenge);
        ininateElments();
        RetrivePlayersUserName();

    }

    private void ininateElments(){
        Send=findViewById(R.id.send);
        cancle=findViewById(R.id.cancel);
        Send.setOnClickListener(this);
        cancle.setOnClickListener(this);
        TextField=findViewById(R.id.Uname);
        Intent intent = getIntent();
        GameControlMode = intent.getStringExtra(CONTROL_MODE_GAME_INTENT);
        GameLevel= intent.getStringExtra(SELECTED_GAME_LEVEL_INTENT);
        GameMode=intent.getStringExtra(CONTROL_GAME_INTENT);
        System.out.println("*************1***************");
        System.out.println(GameControlMode);
        System.out.println(GameLevel);
        System.out.println(GameMode);

    }






    private void RetrivePlayersUserName(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.getKey().equals(CurrentplayeId)){
                        CurrentPlayerUserName=child.child("username").getValue().toString().toLowerCase();
                        SenderPic=child.child("picURL").getValue().toString();
                    }

                    String Uname = child.child("username").getValue().toString().toLowerCase();
                    UserNames.add(Uname);

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void SendChallenge(){


        ReciverUname=TextField.getText().toString().toLowerCase();
        avalibalty=false;
        challengeyourself=false;
        for(int i=0;i<UserNames.size();i++){
            if(ReciverUname.equals(UserNames.get(i))){
                avalibalty=true;

            }}
        if(!avalibalty){

            CreateMessage(1);
        }
        if(CurrentPlayerUserName.equals(ReciverUname)){

            challengeyourself=true;
            CreateMessage(2);
        }


        if(avalibalty&&!challengeyourself){


            GameSetUp=new GameInfo(CurrentPlayerUserName,GameControlMode,SenderPic,GameLevel,GameMode,20,ReciverUname);


            reference2.child("Challenges").push().setValue(GameSetUp);

            CreateMessage(3);

        }




    }
    private void CreateMessage(int z){





        if(message==null){
            message= new ProgressDialog(SendChallenge.this);
            message.setMessage("Please Wait..");
            message.setIndeterminate(false);
            message.setCancelable(true);
            message.show();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirmation message");
        if(z==1)
            builder.setMessage(
                    "there is no player with this username ");

        if(z==2)
            builder.setMessage(
                    "you can not challenge yourself");


        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        message.show();//
                        Intent intent = new Intent(SendChallenge.this, SendChallenge.class);
                        intent.putExtra(CONTROL_MODE_GAME_INTENT,GameControlMode);
                        intent.putExtra(SELECTED_GAME_LEVEL_INTENT,GameLevel);
                        intent.putExtra(CONTROL_GAME_INTENT, GameMode);
                        startActivity(intent);
                        message.dismiss();
                        return;

                    }
                });

        if(z==3){
            builder.setMessage(
                    " The Challenge was successfully sent to " +ReciverUname);
            builder.setPositiveButton("send challenge to another player",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            message.show();//
                            Intent s= new Intent(SendChallenge.this, SendChallenge.class);
                            startActivity(s);
                            message.dismiss();
                            return;

                        }
                    });
            builder.setNegativeButton("Go to home",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            message.show();//
                            Intent intent= new Intent(SendChallenge.this, MainActivity.class);
                            intent.putExtra(CONTROL_MODE_GAME_INTENT,GameControlMode);
                            intent.putExtra(SELECTED_GAME_LEVEL_INTENT,GameLevel);
                            intent.putExtra(CONTROL_GAME_INTENT, GameMode);
                            startActivity(intent);
                            message.dismiss();
                            return;

                        }

                    });

        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onClick(View view) {
        if(view==Send ){
//
            SendChallenge();
        }
        if(view==cancle){
            Intent home= new Intent(SendChallenge.this, MainActivity.class);
            startActivity(home);


        }



    }
}
