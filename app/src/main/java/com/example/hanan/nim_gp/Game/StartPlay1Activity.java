package com.example.hanan.nim_gp.Game;


import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import com.neeuro.NativeNSBPlugin.NativeNSBInterface;

import com.example.hanan.nim_gp.R;


public class StartPlay1Activity extends AppCompatActivity {












  connectionWithHeadset.senzeBandDelegates sbDelegate ;
connectionWithHeadset.scanCallBack scanCB ;
   connectionWithHeadset.connectionCallBack connectionCB ;
    connectionWithHeadset.NSBFunctionsCallBack nsbFunctionsCB ;


    private void initElements(){




        setPlayCallBack();

    }





    private void setPlayCallBack(){

        sbDelegate = connectionWithHeadset.sbDelegate;
        scanCB = connectionWithHeadset.scanCB ;
        connectionCB = connectionWithHeadset.connectionCB;
        nsbFunctionsCB = connectionWithHeadset.nsbFunctionsCB;

    }

    public void initializeSenzeBandBasic()
    {
        NativeNSBInterface.getInstance().initializeNSB(getApplicationContext(),this,nsbFunctionsCB,scanCB,connectionCB,sbDelegate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_play1);


        initElements();
        initializeSenzeBandBasic();




    }






}
