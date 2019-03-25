package com.example.hanan.nim_gp.Game;


import com.example.hanan.nim_gp.GameOver.CompletedActivity;
import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.ManageDevices.Device;
import com.example.hanan.nim_gp.ManageDevices.DeviceType;
import com.example.hanan.nim_gp.R;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.nim_gp.Training.NSBTrainingActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.neeuro.NativeNSBPlugin.NativeNSBInterface;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DeviceCallback;

import static com.example.hanan.nim_gp.Game.ConnectionWithRobotCarActivity.CONNECTED_DEVICE_INTENT;
import static com.example.hanan.nim_gp.Game.SelectGameLevelActivity.SELECTED_GAME_LEVEL_INTENT;
import static com.example.hanan.nim_gp.Game.control_modeActivity.CONTROL_MODE_GAME_INTENT;


public class ConnectionWithHeadset extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    public final static String NEEURO_ADDRESS_OF_SELECTED_DEVICE = "NEEURO_ADDRESS_OF_SELECTED_DEVICE";
    public final static String ROBOT_ADDRESS_OF_SELECTED_DEVICE = "ROBOT_ADDRESS_OF_SELECTED_DEVICE";
    public final static String HEADSET_ADDRESS_OF_SELECTED_DEVICE = "HEADSET_ADDRESS_OF_SELECTED_DEVICE";

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

    boolean mIsContinueCar;

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


    private BluetoothDevice mConnectedDevice ;

    private StartPlay1Activity startPlay1Activity;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private String playerId;

    private String mSelectedRobotDeviceAddress;

    private ArrayList<Device> deviceArrayList;
    private Button mQuitLayout_bt;
    private Button mSave_bt;
    private ConstraintLayout mSaveHeadsetLayout;
    private ImageView mFullScreen;
    private EditText mName_et;
    private int mSelectedHeadsetDeviceIndex;
    private String mSelectedHeadsetDeviceAddress;
    private int selectedDeviceIndex;
    private ArrayList<Device> mNewDevices;
    private ArrayList<String> mNewDevicesString;
    private DeviceListAdapter mNewDeviceListAdapter;

    private Button mContinue_bt;
    private Button mGoToScan_bt;
    private TextView mBeforeScanningDeception_tv;
    private ConstraintLayout mSkip_layout;
    private boolean mIsContinueHeadset;

    private boolean mSelectedHeadsetOn;
    private boolean mConnectToHeadset ;
    private Context mContext;
    private Button mQuitSkipLayout_bt;
    private TextView mSaveHeadsetTitle_tv;
    private Button mQuit_bt;

    private int mScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_trining_connecting_with_neeruo);

        initElements();
        getFormIntent();
        getDevicesFromFirebase();
        connectWithDataBase(controlModeNumber);
        NativeNSBInterface.getInstance().initializeNSB(ConnectionWithHeadset.this,this,nsbFunctionsCB,scanCB,connectionCB,sbDelegate);

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
            mIsContinueCar = false;
        }else {
            mIsContinueCar = true;
            getDevicesFromFirebase();
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

    private void initElementGetDeviceFromFirebase() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        playerId = firebaseAuth.getCurrentUser().getUid();
    }


    private void getDevicesFromFirebase(){

//        progressDialog.show();
        DatabaseReference refrence = FirebaseDatabase.getInstance().getReference().child("DeviceInformation");

        refrence.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() ){
                    for (DataSnapshot child : snapshot.getChildren()) {

                        if (child.getKey().equals(playerId)){

                            GenericTypeIndicator<ArrayList<Device>> t = new GenericTypeIndicator<ArrayList<Device>>() {};
                            ArrayList<Device> value = child.getValue(t);
                            setDevices(value);

                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setDevices(ArrayList<Device> devices) {

        this.deviceArrayList = devices;

        setSelectedDevicesAddress();
        initAdapter();
//        if(devices != null)
//            displaySkipLayout();

    }

    private void displaySkipLayout() {

        mSkip_layout.setVisibility(View.VISIBLE);
        mFullScreen.setVisibility(View.VISIBLE);
        mQuit_bt.setVisibility(View.GONE);



    }
    private void setSelectedDevicesAddress() {

        for(Object device: deviceArrayList){

            if(((Device)device).getSelected().equals(true) && ((Device)device).getType().equals(DeviceType.RobotCar) ){
                mSelectedRobotDeviceAddress = ((Device) device).getAddress();
            }

            if(((Device)device).getSelected().equals(true) && ((Device)device).getType().equals(DeviceType.Headset) ){
                mSelectedHeadsetDeviceAddress = ((Device) device).getAddress();
            }
        }
        progressDialog.dismiss();

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

        mNewDevices = new ArrayList<>();
        mNewDevicesString = new ArrayList<>();

        mQuit_bt = findViewById(R.id.quit_bt);
        mQuit_bt.setOnClickListener(this);

        mStart_bt = findViewById(R.id.start);
        mStart_bt.setOnClickListener(this);

        mBack_bt = findViewById(R.id.button_back);
        mBack_bt.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        mContext = this;
        mSelectedHeadsetDeviceIndex = -1;

        mScore = 0;

        initElementGetDeviceFromFirebase();
        initAdapter();
        initInterfaces();
        initSkipLayoutElements();
        initSaveHeadsetLayoutElements();

    }

    private void initSkipLayoutElements() {

        Typeface font = Typeface.createFromAsset(getAssets(),  "fonts/Tondu_Beta.ttf");


        mQuitSkipLayout_bt = findViewById(R.id.skip_quit_bt);
        mQuitSkipLayout_bt.setOnClickListener(this);

        mContinue_bt = findViewById(R.id.continue_bt);
        mContinue_bt.setOnClickListener(this);
        mContinue_bt.setTypeface(font);

        mGoToScan_bt = findViewById(R.id.go_to_scan_bt);
        mGoToScan_bt.setOnClickListener(this);
        mGoToScan_bt.setTypeface(font);


        mBeforeScanningDeception_tv = findViewById(R.id.before_scanning_deception);
        mBeforeScanningDeception_tv.setOnClickListener(this);
        mBeforeScanningDeception_tv.setTypeface(font);
        mBeforeScanningDeception_tv.setText("Click continue if you want play with selected headset that you played with before, \nelse click scan ");


        mSkip_layout = findViewById(R.id.before_scanning_layout);

    }

    private void initSaveHeadsetLayoutElements() {

        Typeface font = Typeface.createFromAsset(getAssets(),  "fonts/Tondu_Beta.ttf");

        mSaveHeadsetTitle_tv = findViewById(R.id.layout_title);
        mSaveHeadsetTitle_tv.setTypeface(font);

        mQuitLayout_bt = findViewById(R.id.layout_quit_bt);
        mQuitLayout_bt.setOnClickListener(this);

        mSave_bt = findViewById(R.id.save_bt);
        mSave_bt.setOnClickListener(this);

        mSaveHeadsetLayout = findViewById(R.id.save_device_layout);
        mFullScreen = findViewById(R.id.full_screen);

        mName_et = findViewById(R.id.new_name_et);

    }


    private void initInterfaces() {

        scanCB = new ConnectionWithHeadset.scanCallBack();
        nsbFunctionsCB = new ConnectionWithHeadset.NSBFunctionsCallBack();
        sbDelegate = new ConnectionWithHeadset.senzeBandDelegates();
        connectionCB = new ConnectionWithHeadset.connectionCallBack();

    }

    private void initAdapter(){

        mNewDeviceListAdapter = new DeviceListAdapter(ConnectionWithHeadset.this, R.layout.device_adapter_view, mNewDevices);
        mNewDeviceListAdapter.setSavedDeviceList(deviceArrayList);

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if(adapterView == headsetsListView){
            selectedDeviceIndex = i;
//            displaySaveHeadset();
            play(mNewDevices.get(selectedDeviceIndex).getAddress());


        }
    }

    private void play(String neeuroAddress) {

        NativeNSBInterface.getInstance().connectBT(neeuroAddress);
        mConnectToHeadset = true;

        Context context = ConnectionWithHeadset.this;
        Class nextClass = StartPlay1Activity.class;

        Intent intent = new Intent(context,nextClass);

        //TODO send device address to next activity
//        if(mIsContinueCar)
//            intent.putExtra(ROBOT_ADDRESS_OF_SELECTED_DEVICE,mSelectedRobotDeviceAddress);
//        else
            intent.putExtra(CONNECTED_DEVICE_INTENT,mConnectedDeviceIndex);

//        if(mIsContinueHeadset)
//            intent.putExtra(HEADSET_ADDRESS_OF_SELECTED_DEVICE,mSelectedHeadsetDeviceAddress);
//        else
            intent.putExtra(NEEURO_ADDRESS_OF_SELECTED_DEVICE,neeuroAddress);

        intent.putExtra(SELECTED_GAME_LEVEL_INTENT, mSelectedGameLevel);
        intent.putExtra(CONTROL_MODE_GAME_INTENT,controlModeNumber);
        startActivity(intent);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.start:
                progressDialog.setMessage("Scanning ...");
                progressDialog.show();
                NativeNSBInterface.getInstance().startStopScanning(startScan);
                startScan = !startScan;
                break;
            case R.id.button_back:
                goTo(ConnectionWithRobotCarActivity.class);
                break;
            case R.id.quit_bt:
                goTo(MainActivity.class);
                break;
            case R.id.save_bt:
                save();
                break;
            case R.id.layout_quit_bt:
                hideSaveHeadset();
                break;
            case R.id.go_to_scan_bt:
                hideSkipLayout();
                break;
            case R.id.skip_quit_bt:
                hideSkipLayout();
                break;
            case R.id.continue_bt:
                mIsContinueHeadset = true;
                progressDialog.setMessage("Searching ...");
                progressDialog.show();
                NativeNSBInterface.getInstance().startStopScanning(true);
                break;
        }

    }


    private void hideSkipLayout() {

        mSkip_layout.setVisibility(View.GONE);
        mFullScreen.setVisibility(View.GONE);
        mQuit_bt.setVisibility(View.VISIBLE);

    }

    private void goTo(Class nextClass) {

        Context context = ConnectionWithHeadset.this;

        Intent intent = new Intent(context,nextClass);
        startActivity(intent);

    }



    /*save headset*/
    private void displaySaveHeadset(){

        mSaveHeadsetLayout.setVisibility(View.VISIBLE);
        mFullScreen.setVisibility(View.VISIBLE);
        displayNameForExitsDevice(mNewDevices.get(selectedDeviceIndex).getAddress());
        mQuit_bt.setVisibility(View.GONE);



    }


    private void displayNameForExitsDevice(String selectDeviceAddress) {

        if(deviceArrayList == null)
            return;

        for(Object device: deviceArrayList){

            if(((Device)device).getAddress().equals(selectDeviceAddress)){
                mName_et.setText(((Device) device).getName());
            }

        }
    }

    private void hideSaveHeadset() {

        mSaveHeadsetLayout.setVisibility(View.GONE);
        mFullScreen.setVisibility(View.GONE);
        mQuit_bt.setVisibility(View.VISIBLE);


    }

    private void save() {


        if(removeDuplicate()){
            setSelectedHeadsetDeviceIndex();
            makeSelectedHeadsetUnselected();
            deviceArrayList.add((Device) createDeviceObject());
            mDatabase.child("DeviceInformation").child(playerId).setValue(deviceArrayList);

        }

        hideSaveHeadset();
        play(mNewDevices.get(selectedDeviceIndex).getAddress());


    }

    private boolean removeDuplicate() {

        if(deviceArrayList == null)
            return true;

        for(Object device: deviceArrayList){

            if(((Device)device).getAddress().equals(mNewDevices.get(selectedDeviceIndex).getAddress())){
                deviceArrayList.remove(device);
                return true;

            }
        }
        return true;
    }

    private Object createDeviceObject() {

        return new Device(mNewDevices.get(selectedDeviceIndex).getAddress(), DeviceType.Headset,mName_et.getText().toString());
    }


    private void setSelectedHeadsetDeviceIndex(){

        if(deviceArrayList == null)
            return ;

        int count = 0;
        for(Object device: deviceArrayList){

            if(((Device)device).getSelected().equals(true) && ((Device)device).getType().equals(DeviceType.Headset) ){
                mSelectedHeadsetDeviceIndex = count;

                return;
            }

            count ++;
        }
    }

    private void makeSelectedHeadsetUnselected(){

        if(mSelectedHeadsetDeviceIndex != -1){
            deviceArrayList.get(mSelectedHeadsetDeviceIndex).setSelected(false);
            mDatabase.child("DeviceInformation").child(playerId).setValue(deviceArrayList);

        }

    }

    /***scan***/
    public class scanCallBack implements NativeNSBInterface.scanCallBackInterface
    {
        public void deviceFoundCB(String result)
        {

            Log.i(TAG,"One NEEURO device found! " +result);

            dismissPrograss();
            if(!mNewDevicesString.contains(result)){


                mNewDevicesString.add(result);
                mNewDevices.add(new Device(result,DeviceType.Headset,""));
                headsetsListView.setAdapter(mNewDeviceListAdapter);

                if(result.equals(mSelectedHeadsetDeviceAddress))
                    mSelectedHeadsetOn = true;

                checkIfheadsetOn();

            }
        }


        private void checkIfheadsetOn(){

            if(!mConnectToHeadset){
                if(mIsContinueHeadset && mSelectedHeadsetOn)
                    play(mSelectedHeadsetDeviceAddress);
                else if(mIsContinueHeadset && !mSelectedHeadsetOn){
                    //TODO display dialog set your car ON
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(mContext,"OFF",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            NativeNSBInterface.getInstance().startStopScanning(false);

                        }
                    });
            }
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
            checkIfheadsetOn();
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
        private Boolean isStarted;


        private TextView relaxTextView;
        private TextView focusTextView;

        private ConstraintLayout mCompleted_l;
        private TextView mScore_tv;

        private Context playContext;


        public void EEG_GetAttention(float result) {


            if(controlModeNumber == 2 && !isEnded  && isStarted)
                if(result > SignalsAvreg)
                    sendToRobot(String.valueOf((int) Math.floor(2)));
//                    sendToRobot(String.valueOf((int) Math.floor(result/SignalsAvreg)));

            focusTextView.setText(String.valueOf(result));

        }


        public void EEG_GetRelaxation(float result) {

            if(controlModeNumber == 1 && !isEnded && isStarted)
                if(result > SignalsAvreg)
                    sendToRobot(String.valueOf((int) Math.floor(2)));
//                    sendToRobot(String.valueOf((int) Math.floor(result/SignalsAvreg)));


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
                        if(!mIsContinueCar)
                            controlRobotBluetooth.connectToDevice(mConnectedDevice);
                        else if(mSelectedRobotDeviceAddress != null)
                            controlRobotBluetooth.connectToAddress(mSelectedRobotDeviceAddress);
                        else if(mIsContinueCar && mSelectedRobotDeviceAddress == null)
                            setSelectedDevicesAddress();

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

        public void setStarted(Boolean started) {
            isStarted = started;
        }

        private void setBluetooth(){

            startPlay1Activity = new StartPlay1Activity();
            controlRobotBluetooth = startPlay1Activity.getBluetooth();
        }

        public void setSelectedRobotAddress(String address) {

            mSelectedRobotDeviceAddress = address;
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

                    //TODO error message
                }
            });


        }

        private void displayReceivedMsg(final String message) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    if(msg != null){

                        startPlay1Activity = new StartPlay1Activity();
                        playContext = startPlay1Activity.getContext();

                        if(Integer.parseInt(message) == Integer.parseInt(String.valueOf("2")))
                        {
                            msg.setText(message+"\nScore :"+mScore);
                            mScore += Integer.parseInt(message);


//                            if(playContext != null)
//                                startActivity(new Intent(playContext, CompletedActivity.class));

                        }else{


                            if(mScore > 130){
                                msg.setText(message+"\nScore :"+mScore+"\n Win ");
                                isEnded = true;

                            }

                            mScore_tv.setText(String.valueOf(mScore));
                            mCompleted_l.setVisibility(View.VISIBLE);
                        }

                    }
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


        public void setComplatedLayout(ConstraintLayout mCompleted_l) {

            this.mCompleted_l = mCompleted_l;
        }

        public void setScoreTextView(TextView mScore_tv) {

            this.mScore_tv = mScore_tv;
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

