package com.example.hanan.nim_gp.Game;


import com.example.hanan.nim_gp.R;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neeuro.NativeNSBPlugin.NativeNSBInterface;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DeviceCallback;

import static com.example.hanan.nim_gp.Game.ConnectionWithRobotCarActivity.CONNECTED_DEVICE_INTENT;
import static com.example.hanan.nim_gp.Game.SelectGameLevelActivity.SELECTED_GAME_LEVEL_INTENT;
import static com.example.hanan.nim_gp.Game.control_modeActivity.CONTROL_MODE_GAME_INTENT;


public class ConnectionWithHeadset extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    public final String NEEURO_ADDRESS_OF_SELECTED_DEVICE = "NEEURO_ADDRESS_OF_SELECTED_DEVICE";
    FirebaseUser CurrentPlayer = FirebaseAuth.getInstance().getCurrentUser();
    String CurrentplayeId = CurrentPlayer.getUid();
    DatabaseReference refrence= FirebaseDatabase.getInstance().getReference().child("TrainingInformation");
    private ListView headsetsListView;
    private ArrayList<String> headsetsAddressArray;
    private ArrayAdapter<String> adapter;
    private Button mStart_bt;
    private Button mBack_bt;
    private ProgressDialog progressDialog;
    float SignalsAvreg=0;
    float SignalsMax=0;

    private String TAG = "BeforeTrainingConnectingWithNeeruo";


    boolean startScan = true;

     //to find headset
    public static scanCallBack scanCB ;
    //to initialized the headshet
    public static NSBFunctionsCallBack nsbFunctionsCB ;
    public static senzeBandDelegates sbDelegate;
    public static connectionCallBack connectionCB ;

    private int controlModeNumber;
    private int mSelectedGameLevel;
    private int mConnectedDeviceIndex;

    private Timer timer;
    private TimerTask timerTask;
    private int mPlayCounter;
    private Context mContext;


