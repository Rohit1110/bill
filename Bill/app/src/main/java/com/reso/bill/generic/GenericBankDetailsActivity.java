package com.reso.bill.generic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    //    private EditText;
    private TextInputLayout bankNameTextInputLayout, accountNumberTextInputLayout, accountHolderTextInputLayout, ifscTextInputLayout, bankAddressTextInputLayout;
    private TextInputEditText bankName, accountNumber, ifscCode, address, accountHolder;
    private BillUser user;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_bank_details);

        Utility.setActionBar("Edit Bank Info", getSupportActionBar());

        bankNameTextInputLayout = findViewById(R.id.bankNameTextInputLayout);
        accountNumberTextInputLayout = findViewById(R.id.accountNumberTextInputLayout);
        accountHolderTextInputLayout = findViewById(R.id.accountHolderTextInputLayout);
        ifscTextInputLayout = findViewById(R.id.ifscTextInputLayout);
        bankAddressTextInputLayout = findViewById(R.id.bankAddressTextInputLayout);

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

        bankNameListenersPlusValidationSetUp();
        accountNumberListenersPlusValidationSetUp();
        accountHolderListenersPlusValidationSetUp();
        ifscListenersPlusValidationSetUp();
        bankAddressListenersPlusValidationSetUp();
    }

    private void bankNameListenersPlusValidationSetUp() {
        bankNameTextInputLayout.setCounterEnabled(true);
        bankNameTextInputLayout.setCounterMaxLength(40);
        bankName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                bankNameEditTextValidation();
            }
        });

        bankName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bankNameEditTextValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void bankNameEditTextValidation() {
        if (bankName.getText().toString().trim().isEmpty()) {
            bankNameTextInputLayout.setErrorEnabled(true);
            bankNameTextInputLayout.setError("Please enter your bank name");
        } else if (!isAlpha(bankName.getText().toString().trim())) {
            bankNameTextInputLayout.setErrorEnabled(true);
            bankNameTextInputLayout.setError("Enter valid bank name. Enter letters only");
        } else {
            bankNameTextInputLayout.setErrorEnabled(false);
        }
    }

    private void accountNumberListenersPlusValidationSetUp() {
        accountNumberTextInputLayout.setCounterEnabled(true);
        accountNumberTextInputLayout.setCounterMaxLength(20);
        accountNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                accountNumberEditTextValidation();
            }
        });

        accountNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                accountNumberEditTextValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void accountNumberEditTextValidation() {
        if (accountNumber.getText().toString().trim().isEmpty()) {
            accountNumberTextInputLayout.setErrorEnabled(true);
            accountNumberTextInputLayout.setError("Please enter your account number");
        } else if (accountNumber.getText().toString().trim().length() < 10) {
            accountNumberTextInputLayout.setErrorEnabled(true);
            accountNumberTextInputLayout.setError("Account number should be at least 10 characters long");
        } else if (!accountNumber.getText().toString().trim().matches("[a-zA-Z0-9]+")) {
            accountNumberTextInputLayout.setErrorEnabled(true);
            accountNumberTextInputLayout.setError("Remove any spaces/special characters");
        } else {
            accountNumberTextInputLayout.setErrorEnabled(false);
        }
    }

    private void accountHolderListenersPlusValidationSetUp() {
        accountHolderTextInputLayout.setCounterEnabled(true);
        accountHolderTextInputLayout.setCounterMaxLength(40);
        accountHolder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                accountHolderEditTextValidation();
            }
        });

        accountHolder.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                accountHolderEditTextValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void accountHolderEditTextValidation() {
        if (accountHolder.getText().toString().trim().isEmpty()) {
            accountHolderTextInputLayout.setErrorEnabled(true);
            accountHolderTextInputLayout.setError("Please enter account holder name");
        } else {
            accountHolderTextInputLayout.setErrorEnabled(false);
        }
    }

    private void ifscListenersPlusValidationSetUp() {
        ifscTextInputLayout.setCounterEnabled(true);
        ifscTextInputLayout.setCounterMaxLength(11);
        ifscCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ifscEditTextValidation();
            }
        });

        ifscCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ifscEditTextValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void ifscEditTextValidation() {
        if (ifscCode.getText().toString().trim().isEmpty()) {
            ifscTextInputLayout.setErrorEnabled(true);
            ifscTextInputLayout.setError("Please enter bank IFSC");
        } else if (ifscCode.getText().toString().trim().length() != 11 || !ifscCode.getText().toString().trim().matches("[a-zA-Z0-9]+")) {
            ifscTextInputLayout.setErrorEnabled(true);
            ifscTextInputLayout.setError("IFSC should be 11 characters long");
        } else {
            ifscTextInputLayout.setErrorEnabled(false);
        }
    }

    private void bankAddressListenersPlusValidationSetUp() {
        bankAddressTextInputLayout.setCounterEnabled(true);
        bankAddressTextInputLayout.setCounterMaxLength(40);
        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                bankAddressEditTextValidation();
            }
        });

        address.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bankAddressEditTextValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void bankAddressEditTextValidation() {
        if (address.getText().toString().trim().isEmpty()) {
            bankAddressTextInputLayout.setErrorEnabled(true);
            bankAddressTextInputLayout.setError("Please enter your bank address");
        } else {
            bankAddressTextInputLayout.setErrorEnabled(false);
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
        if (!bankName.getText().toString().trim().isEmpty() &&
                !accountNumber.getText().toString().trim().isEmpty() &&
                !address.getText().toString().trim().isEmpty() &&
                !ifscCode.getText().toString().trim().isEmpty() &&
                !accountHolder.getText().toString().trim().isEmpty()) {
            
            
            
            if (accountNumber.getText().toString().trim().length() < 10) {
//                accountNumber.setError("Enter valid account number");
                accountNumberTextInputLayout.setErrorEnabled(true);
                accountNumberTextInputLayout.setError("Enter valid account number");
                return;
            }
            if (ifscCode.getText().toString().trim().length() != 11 || !ifscCode.getText().toString().trim().matches("[a-zA-Z0-9]+")) {
//                ifscCode.setError("Enter a valid IFSC code");
                ifscTextInputLayout.setErrorEnabled(true);
                ifscTextInputLayout.setError("Enter a valid IFSC code");
                return;
            }
            if (!isAlpha(bankName.getText().toString().trim())) {
//                bankName.setError("Enter a valid bank name. Only enter letters");
                bankNameTextInputLayout.setErrorEnabled(true);
                bankNameTextInputLayout.setError("Enter valid bank name. Enter letters only");
                bankName.requestFocus();
                return;
            }
            user.getFinancialDetails().setBankName(bankName.getText().toString());
            user.getFinancialDetails().setAccountNumber(accountNumber.getText().toString());
            user.getFinancialDetails().setBankAddress(address.getText().toString());
            user.getFinancialDetails().setIfscCode(ifscCode.getText().toString());
            user.getFinancialDetails().setAccountHolderName(accountHolder.getText().toString());
            saveBankDetails();


        } else {
            Toast.makeText(this, "Please fill every field before saving", Toast.LENGTH_SHORT).show();
//            if (bankName.getText().toString().isEmpty()) {
//                bankName.setError("Enter bank name");
//            } else if (accountNumber.getText().toString().isEmpty()) {
//                accountNumber.setError("Enter bank accountNumber");
//            } else if (address.getText().toString().isEmpty()) {
//                address.setError("Enter bank address");
//            } else if (ifscCode.getText().toString().isEmpty()) {
//                ifscCode.setError("Enter bank IFSC code");
//            } else if (accountHolder.getText().toString().isEmpty()) {
//                accountHolder.setFocusable(true);
//                accountHolder.setError("Enter accountHolder");
//            }
        }
    }

    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z ]+");
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
