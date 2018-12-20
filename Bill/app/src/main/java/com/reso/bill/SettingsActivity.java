package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import util.ServiceUtil;
import util.Utility;

public class SettingsActivity extends AppCompatActivity {

    private Button saveSettings;
    private CheckBox hideBillDetails;
    private BillUser user;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Utility.setActionBar("Settings", getSupportActionBar());

        saveSettings = (Button) findViewById(R.id.btn_save_settings);
        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }


        });
        hideBillDetails = (CheckBox) findViewById(R.id.chk_settings_bill_hide);
        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);

        if (user.getCurrentBusiness().getShowBillDetails() != null && "N".equals(user.getCurrentBusiness().getShowBillDetails())) {
            hideBillDetails.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, this);
    }

    private void saveSettings() {
        BillServiceRequest request = new BillServiceRequest();

        BillUser requestUser = new BillUser();
        requestUser.setId(user.getId());
        requestUser.setPhone(user.getPhone());
        BillBusiness requestBusiness = new BillBusiness();
        requestBusiness.setId(user.getCurrentBusiness().getId());
        if (hideBillDetails.isChecked()) {
            requestBusiness.setShowBillDetails("N");
            user.getCurrentBusiness().setShowBillDetails("N");
        } else if (user.getCurrentBusiness().getShowBillDetails() != null) {
            requestBusiness.setShowBillDetails("Y");
            user.getCurrentBusiness().setShowBillDetails("Y");
        }
        requestUser.setCurrentBusiness(requestBusiness);
        request.setUser(requestUser);
        pDialog = Utility.getProgressDialogue("Saving ..", this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateUserProfile", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
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
                    Utility.createAlertWithActivityFinish(SettingsActivity.this, "Settings updated successfully!", "Done", null, null, null, null);
                    Utility.writeObject(SettingsActivity.this, Utility.USER_KEY, user);
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(SettingsActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }
}