//    private Bluetooth bluetooth ;
    private BluetoothDevice mConnectedDevice ;

    private StartPlay1Activity startPlay1Activity;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_trining_connecting_with_neeruo);

        getFormIntent();
        connectWithDataBase(controlModeNumber);
        initElements();
        NativeNSBInterface.getInstance().initializeNSB(getApplicationContext(),this,nsbFunctionsCB,scanCB,connectionCB,sbDelegate);

    }


    private void getFormIntent(){

        Intent intent = getIntent();
        if(intent.hasExtra(CONTROL_MODE_GAME_INTENT)){
           String controlMode = intent.getStringExtra(CONTROL_MODE_GAME_INTENT);
            if(controlMode.equals("Relax"))
                controlModeNumber = 1;
            if(controlMode.equals("Focus"))
                controlModeNumber = 2;
        }

        if(intent.hasExtra(CONNECTED_DEVICE_INTENT)){
            mConnectedDeviceIndex = intent.getIntExtra(CONNECTED_DEVICE_INTENT,-1);
            setRobotAddress(mConnectedDeviceIndex);
        }


        if(intent.hasExtra(SELECTED_GAME_LEVEL_INTENT))
            mSelectedGameLevel = intent.getIntExtra(SELECTED_GAME_LEVEL_INTENT,0);

    }

    private void setRobotAddress(int mConnectedDeviceIndex) {

        Bluetooth bluetooth = new Bluetooth(this);
        bluetooth.enable();
        bluetooth.onStart();
        List<BluetoothDevice> devices = bluetooth.getPairedDevices();



        mConnectedDevice = devices.get(mConnectedDeviceIndex);
        bluetooth.onStop();


    }



    private void connectWithDataBase(final int finalControlModeNumber){

            refrence.addValueEventListener(new ValueEventListener() {



            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if(child.getKey().equals(CurrentplayeId))

                    if(finalControlModeNumber == 1) {
                        SignalsAvreg = Float.parseFloat(child.child("avgRelax").getValue().toString());
                        SignalsMax =Float.parseFloat(child.child("maxRelax").getValue().toString());
                    }

                    if(finalControlModeNumber == 2 ){
                        SignalsAvreg = Float.parseFloat(child.child("avgFocus").getValue().toString());
                        SignalsMax =Float.parseFloat(child.child("maxFocus").getValue().toString());
                    }

                }}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            }
        );
        }

    private void initElements() {


        headsetsListView = findViewById(R.id.headsets_lv);
        headsetsListView.setOnItemClickListener(this);

        headsetsAddressArray = new ArrayList<>();

        mStart_bt = findViewById(R.id.start);
        mStart_bt.setOnClickListener(this);

        mBack_bt = findViewById(R.id.button_back);
        mBack_bt.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        initAdapter();
        initInterfaces();

    }


    private void initInterfaces() {
        scanCB = new ConnectionWithHeadset.scanCallBack();
        nsbFunctionsCB = new ConnectionWithHeadset.NSBFunctionsCallBack();
        sbDelegate = new ConnectionWithHeadset.senzeBandDelegates();
        connectionCB = new ConnectionWithHeadset.connectionCallBack();

    }

    private void initAdapter(){

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, headsetsAddressArray);
        headsetsListView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if(adapterView == headsetsListView){
            play(headsetsAddressArray.get(i));

        }
    }

    private void play(String neeuroAddress) {

        NativeNSBInterface.getInstance().connectBT(neeuroAddress);

        Context context = ConnectionWithHeadset.this;
        Class nextClass = StartPlay1Activity.class;

        Intent intent = new Intent(context,nextClass);
        intent.putExtra(NEEURO_ADDRESS_OF_SELECTED_DEVICE,neeuroAddress);
        intent.putExtra(CONNECTED_DEVICE_INTENT,mConnectedDeviceIndex);
        intent.putExtra(SELECTED_GAME_LEVEL_INTENT, mSelectedGameLevel);
        intent.putExtra(CONTROL_MODE_GAME_INTENT,controlModeNumber);
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
            goTo(ConnectionWithRobotCarActivity.class);
        }
    }

    private void goTo(Class nextClass) {

        Context context = ConnectionWithHeadset.this;

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

//to initialized the headshet
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

    /***/
    public class senzeBandDelegates implements NativeNSBInterface.EEGBasicDelegateInterface {


        private Bluetooth controlRobotBluetooth;
        private TextView msg;
        private Boolean isEnded;

        private TextView relaxTextView;
        private TextView focusTextView;

        public void EEG_GetAttention(float result) {


            if(controlModeNumber == 2 && !isEnded)
                if(result>SignalsAvreg)
                    sendToRobot(String.valueOf((int) Math.floor(result/SignalsAvreg)));

            focusTextView.setText(String.valueOf(result));

        }


        public void EEG_GetRelaxation(float result) {

            if(controlModeNumber == 1 && !isEnded)
                if(result>SignalsAvreg)
                    sendToRobot(String.valueOf((int) Math.floor(result/SignalsAvreg)));


            relaxTextView.setText(String.valueOf(result));
            receiveMessageFromRobot();

        }


        public void sendToRobot(final String msg) {

            if(controlRobotBluetooth == null){
                setBluetooth();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (controlRobotBluetooth != null && !controlRobotBluetooth.isConnected())
                        controlRobotBluetooth.connectToDevice(mConnectedDevice);


                    if (controlRobotBluetooth != null && controlRobotBluetooth.getBluetoothAdapter() != null && controlRobotBluetooth.isConnected()) {
                        controlRobotBluetooth.send(String.valueOf(msg));
                    }
                }
            });


        }


        public void setControlRobotBluetooth(Bluetooth controlRobotBluetooth) {
            this.controlRobotBluetooth = controlRobotBluetooth;
        }

        public void setEnded(Boolean ended) {
            isEnded = ended;
        }

        private void setBluetooth(){

            startPlay1Activity = new StartPlay1Activity();
            controlRobotBluetooth = startPlay1Activity.getBluetooth();
        }

        //Too long, its 1000 floats so we won't print this
        public void EEG_GetRawData(float[] floats) {
            Log.i(TAG, "Get raw data is working!" );
        }

        public void EEG_GetBattery(String result) {


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

        public void setTextView(TextView textView){

            msg = textView;
        }

        private void receiveMessageFromRobot(){


            if(controlRobotBluetooth == null){
                setBluetooth();
            }


            controlRobotBluetooth.setDeviceCallback(new DeviceCallback() {
                @Override public void onDeviceConnected(BluetoothDevice device) { }
                @Override public void onDeviceDisconnected(BluetoothDevice device, String message) {}
                @Override public void onMessage(final String message) {
                    //TODO get score from robot
                    displayReceivedMsg(message);


                }
                @Override public void onError(String message) {}
                @Override public void onConnectError(BluetoothDevice device, String message) {

                }
            });


        }

        private void displayReceivedMsg(final String message) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startPlay1Activity = new StartPlay1Activity();

                    if(msg != null)
                        msg.setText(message);
                    else
                        msg = startPlay1Activity.getmMsg_tv();
                }
            });

        }

        public void setRelaxTextView(TextView relaxTextView) {
            this.relaxTextView = relaxTextView;
        }

        public void setFocusTextView(TextView focusTextView) {
            this.focusTextView = focusTextView;
        }
    }


    public class connectionCallBack implements NativeNSBInterface.connectionCallBackInterface
    {
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

        }

        public void errorLog(String s) {
            Log.e(TAG,"connectionCB error Log " + s );
        }
    }




}

