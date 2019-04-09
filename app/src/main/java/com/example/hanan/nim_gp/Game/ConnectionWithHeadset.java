package com.example.hanan.nim_gp.Game;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.ManageDevices.Device;
import com.example.hanan.nim_gp.ManageDevices.DeviceType;
import com.example.hanan.nim_gp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.neeuro.NativeNSBPlugin.NativeNSBInterface;

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

    public final static String NEEURO_ADDRESS_OF_SELECTED_DEVICE = "NEEURO_ADDRESS_OF_SELECTED_DEVICE";
    public final static String ROBOT_ADDRESS_OF_SELECTED_DEVICE = "ROBOT_ADDRESS_OF_SELECTED_DEVICE";
    public final static String HEADSET_ADDRESS_OF_SELECTED_DEVICE = "HEADSET_ADDRESS_OF_SELECTED_DEVICE";
    public static final int LEVEL_ONE_TIME = 240000;
    public static final int LEVEL_TWO_TIME = 60000;

    public static final String CONTROL_GAME_INTENT ="gameMode";

    public static final String Game_Score ="gameScore";
    String Score,GameMode;


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
    double millisecondsToMinutes = 0.000016667;


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
    private int distance;

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
    private boolean beForeScan;


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

        if(intent.hasExtra(Game_Score))
            Score = intent.getStringExtra(Game_Score);
        if(intent.hasExtra(CONTROL_GAME_INTENT))
            GameMode=intent.getStringExtra(CONTROL_GAME_INTENT);

        if(intent.hasExtra(SELECTED_GAME_LEVEL_INTENT))
            mSelectedGameLevel = intent.getIntExtra(SELECTED_GAME_LEVEL_INTENT,1);

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

        progressDialog.setMessage("Loading ...");
        progressDialog.show();

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

        if(IsDeviceArrayListHasHeadset() && beForeScan){
            progressDialog.dismiss();
            displaySkipLayout();
        }

        progressDialog.dismiss();

    }


    private boolean IsDeviceArrayListHasHeadset(){

        for(Device device: deviceArrayList){
            if(device.getType().equals(DeviceType.Headset))
                return true;
        }

        return false;
    }

    private void displaySkipLayout() {

        beForeScan = false;

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


        initElementGetDeviceFromFirebase();
        initAdapter();
        initInterfaces();
        initSkipLayoutElements();
        initSaveHeadsetLayoutElements();

    }

    private void initSkipLayoutElements() {

        Typeface font = Typeface.createFromAsset(getAssets(),  "fonts/Tondu_Beta.ttf");

        beForeScan = true;

//        mQuitSkipLayout_bt = findViewById(R.id.skip_quit_bt);
//        mQuitSkipLayout_bt.setOnClickListener(this);

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

//        mQuitLayout_bt = findViewById(R.id.layout_quit_bt);
//        mQuitLayout_bt.setOnClickListener(this);

        mSave_bt = findViewById(R.id.save_bt);
        mSave_bt.setOnClickListener(this);

        mSaveHeadsetLayout = findViewById(R.id.save_device_layout);
        mFullScreen = findViewById(R.id.full_screen_opacity);

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
            displaySaveHeadset();

        }
    }

    private void play(String neeuroAddress) {
        Class nextClass;
        NativeNSBInterface.getInstance().connectBT(neeuroAddress);
        mConnectToHeadset = true;

        Context context = ConnectionWithHeadset.this;

        nextClass = StartPlay1Activity.class;

        Intent intent = new Intent(context,nextClass);


        intent.putExtra(CONNECTED_DEVICE_INTENT,mConnectedDeviceIndex);

        if(mIsContinueHeadset)
            intent.putExtra(NEEURO_ADDRESS_OF_SELECTED_DEVICE,mSelectedHeadsetDeviceAddress);
        else
            intent.putExtra(NEEURO_ADDRESS_OF_SELECTED_DEVICE,neeuroAddress);

        intent.putExtra(SELECTED_GAME_LEVEL_INTENT,getIntent().getIntExtra(SELECTED_GAME_LEVEL_INTENT,1));
        intent.putExtra(CONTROL_MODE_GAME_INTENT,getIntent().getStringExtra(CONTROL_MODE_GAME_INTENT));
        intent.putExtra(CONTROL_GAME_INTENT,GameMode);
        intent.putExtra(Game_Score,getIntent().getStringExtra(Game_Score));


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
//            case R.id.layout_quit_bt:
//                hideSaveHeadset();
//                break;
            case R.id.go_to_scan_bt:
                hideSkipLayout();
                break;
//            case R.id.skip_quit_bt:
//                hideSkipLayout();
//                break;
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mConnectToHeadset) {
                        if (mIsContinueHeadset && mSelectedHeadsetOn) {
                            mBeforeScanningDeception_tv.setText("ON");

                            play(mSelectedHeadsetDeviceAddress);
                        } else if (mIsContinueHeadset && !mSelectedHeadsetOn) {
                            mBeforeScanningDeception_tv.setText("OFF");
                            progressDialog.dismiss();
                            NativeNSBInterface.getInstance().startStopScanning(false);

                        }

                    }
                }
            });
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
        private Boolean isEnded ;
        private Boolean isStarted ;


        private TextView relaxTextView;
        private TextView focusTextView;

        private ConstraintLayout mCompleted_l;
        private ConstraintLayout mFailed_l;
        private TextView mScore_c_tv;

        private Context playContext;

        private long startTime , endTime;
        private ImageView mFullScreenOpacity;
        private ImageView starsImageView;
        private Timer timer;
        private TimerTask timerTask;
   public double mScore;
        private TextView mScore_f_tv;
        private double numOfStars;
        private int mSavedScore;
        private int selectGameLevel;
        private String mScoreChallenge;
        private TextView mChallengeWin;


        public void EEG_GetAttention(float result) {

            if(isEnded == null)
                setEnded(false);

            if(isStarted == null)
                setStarted(false);

            if(controlModeNumber == 2 && !isEnded  && isStarted){

                if(selectGameLevel == 1)
                    sendToRobotOneLevel(result);


                else if(selectGameLevel == 2)
                    sendToRobotTwoLevel(result);

                focusTextView.setText(String.valueOf(result));
            receiveMessageFromRobot();

            }
        }


        public void EEG_GetRelaxation(float result) {

            if(isEnded == null)
                setEnded(false);

            if(isStarted == null)
                setStarted(false);


            if(controlModeNumber == 1 && !isEnded && isStarted){

                if(selectGameLevel == 1)
                    sendToRobotOneLevel(result);
                else if(selectGameLevel == 2)
                    sendToRobotTwoLevel(result);

            relaxTextView.setText(String.valueOf(result));
            receiveMessageFromRobot();

            }
        }

        private void sendToRobotOneLevel(float result) {

            if(result > SignalsAvreg) {

                sendToRobot(String.valueOf((int) Math.floor(2)));

            }
        }

        private void sendToRobotTwoLevel(float result) {

            if(result > SignalsAvreg){

                sendToRobot(String.valueOf((int) Math.floor(0)));

            }else {
                sendToRobot(String.valueOf((int) Math.floor(6)));
            }
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

            if(ended){
                long currentTime =  System.currentTimeMillis();
                endTime = currentTime - startTime;

            }
        }

        public void setStarted(Boolean started) {
            isStarted = started;

            if(started){
                startTime = System.currentTimeMillis();
                checkOfEndPlayTimer();
            }
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


                    if(message != null && !message.equals("")){

                        startPlay1Activity = new StartPlay1Activity();
                        playContext = startPlay1Activity.getContext();

                        if(Integer.parseInt(message) == Integer.parseInt(String.valueOf("2")))
                        {
                            msg.setText(message+"\nScore :"+distance);
                            distance += Integer.parseInt(message);


                        }else if(Integer.parseInt(message) == Integer.parseInt(String.valueOf("0"))){
                            msg.setText(message+"\nScore :"+distance);
                            distance += 2;

                        }else if(Integer.parseInt(message) == Integer.parseInt(String.valueOf("6"))){ // test for level two
                            msg.setText(message+"\nScore :"+distance);
                            distance += 0;
                        }
                        else{
                                setEnded(true);

                            if(mSelectedGameLevel == 1)
                                calculateScoreLevelOne(false);
                            else if(mSelectedGameLevel == 2)
                                calculateScoreLevelTwo(false,false);
                            }



                        if(mScoreChallenge != null){
                            if(mScore >= Double.parseDouble(mScoreChallenge)){
                                if(mSelectedGameLevel == 1)
                                    calculateScoreLevelOne(true);
                                else if(mSelectedGameLevel == 2)
                                    calculateScoreLevelTwo(true,false);
                            }
                        }

                    }


                    else
                        msg = startPlay1Activity.getmMsg_tv();
                }
            });

        }

        private void calculateScoreLevelTwo(boolean challenge, boolean timeOver) {

            mFullScreenOpacity.setVisibility(View.VISIBLE);

            //Calculate score
            mScore = (distance / (endTime * millisecondsToMinutes));

            if(timeOver || (challenge && mScore >= Double.parseDouble(mScoreChallenge))) {
                mCompleted_l.setVisibility(View.VISIBLE);

                if(challenge)
                    mChallengeWin.setVisibility(View.VISIBLE);
            }
            else if(challenge && mScore < Double.parseDouble(mScoreChallenge)){

                mScore = 0;
            }
            else{

                /** Change imageViewStars in failed.xml according to the player score */
                numOfStars = ((mScore/65)*100); // 65 MUST TO BE CHANGED
                Drawable new_image = ConnectionWithHeadset.this.getResources().getDrawable(R.drawable.starsunfilled);


                if (numOfStars >= 25 && numOfStars<50)
                    new_image = ConnectionWithHeadset.this.getResources().getDrawable(R.drawable.stars75filled);
                if (numOfStars >= 50 && numOfStars <75)
                    new_image = ConnectionWithHeadset.this.getResources().getDrawable(R.drawable.stars50filled);
                if (numOfStars >= 75 && numOfStars <100)
                    new_image= ConnectionWithHeadset.this.getResources().getDrawable(R.drawable.stars25filled);

                starsImageView.setImageDrawable(new_image);
                mFailed_l.setVisibility(View.VISIBLE);

            }

            mScore_c_tv.setText(String.valueOf((int)mScore));
            mScore_f_tv.setText(String.valueOf((int)mScore));



            //Get player
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String playeId = user.getUid();

            //Update player score
            DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("PlayersGameInfo").child(playeId);
            updateData.child("score").setValue(mSavedScore+((int)mScore));

            //Check total score
            //Update player level
            updateData.child("levelNum").setValue(selectGameLevel); //MUST TO BE CHANGED

            StartPlay1Activity startPlay1Activity = new StartPlay1Activity();
            startPlay1Activity.setCurrentScore(mScore);

            controlRobotBluetooth.disconnect();
        }

        public void setRelaxTextView(TextView relaxTextView) {
            this.relaxTextView = relaxTextView;
        }

        public void setFocusTextView(TextView focusTextView) {
            this.focusTextView = focusTextView;
        }

        public void calculateScoreLevelOne(boolean challenge){

            mFullScreenOpacity.setVisibility(View.VISIBLE);

            //Calculate score
            mScore = (distance / (endTime * millisecondsToMinutes));

            if(mScore >= 50 || (challenge && mScore >= Double.parseDouble(mScoreChallenge))) { // 65 MUST TO BE CHANGED
                mCompleted_l.setVisibility(View.VISIBLE);

                if(challenge)
                    mChallengeWin.setVisibility(View.VISIBLE);

            } else if(challenge && mScore < Double.parseDouble(mScoreChallenge)){

                mScore = 0;
                mFailed_l.setVisibility(View.VISIBLE);
            }
            else{

                /** Change imageViewStars in failed.xml according to the player score */
                numOfStars = ((mScore/65)*100); // 65 MUST TO BE CHANGED
                Drawable new_image = ConnectionWithHeadset.this.getResources().getDrawable(R.drawable.starsunfilled);



                if (numOfStars >= 25 && numOfStars<50)
                     new_image= ConnectionWithHeadset.this.getResources().getDrawable(R.drawable.stars25filled);
                if (numOfStars >= 50 && numOfStars <75)
                    new_image = ConnectionWithHeadset.this.getResources().getDrawable(R.drawable.stars50filled);
                if (numOfStars >= 75 && numOfStars <100)
                    new_image = ConnectionWithHeadset.this.getResources().getDrawable(R.drawable.stars75filled);

                starsImageView.setImageDrawable(new_image);
                mFailed_l.setVisibility(View.VISIBLE);

            }

//            if((endTime*millisecondsToMinutes) > 1)
//                mScore = (mScore / (endTime * millisecondsToMinutes));
            mScore_c_tv.setText(String.valueOf((int)mScore));
            mScore_f_tv.setText(String.valueOf((int)mScore));



            //Get player
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String playeId = user.getUid();

            //Update player score
            DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("PlayersGameInfo").child(playeId);
            updateData.child("score").setValue(mSavedScore+((int)mScore));

            //Check total score
            //Update player level
            updateData.child("levelNum").setValue(selectGameLevel); //MUST TO BE CHANGED


            StartPlay1Activity startPlay1Activity = new StartPlay1Activity();
            startPlay1Activity.setCurrentScore(mScore);

            controlRobotBluetooth.disconnect();

        }

        public void setComplatedLayout(ConstraintLayout mCompleted_l) {
            this.mCompleted_l = mCompleted_l;
        }


        public void setFaildLayout(ConstraintLayout mFailed_l){
            this.mFailed_l = mFailed_l;
        }

        public void setScoreCTextView(TextView mScore_tv) {
            this.mScore_c_tv = mScore_tv;
        }

        public void setScoreFTextView(TextView mScore_f_tv) {

            this.mScore_f_tv = mScore_f_tv;
        }
        public void setFullScreenOpacity(ImageView mFullScreenOpacity) {

            this.mFullScreenOpacity = mFullScreenOpacity;
        }


        public void setStarsImageView(ImageView mStarsImageView) {

            this.starsImageView = mStarsImageView;

        }

        private void checkOfEndPlayTimer(){

            sbDelegate.setEnded(false);

            timer = new Timer();
            initTask();
            if(mSelectedGameLevel == 1)
                timer.schedule(timerTask, LEVEL_ONE_TIME);// level one
            else if(mSelectedGameLevel == 2)
                timer.schedule(timerTask, LEVEL_TWO_TIME); // level two



        }

        private void initTask(){

            timerTask = new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(!getEnded()){

                                setEnded(true);
                                if(mScoreChallenge != null){

                                    if(mSelectedGameLevel == 1)
                                        calculateScoreLevelOne(true);
                                    else if(mSelectedGameLevel == 2)
                                        calculateScoreLevelTwo(true, true);
                                }else {

                                    if(mSelectedGameLevel == 1)
                                        calculateScoreLevelOne(false);
                                    else if(mSelectedGameLevel == 2)
                                        calculateScoreLevelTwo(false, true);
                                }
                            }



                        }
                    });

                }
            };
        }


        public void setSavedScore(int mSavedScore) {

            this.mSavedScore = mSavedScore;

        }

        public boolean getEnded() {
            return isEnded;
        }

        public void setmSelectedGameLevel(int mSelectedGameLevel) {

            selectGameLevel = mSelectedGameLevel;

        }

        public double getScore() {
            return mScore;
        }

        public void setScoreChallenge(String mScoreChallenge) {

            this.mScoreChallenge = mScoreChallenge;
        }

        public void setChallengeWin(TextView mChallengeWin) {

            this.mChallengeWin = mChallengeWin;
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

