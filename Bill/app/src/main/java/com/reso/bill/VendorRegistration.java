package com.reso.bill;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.components.MultiSelectionSpinner;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillLocation;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import adapters.LocationAdapter;
import util.FirebaseUtil;
import util.ServiceUtil;
import util.Utility;

public class VendorRegistration extends AppCompatActivity {

    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private FloatingActionButton register;
    private EditText name, panNumber, aadharNumber, email, phone, businessName;
    private MultiSelectionSpinner areas;
    private LocationAdapter adapter;
    private ProgressDialog pDialog;
    private boolean saveRequest;
    private static final int REQUEST_PICK_FILE = 1;
    private File selectedFile;
    String identy;
    private BillUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vender_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.vtoolbar);
        setSupportActionBar(toolbar);
        register = (FloatingActionButton) findViewById(R.id.fab_vregister);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Drawable upArrow = getResources().getDrawable(R.mipmap.backarrow);
        /*upArrow.setColorFilter(Color.parseColor(""), PorterDuff.Mode.SRC_ATOP);*/
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillUser requestUser = new BillUser();
                if(requestUser != null) {
                    requestUser.setId(user.getId());
                }
                requestUser.setName(name.getText().toString());
                requestUser.setEmail(email.getText().toString());
                requestUser.setPanDetails(panNumber.getText().toString());
                requestUser.setPhone(FirebaseUtil.getPhone());
                requestUser.setAadharNumber(aadharNumber.getText().toString());
                BillBusiness business = new BillBusiness();
                business.setName(businessName.getText().toString());
                business.setBusinessLocations(areas.selectedLocations());
                business.setBusinessSector(ServiceUtil.NEWSPAPER_SECTOR);
                requestUser.setCurrentBusiness(business);
                saveUserInfo(requestUser);
            }
        });

        name = (EditText) findViewById(R.id.et_name);
        panNumber = (EditText) findViewById(R.id.et_pan_number);
        aadharNumber = (EditText) findViewById(R.id.et_aadhar_number);
        phone = (EditText) findViewById(R.id.et_phone);
        email = (EditText) findViewById(R.id.et_email);
        businessName = (EditText) findViewById(R.id.et_business_name);

        phone.setText(FirebaseUtil.getPhone());
        phone.setEnabled(false);

        areas = (MultiSelectionSpinner) findViewById(R.id.sp_area);
        //adapter = new LocationAdapter(this, R.layout.spinner_multi_select, new ArrayList<BillLocation>(), VendorRegistration.this);
        //areas.setAdapter(adapter);
        aadharNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (aadharNumber.getRight() - aadharNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Utility.checkcontactPermission(VendorRegistration.this);
                        pickImage();
                        identy = "aadhar";
                        // aadharNumber.setText(identy);
                        return true;
                    }
                }
                return false;
            }
        });


        panNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (panNumber.getRight() - panNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Utility.checkcontactPermission(VendorRegistration.this);
                        pickImage();
                        identy = "pan";
                        return true;
                    }
                }
                return false;
            }
        });


        loadLocations();

        Object o = Utility.readObject(VendorRegistration.this, Utility.USER_KEY);
        if(o != null) {
            user = (BillUser) o;
        }

        if(user != null) {
            name.setText(user.getName());
            email.setText(user.getEmail());
            aadharNumber.setText(user.getAadharNumber());
            panNumber.setText(user.getPanDetails());
            if(user.getCurrentBusiness() != null) {
                businessName.setText(user.getCurrentBusiness().getName());
            }
        }

    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = VendorRegistration.this.getContentResolver().openInputStream(data.getData());
                System.out.println("SSSSSSSSSSSS " + data.getData().getPath());
                if (identy.equals("aadhar")) {
                    //aadharNumber.setText(data.getData().getPath());
                    String filename = data.getData().getPath().substring(data.getData().getPath().lastIndexOf("/") + 1);
                    aadharNumber.setText(filename);
                }
                if (identy.equals("pan")) {
                    // panNumber.setText(data.getData().getPath());
                    String filename = data.getData().getPath().substring(data.getData().getPath().lastIndexOf("/") + 1);
                    panNumber.setText(filename);
                }

                identy = data.getData().getPath();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    private void saveUserInfo(BillUser user) {
        saveRequest = true;
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(user);
        pDialog = Utility.getProgressDialogue("Saving..", this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateUserProfile", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, VendorRegistration.this), request);
        RequestQueue queue = Volley.newRequestQueue(VendorRegistration.this);
        queue.add(myReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Services

    private void loadLocations() {
        saveRequest = false;
        pDialog = Utility.getProgressDialogue("Loading locations", VendorRegistration.this);
        BillServiceRequest request = new BillServiceRequest();
        StringRequest myReq = ServiceUtil.getStringRequest("getAllAreas", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, VendorRegistration.this), request);
        RequestQueue queue = Volley.newRequestQueue(VendorRegistration.this);
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {

                    if (saveRequest) {
                        System.out.println("User saved successfully!");
                        startActivity(new Intent(VendorRegistration.this, MainActivity.class));
                    } else {
                        System.out.println("Locations loaded successfully!");
                        List<BillLocation> locations = prepareLocations(serviceResponse);
                        adapter = new LocationAdapter(VendorRegistration.this, R.layout.spinner_multi_select, locations, VendorRegistration.this);
                        areas.setLocations(locations);
                    }

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    if (saveRequest) {
                        Utility.createAlert(VendorRegistration.this, serviceResponse.getResponse(), "Error");
                    }

                }

            }

        };
    }

    private List<BillLocation> prepareLocations(BillServiceResponse serviceResponse) {
        if(serviceResponse == null || serviceResponse.getLocations() == null) {
            return null;
        }
        if(user == null || user.getCurrentBusiness() == null || user.getCurrentBusiness().getBusinessLocations() == null || user.getCurrentBusiness().getBusinessLocations().size() == 0) {
            return serviceResponse.getLocations();
        }

        for(BillLocation original: serviceResponse.getLocations()) {
            for(BillLocation loc: user.getCurrentBusiness().getBusinessLocations()) {
                if(original.getId() == loc.getId()) {
                    original.setStatus("Selected");
                }
            }
        }
        return serviceResponse.getLocations();
    }

}
