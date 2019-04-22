package com.reso.bill.generic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.reso.bill.R;

import util.Utility;

public class GenericCreateEditInvoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_create_edit_invoice);

        if (getSupportActionBar() != null) {
            Utility.setActionBar("Create New Invoice", getSupportActionBar());
        }

    }
}
