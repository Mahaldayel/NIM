package com.example.hanan.nim_gp.ManageDevices;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.nim_gp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageDevicesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener ,View.OnClickListener{


    private ListView deviceListview;
    private DeviceListAdapter deviceListAdapter;

    private ArrayList<Device> deviceArrayList;

    private ConstraintLayout mLayout;
    private ImageView mScoreFullScreen;

    private Button mSelect_bt;
    private Button mQuitScore_bt;
    private Button mEdit_bt;
    private Button mEditDone;
    private TextView mLayoutTitle;

    private EditText mName_et;

    private int mClickedDeviceIndex;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private String playerId;
    private ProgressDialog progressDialog;

    private int mSelectedRobotDeviceIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_devices);

        initElements();
        getDevicesFromFirebase();

    }


    private void initElements(){

        deviceListview = findViewById(R.id.devices_lv);
        deviceArrayList = new ArrayList<>();
        initDevicLayoutElements();
        initElementGetDeviceFromFirebase();

//        initListForTest();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");



    }


    private void initElementGetDeviceFromFirebase() {


        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        playerId = firebaseAuth.getCurrentUser().getUid();
    }


//    private void initListForTest(){
//
//        for(int i = 0;i < 10 ;i++)
//            deviceArrayList.add(new Device("AB:12:12:DC:12",DeviceType.Headset,"Hanan's Headset"));
//
//    }

    private void initDevicLayoutElements() {


        Typeface font = Typeface.createFromAsset(getAssets(),  "fonts/Tondu_Beta.ttf");

        mQuitScore_bt = findViewById(R.id.score_quit_bt);
        mQuitScore_bt.setOnClickListener(this);

        mSelect_bt = findViewById(R.id.select_bt);
        mSelect_bt.setOnClickListener(this);
        mSelect_bt.setTypeface(font);

        mEdit_bt = findViewById(R.id.edit_bt);
        mEdit_bt.setOnClickListener(this);
        mEdit_bt.setTypeface(font);

        mLayoutTitle = findViewById(R.id.layout_title);
        mLayoutTitle.setTypeface(font);

        mLayout = findViewById(R.id.layout);
        mScoreFullScreen = findViewById(R.id.score_full_screen);

        mName_et = findViewById(R.id.new_name_et);

        mEditDone = findViewById(R.id.edit_done_bt);
        mEditDone.setOnClickListener(this);
        mEditDone.setTypeface(font);


    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        mClickedDeviceIndex = i;
        displayLayout();


    }


    @Override
    public void onClick(View view) {


        switch (view.getId()){
            case R.id.select_bt:
                makeSelectedDeviceUnselect();
                makeCurrentDeviceSelect();
                break;
            case R.id.edit_bt:
                changeLayoutToEditLayout();
                return;
            case R.id.edit_done_bt:
                editName();
                changeLayoutToSelectLayout();
                break;

        }

        hideLayout();
    }

    private void makeCurrentDeviceSelect() {

        deviceArrayList.get(mClickedDeviceIndex).setSelected(true);
        mDatabase.child("DeviceInformation").child(playerId).setValue(deviceArrayList);
        initAdapter();

    }

    private void changeLayoutToSelectLayout() {

        mEditDone.setVisibility(View.GONE);
        mName_et.setVisibility(View.GONE);

        mSelect_bt.setVisibility(View.VISIBLE);
        mEdit_bt.setVisibility(View.VISIBLE);
    }

    private void changeLayoutToEditLayout() {

        mEditDone.setVisibility(View.VISIBLE);
        mName_et.setVisibility(View.VISIBLE);

        mSelect_bt.setVisibility(View.GONE);
        mEdit_bt.setVisibility(View.GONE);

    }

    private void editName() {

        deviceArrayList.get(mClickedDeviceIndex).setName(mName_et.getText().toString());
        mDatabase.child("DeviceInformation").child(playerId).setValue(deviceArrayList);
        initAdapter();

    }

    private void displayLayout(){

        mName_et.setText(deviceArrayList.get(mClickedDeviceIndex).getName());

        mScoreFullScreen.setVisibility(View.VISIBLE);
        mLayout.setVisibility(View.VISIBLE);

    }

    private void hideLayout(){

        mScoreFullScreen.setVisibility(View.GONE);
        mLayout.setVisibility(View.GONE);

    }

    private void getDevicesFromFirebase(){


        progressDialog.show();
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
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setData(ArrayList<Device> devices) {

        this.deviceArrayList = devices;
        initAdapter();
        progressDialog.dismiss();

    }

    private void initAdapter() {

        setSelectedRobotDeviceIndex();
        deviceListAdapter = new DeviceListAdapter(this,R.layout.device_adapter_view,deviceArrayList, mSelectedRobotDeviceIndex);
        deviceListview.setAdapter(deviceListAdapter);
        deviceListview.setOnItemClickListener(this);
    }

    private void makeSelectedDeviceUnselect(){

        deviceArrayList.get(mSelectedRobotDeviceIndex).setSelected(false);
        mDatabase.child("DeviceInformation").child(playerId).setValue(deviceArrayList);
        initAdapter();

    }

    private void setSelectedRobotDeviceIndex(){

        if(deviceArrayList == null)
            return ;

        int count = 0;
        for(Object device: deviceArrayList){

            if(((Device)device).getSelected().equals(true) && ((Device)device).getType().equals(DeviceType.RobotCar) ){
                mSelectedRobotDeviceIndex = count;

                return;
            }

            count ++;
        }
    }
}
