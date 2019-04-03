package com.reso.bill.generic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillCustomerGroup;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.List;

import adapters.generic.CustomerGroupsAdapter;
import util.ServiceUtil;
import util.Utility;

public class GenericCustomerGroupsActivity extends AppCompatActivity {

    private List<BillCustomerGroup> filterList = new ArrayList<>();
    private List<BillCustomerGroup> groups;
    private CustomerGroupsAdapter adapter;
    private ProgressDialog pDialog;
    private BillUser user;
    private RecyclerView recyclerView;
    private FloatingActionButton addGroup;
    private TextView noOfGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_customer_groups);

        Utility.setActionBar("Customer Groups", getSupportActionBar());

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);

        recyclerView = findViewById(R.id.recycler_group_customers);
        addGroup = findViewById(R.id.btn_add_customer_group);

        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Utility.nextIntent(GenericCustomerGroupsActivity.this, GenericAddCustomerGroupActivity.class, false));
            }
        });

        noOfGroups = findViewById(R.id.txt_total_groups);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomerGroups();
    }

    private void loadCustomerGroups() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        pDialog = Utility.getProgressDialogue("Loading ..", this);
        StringRequest myReq = ServiceUtil.getStringRequest("getCustomerGroups", loadGroupsResponse(), ServiceUtil.createMyReqErrorListener(pDialog, this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
    }

    private Response.Listener<String> loadGroupsResponse() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {

                    recyclerView.setLayoutManager(new LinearLayoutManager(GenericCustomerGroupsActivity.this));
                    groups = serviceResponse.getGroups();
                    adapter = new CustomerGroupsAdapter(groups, GenericCustomerGroupsActivity.this);
                    recyclerView.setAdapter(adapter);
                    if (groups != null && groups.size() > 0) {
                        noOfGroups.setText("Total no of groups - " + groups.size());
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericCustomerGroupsActivity.this, serviceResponse.getResponse(), "Error");
                }

            }

        };
    }


    private void filter(final String text) {
        if (groups == null || groups.size() == 0) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // Clear the filter list
                    filterList.clear();

                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {
                        filterList.addAll(groups);
                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (BillCustomerGroup group : groups) {
                            if (group.getGroupName() != null && group.getGroupName().toLowerCase().contains(text.toLowerCase())) {
                                // Adding Matched items
                                filterList.add(group);
                            }
                        }
                    }

                    // Set on UI Thread
                    (GenericCustomerGroupsActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter != null) {
                                adapter.updateSearchList(filterList);
                            }

                        }
                    });
                } catch (Exception e) {
                    System.out.println("Error in filter contacts");
                    e.printStackTrace();
                }


            }
        }).start();
    }
}
