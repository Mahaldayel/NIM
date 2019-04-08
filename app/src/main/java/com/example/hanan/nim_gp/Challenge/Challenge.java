package com.example.hanan.nim_gp.Challenge;

public class Challenge {
    private String SenderUname;
    private String SenderPic;
    private String ChallengeID;
    private String Level;
    private String GameControl;
    private String GameMode;
    int Score;

    public Challenge(String senderUname,String pic,String id,String level,int Score,String control,String mode) {
        SenderUname = senderUname;
        SenderPic=pic;
        ChallengeID=id;
        Level=level;
        this.Score=Score;
        GameControl=control;
        GameMode=mode;
    }




    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }



    public String getGameControl() {
        return GameControl;
    }

    public void setGameControl(String gameControl) {
        GameControl = gameControl;
    }



    public String getGameMode() {
        return GameMode;
    }

    public void setGameMode(String gameMode) {
        GameMode = gameMode;
    }




    public String getSenderPic() {
        return SenderPic;
    }

    public void setSenderPic(String senderPic) {
        SenderPic = senderPic;
    }



    public String getChallengeID() {
        return ChallengeID;
    }

    public void setChallengeID(String challengeID) {
        ChallengeID = challengeID;
    }


    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }



    public String getSenderUname() {
        return SenderUname;
    }

    public void setSenderUname(String senderUname) {
        SenderUname = senderUname;
    }


}


