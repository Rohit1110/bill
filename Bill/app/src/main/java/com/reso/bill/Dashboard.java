package com.reso.bill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rns.web.billapp.service.bo.domain.BillUser;

import java.util.List;

import util.Utility;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    Fragment fragment = null;
    private BillUser user;
    private BottomNavigationView bottomNavigationView;
    private TextView username, businessName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawertest_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user = (BillUser) Utility.readObject(Dashboard.this, Utility.USER_KEY);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_item1:
                        fragment = HomeFragment.newInstance(user);
                        break;
                    case R.id.action_item2:
                        fragment = DailySummaryFragment.newInstance();
                        break;
                    case R.id.action_item3:
                        fragment = FragmentInvoiceSummary.newInstance(user);
                        break;
                }

                Utility.nextFragment(Dashboard.this, fragment);
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_item1);

        View hView = navigationView.getHeaderView(0);
        username = hView.findViewById(R.id.txt_drawer_user_name);
        businessName = hView.findViewById(R.id.txt_drawer_business_name);

    }

    @Override
    protected void onResume() {
        if (fragment == null) {
            bottomNavigationView.setSelectedItemId(R.id.action_item1);
        }
        if (user != null) {
            username.setText(user.getName());
            if (user.getCurrentBusiness() != null) {
                businessName.setText(user.getCurrentBusiness().getName());
            }
        }
        super.onResume();
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
            Utility.nextFragment(Dashboard.this, fragment);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawertest_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {


        if (homeFragmentActive()) {
            Toast.makeText(Dashboard.this, "Cannot Go Back now!!", Toast.LENGTH_LONG);
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawertest_layout);


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }*//* else if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }*//* else {
            if (fragment instanceof FragmentEditInvoice) {
                super.onBackPressed();
            } else {
                if (fragment instanceof CustomerList) {

                    fragment = new MV_BillDetails_two();
                    if (fragment != null) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_layout, fragment);
                        *//*ft.addToBackStack(null);*//*
                        ft.commit();
                    }

                }

                if (fragment instanceof VendorDashBoard) {
                    //Toast.makeText(Dashboard.this,"Back click",Toast.LENGTH_LONG).show();
                    fragment = new MV_BillDetails_two();
                    if (fragment != null) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_layout, fragment);
                        *//*ft.addToBackStack(null);*//*
                        ft.commit();
                    }

                }

                if (fragment instanceof BankDetailsFragment) {
                    //Toast.makeText(Dashboard.this,"Back click",Toast.LENGTH_LONG).show();
                    fragment = new MV_BillDetails_two();
                    if (fragment != null) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_layout, fragment);
                        *//*ft.addToBackStack(null);*//*
                        ft.commit();
                    }

                }
            }

        }*/


    }

    private boolean homeFragmentActive() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment frag : fragments) {
                if (frag.isVisible() && frag instanceof HomeFragment) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }


}
