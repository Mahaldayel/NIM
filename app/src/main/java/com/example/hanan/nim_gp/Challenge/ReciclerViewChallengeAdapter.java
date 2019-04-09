package com.example.hanan.nim_gp.Challenge;
import com.example.hanan.nim_gp.Game.ConnectionWithRobotCarActivity;
import com.example.hanan.nim_gp.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haipq.android.flagkit.FlagImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import android.graphics.Typeface;

public class ReciclerViewChallengeAdapter extends RecyclerView.Adapter<ReciclerViewChallengeAdapter.MyviewHolder> {
    DatabaseReference refrence= FirebaseDatabase.getInstance().getReference().child("Challenges");
    Button accept,Reject;
    Context c;
    Typeface typeface;
    ProgressDialog message;

    ArrayList<Challenge> Challenges;
    String id,GameControlMode,GameLevel,GameMode,Score;
    public static final String SELECTED_GAME_LEVEL_INTENT = "SELECTED_GAME_LEVEL_INTENT" ;
    public static final String CONTROL_MODE_GAME_INTENT ="controlMode";
    public static final String CONTROL_GAME_INTENT ="gameMode";
    public static final String Game_Score ="gameScore";

    public ReciclerViewChallengeAdapter(Context c,  ArrayList<Challenge> Challenges){
        this.c = c;
        this.Challenges= Challenges;
        typeface=Typeface.createFromAsset(c.getAssets(), "fonts/Lalezar-Regular.ttf");
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyviewHolder(LayoutInflater.from(c).inflate(R.layout.challengeitem,viewGroup,false));
    }

    public void onBindViewHolder(@NonNull MyviewHolder myviewHolder, int i) {
       id=Challenges.get(i).getChallengeID();
        myviewHolder.name.setText(Challenges.get(i).getSenderUname());
        myviewHolder.score.setText(String.valueOf(Challenges.get(i).getScore()));
        Picasso.get().load(Challenges.get(i).getSenderPic()).into(myviewHolder.PlayerImage);
        GameControlMode=Challenges.get(i).getGameControl();
        GameLevel=Challenges.get(i).getLevel();
        GameMode=Challenges.get(i).getGameMode();
        Score=Challenges.get(i).getScore();





        accept.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if(message==null){
                    message= new ProgressDialog(c);
                    message.setMessage("Please Wait..");
                    message.setIndeterminate(false);
                    message.setCancelable(true);
                    message.show();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setCancelable(true);
                builder.setTitle("Confirmation message");
                builder.setMessage(
                        "Challenge Game Control Mode is :"+GameControlMode+" , "+"Challenge Level is :"+GameLevel+" , "+"Challenge Game Mode is : "+GameMode+"Player"+" , "+"to win this challenge you have to get Score greater than :"+Score);
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                message.show();
                                Intent intent = new Intent(c, ConnectionWithRobotCarActivity.class);
                                intent.putExtra(CONTROL_MODE_GAME_INTENT,GameControlMode);
                                intent.putExtra(SELECTED_GAME_LEVEL_INTENT,GameLevel);
                                intent.putExtra(CONTROL_GAME_INTENT, GameMode);
                                intent.putExtra(Game_Score, Score);
                                refrence.child(id).removeValue();
                                c.startActivity(intent);
                                message.dismiss();
                                return ;


                            }
                        });
                builder.setNegativeButton("Cancle",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                message.show();//

                                message.dismiss();
                                return;

                            }

                        });
                AlertDialog dialog = builder.create();
                dialog.show();



            }
        });
        Reject.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if(message==null){
                    message= new ProgressDialog(c);
                    message.setMessage("Please Wait..");
                    message.setIndeterminate(false);
                    message.setCancelable(true);
                    message.show();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setCancelable(true);
                builder.setTitle("Confirmation message");
                builder.setMessage(
                        "Are you sure You want to Reject this challenge");

                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                message.show();
                                refrence.child(id).removeValue();
                                Intent s= new Intent(c, PlayerChallenges.class);
                                c.startActivity(s);
                                message.dismiss();
                                return ;


                            }
                        });
                builder.setNegativeButton("Cancle",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                message.show();
                                message.dismiss();
                                return;

                            }

                        });
                AlertDialog dialog = builder.create();
                dialog.show();





            }
        });

    }


    @Override
    public int getItemCount() {
        return Challenges.size();
    }

    class MyviewHolder extends RecyclerView.ViewHolder{

        TextView name,score;
        ImageView PlayerImage;



        public MyviewHolder(View itemView){
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.playerName_tv);
            name.setTypeface(typeface);
            score = (TextView) itemView.findViewById(R.id.Score);
            score.setTypeface(typeface);
            accept = (Button)itemView.findViewById(R.id.Accept);
            Reject=(Button)itemView.findViewById(R.id.Reject);
            PlayerImage=(ImageView) itemView.findViewById(R.id.playerImage_iv);



        }

    }





}

