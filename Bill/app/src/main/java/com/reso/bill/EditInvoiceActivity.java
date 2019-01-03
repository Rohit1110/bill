package com.reso.bill;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapters.BillDetailsEditAdapter;
import util.ServiceUtil;
import util.Utility;

public class EditInvoiceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnpay;
    private Spinner monthspinner;
    private BillUser customer;
    private BillInvoice invoice;
    private BillUser vendor;
    private ProgressDialog pDialog;
    private String[] monthsArray;
    private List<String> yearsList;
    private Spinner yearsSpinner;
    private EditText amount, serviceCharge, credit, pending;
    //private Button save;
    //private Button send;
    private Spinner invoiceStatusSpinner;
    private List<String> statusList;
    private TextView payableAmount;
    private BillDetailsEditAdapter billDetailsEditAdapter;
    private TextView invoiceNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_invoice);

        invoice = (BillInvoice) Utility.getIntentObject(BillInvoice.class, getIntent(), Utility.INVOICE_KEY);
        if (invoice == null) {
            invoice = new BillInvoice();
        }
        customer = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.CUSTOMER_KEY);

        String id = "";
        if (invoice.getId() != null) {
            id = invoice.getId() + "";
        }
        Utility.setActionBar("Invoice #" + id, getSupportActionBar());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_bill_two);
        //btnpay = (Button) findViewById(R.id.btn_pay);

        monthspinner = (Spinner) findViewById(R.id.spn_months);
        monthsArray = getResources().getStringArray(R.array.months_arrays);
        monthspinner.setPrompt("month");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditInvoiceActivity.this, R.layout.spinner_basic_text_white, monthsArray);
        monthspinner.setAdapter(adapter);

        yearsSpinner = (Spinner) findViewById(R.id.spn_year);
        monthspinner.setPrompt("year");
        yearsList = Utility.createYearsArray();
        if (invoice == null || invoice.getId() == null) {
            yearsList.add("Select Year");
        }

        yearsSpinner.setAdapter(new ArrayAdapter<String>(EditInvoiceActivity.this, R.layout.spinner_basic_text_white, yearsList));

        /*btnpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });*/

        payableAmount = (TextView) findViewById(R.id.txt_invoice_payable);

        amount = (EditText) findViewById(R.id.et_bill_details_amount);
        amount.setEnabled(true);

        serviceCharge = (EditText) findViewById(R.id.et_bill_details_service_charge);
        serviceCharge.setEnabled(true);

        //save = (Button) findViewById(R.id.fab_save_invoice);
        /*save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*if (yearsSpinner.getSelectedItemPosition() > 0) {

                    int selectedItemOfMySpinner = yearsSpinner.getSelectedItemPosition();

                    String actualPositionOfMySpinner = (String) yearsSpinner.getItemAtPosition(selectedItemOfMySpinner);

                    if (actualPositionOfMySpinner.isEmpty()) {
                        Utility.createAlert(EditInvoiceActivity.this, "Please select a year!", "Error");
                        return;
                    }
                } else {
                    Utility.createAlert(EditInvoiceActivity.this, "Please select a year!", "Error");
                    return;
                }
                if (monthspinner.getSelectedItemPosition() > 0) {
                    int selectedItemOfMonthSpinner = monthspinner.getSelectedItemPosition();
                    String selectedItemOfMonthsSpinner = (String) monthspinner.getItemAtPosition(selectedItemOfMonthSpinner);

                    if (selectedItemOfMonthsSpinner.isEmpty()) {
                        Utility.createAlert(EditInvoiceActivity.this, "Please select a month!", "Error");
                        return;
                    }
                } else {
                    Utility.createAlert(EditInvoiceActivity.this, "Please select a month!", "Error");
                    return;
                }*//*
                saveInvoice();
            }
        });*/

        /*send = (Button) findViewById(R.id.btn_send_invoice);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvoice();
            }
        });*/

        vendor = (BillUser) Utility.readObject(EditInvoiceActivity.this, Utility.USER_KEY);

        invoiceStatusSpinner = (Spinner) findViewById(R.id.spn_invoice_status);
        prepareStatuses();
        invoiceStatusSpinner.setAdapter(new ArrayAdapter<String>(EditInvoiceActivity.this, R.layout.spinner_basic_text, statusList));

        credit = (EditText) findViewById(R.id.et_bill_details_credit_amount);
        pending = (EditText) findViewById(R.id.et_bill_details_pending_amount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        menu.add(Menu.NONE, Utility.MENU_ITEM_SAVE, Menu.NONE, "Save").setIcon(R.drawable.ic_check_blue_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareOptions();
                return true;
            case android.R.id.home:
                finish();
                return true;
            case Utility.MENU_ITEM_SAVE:
//                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();;
                saveInvoice();
                return true;

        }
        return false;
    }

    private void shareOptions() {
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
                        sendInvoice();
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

    private void shareViaWhatsapp(String paymentMessage) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + customer.getPhone() + "?body=" + paymentMessage));
        //whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        //whatsappIntent.putExtra(Intent.EXTRA_TEXT, serviceResponse.getResponse());
        try {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String whatsappNumber = customer.getPhone();
            if (whatsappNumber != null && !whatsappNumber.contains("+91")) {
                whatsappNumber = "+91" + whatsappNumber;
            }
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + whatsappNumber + "&text=" + paymentMessage));

            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            System.out.println("No whatsapp!!!" + ex);
        }
    }

    private void shareIt() {
        if (invoice == null || TextUtils.isEmpty(invoice.getPaymentMessage())) {
            return;
        }
        //Toast.makeText(EditInvoiceActivity.this,"Click",Toast.LENGTH_LONG).show();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, invoice.getPaymentMessage());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void populateData() {
        if (invoice.getAmount() != null) {
            amount.setText(invoice.getAmount().toString());
        }

        if (invoice.getServiceCharge() != null) {
            serviceCharge.setText(invoice.getServiceCharge().toString());
        } else if (customer.getServiceCharge() != null) {
            serviceCharge.setText(customer.getServiceCharge().toString());
        }


        if (invoice.getCreditBalance() != null) {
            credit.setText(invoice.getCreditBalance().toString());
        }
        if (invoice.getPendingBalance() != null) {
            pending.setText(invoice.getPendingBalance().toString());
        }


        if (invoice.getStatus() != null) {
            int position = statusList.indexOf(invoice.getStatus());
            if (position < 0) {
                invoiceStatusSpinner.setSelection(0);
            } else {
                invoiceStatusSpinner.setSelection(position);
            }
        }


        if (invoice.getYear() != null) {
            yearsSpinner.setSelection(yearsList.indexOf(invoice.getYear().toString()));
            yearsSpinner.setEnabled(false);
        }
        if (invoice.getMonth() != null) {
            monthspinner.setSelection(invoice.getMonth());
            monthspinner.setEnabled(false);
        }


        if (invoice.getAmount() != null) {
            payableAmount.setText(invoice.getAmount().toString());
            calculatePayable();
        } else {
            payableAmount.setText("0.00");
        }

        /*if(invoice.getId() != null) {
            invoiceNumber.setText("Invoice #" + invoice.getId());
        }*/

        //Watchers

        amount.addTextChangedListener(watcher);
        serviceCharge.addTextChangedListener(watcher);
        credit.addTextChangedListener(watcher);
        pending.addTextChangedListener(watcher);


    }

    private void calculatePayable() {
        try {
            BigDecimal payableAmountValue = new BigDecimal(payableAmount.getText().toString());
            if (!TextUtils.isEmpty(amount.getText())) {
                payableAmountValue = new BigDecimal(amount.getText().toString());
            }
            if (!TextUtils.isEmpty(serviceCharge.getText())) {
                payableAmountValue = payableAmountValue.add(new BigDecimal(serviceCharge.getText().toString()));
            }
            if (!TextUtils.isEmpty(pending.getText())) {
                payableAmountValue = payableAmountValue.add(new BigDecimal(pending.getText().toString()));
            }
            if (!TextUtils.isEmpty(credit.getText())) {
                payableAmountValue = payableAmountValue.subtract(new BigDecimal(credit.getText().toString()));
            }
            payableAmount.setText(payableAmountValue.toString());
        } catch (Exception e) {
            System.err.println("Error in input ..");
            payableAmount.setText("0.00");
        }

    }

    private void prepareStatuses() {
        statusList = new ArrayList<>();
        statusList.add("Pending");
        statusList.add("Credit");
        statusList.add("Failed");
    }

    @Override
    public void onResume() {
        super.onResume();

        populateData();

        if (customer.getCurrentSubscription() == null || customer.getCurrentSubscription().getItems() == null || customer.getCurrentSubscription().getItems().size() == 0) {
            Utility.createAlert(EditInvoiceActivity.this, "Add products to this customer first!", "Error");
            return;
        }

        for (BillItem item : customer.getCurrentSubscription().getItems()) {
            if (invoice.getInvoiceItems() != null) {
                for (BillItem invoiceItem : invoice.getInvoiceItems()) {
                    if (invoiceItem.getParentItemId() != null && item.getId() != null && invoiceItem.getParentItemId().intValue() == item.getId().intValue()) {
                        item.setQuantity(invoiceItem.getQuantity());
                        item.setPrice(invoiceItem.getPrice());
                    }
                }
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(EditInvoiceActivity.this));
        billDetailsEditAdapter = new BillDetailsEditAdapter(customer.getCurrentSubscription().getItems(), EditInvoiceActivity.this, amount);
        BillDetailsEditAdapter adapter = billDetailsEditAdapter;
        recyclerView.setAdapter(adapter);
        adapter.setRecyclerView(recyclerView);

        System.out.println("Invoice amount ==>" + amount.getText() + " -- " + invoice.getAmount());
    }

    private void saveInvoice() {
        if (invoice.getYear() == null || invoice.getMonth() == null) {
            invoice.setMonth(getMonth());
            if (yearsSpinner.getSelectedItemPosition() > 0 && yearsSpinner.getSelectedItem() != null && yearsSpinner.getSelectedItem().toString().trim().length() > 0) {
                invoice.setYear(new Integer(yearsSpinner.getSelectedItem().toString()));
            } else {
                Utility.createAlert(EditInvoiceActivity.this, "Please select an year for the invoice", "Error");
            }
            if (invoice.getMonth() == null) {
                Utility.createAlert(EditInvoiceActivity.this, "Please select a month for the invoice", "Error");
                return;
            }
        }


        //if (invoice.getAmount() == null) {
        if (amount.getText() == null || amount.getText().toString().trim().length() == 0) {
            Utility.createAlert(EditInvoiceActivity.this, "Please enter invoice amount", "Error");
            return;
        }
        invoice.setAmount(new BigDecimal(amount.getText().toString()));

        if (!TextUtils.isEmpty(serviceCharge.getText())) {
            invoice.setServiceCharge(new BigDecimal(serviceCharge.getText().toString()));
        }

        if (!TextUtils.isEmpty(pending.getText())) {
            invoice.setPendingBalance(new BigDecimal(pending.getText().toString()));
        }


        if (!TextUtils.isEmpty(credit.getText())) {
            invoice.setCreditBalance(new BigDecimal(credit.getText().toString()));
        }

        invoice.setStatus(invoiceStatusSpinner.getSelectedItem().toString());
        //}
        BillServiceRequest request = new BillServiceRequest();
        invoice.setInvoiceItems(getInvoiceItems());
        request.setInvoice(invoice);
        request.setUser(customer);
        pDialog = Utility.getProgressDialogue("Saving..", EditInvoiceActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerInvoice", invoiceUpdateResponse(), ServiceUtil.createMyReqErrorListener(pDialog, EditInvoiceActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(EditInvoiceActivity.this);
        queue.add(myReq);
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            calculatePayable();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private Integer getMonth() {
        int index = Arrays.asList(monthsArray).indexOf(monthspinner.getSelectedItem());
        if (index > 0) {
            return (index);
        }
        return null;
    }


    private List<BillItem> getInvoiceItems() {
        List<BillItem> invoiceItems = new ArrayList<>();
        int index = 0;
        for (BillItem item : customer.getCurrentSubscription().getItems()) {
            BigDecimal price = billDetailsEditAdapter.getPrice(index);
            BigDecimal quantity = billDetailsEditAdapter.getQuantity(index);
            if (price != null && quantity != null) {
                item.setPrice(price);
                item.setQuantity(quantity);
                invoiceItems.add(item);
            }
            index++;
        }
        return invoiceItems;
    }

    private Response.Listener<String> invoiceUpdateResponse() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (serviceResponse.getInvoice() != null) {
                        invoice = serviceResponse.getInvoice();
                    }
                    Utility.createAlert(EditInvoiceActivity.this, "Invoice updated successfully!", "Done");
                    //Utility.nextFragment(EditInvoiceActivity.this, FragmentCustomerInvoices.newInstance(customer));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(EditInvoiceActivity.this, serviceResponse.getResponse(), "Error");
                }

            }

        };
    }

    private void sendInvoice() {
        if (invoice.getId() == null) {
            Utility.createAlert(EditInvoiceActivity.this, "Please save the invoice first!", "Error");
            return;
        }
        BillServiceRequest request = new BillServiceRequest();
        request.setRequestType("EMAIL");
        request.setInvoice(invoice);
        pDialog = Utility.getProgressDialogue("Sending Invoice..", EditInvoiceActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("sendInvoice", invoiceSendResponse(), ServiceUtil.createMyReqErrorListener(pDialog, EditInvoiceActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(EditInvoiceActivity.this);
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
                    //Utility.createAlert(EditInvoiceActivity.this, "Invoice sent to customer successfully!", "Done");


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditInvoiceActivity.this);
                    alertDialogBuilder.setTitle("Success");
                    alertDialogBuilder.setMessage("Invoice sent to customer via Email/ SMS successfully! Do you want to share it via WhatsApp?");
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
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
                    Utility.createAlert(EditInvoiceActivity.this, serviceResponse.getResponse(), "Error");
                }

            }

        };
    }
}
