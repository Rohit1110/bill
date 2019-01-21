package com.reso.bill.generic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.EditInvoiceActivity;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.math.BigDecimal;

import adapters.generic.GenericInvoiceItemsDisplayAdapter;
import util.ServiceUtil;
import util.Utility;

public class GenericBillDisplayActivity extends AppCompatActivity {

    private BillInvoice invoice;
    private BillUser user;
    private BillUser customer;

    private ProgressDialog pDialog;
    private static final int MENU_ITEM_EDIT = 1;
    private TextView customerPhone, noProductsAvailableTextView, payable, credit, outstanding, pending, serviceCharge, noOfReminders, paidTime, billAmount, invoicePurpose;
    private ImageView paidIcon, invoiceSeenStatus, reminders;
    private GenericInvoiceItemsDisplayAdapter adapter;
    private RecyclerView recyclerView;
    private ConstraintLayout serviceChargeLayout, pendingLayout, outstandingLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_bill_display);

        customer = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.CUSTOMER_KEY);
        if (customer != null) {
            invoice = customer.getCurrentInvoice();
        }

        Utility.setActionBar("Invoice # " + invoice.getId(), getSupportActionBar());

        customerPhone = findViewById(R.id.txt_gn_bill_display_customer_info);
        invoicePurpose = findViewById(R.id.txt_gn_bill_display_invoice_purpose);
        payable = findViewById(R.id.txt_gn_bill_display_total_payable);
        paidIcon = findViewById(R.id.img_bill_display_invoice_status);
        paidTime = findViewById(R.id.txt_bill_display_invoice_paid_time);
        invoiceSeenStatus = findViewById(R.id.img_bill_display_bill_seen_status);
        noOfReminders = findViewById(R.id.txt_customer_reminder_count);
        recyclerView = findViewById(R.id.recycler_bill_display_invoice_items);
        serviceCharge = findViewById(R.id.txt_gn_bill_display_service_charge);
        pending = findViewById(R.id.txt_gn_bill_display_pending_balance);
        outstanding = findViewById(R.id.txt_gn_bill_display_outstanding);
        billAmount = findViewById(R.id.txt_gn_bill_display_total);
        reminders = findViewById(R.id.img_users_icon);
        noProductsAvailableTextView = findViewById(R.id.noProductsAvailableTextView);
        serviceChargeLayout = findViewById(R.id.serviceChargeConstraintLayout);
        pendingLayout = findViewById(R.id.pendingBalanceConstraintLayout);
        outstandingLayout = findViewById(R.id.outstandingConstraintLayout);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.share, menu);

        //Only if invoice is not paid
        if (invoice == null || invoice.getId() == null || invoice.getStatus() == null || !invoice.getStatus().equals(BillConstants.INVOICE_STATUS_PAID)) {
            menu.add(Menu.NONE, MENU_ITEM_EDIT, Menu.NONE, "Edit").setIcon(R.drawable.ic_edit_blue_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_share:
                showShareOptions();
                return true;
            case android.R.id.home:
                //Back button click
                finish();
                return true;
            case MENU_ITEM_EDIT:
//                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();;
                //startActivity(Utility.nextIntent(this, GenericCreateBillActivity.class, true, customer, Utility.CUSTOMER_KEY));
                boolean isQuickBill = getIntent().getBooleanExtra(Utility.INTENT_QUICK_BILL, false);

                if (BillConstants.FRAMEWORK_GENERIC.equals(Utility.getFramework(user)) || (isQuickBill)) {
                    customer.setCurrentInvoice(invoice);
                    //Utility.nextFragment((FragmentActivity) activity, GenericCreateBill.newInstance(customer));
                    startActivity(Utility.nextIntent(this, GenericCreateBillActivity.class, true, customer, Utility.CUSTOMER_KEY));
                } else {
                    //Utility.nextFragment((FragmentActivity) activity, FragmentEditInvoice.newInstance(customer, invoice));
                    Intent intent = Utility.nextIntent(this, EditInvoiceActivity.class, true, invoice, Utility.INVOICE_KEY);
                    intent.putExtra(Utility.CUSTOMER_KEY, ServiceUtil.toJson(customer));
                    startActivity(intent);
                }

                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareInvoice();
    }

    private void prepareInvoice() {
        if (customer.getName() != null) {
            customerPhone.setText(customer.getName());
        } else {
            customerPhone.setText(customer.getPhone());
        }
        String purpose = "";
        if (invoice.getMonth() != null && invoice.getYear() != null) {
            purpose = BillConstants.MONTHS[invoice.getMonth() - 1] + " " + invoice.getYear();
        } else {
            purpose = CommonUtils.convertDate(invoice.getInvoiceDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR);
        }
        invoicePurpose.setText(purpose);
        payable.setText(Utility.getDecimalString(invoice.getPayable()));
        if (invoice.getOutstandingBalance() != null) {
            outstanding.setVisibility(View.VISIBLE);
            outstanding.setText(Utility.getDecimalString(invoice.getOutstandingBalance()));
        } else {
            outstandingLayout.setVisibility(View.GONE);
        }

        if (invoice.getServiceCharge() != null && invoice.getServiceCharge().compareTo(BigDecimal.ZERO) > 0) {
            serviceCharge.setVisibility(View.VISIBLE);
            serviceCharge.setText(Utility.getDecimalString(invoice.getServiceCharge()));
        } else {
            serviceChargeLayout.setVisibility(View.GONE);
        }
        if (invoice.getPendingBalance() != null && invoice.getPendingBalance().compareTo(BigDecimal.ZERO) > 0) {
            pending.setVisibility(View.VISIBLE);
            pending.setText(Utility.getDecimalString(invoice.getPendingBalance()));
        } else {
            pendingLayout.setVisibility(View.GONE);
        }

        if (invoice.getStatus() != null && BillConstants.INVOICE_STATUS_PAID.equals(invoice.getStatus())) {
            paidIcon.setImageResource(R.drawable.ic_invoice_paid);
            Utility.showHelpfulToast(paidIcon, "Invoice Paid", this);
        } else if (invoice.getStatus() != null && "Settled".equals(invoice.getStatus())) {
            paidIcon.setImageResource(R.drawable.ic_txn_settled);
            Utility.showHelpfulToast(paidIcon, "Invoice Paid and Settled", this);
        } else if (invoice.getStatus() != null && "Failed".equals(invoice.getStatus())) {
            paidIcon.setImageResource(R.drawable.ic_invoice_failed);
            Utility.showHelpfulToast(paidIcon, "Invoice Failed", this);
        } else {
            Utility.showHelpfulToast(paidIcon, "Invoice Pending", this);
        }

        if (invoice.getPaidDate() != null) {
            paidTime.setVisibility(View.VISIBLE);
            paidTime.setText(CommonUtils.convertDate(invoice.getPaidDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR_TIME));
        }

        Integer noOfReminders = invoice.getNoOfReminders();
        if (noOfReminders == null) {
            noOfReminders = 0;
        }

        this.noOfReminders.setText(noOfReminders.toString());

        Utility.showHelpfulToast(reminders, "You have sent " + noOfReminders + " reminders to this customer so far", this);

        if (invoice.getPaymentAttempt() != null && invoice.getPaymentAttempt() > 0) {
            invoiceSeenStatus.setImageResource(R.drawable.ic_action_invoice_seen);
            Utility.showHelpfulToast(invoiceSeenStatus, "Invoice seen by the customer", this);
        } else {
            invoiceSeenStatus.setImageResource(R.drawable.ic_action_invoice_unseen);
            Utility.showHelpfulToast(invoiceSeenStatus, "Invoice not yet seen by the customer", this);
        }

        if (invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            adapter = new GenericInvoiceItemsDisplayAdapter(invoice.getInvoiceItems(), this, billAmount);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } else {
            billAmount.setVisibility(View.GONE);
            noProductsAvailableTextView.setVisibility(View.VISIBLE);
        }


    }

    private void showShareOptions() {
        if (invoice == null || invoice.getId() == null || TextUtils.isEmpty(invoice.getPaymentMessage())) {
            Utility.createAlert(this, "Please save the invoice before sharing", "Error");
            return;
        }

        final CharSequence[] items = {"SMS/Email", "WhatsApp", "Other"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Share invoice with");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                System.out.println("Selected option => " + item);
                switch (item) {
                    case 0:
                        sendCustomerBill();
                        break;
                    case 1:
                        shareViaWhatsapp(invoice.getPaymentMessage());
                        break;
                    case 2:
                        shareIt();
                        break;
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void shareIt() {
        if (invoice == null || TextUtils.isEmpty(invoice.getPaymentMessage())) {
            Utility.createAlert(this, "Please save the invoice first before sharing", "Error");
            return;
        }
        //Toast.makeText(GenericCreateBillActivity.this,"Click",Toast.LENGTH_LONG).show();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, invoice.getPaymentMessage());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void shareViaWhatsapp(String serviceResponse) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + customerPhone.getText().toString() + "?body=" + serviceResponse));
        //whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        //whatsappIntent.putExtra(Intent.EXTRA_TEXT, serviceResponse.getResponse());
        try {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String whatsappNumber = customerPhone.getText().toString();
            if (whatsappNumber != null && !whatsappNumber.contains("+91")) {
                whatsappNumber = "+91" + whatsappNumber;
            }
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + whatsappNumber + "&text=" + serviceResponse));

            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            System.out.println("No whatsapp!!!" + ex);
        }
    }

    private void sendCustomerBill() {
        if (invoice == null || invoice.getId() == null) {
            Utility.createAlert(this, "Please save the invoice first!", "Error");
            return;
        }
        BillServiceRequest request = new BillServiceRequest();
        request.setRequestType("EMAIL");
        request.setInvoice(invoice);
        pDialog = Utility.getProgressDialogue("Sending Invoice..", this);
        StringRequest myReq = ServiceUtil.getStringRequest("sendInvoice", invoiceSendResponse(), ServiceUtil.createMyReqErrorListener(pDialog, GenericBillDisplayActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
    }

    private Response.Listener<String> invoiceSendResponse() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();
                final BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    //Utility.createAlert(getContext(), "Invoice sent to customer successfully!", "Done");

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GenericBillDisplayActivity.this);
                    alertDialogBuilder.setTitle("Success");
                    alertDialogBuilder.setMessage("Invoice sent to customer via Email/ SMS successfully! Do you want to share it via WhatsApp?");
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            shareViaWhatsapp(serviceResponse.getResponse());
                        }
                    });
                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericBillDisplayActivity.this, serviceResponse.getResponse(), "Error");
                }

            }

        };
    }
}
