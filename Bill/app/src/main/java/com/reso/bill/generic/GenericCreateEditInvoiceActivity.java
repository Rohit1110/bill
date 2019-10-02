package com.reso.bill.generic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapters.BillDetailsEditAdapter;
import util.ServiceUtil;
import util.Utility;

import static com.rns.web.billapp.service.util.BillConstants.STATUS_ACTIVE;

public class GenericCreateEditInvoiceActivity extends AppCompatActivity {

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
    private Button billDetails;
    private Spinner month;
    private Spinner year;
    private String[] monthsArray;
    private List<String> yearsList;
    //    private Button saveBill;
    private AutoCompleteTextView customerName;
    private EditText serviceCharge;
    private EditText address;
    private ImageView addFromContacts;
    private EditText pendingAmount;
    private EditText creditAmount;
    private ConstraintLayout billPaidLayout;
    private ConstraintLayout customerDetailsViewable;
    private ConstraintLayout customerDetailsEditable;
    private TextView customerNameView;
    private TextView customerPhoneView;
    private List<BillItem> items;
    private AutoCompleteTextView productName;
    private List<BillItem> invoiceItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private BillDetailsEditAdapter invoiceItemsAdapter;
    private BillItem selectedItem;
    private Button moreDetails;
    private TextInputLayout pendingAmountLayout;
    private TextInputLayout creditAmountLayout;
    private List<BillItem> backupItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_create_edit_invoice);

        if (getSupportActionBar() != null) {
            Utility.setActionBar("Create New Invoice", getSupportActionBar());
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Invoice Month and Year Spinner Setup
        month = findViewById(R.id.monthSpinner);
        monthsArray = getResources().getStringArray(R.array.months_arrays);
        month.setPrompt("Month");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GenericCreateEditInvoiceActivity.this, R.layout.spinner_basic_text_white, monthsArray);
        month.setAdapter(adapter);

        year = findViewById(R.id.yearSpinner);
        year.setPrompt("Year");
        yearsList = Utility.createYearsArray();

        year.setAdapter(new ArrayAdapter<String>(GenericCreateEditInvoiceActivity.this, R.layout.spinner_basic_text_white, yearsList));


        customerName = findViewById(R.id.customerNameEditText);
        customerEmail = findViewById(R.id.customerEmailEditText);
        customerPhone = findViewById(R.id.customerPhoneEditText);
        address = findViewById(R.id.customerAddressEditText);

        customerDetailsEditable = findViewById(R.id.customerDetailsConstraintLayout);
        customerDetailsViewable = findViewById(R.id.customerDetailsCardConstraintLayout);
        customerNameView = findViewById(R.id.customerNameTextView);
        customerPhoneView = findViewById(R.id.customerPhoneTextView);

        billAmount = findViewById(R.id.invoiceTotalAmountEditText);

        cashPayment = findViewById(R.id.paidByCashCheckBox);

        billPaidLayout = findViewById(R.id.billPaidMessageLayout);
        paidIcon = findViewById(R.id.invoicePaidIcon);
        billPaidDetails = findViewById(R.id.txt_bill_paid_details);

        serviceCharge = findViewById(R.id.invoiceServiceChargeEditText);
        pendingAmount = findViewById(R.id.invoicePendingAmountEditText);
        creditAmount = findViewById(R.id.invoiceCreditedAmountEditText);
        pendingAmountLayout = findViewById(R.id.invoicePendingAmountTextInputLayout);
        creditAmountLayout = findViewById(R.id.invoiceCreditedAmountTextInputLayout);

        billDetails = findViewById(R.id.addNewItemButton);
        moreDetails = findViewById(R.id.moreDetails);

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);

        addFromContacts = findViewById(R.id.addFromContactsImageView);

        recyclerView = findViewById(R.id.invoiceItemsRecyclerView);

        addFromContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // your action here
                Utility.checkcontactPermission(GenericCreateEditInvoiceActivity.this);
                //identy = "aadhar";
                // aadharNumber.setText(identy);
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, Utility.RESULT_PICK_CONTACT);

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


        if (BillConstants.FRAMEWORK_RECURRING.equals(Utility.getFramework(user))) {
            //Show Month and Year
            month.setVisibility(View.VISIBLE);
            year.setVisibility(View.VISIBLE);
            serviceCharge.setVisibility(View.VISIBLE);

            address.setVisibility(View.VISIBLE);


            monthsArray = getResources().getStringArray(R.array.months_arrays);
            month.setPrompt("Select month");
            adapter = new ArrayAdapter<String>(this, R.layout.spinner_basic_text_white, monthsArray);
            month.setAdapter(adapter);
            yearsList = Utility.createYearsArray();
                /*if (invoice == null || invoice.getId() == null) {
                    yearsList.add("Select Year");
                }*/
            year.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_basic_text_white, yearsList));
            year.setPrompt("Select year");
            setCurrentMonthAndYear();


        } else {
            month.setVisibility(View.GONE);
            year.setVisibility(View.GONE);
            serviceCharge.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
        }

        billDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(GenericCreateEditInvoiceActivity.this);
                dialog.setContentView(R.layout.dialog_select_product);
                dialog.setTitle("Select product");

                // set the custom dialog components - text, image and button
                productName = (AutoCompleteTextView) dialog.findViewById(R.id.et_dialog_select_product);
                if (items != null && items.size() > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(GenericCreateEditInvoiceActivity.this, android.R.layout.select_dialog_item, Utility.convertToStringArrayList(items));
                    productName.setAdapter(adapter);
                }


                productName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            if (items != null && items.size() > 0) {
                                String selected = adapterView.getItemAtPosition(i).toString();
                                selectedItem = (BillItem) Utility.findInStringList(items, selected);
                                //BillUser selectedCustomer = items.get(i);

                            }
                        } catch (Exception e) {
                            System.out.println("Error in setting customer .." + e);
                        }

                    }
                });

                TextView hint = dialog.findViewById(R.id.txt_select_product_hint);
                if (Utility.isNewspaper(user)) {
                    hint.setText("Search newspaper by name and then add to your invoice.");
                }

                Button addProduct = dialog.findViewById(R.id.btn_select_product);
                addProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            if (selectedItem != null) {
                                if (invoiceItems.size() == 0) {
                                    updateInvoiceItems(invoiceItems);
                                } else {
                                    if (isAlreadyAdded(selectedItem)) {
                                        return;
                                    }
                                    updateInvoiceItems(invoiceItems);
                                }
                            } else {
                                if (BillConstants.FRAMEWORK_RECURRING.equals(Utility.getFramework(user))) {
                                    Utility.createAlert(GenericCreateEditInvoiceActivity.this, "Please add this product to your profile first", "Error");
                                    return;
                                }

                                if (TextUtils.isEmpty(productName.getText())) {
                                    Utility.createAlert(GenericCreateEditInvoiceActivity.this, "Please enter a product name", "Error");
                                    return;
                                }
                                BillItem newItem = new BillItem();
                                newItem.setName(productName.getText().toString());
                                newItem.setQuantity(BigDecimal.ONE);
                                newItem.setPrice(BigDecimal.ZERO);
                                if (isAlreadyAdded(newItem)) return;
                                invoiceItems.add(newItem);
                            }
                            prepareInvoiceItems();
                            productName.setText("");
                        } catch (Exception e) {

                        } finally {
                            selectedItem = null;
                            dialog.dismiss();
                        }

                    }
                });


                Button cancelProduct = dialog.findViewById(R.id.btn_cancel_select_product);
                cancelProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        moreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pendingAmountLayout.getVisibility() == View.VISIBLE) {
                    moreDetails.setText("+ More");
                    pendingAmountLayout.setVisibility(View.GONE);
                    creditAmountLayout.setVisibility(View.GONE);
                } else {
                    moreDetails.setText("+ Hide");
                    pendingAmountLayout.setVisibility(View.VISIBLE);
                    creditAmountLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        pendingAmountLayout.setVisibility(View.GONE);
        creditAmountLayout.setVisibility(View.GONE);

        Utility.logFlurry("QuickBillCreate", user);

    }

    private void updateInvoiceItems(List<BillItem> invoiceItems) {
        BillItem invoiceItem = new BillItem();
        invoiceItem.setQuantity(BigDecimal.ONE);
        if (selectedItem.getPrice() != null) {
            invoiceItem.setPrice(invoiceItem.getQuantity().multiply(selectedItem.getPrice()));
        } else {
            invoiceItem.setPrice(BigDecimal.ZERO);
        }
        BillItem parent = new BillItem();
        parent.setId(selectedItem.getId());
        parent.setName(Utility.getItemName(selectedItem));
        invoiceItem.setParentItem(parent);
        invoiceItem.setStatus(STATUS_ACTIVE);
        invoiceItems.add(invoiceItem);
    }

    private void prepareInvoiceItems() {
        if (invoiceItems == null || invoiceItems.size() == 0) {
            return;
        }
        if (invoiceItemsAdapter == null) {
            invoiceItemsAdapter = new BillDetailsEditAdapter(invoiceItems, GenericCreateEditInvoiceActivity.this, billAmount);
            recyclerView.setLayoutManager(new LinearLayoutManager(GenericCreateEditInvoiceActivity.this));
            recyclerView.setAdapter(invoiceItemsAdapter);
        } else {
            invoiceItemsAdapter.setItems(invoiceItems);
            invoiceItemsAdapter.notifyDataSetChanged();
        }
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
        if (resultCode == this.RESULT_OK) {
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
            cursor = this.getContentResolver().query(uri, null, null, null, null);
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
        pDialog = Utility.getProgressDialogue("Sending Invoice..", GenericCreateEditInvoiceActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("sendInvoice", invoiceSendResponse(), ServiceUtil.createMyReqErrorListener(pDialog, GenericCreateEditInvoiceActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericCreateEditInvoiceActivity.this);
        queue.add(myReq);
    }

    private Response.Listener<String> invoiceSendResponse() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                final BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    //Utility.createAlert(getContext(), "Invoice sent to customer successfully!", "Done");

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GenericCreateEditInvoiceActivity.this);
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
                    Utility.createAlert(GenericCreateEditInvoiceActivity.this, serviceResponse.getResponse(), "Error");
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

            GenericCreateEditInvoiceActivity.this.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            System.out.println("No whatsapp!!!" + ex);
        }
    }

    private void saveBill() {
        BillUser customer = prepareInvoice(false);
        if (customer == null) return;

        BillServiceRequest request = new BillServiceRequest();
        /*if (invoice.getInvoiceItems() == null || invoice.getInvoiceItems().size() == 0) {
            invoice.setInvoiceItems(invoiceItems);
        } else {
            if (backupItems != null && backupItems.size() > 0) {
                List<BillItem> deletedItems = new ArrayList<>();
                for (BillItem old : backupItems) {
                    boolean found = false;
                    for (BillItem newItem : invoiceItems) {
                        if (newItem.getId() != null && old.getId() == newItem.getId()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        old.setStatus(BillConstants.STATUS_DELETED);
                        deletedItems.add(old);
                    }
                }
                invoiceItems.addAll(deletedItems);
            }
            invoice.setInvoiceItems(invoiceItems);
        }*/
        request.setInvoice(invoice);
        request.setUser(customer);
        request.setBusiness(user.getCurrentBusiness());
        pDialog = Utility.getProgressDialogue("Saving", GenericCreateEditInvoiceActivity.this);
        StringRequest myReq = ServiceUtil.getBusinessStringRequest("updateBill", billSavedListener(), ServiceUtil.createMyReqErrorListener(pDialog, GenericCreateEditInvoiceActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericCreateEditInvoiceActivity.this);
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

        if (invoice.getInvoiceItems() == null || invoice.getInvoiceItems().size() == 0) {
            invoice.setInvoiceItems(invoiceItems);
        } else {
            if (backupItems != null && backupItems.size() > 0) {
                List<BillItem> deletedItems = new ArrayList<>();
                for (BillItem old : backupItems) {
                    boolean found = false;
                    for (BillItem newItem : invoiceItems) {
                        if (newItem.getId() != null && old.getId() == newItem.getId()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        old.setStatus(BillConstants.STATUS_DELETED);
                        deletedItems.add(old);
                    }
                }
                invoiceItems.addAll(deletedItems);
            }
            invoice.setInvoiceItems(invoiceItems);
        }

        if (customerPhone.getText() == null || customerPhone.getText().toString().length() < 10) {
//            Utility.createAlert(GenericCreateEditInvoiceActivity.this, "Please enter a valid phone number!", "Error");
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_LONG).show();
            return null;
        }
        if (!ignoreBillAmount && (billAmount.getText() == null || billAmount.getText().toString().length() == 0)) {
//            Utility.createAlert(GenericCreateEditInvoiceActivity.this, "Please enter a valid bill amount!", "Error");
            Toast.makeText(this, "Please enter a valid bill amount", Toast.LENGTH_SHORT).show();
            return null;
        }
        BigDecimal amountDecimal = Utility.getDecimal(billAmount);
        BigDecimal serviceChargeDecimal = Utility.getDecimal(this.serviceCharge);

        invoice.setServiceCharge(serviceChargeDecimal);
        invoice.setPendingBalance(Utility.getDecimal(pendingAmount));
        invoice.setCreditBalance(Utility.getDecimal(creditAmount));
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
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
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
                    Utility.createAlert(GenericCreateEditInvoiceActivity.this, "Invoice saved successfully!" + message, "Done");

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericCreateEditInvoiceActivity.this, serviceResponse.getResponse(), "Error");
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
        loadBusinessItems();
    }

    private void loadCustomers() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        //pDialog = Utility.getProgressDialogue("Loading..", GenericCreateEditInvoiceActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("getAllCustomers", customersLoaded(), ServiceUtil.createMyReqErrorListener(pDialog, GenericCreateEditInvoiceActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericCreateEditInvoiceActivity.this);
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GenericCreateEditInvoiceActivity.this, android.R.layout.select_dialog_item, Utility.convertToStringArrayList(customers));
                        customerName.setAdapter(adapter);
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    //customerName.setada
                }


            }

        };
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
        //Toast.makeText(GenericCreateEditInvoiceActivity.this,"Click",Toast.LENGTH_LONG).show();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, invoice.getPaymentMessage());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void prepareInvoice(BillInvoice invoice, BillUser user) {
        if (user != null) {
            customerDetailsViewable.setVisibility(View.VISIBLE);
            customerDetailsEditable.setVisibility(View.GONE);

            customerNameView.setText(user.getName());
            customerPhoneView.setText(user.getPhone());
            customerName.setText(user.getName());
            customerEmail.setText(user.getEmail());
            customerPhone.setText(user.getPhone());
            customerPhone.setEnabled(false);
            if (user.getServiceCharge() != null && (invoice == null || invoice.getId() == null)) {
                serviceCharge.setText(Utility.getDecimalString(user.getServiceCharge()));
            }
            address.setText(user.getAddress());
        }
        prepareInvoice(invoice);
    }

    private void prepareInvoice(BillInvoice inv) {
        if (inv != null) {
            billAmount.setText(Utility.getDecimalString(inv.getAmount()));
            pendingAmount.setText(Utility.getDecimalString(inv.getPendingBalance()));
            creditAmount.setText(Utility.getDecimalString(inv.getCreditBalance()));

            if (inv.getServiceCharge() != null) {
                serviceCharge.setText(Utility.getDecimalString(inv.getServiceCharge()));
            }
            if (inv.getPendingBalance() != null || inv.getCreditBalance() != null) {
                pendingAmountLayout.setVisibility(View.VISIBLE);
                creditAmountLayout.setVisibility(View.VISIBLE);
                moreDetails.setText("+ Hide");
            }

            if (inv.getMonth() != null && inv.getYear() != null) {
                month.setSelection(invoice.getMonth());
                month.setEnabled(false);
                year.setSelection(yearsList.indexOf(invoice.getYear().toString()));
                year.setEnabled(false);
            }

            if (inv.getInvoiceItems() != null) {
                invoiceItems = inv.getInvoiceItems();
                copyAllItems(inv); //Backup for delete
                prepareInvoiceItems();
            }

            if (inv.getStatus() != null && inv.getStatus().equalsIgnoreCase(BillConstants.INVOICE_STATUS_PAID)) {
                cashPayment.setVisibility(View.GONE);
                billPaidLayout.setVisibility(View.VISIBLE);
                //paidIcon.setVisibility(View.VISIBLE);
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
                creditAmount.setEnabled(false);
                pendingAmount.setEnabled(false);
                month.setEnabled(false);
                year.setEnabled(false);
                serviceCharge.setEnabled(false);
                if (invoiceItemsAdapter != null) {
                    invoiceItemsAdapter.setInvoicePaid(true);
                }


            }
        }
    }

    //Code for invoice items

    private void loadBusinessItems() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        //pDialog = Utility.getProgressDialogue("Loading..", GenericUpdateInvoiceItemsActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("loadBusinessItems", itemsLoaded(), ServiceUtil.createMyReqErrorListener(pDialog, this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
    }

    private Response.Listener<String> itemsLoaded() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    items = serviceResponse.getItems();

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    //productName.setada
                }
            }

        };
    }

    private boolean isAlreadyAdded(BillItem item) {
        for (BillItem invItem : invoiceItems) {
            if (invItem.getParentItem() != null && item.getParentItem() != null && invItem.getParentItem().getId() == item.getId()) {
                Utility.createAlert(this, "Product already added to the bill!", "Error");
                return true;
            } else if (invItem.getParentItem() != null && item.getName() != null && item.getName().equalsIgnoreCase(invItem.getParentItem().getName())) {
                Utility.createAlert(this, "Product already added to the bill!", "Error");
                return true;
            } else if (invItem.getName() != null && item.getName() != null && invItem.getName().equalsIgnoreCase(item.getName())) {
                Utility.createAlert(this, "Product already added to the bill!", "Error");
                return true;
            }
        }
        return false;
    }

    private void copyAllItems(BillInvoice inv) {
        if (inv.getInvoiceItems() != null && inv.getInvoiceItems().size() > 0) {
            for (BillItem item : inv.getInvoiceItems()) {
                try {
                    backupItems.add((BillItem) item.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    System.out.println("Cloning failed ..");
                }
            }

        }


    }
}
