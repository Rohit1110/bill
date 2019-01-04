package com.reso.bill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.rns.web.billapp.service.bo.domain.BillUser;

import util.Utility;

public class ProfileDisplayActivity extends AppCompatActivity {
    private static final int MENU_ITEM_EDIT = 33;

    private TextView nameTextView, emailTextView, contactTextView, businessNameTextView, businessAddressTextView, locationsTextView, areaOfBusinessTextView;
    private BillUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_display);

        if (getSupportActionBar() != null) {
            Utility.setActionBar("Profile", getSupportActionBar());
        }

        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        contactTextView = findViewById(R.id.contactTextView);
        businessNameTextView = findViewById(R.id.businessNameTextView);
        businessAddressTextView = findViewById(R.id.businessAddressTextView);
        locationsTextView = findViewById(R.id.locationsTextView);
        areaOfBusinessTextView = findViewById(R.id.areaOfBusinessTextView);

        Object o = Utility.readObject(ProfileDisplayActivity.this, Utility.USER_KEY);
        if (o != null) {
            user = (BillUser) o;
        }

        if (user != null) {
            nameTextView.setText(user.getName());
            emailTextView.setText(user.getEmail());
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
                Intent editProfileActivity = new Intent(ProfileDisplayActivity.this, VendorRegistration.class);
                startActivity(editProfileActivity);
                return true;
        }
        return false;
    }
}
