package com.example.hanan.nim_gp.Game;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hanan.nim_gp.ManageDevices.Device;
import com.example.hanan.nim_gp.ManageDevices.DeviceType;
import com.example.hanan.nim_gp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DeviceCallback;
import me.aflak.bluetooth.DiscoveryCallback;

import static com.example.hanan.nim_gp.Game.SelectGameLevelActivity.SELECTED_GAME_LEVEL_INTENT;
import static com.example.hanan.nim_gp.Game.control_modeActivity.CONTROL_MODE_GAME_INTENT;


public class ConnectionWithRobotCarActivity extends AppCompatActivity implements AdapterView.OnItemClickListener ,View.OnClickListener {


    private static final int REQUEST_ENABLE_BT = 1111;
    public static final String CONNECTED_DEVICE_INTENT = "connected device index";

    public static final int ROBOT_TYPER = 1;

    // you must have bluetooth permissions before calling the constructor
    private Bluetooth bluetooth ;
    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> mPairedDevices;
    private List<BluetoothDevice> mNewDevices;

    public DeviceListAdapter mNewDeviceListAdapter;
    private ProgressDialog progressDialog;


    private ListView mLvNewDevices;

    private Timer timer;
    private TimerTask timerTask;

    private BluetoothDevice mConnectedDevice ;

    private Button mScan_bt;
    private Button mBack_bt;

    private Context mContext;
    private String mControlMode;

    private int mSelectedGameLevel;

    private Button mQuitLayout_bt;
    private Button mSave_bt;
    private ConstraintLayout mSaveCarLayout;
    private EditText mName_et;
    private ImageView mSaveCarFullScreen;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private String playerId;

    private Button mContinue_bt;
    private Button mGoToScan_bt;
    private TextView mBeforeScanningDeception_tv;
    private ConstraintLayout mBeforeTraining_layout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cars_list);


        initElements();
        getControlModeFromIntent();
        getSelectedLevelFromIntent();
        check();


    }

    private void getControlModeFromIntent() {

        Intent intent = getIntent();
        if(intent.hasExtra(CONTROL_MODE_GAME_INTENT)){
           mControlMode = intent.getStringExtra(CONTROL_MODE_GAME_INTENT);
        }
    }

    private void getSelectedLevelFromIntent() {

        Intent intent = getIntent();

        if(intent.hasExtra(SELECTED_GAME_LEVEL_INTENT))
            mSelectedGameLevel = intent.getIntExtra(SELECTED_GAME_LEVEL_INTENT,0);
    }

    private void initElements(){
        bluetooth = new Bluetooth(this);

        mLvNewDevices = findViewById(R.id.lvNewDevices);
        mLvNewDevices.setOnItemClickListener(this);

        mNewDevices = new ArrayList<>();
        mPairedDevices = new ArrayList<>();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        mScan_bt = findViewById(R.id.button_scan);
        mScan_bt.setOnClickListener(this);

        mBack_bt = findViewById(R.id.button_back);
        mBack_bt.setOnClickListener(this);

        mContext = ConnectionWithRobotCarActivity.this;

        progressDialog = new ProgressDialog(this);

        initSaveCarLayoutElements();
        initElementToSaveCars();
        initBeforeTrainingLayoutElements();


    }

    private void initBeforeTrainingLayoutElements() {

        Typeface font = Typeface.createFromAsset(getAssets(),  "fonts/Tondu_Beta.ttf");


        mContinue_bt = findViewById(R.id.continue_bt);
        mContinue_bt.setOnClickListener(this);
        mContinue_bt.setTypeface(font);

        mGoToScan_bt = findViewById(R.id.go_to_scan_bt);
        mGoToScan_bt.setOnClickListener(this);
        mGoToScan_bt.setTypeface(font);

        mBeforeScanningDeception_tv = findViewById(R.id.before_scanning_deception);
        mBeforeScanningDeception_tv.setOnClickListener(this);
        mBeforeScanningDeception_tv.setTypeface(font);

        mBeforeTraining_layout = findViewById(R.id.before_scanning_layout);

    }

    private void initElementToSaveCars() {


        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        playerId = firebaseAuth.getCurrentUser().getUid();
    }

    private void initSaveCarLayoutElements() {


        mQuitLayout_bt = findViewById(R.id.layout_quit_bt);
        mQuitLayout_bt.setOnClickListener(this);

        mSave_bt = findViewById(R.id.save_bt);
        mSave_bt.setOnClickListener(this);

        mSaveCarLayout = findViewById(R.id.save_car_layout);
        mSaveCarFullScreen = findViewById(R.id.full_screen);

        mName_et = findViewById(R.id.new_name_et);

    }

    @Override
    protected void onStart() {
        super.onStart();
        bluetooth.onStart();
        bluetooth.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.onStop();
    }


    private void scan(){

        progressDialogShow("Scanning ...");

        if(!bluetooth.isEnabled()){
            bluetooth.isEnabled();
            scan();
        }else{
            displayNewDevice();
            mPairedDevices =  bluetooth.getPairedDevices();

        }


    }



    private void displayNewDevice(){

        bluetooth.startScanning();

        bluetooth.setDiscoveryCallback(new DiscoveryCallback() {
            @Override public void onDiscoveryStarted() {
            }
            @Override public void onDiscoveryFinished() {}
            @Override public void onDeviceFound(BluetoothDevice device) {
                progressDialog.dismiss();

                if ( (!mNewDevices.contains(device))
                        && device.getType() == ROBOT_TYPER
                        )
                    addNewDeviceToListView(device);


            }
            @Override public void onDevicePaired(BluetoothDevice device) {
                connectPairedDevice(device);


            }
            @Override public void onDeviceUnpaired(BluetoothDevice device) {}
            @Override public void onError(String message) {}
        });

    }

    private void connectPairedDevice(BluetoothDevice device) {
        mPairedDevices.add(device);
        bluetooth.connectToDevice(mConnectedDevice);
        connected();
    }

    private void addNewDeviceToListView(BluetoothDevice device) {
        mNewDevices.add(device);
        mNewDeviceListAdapter = new DeviceListAdapter(ConnectionWithRobotCarActivity.this, R.layout.device_adapter_view,(ArrayList<BluetoothDevice>) mNewDevices);
        mLvNewDevices.setAdapter(mNewDeviceListAdapter);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        if (adapterView == mLvNewDevices){


            displaySaveRobotCar();
            mConnectedDevice = mNewDevices.get(i);

            pairedAndConnectClickedDevice(i);


        }



    }

    private void pairedAndConnectClickedDevice(int i) {

        mConnectedDevice = mNewDevices.get(i);
        progressDialogShow("Connecting ...");
        bluetooth.pair(mConnectedDevice);
        bluetooth.connectToDevice(mConnectedDevice);
    }

    private void progressDialogShow(String msg) {

        progressDialog.setMessage(msg);
        progressDialog.show();
    }


    private void connected(){

        bluetooth.setDeviceCallback(new DeviceCallback() {
            @Override public void onDeviceConnected(BluetoothDevice device) {
                timerTask.cancel();
//                goToNextActivity();

                displaySaveRobotCar();


            }
            @Override public void onDeviceDisconnected(BluetoothDevice device, String message) {}
            @Override public void onMessage(String message) {}
            @Override public void onError(String message) {}
            @Override public void onConnectError(BluetoothDevice device, String message) {

                displayErrorMessage(message);
            }
        });
    }

    private void displayErrorMessage(String message) {

        if(message.contains("read failed"))
           message = "the device not available";


        final String msg = message;


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(mContext);
                }
                builder.setTitle("Error")
                        .setMessage(msg)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.button_scan:
                scan();
                break;
            case R.id.back_bt:
                goTo(player_modeActivity.class);
                break;
            case R.id.save_bt:
                saveRobotCar();
                break;
            case R.id.layout_quit_bt:
                hideSaveRobotCar();
                break;
            case R.id.go_to_scan_bt:
                hideBeforeTrainingLayout();
                break;
            case R.id.continue_bt:
                goTo(ConnectionWithHeadset.class);
                break;
        }
