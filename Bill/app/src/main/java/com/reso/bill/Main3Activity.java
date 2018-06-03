package com.reso.bill;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class Main3Activity extends AppCompatActivity {
    private BottomBar mBottomBar;
    private FragNavController fragNavController;

    //indices to fragments
    private final int TAB_FIRST = FragNavController.TAB1;
    private final int TAB_SECOND = FragNavController.TAB2;
    private final int TAB_THIRD = FragNavController.TAB3;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItems(R.menu.bottombar_menu);
       /* mBottomBar.setBackgroundColor(R.color.bgcolor);*/
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                //switch between tabs
                switch (menuItemId) {
                    case R.id.bottomBarItemOne:
                        fragNavController.switchTab(TAB_FIRST);
                        break;
                    case R.id.bottomBarItemSecond:
                        fragNavController.switchTab(TAB_SECOND);
                        break;
                    case R.id.bottomBarItemThird:
                        fragNavController.switchTab(TAB_THIRD);
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemOne) {
                    fragNavController.clearStack();
                }
            }
        });
        //End of BottomBar menu

        //Navigation drawer
        new DrawerBuilder().withSliderBackgroundColor(R.color.bgcolor).withActivity(this).build();

        //primary items
        PrimaryDrawerItem MyOrders = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName("Vendor dashboard");
                //.withIcon(R.drawable.ic_home_black_24dp);*/
        PrimaryDrawerItem primary_item1 = new PrimaryDrawerItem()
                .withIdentifier(2)
                .withName("Customer Dashboard");

                //.withIcon(R.drawable.ic_looks_one_black_24dp);*/
      /*  PrimaryDrawerItem primary_item2 = new PrimaryDrawerItem()
                .withIdentifier(3);
              *//*  .withName(R.string.drawer_item_option2)
                .withIcon(R.drawable.ic_looks_two_black_24dp);*//*
        //secondary items
        SecondaryDrawerItem secondary_item1 = (SecondaryDrawerItem) new SecondaryDrawerItem()
                .withIdentifier(4)
                .withName("DashBoaed1");
                //.withIcon(R.drawable.ic_looks_one_black_24dp);
        SecondaryDrawerItem secondary_item2 = (SecondaryDrawerItem) new SecondaryDrawerItem()
                .withIdentifier(12)
                .withName("DashBoaed2");
               // .withIcon(R.drawable.ic_looks_two_black_24dp);
        SecondaryDrawerItem secondary_item3 = (SecondaryDrawerItem) new SecondaryDrawerItem()
                .withIdentifier(13)
                .withName("DashBoaed3");
                //.withIcon(R.drawable.ic_looks_3_black_24dp);
        //settings, help, contact items
        SecondaryDrawerItem settings = (SecondaryDrawerItem) new SecondaryDrawerItem()
                .withIdentifier(97)
                .withName("DashBoaed4");
                //.withIcon(R.drawable.ic_settings_black_24dp);
        SecondaryDrawerItem help = (SecondaryDrawerItem) new SecondaryDrawerItem()
                .withIdentifier(98)
                .withName("DashBoaed4");
               // .withIcon(R.drawable.help);
        SecondaryDrawerItem contact = (SecondaryDrawerItem) new SecondaryDrawerItem()
                .withIdentifier(99)
                .withName("DashBoaed5");*/
                //.withIcon(R.drawable.ic_contact_mail_black_24dp);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withTranslucentStatusBar(false)
                .withFullscreen(true)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(
                        MyOrders,
                        primary_item1
                        /*primary_item2,
                        new SectionDrawerItem().withName("Categories"),
                        secondary_item1,
                        secondary_item2,
                        secondary_item2,
                        new DividerDrawerItem(),
                        settings,
                        help,
                        contact*/

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    Fragment fragment =null;
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(Main3Activity.this, VendorDashBoard.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                //intent = new Intent(Main3Activity.this, Class.class);
                            } /*else if (drawerItem.getIdentifier() == 3) {
                                //intent = new Intent(MainActivity.this, Class.class);
                            } else if (drawerItem.getIdentifier() == 11) {
                                //intent = new Intent(MainActivity.this, Class.class);
                            } else if (drawerItem.getIdentifier() == 12) {
                                //intent = new Intent(MainActivity.this, Class.class);
                            } else if (drawerItem.getIdentifier() == 13) {
                                //intent = new Intent(MainActivity.this, Class.class);
                            } else if (drawerItem.getIdentifier() == 97) {
                                //intent = new Intent(Main3Activity.this, DAs.class);
                            } else if (drawerItem.getIdentifier() == 98) {
                                //intent = new Intent(Main3Activity.this, Help.class);
                            } else if (drawerItem.getIdentifier() == 99) {
                               // intent = new Intent(Main3Activity.this, Contact.class);
                            }*/
                            if (intent != null) {
                                Main3Activity.this.startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .build();
        //End of Navigation drawer

    }
    @Override
    public void onBackPressed() {
        if (fragNavController.getCurrentStack().size() > 1) {
            fragNavController.pop();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }

}