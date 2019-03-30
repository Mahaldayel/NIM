package com.example.hanan.nim_gp.Training;

import com.example.hanan.nim_gp.Game.DeviceListAdapter;
import com.example.hanan.nim_gp.MainActivity;
import com.example.hanan.nim_gp.ManageDevices.Device;
import com.example.hanan.nim_gp.ManageDevices.DeviceType;
import com.example.hanan.nim_gp.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
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

    private Button mQuitSkipLayout_bt;
    private Button mContinue_bt;
    private TextView mBeforeScanningDeception_tv;
    private Button mGoToScan_bt;
    private ConstraintLayout mSkip_layout;

    private Button mQuitLayout_bt;
    private Button mSave_bt;
    private ConstraintLayout mSaveHeadsetLayout;
    private ImageView mFullScreen;
    private EditText mName_et;

    private boolean mIsContinueHeadset;

    private DeviceListAdapter mNewDeviceListAdapter;
    private ArrayList<Device> deviceArrayList;

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private String playerId;
    private int mSelectedHeadsetDeviceIndex;
    private ArrayList<Device> mNewDevices;
    private ArrayList<String> mNewDevicesString;
    private String mSelectedRobotDeviceAddress;
    private String mSelectedHeadsetDeviceAddress;
    private int selectedDeviceIndex;
    private boolean mSelectedHeadsetOn;
    private Context mContext;
    private boolean mConnectToHeadset;
    private TextView mSaveHeadsetTitle_tv;
    private boolean beForeScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_trining_connecting_with_neeruo);

        initElements();
        getDevicesFromFirebase();
        NativeNSBInterface.getInstance().initializeNSB(getApplicationContext(),this,nsbFunctionsCB,scanCB,connectionCB,sbDelegate);

    }

    private void initElements() {

        headsetsListView = findViewById(R.id.headsets_lv);
        headsetsListView.setOnItemClickListener(this);

        headsetsAddressArray = new ArrayList<>();
        deviceArrayList = new ArrayList<>();

        mContext = this;

        mNewDevices = new ArrayList<>();
        mNewDevicesString = new ArrayList<>();

        mStart_bt = findViewById(R.id.start);
        mStart_bt.setOnClickListener(this);

        mBack_bt = findViewById(R.id.button_back);
        mBack_bt.setOnClickListener(this);

        mQuit_bt = findViewById(R.id.quit_bt);
        mQuit_bt.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        mSelectedHeadsetDeviceIndex = -1;

        beForeScan = true;

        initAdapter();
        initInterfaces();
        initSaveHeadsetLayoutElements();
        initSkipLayoutElements();
        initElementToSaveCars();

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
        mBeforeScanningDeception_tv.setTypeface(font);

        mBeforeScanningDeception_tv.setText("Click continue if you want play with selected car that you played with before, else click scan ");


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
        mFullScreen = findViewById(R.id.full_screen_opacity);

        mName_et = findViewById(R.id.new_name_et);

    }


    private void initElementToSaveCars() {


        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        playerId = firebaseAuth.getCurrentUser().getUid();
    }

    private void initInterfaces() {
        scanCB = new BeforeTrainingConnectingWithNeeruo.scanCallBack();
        nsbFunctionsCB = new BeforeTrainingConnectingWithNeeruo.NSBFunctionsCallBack();
        sbDelegate = new BeforeTrainingConnectingWithNeeruo.senzeBandDelegates();
        connectionCB = new BeforeTrainingConnectingWithNeeruo.connectionCallBack();

    }

    private void initAdapter(){
//
//        adapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, headsetsAddressArray);
//        headsetsListView.setAdapter(adapter);

        mNewDeviceListAdapter = new DeviceListAdapter(BeforeTrainingConnectingWithNeeruo.this, R.layout.device_adapter_view, mNewDevices);
        mNewDeviceListAdapter.setSavedDeviceList(deviceArrayList);

    }


    private void getDevicesFromFirebase(){

        progressDialog.setMessage("Loading");
        progressDialog.show();

        DatabaseReference refrence = FirebaseDatabase.getInstance().getReference().child("DeviceInformation").child(playerId);

        refrence.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() ){


                    GenericTypeIndicator<ArrayList<Device>> t = new GenericTypeIndicator<ArrayList<Device>>() {};
                    ArrayList<Device> value = snapshot.getValue(t);
                    setDevices(value);


                }else {

                    progressDialog.dismiss();
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




    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if(adapterView == headsetsListView){

            selectedDeviceIndex = i;
            displaySaveHeadset();
//            goToTrainingActivity(mNewDevices.get(i).getAddress());

        }
    }

    private void goToTrainingActivity(String neeuroAddress) {
        mConnectToHeadset = true;

        NativeNSBInterface.getInstance().connectBT(neeuroAddress);

        Context context = BeforeTrainingConnectingWithNeeruo.this;
        Class nextClass = NSBTrainingActivity.class;

        Intent intent = new Intent(context,nextClass);
        intent.putExtra(NEEURO_ADDRESS_OF_SELECTED_DEVICE,neeuroAddress);
        startActivity(intent);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.start:
                progressDialog.setMessage("Scanning ...");
                progressDialog.show();
                NativeNSBInterface.getInstance().startStopScanning(startScan);
                startScan = !startScan;
                break;
            case R.id.button_back:
                goTo(MainActivity.class);
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

    private void goTo(Class nextClass) {

        Context context = BeforeTrainingConnectingWithNeeruo.this;

        Intent intent = new Intent(context,nextClass);
        startActivity(intent);
    }

    private void hideSkipLayout() {

        mSkip_layout.setVisibility(View.GONE);
        mFullScreen.setVisibility(View.GONE);
        mQuit_bt.setVisibility(View.VISIBLE);


    }

    /*save headset*/
    private void displaySaveHeadset(){

        mSaveHeadsetLayout.setVisibility(View.VISIBLE);
        mFullScreen.setVisibility(View.VISIBLE);
        displayNameForExitsDevice(mNewDevices.get(selectedDeviceIndex).getAddress());


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

    }

    private void save() {


        if(removeDuplicate()){
            setSelectedHeadsetDeviceIndex();
            makeSelectedHeadsetUnselected();
            deviceArrayList.add((Device) createDeviceObject());
            mDatabase.child("DeviceInformation").child(playerId).setValue(deviceArrayList);

        }

        hideSaveHeadset();
        goToTrainingActivity(mNewDevices.get(selectedDeviceIndex).getAddress());



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

                            goToTrainingActivity(mSelectedHeadsetDeviceAddress);
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
            checkIfheadsetOn();
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
        private ArrayList<Double> mRelaxArray;
        private ArrayList<Double> mFocusArray;






        public void EEG_GetAttention(float result) {


            if(attentionText != null){
                attentionText.setText("Attention: " + result);
                mFocusArray.add(new Double(result));
            }




        }

        public void EEG_GetRelaxation(float result) {


            if (relaxationText != null ) {
                relaxationText.setText("Relaxation: " + result);
                mRelaxArray.add(new Double(result));
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


            if(mFocusArray.size() != 0){
                trainingInformation.setAvgFocus(getAvarage(mFocusArray));
                trainingInformation.setMaxFocus(Collections.max(mFocusArray));

                mFocusArray.clear();
                mRelaxArray.clear();


                return trainingInformation;

            }else {

                return null;
            }


        }
        public TrainingInformation endTrainRelax(TrainingInformation trainingInformation){


            if(mRelaxArray.size() != 0){


                trainingInformation.setAvgRelax(getAvarage(mRelaxArray));
                trainingInformation.setMaxRelax(Collections.max(mRelaxArray));

                mFocusArray.clear();
                mRelaxArray.clear();


                return trainingInformation;
            }else {
                return null;
            }

        }


        public void clearLayout(){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    relaxationText.setText("");
                    avg_relax.setText("");
                    max_relax.setText("");

                    attentionText.setText("");
                    avg_foucs.setText("");
                    max_focus.setText("");
                }
            });


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
        private boolean mFinish;

        public void connectionSucceed(String address)
        {
            Log.e(TAG,"Connection succeed!");
            NativeNSBInterface.getInstance().startStopEEG(true);

        }

        public void connectionFail(String s, String s1)
        {
            Log.e(TAG,"Connection fail! " + s1);
            displayDialog("Connection fail! ");

        }

        public void connectionBroken(String s)
        {
            Log.e(TAG,"connection broken " + s );

            if(!mFinish)
                displayDialog("Connection Broken ");

        }


        public void errorLog(String s) {
            Log.e(TAG,"connectionCB error Log " + s );
        }



        private void displayDialog(final String messageTitle){

            nsbTrainingActivity = new NSBTrainingActivity();
            traniningContext = nsbTrainingActivity.getContext();



            if(traniningContext != null){



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String message = "The Connection with your headset was fail. \n Please make sure your headset is working and has enough battery ,and try reconnect again";

                        if(messageTitle.equals("Connection fail! "))
                            message = message;
                        else if(messageTitle.equals("Connection Broken "))
                            message = "The Connection with your headset was Broken. \n Please make sure your headset is working and has enough battery ,and try reconnect again";


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                traniningContext);
                        // set title
                        alertDialogBuilder.setTitle(messageTitle);
                        // set dialog message
                        alertDialogBuilder
                                .setMessage( message)
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

        public void setFinish(boolean finish) {

            mFinish = finish;
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
