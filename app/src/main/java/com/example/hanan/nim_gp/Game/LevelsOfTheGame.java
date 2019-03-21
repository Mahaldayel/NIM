package com.example.hanan.nim_gp.Game;

public class LevelsOfTheGame {

    private int levelNum;
    private int levelScore;
    private String levelName;
    private double levelOptimalTime;
    private double levelMaxTime;


    public LevelsOfTheGame( int levelNum,int levelScore, String levelName, double levelOptimalTime, double levelMaxTime){
        this.levelNum = levelNum;
        this.levelScore = levelScore;
        this.levelName = levelName;
        this.levelOptimalTime = levelOptimalTime;
        this.levelMaxTime = levelMaxTime;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public void setLevelScore(int levelScore) {
        this.levelScore = levelScore;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public void setLevelOptimalTime(double levelOptimalTime) {
        this.levelOptimalTime = levelOptimalTime;
    }

    public void setLevelMaxTime(double levelMaxTime) {
        this.levelMaxTime = levelMaxTime;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public int getLevelScore() {
        return levelScore;
    }

    public String getLevelName() {
        return levelName;
    }

    public double getLevelOptimalTime() {
        return levelOptimalTime;
    }

    public double getLevelMaxTime() {
        return levelMaxTime;
    }



}





