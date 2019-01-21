package com.reso.bill.generic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.reso.bill.R;

import util.Utility;

public class GenericQuickReportActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_report);

        if (getSupportActionBar() != null) {
            Utility.setActionBar("Quick Report", getSupportActionBar());
        }
    }

}
