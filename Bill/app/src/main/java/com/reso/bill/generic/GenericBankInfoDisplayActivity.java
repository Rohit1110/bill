package com.reso.bill.generic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillUser;

import util.Utility;

public class GenericBankInfoDisplayActivity extends AppCompatActivity {

    private static final int MENU_ITEM_EDIT = 35;
    private TextView bankName;
    private TextView bankAddress;
    private TextView bankIfscCode;
    private TextView accountNumber;
    private TextView accountHolder;
    private BillUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_info_display);

        if (getSupportActionBar() != null) {
            Utility.setActionBar("Your Bank Info", getSupportActionBar());
        }

        bankName = findViewById(R.id.bankNameTextView);
        bankAddress = findViewById(R.id.bankAddressTextView);
        bankIfscCode = findViewById(R.id.ifscTextView);
        accountNumber = findViewById(R.id.accountNumberTextView);
        accountHolder = findViewById(R.id.accountHolderNameTextView);

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);
        if (user != null && user.getFinancialDetails() != null) {
            bankName.setText(user.getFinancialDetails().getBankName());
            bankAddress.setText(user.getFinancialDetails().getBankAddress());
            bankIfscCode.setText(user.getFinancialDetails().getIfscCode());
            accountNumber.setText(user.getFinancialDetails().getAccountNumber());
            accountHolder.setText(user.getFinancialDetails().getAccountHolderName());
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
