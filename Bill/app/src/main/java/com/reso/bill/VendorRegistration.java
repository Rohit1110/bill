package com.reso.bill;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.components.MultiSelectionSpinner;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import adapters.LocationAdapter;
import util.FirebaseUtil;
import util.ServiceUtil;
import util.Utility;

public class VendorRegistration extends AppCompatActivity {

    private FloatingActionButton register;
    private EditText name, panNumber, aadharNumber, email, phone, businessName;
    private MultiSelectionSpinner areas;
    private LocationAdapter adapter;
    private ProgressDialog pDialog;
    private boolean saveRequest;


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
                BillUser user = new BillUser();
                user.setName(name.getText().toString());
                user.setPanDetails(panNumber.getText().toString());
                user.setPhone(FirebaseUtil.getPhone());
                user.setAadharNumber(aadharNumber.getText().toString());
                BillBusiness business = new BillBusiness();
                business.setName(businessName.getText().toString());
                business.setBusinessLocations(areas.selectedLocations());
                business.setBusinessSector(ServiceUtil.NEWSPAPER_SECTOR);
                user.setCurrentBusiness(business);
                saveUserInfo(user);
            }
        });

        name =(EditText) findViewById(R.id.et_name);
        panNumber =(EditText) findViewById(R.id.et_pan_number);
        aadharNumber =(EditText) findViewById(R.id.et_aadhar_number);
        phone =(EditText) findViewById(R.id.et_phone);
        email =(EditText) findViewById(R.id.et_email);
        businessName =(EditText) findViewById(R.id.et_business_name);

        phone.setText(FirebaseUtil.getPhone());
        phone.setEnabled(false);

        areas = (MultiSelectionSpinner) findViewById(R.id.sp_area);
        //adapter = new LocationAdapter(this, R.layout.spinner_multi_select, new ArrayList<BillLocation>(), VendorRegistration.this);
        //areas.setAdapter(adapter);


        loadLocations();
    }

    private void saveUserInfo(BillUser user) {
        saveRequest = true;
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(user);
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

                    if(saveRequest) {
                        System.out.println("User saved successfully!");
                        startActivity(new Intent(VendorRegistration.this, MainActivity.class));
                    } else {
                        System.out.println("Locations loaded successfully!");
                        adapter = new LocationAdapter(VendorRegistration.this, R.layout.spinner_multi_select, serviceResponse.getLocations(), VendorRegistration.this);
                        areas.setLocations(serviceResponse.getLocations());
                    }

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    if(saveRequest) {
                        Utility.createAlert(VendorRegistration.this, serviceResponse.getResponse(), "Error");
                    }

                }

            }

        };
    }

}
