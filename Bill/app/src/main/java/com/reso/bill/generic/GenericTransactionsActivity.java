package com.reso.bill.generic;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.MainActivity;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapters.TransactionsAdapter;
import model.BillFilter;
import util.ServiceUtil;
import util.Utility;

public class GenericTransactionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private BillUser user;
    private List<BillUser> users;
    private List<BillUser> filterList = new ArrayList<>();
    private Date date;
    //private Button requestTransactions;
    private TransactionsAdapter adapter;
    private Button clear;
    private Spinner month;
    private Spinner year;
    private List<String> yearsList;
    private boolean loading;
    private Menu mainMenu;
    private BillFilter filter;
    private DialogInterface.OnDismissListener dismissListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_transactions);
        Utility.setActionBar("All Transactions", getSupportActionBar());

        date = new Date();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_group_customers);

        /*requestTransactions = (Button) findViewById(R.id.btn_get_transactions);

        requestTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTransactions();
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
                    loadTransactions();
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
                    loadTransactions();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
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
                filter(newText);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        menu.add(Menu.NONE, Utility.MENU_ITEM_FILTER, Menu.NONE, "Filter").setIcon(R.drawable.ic_action_filter_list_disabled).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        mainMenu = menu;

        return true;
    }

    private void loadTransactions() {
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
        BillInvoice currInvoice = new BillInvoice();
        currInvoice.setMonth(month.getSelectedItemPosition());
        currInvoice.setYear(Integer.parseInt(year.getSelectedItem().toString()));
        request.setInvoice(currInvoice);
        if (filter != null) {
            request.setCustomerGroup(filter.getGroup());
        }
        pDialog = Utility.getProgressDialogue("Loading", this);
        StringRequest myReq = ServiceUtil.getStringRequest("getTransactions", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, this), request);
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
                    users = serviceResponse.getUsers();
                    if (users != null && users.size() > 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(GenericTransactionsActivity.this));
                        adapter = new TransactionsAdapter(users, GenericTransactionsActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        recyclerView.setAdapter(new TransactionsAdapter(new ArrayList<BillUser>(), GenericTransactionsActivity.this));
                    }
                    //TODO causing double line so removed temporarily
                    /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
                    recyclerView.addItemDecoration(dividerItemDecoration);*/
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericTransactionsActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        }
        return true;
    }

    public void filter(final String text) {

        try {

            //Toast.makeText(this,text,Toast.LENGTH_LONG).show();

            // Searching could be complex..so we will dispatch it to a different thread...
            new Thread(new Runnable() {
                @Override
                public void run() {


                    // Clear the filter list
                    filterList.clear();

                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {

                        /*hideicon = true;
                        invalidateOptionsMenu();*/

                        filterList.addAll(users);


                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (BillUser customer : users) {
                            System.out.println("Get Name --->>> " + customer.getName());
                            if (customer.getName() != null && customer.getName().toLowerCase().contains(text.toLowerCase()) /*|| comparePhone(item, text)*/) {
                                // Adding Matched items
                                filterList.add(customer);
                            }

                        }
                    }

                    // Set on UI Thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.updateSearchList(filterList);
                        }
                    });


                }
            }).start();

        } catch (Exception e) {
            System.out.println("Error in filter contacts");
            e.printStackTrace();
        }


    }
}
