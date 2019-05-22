package com.reso.bill.generic;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reso.bill.AddBusinessLogo;
import com.reso.bill.CustomerList;
import com.reso.bill.DistributorsActivity;
import com.reso.bill.FragmentInvoiceSummary;
import com.reso.bill.HelpActivity;
import com.reso.bill.HomeFragment;
import com.reso.bill.LoginActivity;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.util.BillConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

import util.FirebaseUtil;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Admin on 13/11/2018.
 */

public class GenericDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int TIME_DELAY = 2000;
    //public static Fragment currentFragment;
    private static long back_pressed;
    private static Fragment fragment = null;
    private BillUser user;
    //private BottomNavigationView bottomNavigationView;
    private TextView username, businessName;
    private ImageView profilePic;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private boolean drawerChanged = true;
    //private List<Class<?>> fragments = new ArrayList<Class<?>>();

    /*public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }*/

    public ImageView getProfilePic() {
        return profilePic;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public boolean isDrawerChanged() {
        return drawerChanged;
    }

    public void setDrawerChanged(boolean drawerChanged) {
        this.drawerChanged = drawerChanged;
    }

    /*private void initFragments() {
        fragments.add(GenericInvoices.class);
        fragments.add(CustomerList.class);
        fragments.add(BankDetailsFragment.class);
        fragments.add(GenericVendorDashBoard.class);
    }*/

    /*private boolean isDrawerFragment(Fragment fragment) {
        for (Class cls : fragments) {
            if (fragment.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_activity_dashboard);


        drawer = (DrawerLayout) findViewById(R.id.drawertest_layout);

        user = (BillUser) Utility.readObject(GenericDashboard.this, Utility.USER_KEY);

        if (!BillConstants.FRAMEWORK_RECURRING.equals(Utility.getFramework(user))) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_quick_bill).setVisible(false);
        }

        //initFragments();
        /*
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_item1:
                        fragment = GenericInvoices.newInstance();
                        break;
                    case R.id.action_item2:
                        fragment = GenericMyProducts.newIntance();
                        break;
                    case R.id.action_item3:
                        //fragment = FragmentInvoiceSummary.newInstance(user);
                        break;
                }

                Utility.nextFragment(GenericDashboard.this, fragment);
                return true;
            }
        });*//*

        bottomNavigationView.setSelectedItemId(R.id.action_item1);*/

        //bottomNavigationView.getMenu().getItem(0).setChecked(false);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                try {

                    List<Fragment> fragments = getSupportFragmentManager().getFragments();
                    if (fragments == null || fragments.size() == 0) {
                        fragment = null;
                        finish();
                    }
                    Fragment currentFragment = fragments.get(0);
                    /*if (currentFragment instanceof GenericInvoices || currentFragment instanceof GenericMyProducts || currentFragment instanceof GenericTransactions) {
                        //On the main screen, so enable bottom nav
                        *//*getBottomNavigationView().getMenu().getItem(0).setCheckable(true);
                        getBottomNavigationView().getMenu().getItem(1).setCheckable(true);
                        getBottomNavigationView().getMenu().getItem(2).setCheckable(true);
                        if (currentFragment instanceof GenericInvoices) {
                            getBottomNavigationView().getMenu().getItem(0).setChecked(true);
                        } else if (currentFragment instanceof GenericMyProducts) {
                            getBottomNavigationView().getMenu().getItem(1).setChecked(true);
                        } else {
                            getBottomNavigationView().getMenu().getItem(2).setChecked(true);
                        }*//*

                    } else {
                        getBottomNavigationView().getMenu().getItem(0).setCheckable(false);
                        getBottomNavigationView().getMenu().getItem(1).setCheckable(false);
                        getBottomNavigationView().getMenu().getItem(2).setCheckable(false);
                    }*/
                    /*if (isDrawerFragment(currentFragment)) {
                        setDrawer();
                    }*/

                    System.out.println("...... Fragment changed .... " + fragment + " CURR => " + currentFragment);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Utility.hideKeyboard(GenericDashboard.this);

            }
        });

        System.out.println("........ On Create called .... " + fragment);
        setDrawer();

        Utility.logFlurry("Dashboard", user);
    }

    public void setDrawer() {

        if (!isDrawerChanged()) {
            return;
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        username = hView.findViewById(R.id.txt_drawer_user_name);
        businessName = hView.findViewById(R.id.txt_drawer_business_name);
        profilePic = hView.findViewById(R.id.img_profile_pic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Utility.nextFragment(GenericDashboard.this, AddBusinessLogo.newInstance());
            }
        });
        //getBottomNavigationView().setVisibility(View.VISIBLE);
        setDrawerChanged(false);
        Utility.hideKeyboard(GenericDashboard.this);
    }

    @Override
    protected void onResume() {
        System.out.println("........ On resume called .... " + fragment);

        user = (BillUser) Utility.readObject(GenericDashboard.this, Utility.USER_KEY);

        if (fragment == null) {
            //bottomNavigationView.setSelectedItemId(R.id.action_item1);
            fragment = Utility.getHomeFragment(user);
            Utility.nextFragment(GenericDashboard.this, fragment);
        } else if (getSupportFragmentManager().getFragments() == null || getSupportFragmentManager().getFragments().size() == 0) {
            Utility.nextFragment(GenericDashboard.this, fragment);
        }

        if (user != null) {
            username.setText(user.getName());
            if (user.getCurrentBusiness() != null) {
                businessName.setText(user.getCurrentBusiness().getName());

                if (user.getCurrentBusiness().getLogo() != null && user.getCurrentBusiness().getLogo().getFileName() != null) {
                    updateBusinessLogo(user);
                } else {
                    profilePic.setImageResource(R.drawable.logo_sample);
                }
                //profilePic.setImageBitmap(b);
            }
        }
        super.onResume();
    }

    public void updateBusinessLogo(BillUser user) {
        Picasso.get().load(ServiceUtil.ROOT_URL + "getImage/logo/" + user.getCurrentBusiness().getId()).into(profilePic);
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search,menu);
        return onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {

            case R.id.nav_dashboard:
                //Toast.makeText(Dashboard.this, "Click", Toast.LENGTH_LONG).show();
//                fragment = new GenericNewDashboard();
                fragment = Utility.getHomeFragment(user);
                break;
            case R.id.nav_myorder:
                //Toast.makeText(Dashboard.this, "Click", Toast.LENGTH_LONG).show();
                fragment = new CustomerList();
                break;
            case R.id.nav_myitems:
                fragment = new GenericVendorDashBoard();
                break;
            /*case R.id.nav_bank_info:
                fragment = new BankDetailsFragment();
                break;*/
            case R.id.nav_profile:
                Intent i = new Intent(GenericDashboard.this, GenericProfileDisplayActivity.class);
                startActivity(i);
                break;
            case R.id.nav_home:
//                fragment = Utility.getHomeFragment(user);
                fragment = HomeFragment.newInstance(user);
                break;
            case R.id.nav_pending_invoices:
                fragment = FragmentInvoiceSummary.newInstance(user);
                break;
            case R.id.nav_quick_bill:
                fragment = GenericInvoices.newInstance();
                break;
            case R.id.nav_logout:
                new AlertDialog.Builder(GenericDashboard.this).setTitle("Logout?").setMessage("Do you really want to logout?")
//                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                FirebaseUtil.logout();

                                startActivity(Utility.nextIntent(GenericDashboard.this, LoginActivity.class, false));
                                GenericDashboard.this.finish();
                            }
                        }).setNegativeButton(android.R.string.no, null).show();

                break;
            /*case R.id.nav_settings:
                fragment = Settings.newInstance();
                break;*/

        }
        if (fragment != null) {
            Utility.nextFragment(GenericDashboard.this, fragment);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawertest_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (homeFragmentActive()) {
            Toast.makeText(GenericDashboard.this, "Cannot Go Back now!!", Toast.LENGTH_LONG);
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    private boolean homeFragmentActive() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment frag : fragments) {
                if (frag.isVisible() && frag instanceof GenericInvoices) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            /*int heightDiff = bottomNavigationView.getRootView().getHeight() - bottomNavigationView.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(GenericDashboard.this);

            if (heightDiff <= contentViewTop) {
                onHideKeyboard();

                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard(keyboardHeight);

                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight", keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }*/
        }
    };

    private boolean keyboardListenersAttached = false;
    //private ViewGroup rootLayout;


    protected void onShowKeyboard(int keyboardHeight) {
    }

    protected void onHideKeyboard() {
    }

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        //rootLayout = (ViewGroup) findViewById(R.id.rootLayout);
        //bottomNavigationView.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            //bottomNavigationView.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }
}
