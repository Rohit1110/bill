package com.reso.bill;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;

import util.FirebaseUtil;
import util.ServiceUtil;
import util.Utility;

public class MainActivity extends AppCompatActivity  {

    private Toolbar toolbar;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

      /*  NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.commit();*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
        /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.commit();*/

        if(FirebaseUtil.getPhone() != null) {
            //Phone number already given
            loadProfile(FirebaseUtil.getPhone());
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

    }

    void loadProfile(String phone) {

        pDialog = Utility.getProgressDialogue("Loading Profile", MainActivity.this);
        BillServiceRequest request = new BillServiceRequest();
        BillUser user = new BillUser();
        user.setPhone(phone);
        request.setUser(user);
        StringRequest myReq = ServiceUtil.getStringRequest("loadProfile", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, MainActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
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
                    System.out.println("Profile loaded successfully!");
                    Utility.writeObject(MainActivity.this, Utility.USER_KEY, serviceResponse.getUser());
                    if(serviceResponse.getWarningCode() != null) {
                        //Utility.createAlert(MainActivity.this, serviceResponse.getWarningText(), "Warning");
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setTitle("Warning");
                        alertDialogBuilder.setMessage(serviceResponse.getWarningText());
                        alertDialogBuilder.setPositiveButton("ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        startActivity(new Intent(MainActivity.this, Dashboard.class));
                                        finish();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        startActivity(new Intent(MainActivity.this, Dashboard.class));
                        finish();
                    }

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    if(serviceResponse.getStatus() == BillConstants.ERROR_CODE_GENERIC) {
                        startActivity(new Intent(MainActivity.this, VendorRegistration.class));
                        finish();
                    } else if (serviceResponse.getStatus() == -222) {
                        Utility.createAlert(MainActivity.this, serviceResponse.getResponse(), "Profile Not Approved");
                    } else {
                        Utility.createAlert(MainActivity.this, "Something went wrong! Please try again!", "Error");
                    }
                }


            }

        };
    }
}

