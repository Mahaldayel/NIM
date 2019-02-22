package com.example.hanan.nim_gp.Training;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrainingInformation implements Serializable {


    private String trainingType;
    private double maxRelax;
    private double avgRelax;
    private double maxFocus;
    private double avgFocus;


    public TrainingInformation(String trainingType, double maxRelax, double avgRelax, double maxFocus, double avgFocus) {
        this.trainingType = trainingType;
        this.maxRelax = maxRelax;
        this.avgRelax = avgRelax;
        this.maxFocus = maxFocus;
        this.avgFocus = avgFocus;
    }

    public TrainingInformation() {
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
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
}

