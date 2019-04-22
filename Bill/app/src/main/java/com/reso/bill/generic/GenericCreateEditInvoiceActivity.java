package com.reso.bill.generic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.reso.bill.EditInvoiceActivity;
import com.reso.bill.R;

import java.util.List;

import util.Utility;

public class GenericCreateEditInvoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_create_edit_invoice);

        if (getSupportActionBar() != null) {
            Utility.setActionBar("Create New Invoice", getSupportActionBar());
        }


        // Invoice Month and Year Spinner Setup
        Spinner monthSpinner;
        String[] monthsArray;
        monthSpinner = findViewById(R.id.monthSpinner);
        monthsArray = getResources().getStringArray(R.array.months_arrays);
        monthSpinner.setPrompt("Month");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GenericCreateEditInvoiceActivity.this, R.layout.spinner_basic_text_white, monthsArray);
        monthSpinner.setAdapter(adapter);

        Spinner yearSpinner;
        List<String> yearsList;
        yearSpinner = findViewById(R.id.yearSpinner);
        yearSpinner.setPrompt("Year");
        yearsList = Utility.createYearsArray();

        yearSpinner.setAdapter(new ArrayAdapter<String>(GenericCreateEditInvoiceActivity.this, R.layout.spinner_basic_text_white, yearsList));
    }
}
