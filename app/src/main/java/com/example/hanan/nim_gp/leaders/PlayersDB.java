package com.example.hanan.nim_gp.leaders;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.hanan.nim_gp.R;

public class PlayersDB extends AppCompatActivity {

    private int Score;
    private String Uname;
    private String pic ,Country,ID;

    public PlayersDB(){}

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public PlayersDB(int Score, String PUname , String PPic, String C) {
        this.pic = PPic;
        this.Score = Score;
        this.Uname = PUname;
        Country= C ;


    }


    public int getScore() {
        return Score;
    }

    public String getUname() {
        return Uname;
    }

    public String getPic() {
        return pic;
    }



    public void setScore(int score) {
        Score = score;
    }

    public void setUname(String uname) {
        Uname = uname;
    }



    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
