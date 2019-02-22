package com.example.hanan.nim_gp.Training;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrainingInformation implements Serializable {


    private double maxRelax;
    private double avgRelax;
    private double maxFocus;
    private double avgFocus;
    private String playerEmail;




    public TrainingInformation() {
    }


    public void setMaxRelax(double maxRelax) {
        this.maxRelax = maxRelax;
    }

    public void setAvgRelax(double avgRelax) {
        this.avgRelax = avgRelax;
    }

    public void setMaxFocus(double maxFocus) {
        this.maxFocus = maxFocus;
    }

    public void setAvgFocus(double avgFocus) {
        this.avgFocus = avgFocus;
    }


    public double getMaxRelax() {
        return maxRelax;
    }

    public double getAvgRelax() {
        return avgRelax;
    }

    public double getMaxFocus() {
        return maxFocus;
    }

    public double getAvgFocus() {
        return avgFocus;
    }

    public void setPlayerEmail(String playerEmail) {
        this.playerEmail = playerEmail;
    }

    public String getPlayerEmail() {
        return playerEmail;
    }
}

