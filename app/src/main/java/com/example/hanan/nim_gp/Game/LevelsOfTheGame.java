package com.example.hanan.nim_gp.Game;

import android.app.Application;

import java.util.ArrayList;

public class LevelsOfTheGame extends Application {

    private int levelNum;
    private int levelScore;
    private String levelName;
    private double levelOptimalTime;
    private double levelMaxTime;
    ArrayList<LevelsOfTheGame> ArrayListOfLevels=new ArrayList<LevelsOfTheGame>();


    public LevelsOfTheGame(){}
    public LevelsOfTheGame( int levelNum,int levelScore, String levelName, double levelOptimalTime, double levelMaxTime){
        this.levelNum = levelNum;
        this.levelScore = levelScore;
        this.levelName = levelName;
        this.levelOptimalTime = levelOptimalTime;
        this.levelMaxTime = levelMaxTime;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        createArrayListOfLevels();
    }

    private void createArrayListOfLevels() {

        //Create first level
        LevelsOfTheGame level1 = new LevelsOfTheGame(0,100,"RACING",10.0,20.0);
        ArrayListOfLevels.add(level1);

        //Create second level

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

    public void setLevelOptimalTime(double levelOptimalTime) { this.levelOptimalTime = levelOptimalTime; }

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





