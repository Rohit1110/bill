package com.reso.bill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import util.Utility;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Deliveries 2018/05/14");
            System.out.println("Set action bar title ..");
        }*/



       /*final Drawable upArrow = getResources().getDrawable(R.mipmap.backarrow);
        *//*upArrow.setColorFilter(Color.parseColor(""), PorterDuff.Mode.SRC_ATOP);*//*
        getSupportActionBar().setHomeAsUpIndicator(upArrow);*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawertest_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new CustomerList());
        ft.commit();*/

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_item1:
                        //selectedFragment = HomeFragment.newInstance();
                        //selectedFragment = FragmentEditInvoice.newInstance();
                        fragment = HomeFragment.newInstance();
                        break;
                    case R.id.action_item2:
                        //selectedFragment = Fragmenttwo.newInstance();
                        fragment = MV_BillDetails_two.newInstance();
                        break;
                    case R.id.action_item3:
                        //fragment = FragmentCustomerInvoices.newInstance();

                        //selectedFragment = DaysToDeliver.newInstance();
                        break;


                }

                Utility.nextFragment(Dashboard.this, fragment);
                return true;
            }
        });

    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {

            case R.id.nav_myorder:
                //Toast.makeText(Dashboard.this, "Click", Toast.LENGTH_LONG).show();
                fragment = new CustomerList();
                break;
            case R.id.nav_myitems:
                fragment = new VendorDashBoard();
                break;
            case R.id.nav_bank_info:
                fragment = new BankDetailsFragment();
                break;
            case R.id.nav_profile:
                Intent i = new Intent(Dashboard.this, VendorRegistration.class);
                startActivity(i);
                break;


        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_layout, fragment);
            /*ft.addToBackStack(null);*/
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawertest_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawertest_layout);


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }/* else if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }*/ else {
            if (fragment instanceof FragmentEditInvoice) {
                super.onBackPressed();
            } else {
                if (fragment instanceof CustomerList) {

                    fragment = new FragmentEditInvoice();
                    if (fragment != null) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_layout, fragment);
                        /*ft.addToBackStack(null);*/
                        ft.commit();
                    }

                }

                if (fragment instanceof VendorDashBoard) {
                    //Toast.makeText(Dashboard.this,"Back click",Toast.LENGTH_LONG).show();
                    fragment = new FragmentEditInvoice();
                    if (fragment != null) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_layout, fragment);
                        /*ft.addToBackStack(null);*/
                        ft.commit();
                    }

                }

                if (fragment instanceof BankDetailsFragment) {
                    //Toast.makeText(Dashboard.this,"Back click",Toast.LENGTH_LONG).show();
                    fragment = new FragmentEditInvoice();
                    if (fragment != null) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_layout, fragment);
                        /*ft.addToBackStack(null);*/
                        ft.commit();
                    }

                }
            }

        }


    }


}
