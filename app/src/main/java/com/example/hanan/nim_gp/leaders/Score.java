package com.example.hanan.nim_gp.leaders;

public class Score {
    String id;
    int Score;

    public Score(String ID,int score){
        id=ID;
        Score=score;
    }

    public void setId(String ID) {
        this.id = ID;
    }

    public String getID() {
        return id;
    }

    public void setScore(int s) {
        this.Score = s;
    }

    public int getScore() {
        return Score;
    }


}