//        if(view == mScan_bt)
//            scan();
//
//        else if(view == mBack_bt){
//
//            goTo(player_modeActivity.class);
//
//        }


    }

    private void hideBeforeTrainingLayout() {

        mBeforeTraining_layout.setVisibility(View.GONE);
    }

    private void goTo(Class nextClass) {

        Context context = ConnectionWithRobotCarActivity.this;

        Intent intent = new Intent(context,nextClass);
        startActivity(intent);

    }

    private void goToNextActivity(){


        progressDialog.dismiss();

        Context context = ConnectionWithRobotCarActivity.this;
        Class nextClass = ConnectionWithHeadset.class;

        Intent intent = new Intent(context,nextClass);
        int index = mPairedDevices.indexOf(mConnectedDevice);
        intent.putExtra(CONNECTED_DEVICE_INTENT,index);
        intent.putExtra(SELECTED_GAME_LEVEL_INTENT, mSelectedGameLevel);
        intent.putExtra(CONTROL_MODE_GAME_INTENT,mControlMode);

        startActivity(intent);

    }



    private void check(){

        timer = new Timer();
        initTask();
        timer.schedule(timerTask,10);

    }

    private void initTask(){

        timerTask = new TimerTask() {
            @Override
            public void run() {
                connected();

            }
        };
    }


    private void displaySaveRobotCar(){

        mSaveCarLayout.setVisibility(View.VISIBLE);
        mSaveCarFullScreen.setVisibility(View.VISIBLE);


    }

    private void hideSaveRobotCar(){

        mSaveCarLayout.setVisibility(View.GONE);
        mSaveCarFullScreen.setVisibility(View.GONE);


    }

    private void saveRobotCar(){


        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String playerId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference refrence = FirebaseDatabase.getInstance().getReference().child("DeviceInformation");

        refrence.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() ){
                    for (DataSnapshot child : snapshot.getChildren()) {


                        if (child.getKey().equals(playerId)){

                            GenericTypeIndicator<ArrayList<Device>> t = new GenericTypeIndicator<ArrayList<Device>>() {};
                            ArrayList<Device> value = child.getValue(t);
                            setData(value);

                        }
                    }
                }else {

                    mDatabase.child("DeviceInformation").child(playerId).setValue(createDeviceListObject());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setData(ArrayList<Device> devices) {

        if(notAvailable(devices)){
            devices.add((Device) createDeviceObject());
            mDatabase.child("DeviceInformation").child(playerId).setValue(devices);

        }

        goToNextActivity();

    }

    private boolean notAvailable(ArrayList<Device> devices) {


        if(devices == null)
            return true;

        for(Object device: devices){

            if(((Device)device).getAddress().equals(mConnectedDevice.getAddress().toString())){
                return false;

            }
        }



        return true;
    }

    private Object createDeviceObject() {

        return new Device(mConnectedDevice.getAddress(), DeviceType.RobotCar,mName_et.getText().toString());
    }

    private ArrayList<Device> createDeviceListObject() {

        ArrayList<Device> devices = new ArrayList<>();
        Device device = new Device(mConnectedDevice.getAddress(), DeviceType.RobotCar,mName_et.getText().toString());
         devices.add(device);

        return devices;
    }


}
