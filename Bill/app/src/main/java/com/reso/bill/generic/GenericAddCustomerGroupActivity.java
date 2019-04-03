package com.reso.bill.generic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillCustomerGroup;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import util.ServiceUtil;
import util.Utility;

public class GenericAddCustomerGroupActivity extends AppCompatActivity {

    private static final int MENU_ITEM_SAVE = 1;
    private EditText groupName;
    private EditText groupDescription;
    private BillUser user;
    private BillCustomerGroup customerGroup;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_add_customer_group);

        Utility.setActionBar("Update Group Information", getSupportActionBar());

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);
        customerGroup = (BillCustomerGroup) Utility.getIntentObject(BillCustomerGroup.class, getIntent(), Utility.GROUP_KEY);

        groupName = findViewById(R.id.et_group_name);
        groupDescription = findViewById(R.id.et_group_description);

        if (customerGroup != null) {
            groupName.setText(customerGroup.getGroupName());
            groupDescription.setText(customerGroup.getGroupDescription());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, "Save").setIcon(R.drawable.ic_check_blue_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                //Back button click
                finish();
                return true;
            case MENU_ITEM_SAVE:
//                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();;
                saveGroup();
                return true;
        }
        return false;
    }

    private void saveGroup() {
        if (TextUtils.isEmpty(groupName.getText())) {
            Toast.makeText(this, "Please enter a group name", Toast.LENGTH_LONG).show();
            return;
        }
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        if (customerGroup == null) {
            customerGroup = new BillCustomerGroup();
        }
        customerGroup.setGroupName(groupName.getText().toString());
        customerGroup.setGroupDescription(groupDescription.getText().toString());
        request.setCustomerGroup(customerGroup);
        pDialog = Utility.getProgressDialogue("Saving ..", this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerGroup", saveGroupListener(), ServiceUtil.createMyReqErrorListener(pDialog, this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
    }

    private Response.Listener<String> saveGroupListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlertWithActivityFinish(GenericAddCustomerGroupActivity.this, "Group saved successfully!", "Done", null, null, null, null);
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericAddCustomerGroupActivity.this, serviceResponse.getResponse(), "Error");
                }

            }

        };
    }
}
