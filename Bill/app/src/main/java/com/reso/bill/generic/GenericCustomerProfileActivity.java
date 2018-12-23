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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.CustomerOrderHistoryActivity;
import com.reso.bill.CustomerSubscriptionsActivity;
import com.reso.bill.EditInvoiceActivity;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.List;

import adapters.CustomerInvoiceAdapter;
import util.ServiceUtil;
import util.Utility;

public class GenericCustomerProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BillUser selectedCustomer;
    private TextView name, email, phone, address, billsDue, lastPaid;
    private View billSummary;
    private TextView billDetails, addNewBill;
    private ImageView editProfile, call, btn_manage_subscriptions, btn_view_customer_activities;

    private ProgressDialog pDialog;
    private BillUser user;
    private List<BillInvoice> invoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_customer_profile);

        Utility.setActionBar("Customer's Profile", getSupportActionBar());

        name = findViewById(R.id.txt_profile_customer_name);
        email = findViewById(R.id.txt_profile_customer_email);
        phone = findViewById(R.id.txt_profile_customer_phone);
        address = findViewById(R.id.txt_profile_customer_address);
        editProfile = findViewById(R.id.btn_edit_profile);
        billsDue = findViewById(R.id.txt_profile_bills_due);
        lastPaid = findViewById(R.id.txt_profile_last_paid_bill);
        call = findViewById(R.id.img_profile_call_customer);
        btn_manage_subscriptions = findViewById(R.id.btn_manage_subscriptions);
        btn_view_customer_activities = findViewById(R.id.btn_view_customer_activities);

        user = (BillUser) Utility.readObject(GenericCustomerProfileActivity.this, Utility.USER_KEY);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.nextFragment(GenericCustomerProfileActivity.this, CustomerInfo.newInstance(selectedCustomer));
                startActivity(Utility.nextIntent(GenericCustomerProfileActivity.this, GenericCustomerInfoActivity.class, true, selectedCustomer, Utility.CUSTOMER_KEY));
            }
        });
        editProfile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(GenericCustomerProfileActivity.this, "Edit Profile", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        btn_manage_subscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Utility.nextIntent(GenericCustomerProfileActivity.this, CustomerSubscriptionsActivity.class, true, selectedCustomer, Utility.CUSTOMER_KEY));
            }
        });
        btn_manage_subscriptions.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(GenericCustomerProfileActivity.this, "Manage Subscriptions", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        btn_view_customer_activities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.nextFragment(GenericCustomerProfileActivity.this, ActivityScreen.newInstance(selectedCustomer));
                startActivity(Utility.nextIntent(GenericCustomerProfileActivity.this, CustomerOrderHistoryActivity.class, true, selectedCustomer, Utility.CUSTOMER_KEY));
            }
        });
        btn_view_customer_activities.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(GenericCustomerProfileActivity.this, "View Customer Activity", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        billSummary = findViewById(R.id.layout_profile_customer_bill_summary);
        billSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.nextFragment(GenericCustomerProfileActivity.this, FragmentCustomerInvoices.newInstance(selectedCustomer));
                startActivity(Utility.nextIntent(GenericCustomerProfileActivity.this, GenericCustomerInvoicesActivity.class, true, selectedCustomer, Utility.CUSTOMER_KEY));
            }
        });

        billDetails = findViewById(R.id.btn_view_customer_bills);

        billDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Utility.nextIntent(GenericCustomerProfileActivity.this, GenericCustomerInvoicesActivity.class, true, selectedCustomer, Utility.CUSTOMER_KEY));

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
        call.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(GenericCustomerProfileActivity.this, "Call Customer", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        addNewBill = findViewById(R.id.btn_add_new_bill);
        addNewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BillConstants.FRAMEWORK_GENERIC.equals(Utility.getFramework(user))) {
                    //customer.setCurrentInvoice(invoice);
                    //Utility.nextFragment((FragmentActivity) activity, GenericCreateBill.newInstance(customer));
                    startActivity(Utility.nextIntent(GenericCustomerProfileActivity.this, GenericCreateBillActivity.class, true));
                } else {

                    //Utility.nextFragment((FragmentActivity) activity, FragmentEditInvoice.newInstance(customer, invoice));
                    startActivity(Utility.nextIntent(GenericCustomerProfileActivity.this, EditInvoiceActivity.class, true, selectedCustomer, Utility.CUSTOMER_KEY));
                }
            }
        });

        if (!BillConstants.FRAMEWORK_RECURRING.equals(Utility.getFramework(user))) {
            btn_manage_subscriptions.setVisibility(View.GONE);
            btn_view_customer_activities.setVisibility(View.GONE);
        }

        recyclerView = findViewById(R.id.recycler_view_customer_pending_bills);

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

            if (TextUtils.isEmpty(name.getText())) {
                name.setText("No name");
            }
            if (TextUtils.isEmpty(email.getText())) {
                email.setText("No email");
            }

            if (TextUtils.isEmpty(address.getText())) {
                address.setText("No address");
            }

            if (selectedCustomer.getCurrentSubscription() != null) {
                billsDue.setText("Bills Due: " + selectedCustomer.getCurrentSubscription().getBillsDue());
                if (selectedCustomer.getCurrentSubscription().getLastBillPaid() != null) {
                    lastPaid.setText("Last paid on: " + CommonUtils.convertDate(selectedCustomer.getCurrentSubscription().getLastBillPaid()));
                } else {
                    lastPaid.setText("No payment found");
                }
            } else {
                lastPaid.setText("No payment found");
            }

            if (invoices != null && invoices.size() > 0) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                CustomerInvoiceAdapter adapter = new CustomerInvoiceAdapter(invoices, this, selectedCustomer);
                adapter.setUser(user);
                recyclerView.setAdapter(adapter);
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
                    invoices = serviceResponse.getInvoices();
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
