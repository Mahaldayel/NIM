package com.example.hanan.nim_gp.leaders;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.hanan.nim_gp.R;

public class PlayersLB extends AppCompatActivity {

    private int Score,order;
    private String Uname ;
    private String pic,Cpic;




    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getCpic() {
        return Cpic;
    }

    public void setCpic(String cpic) {
        Cpic = cpic;
    }



    public PlayersLB(int S, String UN, String Pic, String cpic,int o) {
        Score=S;
        Uname = UN;
        pic=Pic;
        Cpic=cpic;
        order=o;
    }

    public PlayersLB() {
    }

    public int getScore() {
        return Score;
    }

    public void setUname(String uname) {
        Uname = uname;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPic() {
        return pic;
    }

    public void setScore(int score) {
        Score = score;
    }

    public String getUname() {
        return Uname;
    }

}
