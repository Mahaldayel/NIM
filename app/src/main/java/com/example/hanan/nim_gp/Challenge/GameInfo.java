package com.example.hanan.nim_gp.Challenge;

public class GameInfo {
    private String senderUname,gameControlMode,senderPic,gameLevel,gameMode,Reciver;



    private int score;

    public GameInfo(String senderUname,String gameControlMode,String senderPic,String gameLevel,String gameMode,int score,String Reciver) {
        this.senderUname=senderUname;
        this.gameControlMode=gameControlMode;
        this.senderPic=senderPic;
        this.gameLevel = gameLevel;
        this.gameMode=gameMode;
        this.score=score;
        this.Reciver=Reciver;

    }

    public String getGameControlMode() {
        return gameControlMode;
    }
    public String getReciver() {
        return Reciver;
    }


    public String getGameLevel() {
        return gameLevel;
    }

    public String getSenderUname() {
        return senderUname;
    }

    public String getSenderPic() {
        return senderPic;
    }


    public String getGameMod() {
        return gameMode;
    }


    public int getScore() {
        return score;
    }






}
