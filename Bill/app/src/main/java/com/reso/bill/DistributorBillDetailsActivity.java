package com.reso.bill;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
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
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

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
    private BillUser distributor;
    private Date fromDate;
    private TextView invoicePaidMessage;
    private ImageView invoicePaid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_bill_details);

        Utility.setActionBar("Purchase details", getSupportActionBar());

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);
        invoice = (BillInvoice) Utility.getIntentObject(BillInvoice.class, getIntent(), Utility.INVOICE_KEY);
        distributor = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.DISTRIBUTOR_KEY);

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
                setItemsAdapter();
            }

        } else {
            //For new invoice, invoice items will be provided by the API
            setupItems();

        }


    }


    private void setItemsAdapter() {
        adapter = new PurchaseInvoiceItemsAdapter(invoiceItems, this, totalBillAmount);
        if (invoicePaid()) {
            adapter.setDisableEdit(true);
            invoicePaid.setVisibility(View.VISIBLE);
            invoicePaidMessage.setText("This invoice was paid at " + CommonUtils.convertDate(invoice.getPaidDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR_TIME));
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
        request.setUser(distributor);
        request.setBusiness(user.getCurrentBusiness());
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
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (serviceResponse.getItems() != null) {
                        invoiceItems = serviceResponse.getItems();
                        setItemsAdapter();
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
            BillServiceRequest request = new BillServiceRequest();
            request.setUser(user);
            if (distributor != null) {
                request.setBusiness(distributor.getCurrentBusiness());
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
                pDialog.dismiss();

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

}
