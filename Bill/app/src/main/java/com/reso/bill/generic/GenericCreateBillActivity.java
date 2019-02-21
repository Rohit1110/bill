package com.reso.bill.generic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import util.ServiceUtil;
import util.Utility;

public class GenericCreateBillActivity extends AppCompatActivity {

    public static final String FINISH_BILL_ACTIVITY = "finish billActivity";
    private ProgressDialog pDialog;
    private static final int MENU_ITEM_SAVE = 1;
    private EditText customerPhone;
    private EditText customerEmail;
    private BillInvoice invoice;
    private BillUser user;
    private EditText billAmount;
    private CheckBox cashPayment;
    private BillUser customer;
    private ImageView paidIcon;
    private TextView billPaidDetails;
    private List<BillUser> customers;
    private TextView billDetails;
    private Spinner month;
    private Spinner year;
    private String[] monthsArray;
    private List<String> yearsList;
    //    private Button saveBill;
    private AutoCompleteTextView customerName;
    private EditText serviceCharge;
    private EditText address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_create_bill);
        Utility.setActionBar("Add/Update bill", getSupportActionBar());

        //Hide keyboard on load
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        saveBill = findViewById(R.id.gn_btn_save_bill);

//        saveBill.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                saveBill();
//            }
//        });

        customerName = findViewById(R.id.et_group_customer_name);
        customerEmail = findViewById(R.id.et_group_name);
        customerPhone = findViewById(R.id.et_group_description);

        billAmount = findViewById(R.id.et_customer_bill_amount);

        cashPayment = findViewById(R.id.chk_gn_bill_offline_payment);
        paidIcon = findViewById(R.id.img_gn_delete_inv_item);
        billPaidDetails = findViewById(R.id.txt_bill_paid_details);

        billDetails = findViewById(R.id.btn_gn_bill_details);
        billDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillUser currCustomer = prepareInvoice(true);
                if (currCustomer == null || TextUtils.isEmpty(currCustomer.getPhone())) {
                    //Utility.createAlert(GenericCreateBillActivity.this, "Please enter phone number first!", "Error");
                    return;
                }
                if (invoice.getAmount() == null) {
                    invoice.setAmount(BigDecimal.ZERO);
                }
                if (invoice.getPayable() == null) {
                    invoice.setPayable(BigDecimal.ZERO);
                }
                currCustomer.setCurrentInvoice(invoice);
                //Utility.nextFragment(GenericCreateBillActivity.this, GenericUpdateInvoiceItems.newInstance(currCustomer));
                startActivity(Utility.nextIntent(GenericCreateBillActivity.this, GenericUpdateInvoiceItemsActivity.class, false, currCustomer, Utility.CUSTOMER_KEY));
            }
        });

        month = findViewById(R.id.spn_gn_month);
        year = findViewById(R.id.spn_gn_year);

        serviceCharge = findViewById(R.id.et_customer_service_charge);
        address = findViewById(R.id.et_customer_address);

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);

        customerName.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (customerName.getRight() - customerName.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Utility.checkcontactPermission(GenericCreateBillActivity.this);
                        //identy = "aadhar";
                        // aadharNumber.setText(identy);
                        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        startActivityForResult(contactPickerIntent, Utility.RESULT_PICK_CONTACT);
                        return true;
                    }
                }
                return false;
            }
        });


        customerName.setThreshold(2);

        customerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (customers != null && customers.size() > 0) {
                        String selected = adapterView.getItemAtPosition(i).toString();
                        BillUser selectedCustomer = (BillUser) Utility.findInStringList(customers, selected);
                        //BillUser selectedCustomer = customers.get(i);
                        customerPhone.setText(selectedCustomer.getPhone());
                        if (!TextUtils.isEmpty(selectedCustomer.getEmail())) {
                            customerEmail.setText(selectedCustomer.getEmail());
                        }
                        if (selectedCustomer.getServiceCharge() != null) {
                            serviceCharge.setText(Utility.getDecimalString(selectedCustomer.getServiceCharge()));
                        }
                        address.setText(selectedCustomer.getAddress());
                    }
                } catch (Exception e) {
                    System.out.println("Error in setting customer .." + e);
                }

            }
        });

        //Broadcast receiver to finish activity from outside
        BroadcastReceiver broadcastReciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals(FINISH_BILL_ACTIVITY)) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReciever, new IntentFilter(FINISH_BILL_ACTIVITY));

        if (BillConstants.FRAMEWORK_RECURRING.equals(Utility.getFramework(user))) {
            //Show Month and Year
            month.setVisibility(View.VISIBLE);
            year.setVisibility(View.VISIBLE);
            serviceCharge.setVisibility(View.VISIBLE);

            address.setVisibility(View.VISIBLE);

            if (invoice != null && invoice.getMonth() != null && invoice.getYear() != null) {
                month.setSelection(invoice.getMonth());
                month.setEnabled(false);
                year.setSelection(yearsList.indexOf(invoice.getYear().toString()));
                year.setEnabled(false);
            } else {
                monthsArray = getResources().getStringArray(R.array.months_arrays);
                month.setPrompt("Select month");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_basic_text_white, monthsArray);
                month.setAdapter(adapter);
                yearsList = Utility.createYearsArray();
                /*if (invoice == null || invoice.getId() == null) {
                    yearsList.add("Select Year");
                }*/
                year.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_basic_text_white, yearsList));
                year.setPrompt("Select year");
                setCurrentMonthAndYear();
            }
        }

        Utility.logFlurry("QuickBillCreate", user);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.share, menu);
        menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, "Save").setIcon(R.drawable.ic_check_blue_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        //Disable save button if invoice is paid
        if (invoice != null && invoice.getId() != null && invoice.getStatus() != null && invoice.getStatus().equals(BillConstants.INVOICE_STATUS_PAID)) {
            menu.getItem(1).setEnabled(false);
            menu.getItem(1).setIcon(R.drawable.ic_action_check_disabled);
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
            case MENU_ITEM_SAVE:
//                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();;
                saveBill();
                return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == GenericCreateBillActivity.this.RESULT_OK) {
            switch (requestCode) {
                case Utility.RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }

        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String cname = null;
            Uri uri = data.getData();
            cursor = GenericCreateBillActivity.this.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();

            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            phoneNo = cursor.getString(phoneIndex);
            cname = cursor.getString(nameIndex);

            customerName.setText(cname);
            customerPhone.setText(phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
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
        pDialog = Utility.getProgressDialogue("Sending Invoice..", GenericCreateBillActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("sendInvoice", invoiceSendResponse(), ServiceUtil.createMyReqErrorListener(pDialog, GenericCreateBillActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericCreateBillActivity.this);
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

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GenericCreateBillActivity.this);
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
                    Utility.createAlert(GenericCreateBillActivity.this, serviceResponse.getResponse(), "Error");
                }

            }

        };
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

            GenericCreateBillActivity.this.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            System.out.println("No whatsapp!!!" + ex);
        }
    }

    private void saveBill() {
        BillUser customer = prepareInvoice(false);
        if (customer == null) return;

        BillServiceRequest request = new BillServiceRequest();
        request.setInvoice(invoice);
        request.setUser(customer);
        request.setBusiness(user.getCurrentBusiness());
        pDialog = Utility.getProgressDialogue("Saving", GenericCreateBillActivity.this);
        StringRequest myReq = ServiceUtil.getBusinessStringRequest("updateBill", billSavedListener(), ServiceUtil.createMyReqErrorListener(pDialog, GenericCreateBillActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericCreateBillActivity.this);
        queue.add(myReq);
    }

    private void setCurrentMonthAndYear() {
        Calendar cal = Calendar.getInstance();
        //By default select previous month
        cal.add(Calendar.MONTH, -1);
        month.setSelection(cal.get(Calendar.MONTH) + 1);
        year.setSelection(yearsList.indexOf(String.valueOf(cal.get(Calendar.YEAR))));
    }

    private Integer getMonth() {
        int index = Arrays.asList(monthsArray).indexOf(month.getSelectedItem());
        if (index > 0) {
            return (index);
        }
        return null;
    }

    @Nullable
    private BillUser prepareInvoice(boolean ignoreBillAmount) {
        if (invoice == null) {
            invoice = new BillInvoice();

        }
        if (invoice.getId() == null) {
            if (month.getVisibility() == View.VISIBLE) {
                if (month.getSelectedItemPosition() == 0 || year.getSelectedItemPosition() == 0) {
                    Utility.createAlert(this, "Please select month and year!", "Error");
                    return null;
                }
                invoice.setMonth(getMonth());
                invoice.setYear(new Integer(year.getSelectedItem().toString()));
            } else {
                invoice.setInvoiceDate(new Date());
            }
        }

        if (customerPhone.getText() == null || customerPhone.getText().toString().length() < 10) {
//            Utility.createAlert(GenericCreateBillActivity.this, "Please enter a valid phone number!", "Error");
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_LONG).show();
            return null;
        }
        if (!ignoreBillAmount && (billAmount.getText() == null || billAmount.getText().toString().length() == 0)) {
//            Utility.createAlert(GenericCreateBillActivity.this, "Please enter a valid bill amount!", "Error");
            Toast.makeText(this, "Please enter a valid bill amount", Toast.LENGTH_SHORT).show();
            return null;
        }
        BigDecimal amountDecimal = Utility.getDecimal(billAmount);
        BigDecimal serviceChargeDecimal = Utility.getDecimal(this.serviceCharge);

        invoice.setServiceCharge(serviceChargeDecimal);
        invoice.setAmount(amountDecimal);
        if (cashPayment.isChecked()) {
            invoice.setStatus(BillConstants.INVOICE_STATUS_PAID);
            invoice.setPaidDate(new Date());
        }
        BillUser customer = new BillUser();
        customer.setPhone(customerPhone.getText().toString());
        if (customerEmail.getText() != null) {
            customer.setEmail(customerEmail.getText().toString());
        }
        if (customerName.getText() != null) {
            customer.setName(customerName.getText().toString());
        }
        customer.setServiceCharge(serviceChargeDecimal);

        if (!TextUtils.isEmpty(address.getText())) {
            customer.setAddress(address.getText().toString());
        }
        return customer;
    }

    private Response.Listener<String> billSavedListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    String message = "";
                    if (invoice.getId() == null) {
                        message = " Now you can send the invoice via SMS/Email";
                    }
                    if (serviceResponse.getInvoice() != null) {
                        BigDecimal oldAmount = new BigDecimal(invoice.getAmount().toString());
                        invoice = serviceResponse.getInvoice();
                        invoice.setAmount(oldAmount);
                        invoice.setPayable(oldAmount);
                        customerPhone.setEnabled(false);
                        prepareInvoice(invoice);
                    }
                    Utility.createAlert(GenericCreateBillActivity.this, "Invoice saved successfully!" + message, "Done");

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericCreateBillActivity.this, serviceResponse.getResponse(), "Error");
                }


            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        //loadBillDetails();
        if (customer == null) {
            customer = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.CUSTOMER_KEY);
            if (customer != null) {
                invoice = customer.getCurrentInvoice();
            }
        }
        prepareInvoice(invoice, customer);
        if (invoice == null || invoice.getId() == null) {
            loadCustomers(); //For Suggestions
        }

    }

    private void loadCustomers() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        //pDialog = Utility.getProgressDialogue("Loading..", GenericCreateBillActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("getAllCustomers", customersLoaded(), ServiceUtil.createMyReqErrorListener(pDialog, GenericCreateBillActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericCreateBillActivity.this);
        queue.add(myReq);
    }

    private Response.Listener<String> customersLoaded() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    customers = serviceResponse.getUsers();
                    if (customers != null && customers.size() > 0) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GenericCreateBillActivity.this, android.R.layout.select_dialog_item, Utility.convertToStringArrayList(customers));
                        customerName.setAdapter(adapter);
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    //customerName.setada
                }


            }

        };
    }
