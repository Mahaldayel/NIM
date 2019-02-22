package com.example.hanan.nim_gp.leaders;

import com.example.hanan.nim_gp.R;
import android.content.Context;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.haipq.android.flagkit.FlagImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import android.graphics.Typeface;


public class ReciclerViewLBAdapterActivity extends RecyclerView.Adapter<ReciclerViewLBAdapterActivity.MyviewHolder> {


    Context c;
    Typeface typeface;

    ArrayList<PlayersLB> playersLB;


    public ReciclerViewLBAdapterActivity(Context c,  ArrayList<PlayersLB> playersLB ){
        this.c = c;
        this.playersLB = playersLB;
     typeface=Typeface.createFromAsset(c.getAssets(), "fonts/Lalezar-Regular.ttf");
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyviewHolder(LayoutInflater.from(c).inflate(R.layout.board_item,viewGroup,false));
    }

    public void onBindViewHolder(@NonNull MyviewHolder myviewHolder, int i) {
        myviewHolder.name.setText(playersLB.get(i).getUname());
        myviewHolder.score.setText(String.valueOf(playersLB.get(i).getScore()));
        String order =String.valueOf(playersLB.get(i).getOrder());
        myviewHolder.order.setText("#"+order);


        Picasso.get().load(playersLB.get(i).getPic()).into(myviewHolder.PlayerImage);
        if(playersLB.get(i).getCpic().equals("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0d/Flag_of_Saudi_Arabia.svg/2000px-Flag_of_Saudi_Arabia.svg.png")){
            System.out.println("*************SHAHAD***********************");
            Picasso.get().load(playersLB.get(i).getCpic()).into(myviewHolder.CountryImage);
        }
        else myviewHolder.CountryImage.setCountryCode(String.valueOf(playersLB.get(i).getCpic()));


    }


    @Override
    public int getItemCount() {
        return playersLB.size();
    }

    class MyviewHolder extends RecyclerView.ViewHolder{

        TextView order,name,score;
        ImageView PlayerImage;
        FlagImageView CountryImage;


        public MyviewHolder(View itemView){
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.playerName_tv);
          name.setTypeface(typeface);
          score = (TextView) itemView.findViewById(R.id.playerScore_tv);
        score.setTypeface(typeface);
           order = (TextView) itemView.findViewById(R.id.orderNumber_tv);
          order.setTypeface(typeface);
           PlayerImage=(ImageView) itemView.findViewById(R.id.playerImage_iv);
          CountryImage =(FlagImageView) itemView.findViewById(R.id.playerCountry_iv);


        }

    }





}
