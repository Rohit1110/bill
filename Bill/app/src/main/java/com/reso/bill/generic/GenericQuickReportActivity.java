package com.reso.bill.generic;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillPaymentSummary;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.bo.domain.BillUserLog;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import model.BillFilter;
import util.ServiceUtil;
import util.Utility;

public class GenericQuickReportActivity extends AppCompatActivity {

    private TextView pendingInvoices;
    private TextView pendingAmount;
    private TextView onlinePayments;
    private TextView offlinePayments;
    private TextView onlineAmount;
    private TextView offlineAmount;
    private TextView amountSettled;
    private Menu mainMenu;
    private BillFilter filter;
    private DialogInterface.OnDismissListener dismissListener;
    private BillUser user;
    private Spinner timeSpinner;
    private ProgressDialog pDialog;
    private boolean loading = false;
    private BillFilter billFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_report);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Utility.setActionBar("Quick Report", getSupportActionBar());
        }

        pendingInvoices = findViewById(R.id.pendingInvoicesTextView);
        pendingAmount = findViewById(R.id.amountPendingInvoicesTextView);
        onlinePayments = findViewById(R.id.invoicesPaidOnlineTextView);
        onlineAmount = findViewById(R.id.amountInvoicesPaidOnlineTextView);
        offlinePayments = findViewById(R.id.invoicesPaidOfflineTextView);
        offlineAmount = findViewById(R.id.amountInvoicesPaidOfflineTextView);
        amountSettled = findViewById(R.id.amountSettledTextView);
        timeSpinner = findViewById(R.id.filterSpinner);

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_basic_text_white, getResources().getStringArray(R.array.durations_arrays));
        timeSpinner.setAdapter(adapter);
        timeSpinner.setSelection(1);

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ("Custom".equals(timeSpinner.getSelectedItem())) {
                    //Show alert
                    billFilter.showDateFilter();
                    billFilter.getDateDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            loadSummary();
                        }
                    });
                    return;
                }

                loadSummary();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        billFilter = new BillFilter(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(Menu.NONE, Utility.MENU_ITEM_FILTER, Menu.NONE, "Filter").setIcon(R.drawable.ic_action_filter_list_disabled).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case Utility.MENU_ITEM_FILTER:
                System.out.println("FIlter called ...");
                if (filter == null) {
                    filter = new BillFilter(this, user);
                }
                filter.showFilterDialog();

                if (dismissListener == null) {
                    dismissListener = new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            mainMenu.getItem(0).setIcon(filter.getFilterIcon());
                            loadSummary();
                        }
                    };
                    filter.getDialog().setOnDismissListener(dismissListener);
                }

                mainMenu.getItem(0).setIcon(filter.getFilterIcon());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSummary();
    }


    private void loadSummary() {
        if (user == null || user.getCurrentBusiness() == null) {
            return;
        }

        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());

        BillUserLog log = billFilter.getUserLogFromSpinner(timeSpinner);

        if (log.getFromDate() != null && log.getToDate() != null) {
            String title = "Payments " + CommonUtils.convertDate(log.getFromDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR);
            if (!"Today".equalsIgnoreCase(timeSpinner.getSelectedItem().toString())) {
                title = title + " - " + CommonUtils.convertDate(log.getToDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR);
            }
            Utility.setActionBar(title, getSupportActionBar());
        }

        BillItem item = new BillItem();
        item.setChangeLog(log);
        request.setItem(item);
        if (filter != null) {
            request.setCustomerGroup(filter.getGroup());
        }
        if (loading) {
            return;
        }
        loading = true;
        pDialog = Utility.getProgressDialogue("Loading", this);
        StringRequest myReq = ServiceUtil.getStringRequest("getPaymentSummary", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading = false;
                System.out.println("## response:" + response);
                pDialog.dismiss();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    BillPaymentSummary summary = serviceResponse.getDashboard();
                    if (summary != null) {
                        pendingInvoices.setText(Utility.getText(summary.getPendingInvoices()));
                        pendingAmount.setText(Utility.getDecimalString(summary.getPendingAmount()));
                        offlinePayments.setText(Utility.getText(summary.getOfflineInvoices()));
                        offlineAmount.setText(Utility.getDecimalString(summary.getOfflinePaid()));
                        onlinePayments.setText(Utility.getText(summary.getOnlineInvoices()));
                        onlineAmount.setText(Utility.getDecimalString(summary.getOnlinePaid()));
                        amountSettled.setText(Utility.getDecimalString(summary.getCompletedSettlements()));
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericQuickReportActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }


}
