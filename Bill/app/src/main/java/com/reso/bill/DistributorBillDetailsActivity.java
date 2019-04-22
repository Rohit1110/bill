package com.reso.bill;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.components.Bluetooth;
import com.reso.bill.components.DeviceListActivity;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;
import com.zj.btsdk.BluetoothService;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapters.PurchaseInvoiceItemsAdapter;
import util.ServiceUtil;
import util.Utility;

public class DistributorBillDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private PurchaseInvoiceItemsAdapter adapter;
    //private Button saveBill;
    private Button addProduct;
    private TextInputEditText selectedDate, comments, totalBillAmount;
    private BillInvoice invoice;
    private BillUser user;
    private BillUser customer;
    private List<BillItem> items;
    private BillItem selectedItem;
    private List<BillItem> invoiceItems = new ArrayList<>();
    private BillUser selectedUser;//Could be vendor or distributor
    private Date fromDate;
    private TextView invoicePaidMessage;
    private ImageView invoicePaid;
    private TextView btnShowOptions;
    private TextView purchaseTotal;
    private TextView returnTotal;

    int REQUEST_ENABLE_BT = 4, REQUEST_CONNECT_DEVICE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_bill_details);

        Utility.setActionBar("Purchase details", getSupportActionBar());

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);
        invoice = (BillInvoice) Utility.getIntentObject(BillInvoice.class, getIntent(), Utility.INVOICE_KEY);
        selectedUser = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.DISTRIBUTOR_KEY);

        selectedDate = findViewById(R.id.et_purchase_date);

        fromDate = new Date();
        if (invoice != null && invoice.getId() != null) {
            selectedDate.setEnabled(false);
            fromDate = invoice.getInvoiceDate();
        }

        selectedDate.setText(CommonUtils.convertDate(fromDate));

        System.out.println("Date selected =>" + selectedDate.getText());
        //txtfromdate.setText((dd+1)+"-"+mm+"-"+yy);

        selectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");*/
                //isoffday = false;
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                final int yyyy = yy;
                final int mon = mm;
                final int day = dd;
                DatePickerDialog datePicker = new DatePickerDialog(DistributorBillDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                        SimpleDateFormat sdf = new SimpleDateFormat(BillConstants.DATE_FORMAT);
                        fromDate = null;
                        try {
                            fromDate = sdf.parse(selectedDateString);
                            if (fromDate != null) {
                                selectedDate.setText(new SimpleDateFormat(BillConstants.DATE_FORMAT).format(fromDate));
                            }
                            setupItems();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //

                    }
                }, yy, mm, dd);
                //TODO not sure datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis() + (1000 * 24 * 60 * 60));

                datePicker.show();

            }
        });

        totalBillAmount = findViewById(R.id.txt_total_purchase_amount);
        recyclerView = findViewById(R.id.recycler_purchase_items);

        //btnAddComments = findViewById(R.id.btn_add_purchase_comment);
        comments = findViewById(R.id.txt_purchase_comments);
        invoicePaid = findViewById(R.id.img_purchase_already_paid);
        invoicePaidMessage = findViewById(R.id.txt_purchase_paid);
        btnShowOptions = findViewById(R.id.txt_purchase_show_options);

        btnShowOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter != null) {
                    if (adapter.isShowOptions()) {
                        adapter.setShowOptions(false);
                        btnShowOptions.setText("More options");
                    } else {
                        adapter.setShowOptions(true);
                        btnShowOptions.setText("Hide options");
                    }
                }
            }
        });

        purchaseTotal = findViewById(R.id.txt_purchase_total);
        returnTotal = findViewById(R.id.txt_return_total);

        /*totalBillAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAmountChangeDialog();
            }
        });

        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentDialog();
            }
        });

        btnAddComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentDialog();
            }
        });*/

    }

    private void showAmountChangeDialog() {

        final Dialog dialog = new Dialog(DistributorBillDetailsActivity.this);
        dialog.setContentView(R.layout.dialog_edit_purchase_amount);
        dialog.setTitle("Edit Amount");


        final EditText amount = (EditText) dialog.findViewById(R.id.et_dialog_purchase_total);
        //amount.setText("0");
        if (!TextUtils.isEmpty(totalBillAmount.getText())) {
            amount.setText(totalBillAmount.getText());
        }


        Button saveAmount = (Button) dialog.findViewById(R.id.btn_dialog_save_purchase_amount);
        // if button is clicked, close the custom dialog
        saveAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!Utility.validateDecimal(amount)) {
                    amount.setError("Invalid amount!");
                    return;
                }
                totalBillAmount.setText(amount.getText());
                dialog.dismiss();
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.btn_dialog_cancel_purchase_amount);
        // if button is clicked, close the custom dialog
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showCommentDialog() {

        final Dialog dialog = new Dialog(DistributorBillDetailsActivity.this);
        dialog.setContentView(R.layout.dialog_edit_purchase_comment);
        dialog.setTitle("Add comment");


        final EditText comment = (EditText) dialog.findViewById(R.id.et_dialog_purchase_comment);
        //comment.setText("0");
        if (!TextUtils.isEmpty(comments.getText())) {
            comment.setText(comments.getText());
        }


        Button saveComment = (Button) dialog.findViewById(R.id.btn_dialog_save_purchase_comment);
        // if button is clicked, close the custom dialog
        saveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comments.setText(comment.getText());
                comments.setVisibility(View.VISIBLE);
                //btnAddComments.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.btn_dialog_cancel_purchase_comment);
        // if button is clicked, close the custom dialog
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Utility.MENU_ITEM_SAVE, Menu.NONE, "Save").setIcon(R.drawable.ic_check_blue_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if (invoicePaid()) {
            //TODO disable the save button
            menu.getItem(0).setEnabled(false);
            menu.getItem(0).setIcon(R.drawable.ic_action_check_disabled);
        }
        menu.add(Menu.NONE, Utility.MENU_ITEM_PRINT, Menu.NONE, "Save").setIcon(R.drawable.ic_action_local_printshop).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    private boolean invoicePaid() {
        return invoice != null && invoice.getStatus() != null && invoice.getStatus().equalsIgnoreCase(BillConstants.INVOICE_STATUS_PAID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Back button click
                finish();
                return true;
            case Utility.MENU_ITEM_SAVE:
//                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();;
                saveBill();
                return true;
            case Utility.MENU_ITEM_PRINT:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, Utility.MY_PERMISSIONS_BLUETOOTH);
                    return true;
                }
                print();
                return true;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareInvoice();
    }

    private void prepareInvoice() {
        if (invoice != null && invoice.getId() != null) {
            if (!TextUtils.isEmpty(invoice.getComments())) {
                /*btnAddComments.setVisibility(View.GONE);
                comments.setVisibility(View.VISIBLE);*/
                comments.setText(invoice.getComments());
            }
            totalBillAmount.setText(Utility.getDecimalString(invoice.getAmount()));
            //For old invoice, invoice items to be picked from invoice data
            if (invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
                invoiceItems = invoice.getInvoiceItems();
                setItemsAdapter(invoice); //Send return Invoice
            }

        } else {
            //For new invoice, invoice items will be provided by the API
            setupItems();

        }


    }


    private void setItemsAdapter(BillInvoice invoice) {
        adapter = new PurchaseInvoiceItemsAdapter(invoiceItems, this, totalBillAmount);
        adapter.setPurchaseTotal(purchaseTotal);
        adapter.setReturnTotal(returnTotal);
        if (invoice != null && invoice.getExtraParams() != null) {
            adapter.setReturnInvoice((BillInvoice) ServiceUtil.fromJson(invoice.getExtraParams(), BillInvoice.class));
            adapter.setShowOptions(true);
            btnShowOptions.setText("Hide options");
        } else {
            adapter.setReturnInvoice(invoice);
        }
        if (invoicePaid()) {
            adapter.setDisableEdit(true);
            invoicePaid.setVisibility(View.VISIBLE);
            invoicePaidMessage.setText("This invoice was paid at " + CommonUtils.convertDate(this.invoice.getPaidDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR_TIME));
            invoicePaidMessage.setVisibility(View.VISIBLE);
            comments.setEnabled(false);
            totalBillAmount.setEnabled(false);
            //btnAddComments.setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupItems() {
        BillServiceRequest request = new BillServiceRequest();
        request.setRequestedDate(fromDate);
        if (Utility.isDistributor(user)) {
            request.setUser(user);//Send distributor details
            request.setBusiness(selectedUser.getCurrentBusiness());
        } else {
            request.setUser(selectedUser);//Send distributor details
            request.setBusiness(user.getCurrentBusiness());
        }

        pDialog = Utility.getProgressDialogue("Loading..", DistributorBillDetailsActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("getBusinessItemsByDate", businessItemsResponse(), ServiceUtil.createMyReqErrorListener(pDialog, DistributorBillDetailsActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(DistributorBillDetailsActivity.this);
        queue.add(myReq);
    }

    private Response.Listener<String> businessItemsResponse() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (serviceResponse.getItems() != null) {
                        invoiceItems = serviceResponse.getItems();
                        setItemsAdapter(serviceResponse.getInvoice());
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(DistributorBillDetailsActivity.this, serviceResponse.getResponse(), "Error");
                }

            }

        };
    }

    private void copyAllItems(BillInvoice inv) {
        if (inv.getInvoiceItems() != null && inv.getInvoiceItems().size() > 0) {
            for (BillItem item : inv.getInvoiceItems()) {
                try {
                    invoiceItems.add((BillItem) item.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    System.out.println("Cloning failed ..");
                }
            }

        }


    }

    private void saveBill() {
        if (invoiceItems != null && invoiceItems.size() > 0) {
            List<BillItem> purchaseItems = new ArrayList<>();
            if (invoice == null || invoice.getId() == null) {
                invoice = new BillInvoice();
                if (TextUtils.isEmpty(selectedDate.getText())) {
                    Utility.createAlert(this, "Error", "Please select date of purchase!");
                    return;
                }
                invoice.setInvoiceDate(CommonUtils.convertDate(selectedDate.getText().toString()));
                for (BillItem item : invoiceItems) {
                    BillItem pItem = new BillItem();
                    pItem.setQuantity(item.getQuantity());
                    pItem.setPrice(Utility.getCostPrice(item));
                    pItem.setUnitSellingPrice(item.getUnitSellingPrice());
                    pItem.setParentItem(item);
                    pItem.setParentItemId(item.getParentItemId());
                    purchaseItems.add(pItem);
                }
            } else {
                purchaseItems = invoiceItems;
            }
            invoice.setInvoiceItems(purchaseItems);
            invoice.setAmount(Utility.getDecimal(totalBillAmount));
            invoice.setComments(comments.getText().toString());
            if (adapter != null) {
                if (adapter.getReturnInvoice() != null) {
                    invoice.setExtraParams(ServiceUtil.toJson(adapter.getReturnInvoice()));
                }
                if (adapter.getSoldAmount() != null) {
                    invoice.setSoldAmount(adapter.getSoldAmount());
                }
            }
            BillServiceRequest request = new BillServiceRequest();
            if (Utility.isDistributor(user)) {
                request.setUser(selectedUser);
                if (selectedUser != null) {
                    request.setBusiness(user.getCurrentBusiness());
                }
            } else {
                request.setUser(user);
                if (selectedUser != null) {
                    request.setBusiness(selectedUser.getCurrentBusiness());
                }
            }

            request.setInvoice(invoice);
            pDialog = Utility.getProgressDialogue("Saving..", DistributorBillDetailsActivity.this);
            StringRequest myReq = ServiceUtil.getStringRequest("updateBusinessInvoice", invoiceSaved(), ServiceUtil.createMyReqErrorListener(pDialog, DistributorBillDetailsActivity.this), request);
            RequestQueue queue = Volley.newRequestQueue(DistributorBillDetailsActivity.this);
            queue.add(myReq);
        }
    }

    private Response.Listener<String> invoiceSaved() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
//                    Utility.createAlert(DistributorBillDetailsActivity.this, "Done", "Invoice saved successfully!");
                    finish();
                    Toast.makeText(DistributorBillDetailsActivity.this, "Purchase details saved successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(DistributorBillDetailsActivity.this, serviceResponse.getResponse(), "Error");
                }

            }

        };
    }

    //Bluetooth printing code

    private void print() {

        try {
            if (Bluetooth.isPrinterConnected(getApplicationContext(), this)) {

                byte[] normalText = {27, 33, 0};
                byte[] boldText = {27, 33, 0};
//        byte[] normalLeftText = {27, 97, 0};
                // byte[] normalCenterText = {27, 97, 1};
//        byte[] normalRightText = {27, 97, 2};

                DateFormat dateFormat = new SimpleDateFormat("MMM dd yy HH:mm:ss");
                Date date = new Date();
                //System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43

                boldText[2] = ((byte) (0x8 | normalText[2]));

                byte[] doubleHeightText = {27, 33, 0};
                doubleHeightText[2] = ((byte) (0x10 | normalText[2]));

                byte[] doubleHeightBoldText = {27, 33, 0};
                doubleHeightBoldText[2] = ((byte) (0x8 | 0x10 | normalText[2]));

                byte[] doubleWidthText = {27, 33, 0};
                doubleWidthText[2] = ((byte) (0x20 | normalText[2]));

                byte[] boldDoubleWidthText = {27, 33, 0};
                boldDoubleWidthText[2] = ((byte) (0x20 | normalText[2]));

                byte[] doubleWidthHeightText = {27, 33, 0};
                doubleWidthHeightText[2] = ((byte) (0x10 | 0x20 | normalText[2]));

                byte[] boldDoubleWidthHeightText = {27, 33, 0};
                boldDoubleWidthHeightText[2] = ((byte) (0x10 | 0x20 | normalText[2]));

                BluetoothService mService = null;
                mService = Bluetooth.getServiceInstance();

                //examples for printing data
                String str3 = user.getCurrentBusiness().getName() + "\n" + "PURCHASE";
                String str2 = dateFormat.format(date) + "\n" + user.getPhone();
                String str4 = "----------------";
                StringBuilder builder = new StringBuilder();
                int index = 0;
                for (BillItem item : invoiceItems) {
                    appendItem(builder, item, index == (invoiceItems.size() - 1));
                    index++;
                }

                String str1 = builder.toString();
                String str5 = "----------------" + "\n" + purchaseTotal.getText();
                builder = new StringBuilder();
                String str8 = "", str9 = "";
                if (adapter.getReturnInvoice() != null && adapter.getReturnInvoice().getInvoiceItems() != null) {
                    index = 0;
                    for (BillItem item : adapter.getReturnInvoice().getInvoiceItems()) {
                        appendItem(builder, item, index == (adapter.getReturnInvoice().getInvoiceItems().size() - 1));
                        index++;
                    }
                    if (builder.toString().contains("*")) {
                        str8 = "\n" + "Return (-less)" + "\n" + builder.toString();
                        str9 = "----------------" + "\n" + returnTotal.getText() + "\n" + "----------------";
                    }
                }

                String str6 = "Total = " + totalBillAmount.getText();
                String str7 = "\n" + "Note:- " + comments.getText();

                mService.write(doubleWidthText);
                mService.sendMessage(str3, "GBK");

                mService.write(boldText);
                mService.sendMessage(str2, "GBK");


                mService.write(doubleHeightText);
                mService.sendMessage(str4, "GBK");


                mService.write(normalText);
                mService.sendMessage(str1, "GBK");

                mService.write(normalText);
                mService.sendMessage(str5, "GBK");
                mService.write(normalText);
                mService.sendMessage(str8, "GBK");

                mService.write(normalText);
                mService.sendMessage(str9, "GBK");

                mService.write(normalText);
                mService.sendMessage(str6, "GBK");

                mService.write(normalText);
                mService.sendMessage(str7, "GBK");


            } else {
                //Printer not connected and send request for connecting printer
                Bluetooth.connectPrinter(getApplicationContext(), this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        BluetoothService mService = null;
//        mService = Bluetooth.getServiceInstance();
        //PrintMultilingualText pml = new PrintMultilingualText(TextPrintActivity.this, TextPrintActivity.this, mService, dirpath, effectivePrintWidth);
        //  String text = printMessage.getText().toString();
        //pml.startPrinting(text);
        //mService.write(text.getBytes());
//        String htmlContent = "यह एक सैंपल पैराग्राफ है जो हिंदी लैंग्वेज में दिया गया है ! आप किसी भी लैंग्वेज में डाटा प्रिंट कर सकते हो !<br /><center><b>मते टेक्नोलॉजीज</b></center>";
//
//      mService.sendMessage(htmlContent, "GBK");


    }

    private void appendItem(StringBuilder builder, BillItem item, boolean lastItem) {
        if (item.getQuantity() == null || item.getQuantity().equals(BigDecimal.ZERO)) {
            return;
        }
        BigDecimal costPrice = Utility.getCostPrice(item);
        if (costPrice == null || costPrice.equals(BigDecimal.ZERO)) {
            return;
        }
        String totalCp = Utility.getDecimalString(costPrice.multiply(item.getQuantity()));
        builder.append(Utility.getItemName(item)).append(" ").append(Utility.getDecimalString(costPrice)).append(" * ").append(Utility.getDecimalString(item.getQuantity())).append(" = ").append(totalCp);
        if (!lastItem) {
            builder.append("\n");
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        String msg = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//        Toast toast = Toast.makeText(TextPrintActivity.this, msg, Toast.LENGTH_LONG);
//        toast.show();
//        Bluetooth.pairedPrinterAddress(getApplicationContext(), TextPrintActivity.this, msg);
//        print();
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            //bluetooth enabled and request for showing available bluetooth devices

            SharedPreferences prefs = getSharedPreferences("PrinterApp", Context.MODE_PRIVATE);
            String bluetootMacAddress = prefs.getString("PairedDevice", null);
            if (bluetootMacAddress != null) {
                Bluetooth.pairedPrinterAddress(getApplicationContext(), this, bluetootMacAddress);

            } else {
                Bluetooth.pairPrinter(getApplicationContext(), this);
            }


        } else if (requestCode == REQUEST_CONNECT_DEVICE && resultCode == RESULT_OK) {
            //bluetooth device selected and request pairing with device

            String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            Bluetooth.pairedPrinterAddress(getApplicationContext(), this, address);
            SharedPreferences settings = getApplicationContext().getSharedPreferences("PrinterApp", 0);
            settings = getApplicationContext().getSharedPreferences("PrinterApp", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("PairedDevice", address);
            editor.commit();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_BLUETOOTH: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    print();
                } else {
                    //Utility.nextFragment(GenericCustomerInfoActivity.this, getNextFragment());
                    System.out.println("No permission!");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}
