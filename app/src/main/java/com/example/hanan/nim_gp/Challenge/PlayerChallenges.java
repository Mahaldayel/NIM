package com.example.hanan.nim_gp.Challenge;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

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

public class PlayerChallenges extends AppCompatActivity {
    DatabaseReference refrence= FirebaseDatabase.getInstance().getReference();
    FirebaseUser CurrentPlayer = FirebaseAuth.getInstance().getCurrentUser();
    String CurrentplayeId = CurrentPlayer.getUid();
    String CurrentPlayerUname;
    ArrayList<Challenge> Challenges =new ArrayList<Challenge>();
    Button accept,Reject;
    RecyclerView recyclerView ;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_challenges);

        recyclerView = (RecyclerView) findViewById(R.id.rec);

        Button Backbutton=(Button) findViewById(R.id.backButton);
        Backbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent LB2= new Intent(PlayerChallenges.this, MainActivity.class);
                startActivity(LB2);
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        GetCurrentPlayerUname();
    }
    private void ininatadapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ReciclerViewChallengeAdapter reciclerViewLBAdapter = new ReciclerViewChallengeAdapter (this, read());
        recyclerView.setAdapter(reciclerViewLBAdapter);
        recyclerView.setHasFixedSize(true);
        progressDialog.dismiss();
    }

    private void GetCurrentPlayerUname(){
        refrence= FirebaseDatabase.getInstance().getReference().child("Players");;
        refrence.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {


                    if(child.getKey().equals(CurrentplayeId))
                        CurrentPlayerUname=child.child("username").getValue().toString().toLowerCase();


                }
                System.out.println("*************************************"+ CurrentPlayerUname);


                GetPlayerChallenges();

            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());

            }
        });

    }


    private void  GetPlayerChallenges(){
        refrence= FirebaseDatabase.getInstance().getReference().child("Challenges");;
        refrence.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {


                    if(child.child("reciver").getValue().toString().equals(CurrentPlayerUname)){
                        String id=child.getKey().toString();
                        String level=child.child("gameLevel").getValue().toString();
                        String GameMode=child.child("gameMod").getValue().toString();
                        String GameControlMode=child.child("gameControlMode").getValue().toString();
                      String Score=child.child("score").getValue().toString();
                        String SenderPic=child.child("senderPic").getValue().toString();
                        String SenderName=child.child("senderUname").getValue().toString();
                        Challenge challenge=new Challenge(SenderName,SenderPic,id,level,Score,GameControlMode,GameMode);
                        Challenges.add(challenge);
                        System.out.println("**************************"+Score);


                    }



                }


                ininatadapter();

            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());

            }
        });


    }
    public  ArrayList<Challenge> read(){
        return Challenges;

    }
}
