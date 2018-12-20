package com.reso.bill.generic;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.ActivityScreen;
import com.reso.bill.CustomerSubscriptionsActivity;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import model.ListThree;
import util.ServiceUtil;
import util.Utility;

public class GenericCustomerProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BillUser selectedCustomer;
    private TextView name, email, phone, address, billsDue, lastPaid;
    private View manageSubscriptions, viewactivity, billSummary;
    private TextView editProfile, billDetails;
    private ImageView call;


    private List<ListThree> list = new ArrayList<>();
    private ProgressDialog pDialog;
    private BillUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_customer_profile);

        Utility.setActionBar("Customer Profile", getSupportActionBar());

        name = (TextView) findViewById(R.id.txt_profile_customer_name);
        email = (TextView) findViewById(R.id.txt_profile_customer_email);
        phone = (TextView) findViewById(R.id.txt_profile_customer_phone);
        address = (TextView) findViewById(R.id.txt_profile_customer_address);
        editProfile = (TextView) findViewById(R.id.btn_edit_profile);
        billsDue = (TextView) findViewById(R.id.txt_profile_bills_due);
        lastPaid = (TextView) findViewById(R.id.txt_profile_last_paid_bill);
        call = (ImageView) findViewById(R.id.img_profile_call_customer);

        user = (BillUser) Utility.readObject(GenericCustomerProfileActivity.this, Utility.USER_KEY);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.nextFragment(GenericCustomerProfileActivity.this, CustomerInfo.newInstance(selectedCustomer));
                startActivity(Utility.nextIntent(GenericCustomerProfileActivity.this, GenericCustomerInfoActivity.class, true, selectedCustomer, Utility.CUSTOMER_KEY));
            }
        });

        manageSubscriptions = (View) findViewById(R.id.layout_profile_customer_subscriptions);

        manageSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.nextFragment(GenericCustomerProfileActivity.this, AddSubcription.newInstance(selectedCustomer));
                startActivity(Utility.nextIntent(GenericCustomerProfileActivity.this, CustomerSubscriptionsActivity.class, true, selectedCustomer, Utility.CUSTOMER_KEY));
            }
        });


        billSummary = (View) findViewById(R.id.layout_profile_customer_bill_summary);
        billSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.nextFragment(GenericCustomerProfileActivity.this, FragmentCustomerInvoices.newInstance(selectedCustomer));
                startActivity(Utility.nextIntent(GenericCustomerProfileActivity.this, GenericCustomerInvoicesActivity.class, true, selectedCustomer, Utility.CUSTOMER_KEY));
            }
        });

        billDetails = (TextView) findViewById(R.id.btn_view_customer_bills);

        billDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Utility.nextIntent(GenericCustomerProfileActivity.this, GenericCustomerInvoicesActivity.class, true, selectedCustomer, Utility.CUSTOMER_KEY));

            }
        });
        viewactivity = (View) findViewById(R.id.layout_profile_customer_activities);
        viewactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(GenericCustomerProfileActivity.this, ActivityScreen.newInstance(selectedCustomer));
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCustomer != null) {

                    if (ContextCompat.checkSelfPermission(GenericCustomerProfileActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(GenericCustomerProfileActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, Utility.MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    } else {
                        //You already have permission
                        try {
                            callCustomer();
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        if (!BillConstants.FRAMEWORK_RECURRING.equals(Utility.getFramework(user))) {
            manageSubscriptions.setVisibility(View.GONE);
            viewactivity.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, this);
    }

    private void callCustomer() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + selectedCustomer.getPhone()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();

        selectedCustomer = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.CUSTOMER_KEY);
        setCustomerInfo();

        loadCustomerProfile();

    }

    private void setCustomerInfo() {
        if (selectedCustomer != null) {
            name.setText(selectedCustomer.getName());
            email.setText(selectedCustomer.getEmail());
            phone.setText(selectedCustomer.getPhone());
            address.setText(selectedCustomer.getAddress());

            if (selectedCustomer.getCurrentSubscription() != null) {
                billsDue.setText(selectedCustomer.getCurrentSubscription().getBillsDue() + " Bills Due");
                if (selectedCustomer.getCurrentSubscription().getLastBillPaid() != null) {
                    lastPaid.setText("Last paid on " + CommonUtils.convertDate(selectedCustomer.getCurrentSubscription().getLastBillPaid()));
                } else {
                    lastPaid.setText("No payment found");
                }
            } else {
                lastPaid.setText("No payment found");
            }

        } else {
            Utility.createAlert(GenericCustomerProfileActivity.this, "No Customer selected", "Error");
        }
    }

    //
    private void loadCustomerProfile() {
        BillServiceRequest request = new BillServiceRequest();
        BillUser user = new BillUser();
        if (selectedCustomer.getCurrentSubscription() != null) {
            user.setId(selectedCustomer.getCurrentSubscription().getId());
        } else {
            user.setId(selectedCustomer.getId());
        }
        request.setUser(user);
        pDialog = Utility.getProgressDialogue("Loading..", GenericCustomerProfileActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("getCustomerProfile", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, GenericCustomerProfileActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericCustomerProfileActivity.this);
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200 && serviceResponse.getUser() != null && serviceResponse.getUser().getCurrentSubscription() != null) {

                    selectedCustomer = serviceResponse.getUser();

                    setCustomerInfo();
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericCustomerProfileActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callCustomer();
                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
