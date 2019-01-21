package com.reso.bill.generic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.reso.bill.R;
import com.reso.bill.VendorRegistration;

import util.Utility;

public class GenericQuickReportActivity extends AppCompatActivity {

    private static final int MENU_ITEM_EDIT = 357;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_report);

        if (getSupportActionBar() != null) {
            Utility.setActionBar("Quick Report", getSupportActionBar());
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
