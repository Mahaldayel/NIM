package com.example.hanan.nim_gp.leaders;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.view.View;

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

import com.example.hanan.nim_gp.R;
import com.haipq.android.flagkit.FlagImageView;
import com.squareup.picasso.Picasso;

public class CountryLeadersActivity extends AppCompatActivity {


    DatabaseReference refrence= FirebaseDatabase.getInstance().getReference().child("Players");;
    ArrayList<PlayersDB> players=new ArrayList<PlayersDB>();
    ArrayList<PlayersLB> Fplayers =new ArrayList<PlayersLB>();
    RecyclerView recyclerView ;
    TextView world;
    TextView country;

    ImageView Player1Pic,Player2Pic,Player3Pic,CurrentPlayerPic;
    TextView Player1Name,Player2Name,Player3Name,CurrentPlayerName,Player1Score,Player2Score,Player3Score,CurrentPlayerScore,CurrentPlayerOrder;
    FlagImageView Player1Country,Player2Country,Player3Country,CurrentPlayerCountry;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_leaders);
        Button Wbutton=(Button) findViewById(R.id.CWbuttonof);


        recyclerView = (RecyclerView) findViewById(R.id.reciclerView2);

        Player1Name= (TextView) findViewById(R.id.CPlayer1Name);
        Player1Score= (TextView)findViewById(R.id.CPlayer1score);
        Player1Pic = (ImageView)findViewById(R.id.CPlayer1Pic);
        Player1Country=(FlagImageView) findViewById(R.id.CPlayer1Country);
        Player2Name= (TextView)findViewById(R.id.CPlayer2Name);
        Player2Score= (TextView)findViewById(R.id.CPlayer2Score);
        Player2Pic = (ImageView)findViewById(R.id.CPlayer2Pic);
        Player2Country=(FlagImageView) findViewById(R.id.CPlayer2Country);
        Player3Name= (TextView)findViewById(R.id.CPlayer3Name);
        Player3Score= (TextView)findViewById(R.id.CPlayer3Score);
        Player3Pic = (ImageView)findViewById(R.id.CPlayer3Pic);
        Player3Country=(FlagImageView) findViewById(R.id.CPlayer3Country);
        CurrentPlayerName= (TextView)findViewById(R.id.CCurrentPlayerName);
        CurrentPlayerScore= (TextView)findViewById(R.id.CCurrentPlayerScore);
        CurrentPlayerPic=(ImageView)findViewById(R.id.CCurrentPlayerPic);
        CurrentPlayerCountry=(FlagImageView) findViewById(R.id.CCurrentPlayerCountry);
        CurrentPlayerOrder=(TextView) findViewById(R.id.CCurrentPlayerOrder);
        Wbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent LB2= new Intent(CountryLeadersActivity.this, LeadersActivity.class);
                startActivity(LB2);
            }
        });

        Button Cbutton=(Button) findViewById(R.id.CCbuttonon);
        Cbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent LB2= new Intent(CountryLeadersActivity.this,CountryLeadersActivity.class);
                startActivity(LB2);
            }
        });

        Button Bbutton=(Button) findViewById(R.id.CCbackButton);
        Bbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent LB2= new Intent(CountryLeadersActivity.this, MainActivity.class);
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

        refrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    int score =Integer.parseInt(child.child("score").getValue().toString()) ;

                    String Uname = child.child("username").getValue().toString();
                    String pic= child.child("picURL").getValue().toString();
                    String Country=child.child("countyCode").getValue().toString();
                    PlayersDB p = new PlayersDB(score,Uname,pic,Country);
                  //  if(p!=null&&Country.equals("AR"))
                        players.add(p);



                }
                System.out.println("*******************"+players.size());
                arrange();
                ininatadapter();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());

            }
        });


    }

    private void ininatadapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ReciclerViewLBAdapterActivity reciclerViewLBAdapter = new ReciclerViewLBAdapterActivity (this, read());
        recyclerView.setAdapter(reciclerViewLBAdapter);
        recyclerView.setHasFixedSize(true);
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
