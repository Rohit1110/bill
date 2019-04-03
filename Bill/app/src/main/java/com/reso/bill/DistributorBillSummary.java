package com.reso.bill;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapters.PurchaseInvoiceAdapter;
import model.BillFilter;
import util.ServiceUtil;
import util.Utility;

public class DistributorBillSummary extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private BillUser user;
    private List<BillUser> users;
    private List<BillUser> filterList = new ArrayList<>();
    private Date date;
    //private Button requestTransactions;
    //private TransactionsAdapter adapter;
    private Button clear;
    private Spinner month;
    private Spinner year;
    private List<String> yearsList;
    private boolean loading;
    private Menu mainMenu;
    private BillFilter filter;
    private DialogInterface.OnDismissListener dismissListener;
    private BillUser selectedDistributor;
    private List<BillInvoice> invoices;
    private PurchaseInvoiceAdapter adapter;
    private FloatingActionButton addNewPurchase;
    private TextView totalPending;
    private TextView totalProfit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_bill_summary);

        selectedDistributor = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.DISTRIBUTOR_KEY);
        String distributor = "";
        if (selectedDistributor != null) {
            distributor = selectedDistributor.getName();
        }
        Utility.setActionBar(distributor + "'s purchases", getSupportActionBar());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_group_distributor_invoices);

        /*requestTransactions = (Button) findViewById(R.id.btn_get_transactions);

        requestTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadSummary();
            }
        });*/

        month = (Spinner) findViewById(R.id.spn_txn_month);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_basic_text_white, getResources().getStringArray(R.array.months_arrays));
        month.setAdapter(adapter);
        month.setSelection(Calendar.getInstance().get(Calendar.MONTH) + 1);

        year = (Spinner) findViewById(R.id.spn_txn_year);
        yearsList = Utility.createYearsArray();
        year.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_basic_text_white, yearsList));
        year.setSelection(yearsList.indexOf(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);

        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!loading) {
                    loadSummary();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!loading) {
                    loadSummary();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addNewPurchase = (FloatingActionButton) findViewById(R.id.btn_add_new_distributor_invoice);
        addNewPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Utility.nextIntent(DistributorBillSummary.this, DistributorBillDetailsActivity.class, true, selectedDistributor, Utility.DISTRIBUTOR_KEY));

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) addNewPurchase.hide();
                    else if (scrollY < oldScrollY) addNewPurchase.show();
                }
            });
        }

        totalPending = findViewById(R.id.txt_total_distributor_pending);
        totalProfit = findViewById(R.id.txt_total_distributor_profit);

    }

    private void loadSummary() {
        if (user == null || user.getCurrentBusiness() == null) {
            this.startActivity(new Intent(this, MainActivity.class));
            return;
        }
        if (month.getSelectedItemPosition() < 1 || year.getSelectedItemPosition() < 1) {
            Utility.createAlert(this, "Please select a month and year!", "Error");
            return;
        }
        loading = true;
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        request.setUser(selectedDistributor);
        BillInvoice currInvoice = new BillInvoice();
        currInvoice.setMonth(month.getSelectedItemPosition());
        currInvoice.setYear(Integer.parseInt(year.getSelectedItem().toString()));
        request.setInvoice(currInvoice);
        if (filter != null) {
            request.setCustomerGroup(filter.getGroup());
        }
        pDialog = Utility.getProgressDialogue("Loading", this);
        StringRequest myReq = ServiceUtil.getStringRequest("getBusinessInvoices", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                loading = false;
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    invoices = serviceResponse.getInvoices();
                    if (invoices != null && invoices.size() > 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(DistributorBillSummary.this));
                        adapter = new PurchaseInvoiceAdapter(invoices, DistributorBillSummary.this, selectedDistributor);
                        recyclerView.setAdapter(adapter);
                    } else {
                        recyclerView.setAdapter(new PurchaseInvoiceAdapter(new ArrayList<BillInvoice>(), DistributorBillSummary.this, selectedDistributor));
                    }
                    if (serviceResponse.getInvoice() != null) {
                        totalPending.setText("Pending: " + Utility.getDecimalString(serviceResponse.getInvoice().getPayable()) + "/-");
                        totalProfit.setText("Profit: " + Utility.getDecimalString(serviceResponse.getInvoice().getSoldAmount()) + "/-");
                    }
                    //TODO causing double line so removed temporarily
                    /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
                    recyclerView.addItemDecoration(dividerItemDecoration);*/
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(DistributorBillSummary.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSummary();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
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
                            mainMenu.getItem(1).setIcon(filter.getFilterIcon());
                            loadTransactions();
                        }
                    };
                    filter.getDialog().setOnDismissListener(dismissListener);
                }

                mainMenu.getItem(1).setIcon(filter.getFilterIcon());
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }*/
        return Utility.backDefault(item, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "Result " + resultCode, Toast.LENGTH_LONG).show();
        if (data != null) {
            System.out.println("...... " + requestCode + ":" + resultCode + ":" + data.getExtras() + " ....");
            Log.d("PAYMENT_RESULT", "onActivityResult: data: " + data.getStringExtra("response"));
            String res = data.getStringExtra("response");
            String search = "SUCCESS";
            System.out.println("Get current invoice => " + adapter.getCurrentInvoice());
            if (res.toLowerCase().contains(search.toLowerCase())) {
                //Toast.makeText(this, "Payment Successful " + res, Toast.LENGTH_LONG).show();
                adapter.getCurrentInvoice().setStatus(BillConstants.INVOICE_STATUS_PAID);

            } else {
                //Toast.makeText(this, "Payment Failed " + res, Toast.LENGTH_LONG).show();
                adapter.getCurrentInvoice().setStatus("Failed");
            }
            Intent intent = Utility.nextIntent(this, PaymentSuccessfulActivity.class, true, adapter.getCurrentInvoice(), Utility.INVOICE_KEY);
            Utility.putIntenObject(selectedDistributor, Utility.DISTRIBUTOR_KEY, intent);
            startActivity(intent);
        }
    }
}
