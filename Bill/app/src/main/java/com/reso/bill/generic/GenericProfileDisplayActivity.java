package com.reso.bill.generic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.reso.bill.R;
import com.reso.bill.VendorRegistration;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.squareup.picasso.Picasso;

import util.ServiceUtil;
import util.Utility;

public class GenericProfileDisplayActivity extends AppCompatActivity {
    private static final String TAG = "GenericProfileDisplayAc";
    private static final int MENU_ITEM_EDIT = 33;
    Toolbar toolbar;
    private BillUser user;
    private ImageView businessLogo;
    private TextView emailTextView, contactTextView, businessNameTextView, businessAddressTextView, locationsTextView, areaOfBusinessTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_display);

        toolbar = findViewById(R.id.toolbar);
        if (getSupportActionBar() != null) {
            Log.d(TAG, "onCreate: getSupportActionBar is not NULL");
//            Utility.setActionBar("Profile", getSupportActionBar());
        } else {
            Log.d(TAG, "onCreate: getSupportActionBar is NULL");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        businessLogo = (ImageView) findViewById(R.id.businessLogoImageView);
        emailTextView = findViewById(R.id.emailTextView);
        contactTextView = findViewById(R.id.contactTextView);
        businessNameTextView = findViewById(R.id.businessNameTextView);
        businessAddressTextView = findViewById(R.id.businessAddressTextView);
        locationsTextView = findViewById(R.id.locationsTextView);
        areaOfBusinessTextView = findViewById(R.id.areaOfBusinessTextView);

        Object o = Utility.readObject(GenericProfileDisplayActivity.this, Utility.USER_KEY);
        if (o != null) {
            user = (BillUser) o;
        }

        if (user != null) {
            getSupportActionBar().setTitle(user.getName());
            emailTextView.setText(user.getEmail());
            contactTextView.setText(user.getPhone());
            BillBusiness currentBusiness = user.getCurrentBusiness();
            if (currentBusiness != null) {
                businessNameTextView.setText(currentBusiness.getName());
                businessAddressTextView.setText(currentBusiness.getAddress());
                if (currentBusiness.getBusinessSector() != null) {
                    areaOfBusinessTextView.setText(currentBusiness.getBusinessSector().getName());
                }
                if (currentBusiness.getBusinessLocations() != null && currentBusiness.getBusinessLocations().size() > 0) {
                    locationsTextView.setText(Utility.getLocationString(currentBusiness.getBusinessLocations()));
                }

            }
            Picasso.get().load(ServiceUtil.ROOT_URL + "getImage/logo/" + user.getCurrentBusiness().getId()).into(businessLogo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_EDIT, Menu.NONE, "Edit").setIcon(R.drawable.ic_edit_blue_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //Back button click
                finish();
                return true;
            case MENU_ITEM_EDIT:
                startActivity(Utility.nextIntent(this, VendorRegistration.class, false));
                return true;
        }
        return false;
    }
}
