package com.example.hanan.nim_gp.AccountActivity;

public class PlayerGameInfo {

    int score;
    int levelNum;



    public  PlayerGameInfo(int score,int levelNum){
        this.score = score;
        this.levelNum = levelNum;
    }


    public void setScore(int score) {
        this.score = score;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public int getScore() {
        return score;
    }

    public int getLevelNum() {
        return levelNum;
    }
}
