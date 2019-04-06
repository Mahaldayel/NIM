package com.example.hanan.nim_gp.leaders;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.view.View;
import com.google.firebase.auth.FirebaseUser;
import com.example.hanan.nim_gp.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import com.google.firebase.auth.FirebaseAuth;
import com.example.hanan.nim_gp.R;
import com.haipq.android.flagkit.FlagImageView;
import com.squareup.picasso.Picasso;

public class LeadersActivity extends AppCompatActivity {

    DatabaseReference refrence= FirebaseDatabase.getInstance().getReference().child("Players");;
    DatabaseReference refrence2= FirebaseDatabase.getInstance().getReference().child("PlayersGameInfo");;
   ArrayList<PlayersDB> players=new ArrayList<PlayersDB>();
    ArrayList<Score> Scores=new ArrayList<Score>();
   ArrayList<PlayersLB> Fplayers =new ArrayList<PlayersLB>();
   RecyclerView recyclerView ;

String CurrentPlayerUserName;
ImageView Player1Pic,Player2Pic,Player3Pic,CurrentPlayerPic,secondpic,thirdpic;
TextView Player1Name,Player2Name,Player3Name,CurrentPlayerName,Player1Score,Player2Score,Player3Score,CurrentPlayerScore,CurrentPlayerOrder;
FlagImageView Player1Country,Player2Country,Player3Country,CurrentPlayerCountry;
    FirebaseUser CurrentPlayer = FirebaseAuth.getInstance().getCurrentUser();
    String CurrentplayeId = CurrentPlayer.getUid();
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaders);
        ininatItems();
        retrivePlayerInfo();
        }



        private void ininatItems(){
            recyclerView = (RecyclerView) findViewById(R.id.reciclerView1);
            Player1Name= (TextView) findViewById(R.id.Player1Name);
            Player1Score= (TextView)findViewById(R.id.Player1Score);
            Player1Pic = (ImageView)findViewById(R.id.Player1Pic);
            Player1Country=(FlagImageView) findViewById(R.id.Player1Country);
            Player2Name= (TextView)findViewById(R.id.Player2Name);
            Player2Score= (TextView)findViewById(R.id.Player2Score);
            Player2Pic = (ImageView)findViewById(R.id.Player2Pic);
            Player2Country=(FlagImageView) findViewById(R.id.Player2Country);
            Player3Name= (TextView)findViewById(R.id.Player3Name);
            Player3Score= (TextView)findViewById(R.id.Player3Score);
            Player3Pic = (ImageView)findViewById(R.id.Player3Pic);
            Player3Country=(FlagImageView) findViewById(R.id.Player3Country);
            CurrentPlayerName= (TextView)findViewById(R.id.CurrentPlayerName);
            CurrentPlayerScore= (TextView)findViewById(R.id.CurrentPlayerScore);
            CurrentPlayerPic=(ImageView)findViewById(R.id.CurrentPlayerPic);
            CurrentPlayerCountry=(FlagImageView) findViewById(R.id.CurrentPlayerCountry);
            CurrentPlayerOrder=(TextView) findViewById(R.id.CurrentPlayerOrder);
            secondpic=(ImageView)findViewById(R.id.second);
            thirdpic=(ImageView)findViewById(R.id.third);

            Button Wbutton=(Button) findViewById(R.id.WbuttonO);
            Wbutton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent LB2= new Intent(LeadersActivity.this, LeadersActivity.class);
                    startActivity(LB2);
                }
            });

            Button Cbutton=(Button) findViewById(R.id.CbuttonOF);
            Cbutton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent LB2= new Intent(LeadersActivity.this,CountryLeadersActivity.class);
                    startActivity(LB2);
                }
            });

            Button Bbutton=(Button) findViewById(R.id.backButton);
            Bbutton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent LB2= new Intent(LeadersActivity.this, MainActivity.class);
                    startActivity(LB2);
                }
            });




            Typeface typeface=Typeface.createFromAsset(getAssets(), "fonts/Lalezar-Regular.ttf");
            Player1Name.setTypeface(typeface);
            Player1Score.setTypeface(typeface);
            Player2Name.setTypeface(typeface);
            Player2Score.setTypeface(typeface);
            Player3Name.setTypeface(typeface);
            Player3Score.setTypeface(typeface);
            CurrentPlayerName.setTypeface(typeface);
            CurrentPlayerScore.setTypeface(typeface);
            CurrentPlayerOrder.setTypeface(typeface);

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading ...");
            progressDialog.show();
        }


        private void retrivePlayerInfo(){
            refrence.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {


                        if(child.getKey().equals(CurrentplayeId))
                            CurrentPlayerUserName=child.child("username").getValue().toString();

String id=child.getKey();
                        String Uname = child.child("username").getValue().toString();
                        String pic= child.child("picURL").getValue().toString();
                        String Country=child.child("countyCode").getValue().toString();
                        PlayersDB p = new PlayersDB(10,Uname,pic,Country,id);
                        if(p!=null)
                            players.add(p);





                    }
                    System.out.println("*************************************"+players.size());

                    if(players.size()==1){


                        Player2Name.setVisibility(View.INVISIBLE);
                        Player2Score.setVisibility(View.INVISIBLE);
                        Player2Pic.setVisibility(View.INVISIBLE);
                        Player2Country.setVisibility(View.INVISIBLE);
                        secondpic.setVisibility(View.INVISIBLE);
                        Player3Name.setVisibility(View.INVISIBLE);
                        Player3Score.setVisibility(View.INVISIBLE);
                        Player3Pic.setVisibility(View.INVISIBLE);
                        Player3Country.setVisibility(View.INVISIBLE);
                        thirdpic.setVisibility(View.INVISIBLE);
                    }
                    if(players.size()==2){
                        Player3Name.setVisibility(View.INVISIBLE);
                        Player3Score.setVisibility(View.INVISIBLE);
                        Player3Pic.setVisibility(View.INVISIBLE);
                        Player3Country.setVisibility(View.INVISIBLE);
                        thirdpic.setVisibility(View.INVISIBLE);
                    }

                    retrivePlayerScore();

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getMessage());

                }
            });
        }

        private void retrivePlayerScore(){
            refrence2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String id=child.getKey();
                        int score =Integer.parseInt(child.child("score").getValue().toString()) ;
                        Score s=new Score(id,score);
                        Scores.add(s);

                    }
                    replasScore();
                    System.out.println("*************************************"+Scores.size());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        private void replasScore(){

            for (int i=0;i<players.size();i++) {
                for(int j=0;j< Scores.size();j++){
                    if(players.get(i).getID().equals(Scores.get(j).getID()))

                        players.get(i).setScore(Scores.get(j).getScore());

                }

            }
            arrange();
            ininatadapter();
        }

    private void ininatadapter() {
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
 ReciclerViewLBAdapterActivity reciclerViewLBAdapter = new ReciclerViewLBAdapterActivity (this, read());
       recyclerView.setAdapter(reciclerViewLBAdapter);
       recyclerView.setHasFixedSize(true);
       progressDialog.dismiss();
    }

    private void arrange() {

        for (int i = 0; i < players.size(); i++) {
            // if(players.get(i)==null)

            for (int j = i + 1; j < players.size(); j++) {

                if (players.get(i).getScore() < players.get(j).getScore()) {
                    int tempS= players.get(i).getScore() ;
                    String tempUn=players.get(i).getUname();
                    String tempPic=players.get(i).getPic();

                    String tempC=players.get(i).getCountry();

                    players.get(i).setScore(players.get(j).getScore());

                    players.get(i).setCountry(players.get(j).getCountry());
                    players.get(i).setUname(players.get(j).getUname());
                    players.get(i).setPic(players.get(j).getPic());

                    players.get(j).setScore(tempS);

                    players.get(j).setCountry(tempC);
                    players.get(j).setUname(tempUn);
                    players.get(j).setPic(tempPic);
                }

            }

        }


    }




    public  ArrayList<PlayersLB> read(){

        String Country;


        for(int z=0;z<players.size();z++) {


            if(players.get(z).getCountry().equals("SA"))
                Country="https://upload.wikimedia.org/wikipedia/commons/thumb/0/0d/Flag_of_Saudi_Arabia.svg/2000px-Flag_of_Saudi_Arabia.svg.png";
            else Country=players.get(z).getCountry();
            PlayersLB temp=new PlayersLB(players.get(z).getScore(),players.get(z).getUname(),players.get(z).getPic(),Country,z+1);

            if(players.get(z).getUname().equals(CurrentPlayerUserName)){


                CurrentPlayerName.setText(players.get(z).getUname());
                CurrentPlayerScore.setText(String.valueOf(players.get(z).getScore()));
                Picasso.get().load(String.valueOf(players.get(z).getPic())).into(CurrentPlayerPic);
                if(players.get(z).getCountry().equals("SA"))
                    CurrentPlayerCountry.setImageResource(R.drawable.saudi_flag);
                else CurrentPlayerCountry.setCountryCode(players.get(z).getCountry());
                String o=String.valueOf(z+1);
                CurrentPlayerOrder.setText("#"+o);

            }
                  if(z==0){

             Player1Name.setText(players.get(z).getUname());
             Player1Score.setText(String.valueOf(players.get(z).getScore()));
         Picasso.get().load(String.valueOf(players.get(z).getPic())).into(Player1Pic);
         if(players.get(z).getCountry().equals("SA"))
             Player1Country.setImageResource(R.drawable.saudi_flag);
             else Player1Country.setCountryCode(players.get(z).getCountry());


            }
                if(z==1) {

                    Player2Name.setText(players.get(z).getUname());
                    Player2Score.setText(String.valueOf(players.get(z).getScore()));
             Picasso.get().load(String.valueOf(players.get(z).getPic())).into(Player2Pic);
                    if(players.get(z).getCountry().equals("SA"))
                        Player2Country.setImageResource(R.drawable.saudi_flag);
                    else Player2Country.setCountryCode(players.get(z).getCountry());
                }
                if(z==2){

                    Player3Name.setText(players.get(z).getUname());
                    Player3Score.setText(String.valueOf(players.get(z).getScore()));
              Picasso.get().load(String.valueOf(players.get(z).getPic())).into(Player3Pic);
                    if(players.get(z).getCountry().equals("SA"))
                        Player3Country.setImageResource(R.drawable.saudi_flag);
                    else Player3Country.setCountryCode(players.get(z).getCountry());
                }


                if(z>2 && z<=19)
            Fplayers.add(temp);
        }
        return Fplayers;
    }

}