//
//    private void loadBillDetails() {
//        if (user == null || user.getCurrentBusiness() == null) {
//            GenericCreateBillActivity.this.startActivity(new Intent(GenericCreateBillActivity.this, MainActivity.class));
//            return;
//        }
//
//        if (invoice == null || invoice.getId() == null) {
//            return;
//        }
//        /*if (durations.getSelectedItemPosition() < 1) {
//            Utility.createAlert(GenericCreateBillActivity.this, "Please select a durations!", "Error");
//            return;
//        }*/
//        BillServiceRequest request = new BillServiceRequest();
//        request.setRequestType("READONLY");
//        request.setBusiness(user.getCurrentBusiness());
//        request.setInvoice(invoice);
//        pDialog = Utility.getProgressDialogue("Loading", GenericCreateBillActivity.this);
//        StringRequest myReq = ServiceUtil.getStringRequest("sendInvoice", invoiceLoader(), ServiceUtil.createMyReqErrorListener(pDialog, GenericCreateBillActivity.this), request);
//        RequestQueue queue = Volley.newRequestQueue(GenericCreateBillActivity.this);
//        queue.add(myReq);
//    }

//    private Response.Listener<String> invoiceLoader() {
//        return new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                System.out.println("## response:" + response);
//                pDialog.dismiss();
//                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
//                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
//                    prepareInvoice(serviceResponse.getInvoice(), serviceResponse.getUser());
//                } else {
//                    System.out.println("Error .." + serviceResponse.getResponse());
//                    Utility.createAlert(GenericCreateBillActivity.this, serviceResponse.getResponse(), "Error");
//                }
//
//
//            }
//
//        };
//
//
//    }

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

    private void prepareInvoice(BillInvoice invoice, BillUser user) {
        if (user != null) {
            customerName.setText(user.getName());
            customerEmail.setText(user.getEmail());
            customerPhone.setText(user.getPhone());
            customerPhone.setEnabled(false);
            if (user.getServiceCharge() != null) {
                serviceCharge.setText(Utility.getDecimalString(user.getServiceCharge()));
            }
            address.setText(user.getAddress());
        }
        prepareInvoice(invoice);
    }

    private void prepareInvoice(BillInvoice inv) {
        if (inv != null) {
            billAmount.setText(Utility.getDecimalText(inv.getAmount()));
            if (inv.getServiceCharge() != null) {
                serviceCharge.setText(Utility.getDecimalString(inv.getServiceCharge()));
            }
            if (inv.getStatus() != null && inv.getStatus().equalsIgnoreCase(BillConstants.INVOICE_STATUS_PAID)) {
                cashPayment.setVisibility(View.GONE);
                billPaidDetails.setVisibility(View.VISIBLE);
                paidIcon.setVisibility(View.VISIBLE);
                String paymentMode = "";
                if (inv.getPaymentType() != null) {
                    paymentMode = inv.getPaymentType();
                }
                billPaidDetails.setText("Paid");
                if (inv.getPaidDate() != null) {
                    billPaidDetails.setText("Paid " + paymentMode + " on " + CommonUtils.convertDate(inv.getPaidDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR_TIME));
                }
                //Disable all fields
                customerName.setEnabled(false);
                int disabledColor = R.color.md_blue_grey_200;
                customerName.setTextColor(getResources().getColor(disabledColor));
                customerPhone.setEnabled(false);
                customerPhone.setTextColor(getResources().getColor(disabledColor));
                customerEmail.setEnabled(false);
                customerEmail.setTextColor(getResources().getColor(disabledColor));
                billAmount.setEnabled(false);
                billAmount.setTextColor(getResources().getColor(disabledColor));

            }
        }
    }


}
