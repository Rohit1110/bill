package com.reso.bill.generic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillFinancialDetails;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import util.ServiceUtil;
import util.Utility;

public class GenericBankDetailsActivity extends AppCompatActivity {
    private static final String TAG = "GenericBankDetailsActiv";

    private EditText bankName, accountNumber, ifscCode, address, accountHolder;
    private BillUser user;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_bank_details);

        Utility.setActionBar("Edit Bank Info", getSupportActionBar());


        bankName = findViewById(R.id.et_bank_name);
        ifscCode = findViewById(R.id.et_ifsc_code);
        accountNumber = findViewById(R.id.et_account_number);
        address = findViewById(R.id.et_bank_address);
        accountHolder = findViewById(R.id.et_account_holder);

        user = (BillUser) Utility.readObject(GenericBankDetailsActivity.this, Utility.USER_KEY);
        if (user != null && user.getFinancialDetails() != null) {
            bankName.setText(user.getFinancialDetails().getBankName());
            ifscCode.setText(user.getFinancialDetails().getIfscCode());
            accountNumber.setText(user.getFinancialDetails().getAccountNumber());
            address.setText(user.getFinancialDetails().getBankAddress());
            accountHolder.setText(user.getFinancialDetails().getAccountHolderName());
        }


    }

    /**
     * Validates the bank info details before saving them
     */
    private void validateBankInfoFields() {
        if (user == null) {
            user = new BillUser();
        }

        if (user.getFinancialDetails() == null) {
            user.setFinancialDetails(new BillFinancialDetails());
        }
        if (!bankName.getText().toString().equals("") && !accountNumber.getText().toString().equals("") && !address.getText().toString().equals("") && !ifscCode.getText().toString().equals("") && !accountHolder.getText().toString().equals("")) {
            if (accountNumber.getText().toString().length() < 10) {
                accountNumber.setError("Enter valid A/C number");
            } else if (ifscCode.getText().toString().length() < 11 && ifscCode.getText().toString().length() > 11) {
                ifscCode.setError("enter valid ifsc code");
            } else {
                user.getFinancialDetails().setBankName(bankName.getText().toString());
                user.getFinancialDetails().setAccountNumber(accountNumber.getText().toString());
                user.getFinancialDetails().setBankAddress(address.getText().toString());
                user.getFinancialDetails().setIfscCode(ifscCode.getText().toString());
                user.getFinancialDetails().setAccountHolderName(accountHolder.getText().toString());
                saveBankDetails();

            }
        } else {
            if (bankName.getText().toString().equals("")) {
                bankName.setError("Enter bank name");
            } else if (accountNumber.getText().toString().equals("")) {
                accountNumber.setError("Enter bank accountNumber");
            } else if (address.getText().toString().equals("")) {
                address.setError("Enter bank address");
            } else if (ifscCode.getText().toString().equals("")) {
                ifscCode.setError("Enter bank ifscCode");
            } else if (accountHolder.getText().toString().equals("")) {
                accountHolder.setFocusable(true);
                accountHolder.setError("Enter accountHolder");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_bank_info_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.save_menu_item) {
            Log.d(TAG, "onOptionsItemSelected: Save bank info");
            validateBankInfoFields();
            return super.onOptionsItemSelected(item);
        } else {
            return Utility.backDefault(item, GenericBankDetailsActivity.this);
        }


    }

    private void saveBankDetails() {
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(user);

        pDialog = Utility.getProgressDialogue("Saving Bank Details..", GenericBankDetailsActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateBankDetails", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, GenericBankDetailsActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericBankDetailsActivity.this);
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
//                    Utility.createAlertWithActivityFinish(GenericBankDetailsActivity.this, "Bank details updated successfully!", "Done", null, null, null, null);
                    Utility.writeObject(GenericBankDetailsActivity.this, Utility.USER_KEY, user);

                    Toast.makeText(GenericBankDetailsActivity.this, "Bank details updated successfully!", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
//                    Utility.createAlert(GenericBankDetailsActivity.this, serviceResponse.getResponse(), "Error");
                    Toast.makeText(GenericBankDetailsActivity.this, "Error: " + serviceResponse.getResponse(), Toast.LENGTH_SHORT).show();
                }


            }

        };
    }

}
