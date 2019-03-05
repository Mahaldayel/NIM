package com.example.hanan.nim_gp.Training;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.neeuro.NativeNSBPlugin.NativeNSBInterface;

import java.util.ArrayList;
import java.util.Collections;

public class BeforeTrainingConnectingWithNeeruo extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {


    public final String NEEURO_ADDRESS_OF_SELECTED_DEVICE = "NEEURO_ADDRESS_OF_SELECTED_DEVICE";

    private ListView headsetsListView;
    private ArrayList<String> headsetsAddressArray;
    private ArrayAdapter<String> adapter;
    private Button mStart_bt;
    private Button mBack_bt;
    private Button mQuit_bt;
    private ProgressDialog progressDialog;


    private String TAG = "BeforeTrainingConnectingWithNeeruo";


    boolean startScan = true;


    public static scanCallBack scanCB ;
    public static NSBFunctionsCallBack nsbFunctionsCB ;
    public static senzeBandDelegates sbDelegate;
    public static connectionCallBack connectionCB ;

    private NSBTrainingActivity nsbTrainingActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_trining_connecting_with_neeruo);

        initElements();
        NativeNSBInterface.getInstance().initializeNSB(getApplicationContext(),this,nsbFunctionsCB,scanCB,connectionCB,sbDelegate);

    }

    private void initElements() {

        headsetsListView = findViewById(R.id.headsets_lv);
        headsetsListView.setOnItemClickListener(this);

        headsetsAddressArray = new ArrayList<>();

        mStart_bt = findViewById(R.id.start);
        mStart_bt.setOnClickListener(this);

        mBack_bt = findViewById(R.id.button_back);
        mBack_bt.setOnClickListener(this);

        mQuit_bt = findViewById(R.id.quit_bt);
        mQuit_bt.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        initAdapter();
        initInterfaces();

    }

    private void initInterfaces() {
        scanCB = new BeforeTrainingConnectingWithNeeruo.scanCallBack();
        nsbFunctionsCB = new BeforeTrainingConnectingWithNeeruo.NSBFunctionsCallBack();
        sbDelegate = new BeforeTrainingConnectingWithNeeruo.senzeBandDelegates();
        connectionCB = new BeforeTrainingConnectingWithNeeruo.connectionCallBack();

    }

    private void initAdapter(){

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, headsetsAddressArray);
        headsetsListView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if(adapterView == headsetsListView){
            goToTrainingActivity(headsetsAddressArray.get(i));

        }
    }

    private void goToTrainingActivity(String neeuroAddress) {

        NativeNSBInterface.getInstance().connectBT(neeuroAddress);

        Context context = BeforeTrainingConnectingWithNeeruo.this;
        Class nextClass = NSBTrainingActivity.class;

        Intent intent = new Intent(context,nextClass);
        intent.putExtra(NEEURO_ADDRESS_OF_SELECTED_DEVICE,neeuroAddress);
        startActivity(intent);


    }

    @Override
    public void onClick(View view) {

        if(mStart_bt == view){

            progressDialog.setMessage("Scanning ...");
            progressDialog.show();
            NativeNSBInterface.getInstance().startStopScanning(startScan);
            startScan = !startScan;

        }else if(view == mBack_bt){
            goTo(MainActivity.class);
        }else if(view == mQuit_bt)
        {
            goTo(MainActivity.class);

        }
    }

    private void goTo(Class nextClass) {

        Context context = BeforeTrainingConnectingWithNeeruo.this;

        Intent intent = new Intent(context,nextClass);
        startActivity(intent);
    }

    /***scan***/
    public class scanCallBack implements NativeNSBInterface.scanCallBackInterface
    {
        public void deviceFoundCB(String result)
        {


            Log.i(TAG,"One NEEURO device found! " +result);

            dismissPrograss();
            if(!headsetsAddressArray.contains(result)){

                headsetsAddressArray.add(result);
                headsetsListView.setAdapter(adapter);


            }
        }

        private void dismissPrograss(){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                      progressDialog.dismiss();
                    }
            });
        }

        public void scanReset()
        {
            Log.i(TAG,"Scan reset " );
        }


        public void errorLog(String s) {
            Log.e(TAG,"Scan errorLog " + s );
        }
    }


    /**Functions**/
    public class NSBFunctionsCallBack implements NativeNSBInterface.NSBFunctionsCallBackInterface
    {
        public void initializedFinished()
        {
            Log.e(TAG,"Initialized Finished!");

            NativeNSBInterface.getInstance().connection_ReturnInUIThread(true);
            NativeNSBInterface.getInstance().EEG_ReturnInUIThread(true);
            NativeNSBInterface.getInstance().EEG_ReturnRawDataInUIThread(false);

        }

        public void bluetoothStatusCallBack(boolean btStatus) {
            Log.i(TAG,"Bluetooth status is " + btStatus);
        }

        public void errorLog(String s) {
            Log.e(TAG,"NSBCB Error Log " + s);
        }
    }

    /**senzeBandDelegates**/
    public class senzeBandDelegates implements NativeNSBInterface.EEGBasicDelegateInterface {


        public TextView attentionText;
        public TextView relaxationText;
        public TextView batteryText;

        public TextView avg_foucs;
        public TextView max_focus;
        public TextView avg_relax;
        public TextView max_relax;

        public  int count = -1;

        private ArrayList<Double> mRelaxArray;
        private ArrayList<Double> mFocusArray;
        public static final int TRAINING_TIME = 30;





        public void EEG_GetAttention(float result) {


            if(attentionText != null && count >= 0){
                attentionText.setText("Attention: " + result +" \n Counter :"+count);
                mFocusArray.add(new Double(result));
                count++;
            }

            if(count == TRAINING_TIME){
                avg_foucs.setText("avg : "+String.valueOf(getAvarage(mFocusArray)));
                max_focus.setText("max : "+ String.valueOf(Collections.max(mFocusArray)));

            }



        }

        public void EEG_GetRelaxation(float result) {


            if (relaxationText != null && count >= 0) {
                relaxationText.setText("Relaxation: " + result);
                mRelaxArray.add(new Double(result));
            }
            if(count == TRAINING_TIME){
                avg_relax.setText("avg : "+String.valueOf(getAvarage(mRelaxArray)));
                max_relax.setText("max : "+ String.valueOf(Collections.max(mRelaxArray)));

            }

        }



        //Too long, its 1000 floats so we won't print this
        public void EEG_GetRawData(float[] floats) {
            Log.i(TAG, "Get raw data is working!" );
        }


        public void EEG_GetBattery(String result)
        {
            if(batteryText != null){
                batteryText.setText("Battery left " + result);
            }

        }

        public TrainingInformation endTrainFocus(TrainingInformation trainingInformation){

            count = 0;

            trainingInformation.setAvgFocus(getAvarage(mFocusArray));
            trainingInformation.setMaxFocus(Collections.max(mFocusArray));


            return trainingInformation;

        }
        public TrainingInformation endTrainRelax(TrainingInformation trainingInformation){

            count = 0;

            trainingInformation.setAvgRelax(getAvarage(mRelaxArray));
            trainingInformation.setMaxRelax(Collections.max(mRelaxArray));

            return trainingInformation;

        }


        public void clearLayout(){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    count = 0;

                    relaxationText.setText("");
                    avg_relax.setText("");
                    max_relax.setText("");

                    attentionText.setText("");
                    avg_foucs.setText("");
                    max_focus.setText("");
                }
            });


        }

        public void setCountToZero(){

            count = 0;
        }

        public  void clearCount(){

            count = -1;
        }


        public int getCounter(){

            return count;
        }
        @Override
        public void EEG_ChannelStatus(boolean[] booleen) {
        }

        public void EEG_GetMCUID(String result)
        {
        }

        public void EEG_GetAccXYZ(float[] result) {

        }

        public void EEG_GetConnectionStatus(boolean result)
        {
        }

        public void pumpAttentionTextView(TextView tv)
        {
            attentionText = tv;

            mRelaxArray = new ArrayList<>();
            mFocusArray = new ArrayList<>();
        }

        public void pumpRelaxationTextView(TextView tv)
        {
            count = 0;
            relaxationText = tv;
        }

        public void pumpBatteryTextView(TextView tv)
        {
            batteryText = tv;
        }

        public void pumpMaxAttentionTextView(TextView maxFocus) {
            max_focus = maxFocus;
        }

        public void pumpAvgAttentionTextView(TextView avgFocus) {
            avg_foucs = avgFocus;
        }

        public void pumpMaxRelaxationTextView(TextView relaxMax) {

            avg_relax = relaxMax;
        }

        public void pumpAvgRelaxationTextView(TextView relaxAvg) {

            max_relax = relaxAvg;

        }
    }


    /**connection**/
    public class connectionCallBack implements NativeNSBInterface.connectionCallBackInterface
    {

        private Context traniningContext;

        public void connectionSucceed(String address)
        {
            Log.e(TAG,"Connection succeed!");
            NativeNSBInterface.getInstance().startStopEEG(true);


        }

        public void connectionFail(String s, String s1)
        {
            Log.e(TAG,"Connection fail! " + s1);
        }

        public void connectionBroken(String s)
        {
            Log.e(TAG,"connection broken " + s );
            displayDialog();

        }


        public void errorLog(String s) {
            Log.e(TAG,"connectionCB error Log " + s );
        }



        private void displayDialog(){

            nsbTrainingActivity = new NSBTrainingActivity();
            traniningContext = nsbTrainingActivity.getContext();

            if(traniningContext != null){

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                traniningContext);
                        // set title
                        alertDialogBuilder.setTitle("Connection Broken");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("The Connection with your headset was Broken. \n Please make sure your headset is working and has enough battery ")
                                .setCancelable(false)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int which) {
                                                goToScanningActivity();
                                            }
                                        });


                        AlertDialog alertDialog = alertDialogBuilder.create();
                        try {

                            alertDialog.show();
                        }
                        catch (WindowManager.BadTokenException e) {
                            //use a log message
                            Log.e(TAG,"BadTokenException  " +e.getMessage());


                        }



                    }
                });
            }

        }

        private void goToScanningActivity(){

            Context context = traniningContext;
            Class nextClass = BeforeTrainingConnectingWithNeeruo.class;
            Intent intent = new Intent(context,nextClass);
            startActivity(intent);
        }

    }



    public double getAvarage(ArrayList<Double> list){

        double sum = 0;
        for (double i : list) {
            sum += i;
        }

        return sum / list.size();
    }



}
