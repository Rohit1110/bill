package com.reso.bill;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.reso.bill.generic.GenericBankDetailsActivity;

import util.Utility;

public class BankInfoDisplayActivity extends AppCompatActivity {

    private static final int MENU_ITEM_EDIT = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_info_display);

        if (getSupportActionBar() != null) {
            Utility.setActionBar("Your Bank Info", getSupportActionBar());
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
                startActivity(Utility.nextIntent(this, GenericBankDetailsActivity.class, false));
                return true;
        }
        return false;
    }
}
