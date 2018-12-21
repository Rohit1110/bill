package com.reso.bill;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.components.ClickListener;
import com.reso.bill.components.RecycleClickListener;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillOrder;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.bo.domain.BillUserLog;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import adapters.CustomerActivityAdapter;
import adapters.CustomerLogActivityAdapter;
import model.BillCustomer;
import util.ServiceUtil;
import util.Utility;

public class CustomerOrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<BillCustomer> list;
    private ProgressDialog pDialog;
    private RadioButton orders, holiday;
    private Spinner months, years;
    private BillUser customer;
    private List<String> yearsList;
    private boolean firstLoad;
    private List<BillOrder> ordersList;
    private List<BillUserLog> log;
    private CustomerLogActivityAdapter logAdapter;
    private boolean logs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_history);

        customer = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.CUSTOMER_KEY);

        Utility.setActionBar("Customer Activity", getSupportActionBar());
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_cust_list);
        orders = (RadioButton) findViewById(R.id.radio_orders);
        orders.setSelected(true);
        orders.setChecked(true);
        holiday = (RadioButton) findViewById(R.id.radio_holiday);
        months = (Spinner) findViewById(R.id.spinner_month);
        years = (Spinner) findViewById(R.id.spinner_year);
        yearsList = Utility.createYearsArray();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CustomerOrderHistoryActivity.this, android.R.layout.simple_spinner_item, yearsList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        years.setAdapter(dataAdapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomerOrderHistoryActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.months_arrays));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        months.setAdapter(adapter);

        years.setSelection(yearsList.indexOf(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
        months.setSelection(Calendar.getInstance().get(Calendar.MONTH) + 1);

        years.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (firstLoad) {
                    loadActivity();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        months.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (firstLoad) {
                    loadActivity();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOrders();
            }
        });

        holiday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogs();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*menu.clear();
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(getResources().getColor(R.color.md_black_1000));
        MenuItemCompat.setActionView(item, searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filter(newText);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadActivity();

    }

    private void loadActivity() {
        if (years.getSelectedItem() == null || years.getSelectedItemPosition() <= 0) {
            return;
        }
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(customer.getCurrentBusiness());
        BillInvoice invoice = new BillInvoice();
        invoice.setYear(new Integer(yearsList.get(yearsList.indexOf(years.getSelectedItem()))));
        invoice.setMonth(Arrays.asList(getResources().getStringArray(R.array.months_arrays)).indexOf(months.getSelectedItem()));
        request.setInvoice(invoice);
        request.setUser(customer);
        pDialog = Utility.getProgressDialogue("Loading..", CustomerOrderHistoryActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("getCustomerActivity", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, CustomerOrderHistoryActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(CustomerOrderHistoryActivity.this);
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                firstLoad = true;
                System.out.println("## response:" + response);
                pDialog.dismiss();

                list = new ArrayList<>();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    ordersList = serviceResponse.getOrders();
                    log = serviceResponse.getLogs();
                    if (!logs) {
                        setOrders();
                    } else {
                        setLogs();
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(CustomerOrderHistoryActivity.this, serviceResponse.getResponse(), "Error");
                }
            }

        };
    }

    private void setOrders() {
        if (ordersList == null) {
            ordersList = new ArrayList<>();
        }
        logAdapter = null;
        recyclerView.setLayoutManager(new LinearLayoutManager(CustomerOrderHistoryActivity.this));
        CustomerActivityAdapter adapter = new CustomerActivityAdapter(ordersList, CustomerOrderHistoryActivity.this);
        recyclerView.setAdapter(adapter);
        logs = false;

    }

    private void setLogs() {
        if (log == null) {
            log = new ArrayList<>();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(CustomerOrderHistoryActivity.this));
        logAdapter = new CustomerLogActivityAdapter(log, CustomerOrderHistoryActivity.this);
        recyclerView.setAdapter(logAdapter);
        logs = true;
        recyclerView.addOnItemTouchListener(new RecycleClickListener(CustomerOrderHistoryActivity.this, recyclerView, new ClickListener() {

            public AlertDialog alertDialog;

            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder builder = Utility.SingleChoiceWithRadioButton(CustomerOrderHistoryActivity.this);
                builder.setSingleChoiceItems(Utility.LIST_OPTIONS, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == Utility.LIST_OPT_DELETE) {
                            List<BillUserLog> userLog = logAdapter.getList();
                            if (userLog != null && userLog.size() > 0 && position < userLog.size()) {
                                BillUserLog log = userLog.get(position);
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                                deleteLog(log);

                            }
                        }
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        }));

    }

    private void deleteLog(BillUserLog log) {
        if (log != null) {
            BillServiceRequest request = new BillServiceRequest();
            request.setUser(customer);
            BillItem subItem = new BillItem();
            subItem.setQuantity(BigDecimal.ZERO);
            subItem.setPrice(BigDecimal.ZERO);
            subItem.setChangeLog(log);
            request.setItem(subItem);
            request.setRequestType("DELETE");
            pDialog = Utility.getProgressDialogue("Saving..", CustomerOrderHistoryActivity.this);
            StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerItemTemp", logDeleteListener(), ServiceUtil.createMyReqErrorListener(pDialog, CustomerOrderHistoryActivity.this), request);
            RequestQueue queue = Volley.newRequestQueue(CustomerOrderHistoryActivity.this);
            queue.add(myReq);
        }
    }

    private Response.Listener<String> logDeleteListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                firstLoad = true;
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlert(CustomerOrderHistoryActivity.this, "Holiday deleted successfully", "Success");
                    loadActivity();
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(CustomerOrderHistoryActivity.this, serviceResponse.getResponse(), "Error");
                }
            }

        };
    }
}
