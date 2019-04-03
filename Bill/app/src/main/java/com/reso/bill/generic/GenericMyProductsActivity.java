package com.reso.bill.generic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.List;

import adapters.AddNewspaperAdapter;
import util.ServiceUtil;
import util.Utility;

public class GenericMyProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    BillServiceResponse serviceResponse;
    private AddNewspaperAdapter mAdapter;
    private BillUser user;
    private ProgressDialog pDialog;
    private List<BillItem> businessItems;
    private Button add;

    //private List<BillItem> = new ArrayList<>();
    List<BillItem> filterList = new ArrayList<BillItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_my_products);

        String title = "My Products";
        ActionBar actionBar = getSupportActionBar();
        Utility.setActionBar(title, actionBar);

        recyclerView = findViewById(R.id.recycler_view_newspaper);


        FloatingActionButton addNewProductFab = findViewById(R.id.addNewProductFab);
        addNewProductFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(GenericMyProductsActivity.this, GenericAddProductActivity.class, false));
            }
        });

        user = (BillUser) Utility.readObject(GenericMyProductsActivity.this, Utility.USER_KEY);

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadBusinessItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);

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

    private void loadBusinessItems() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        pDialog = Utility.getProgressDialogue("Loading..", GenericMyProductsActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("loadBusinessItems", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, GenericMyProductsActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericMyProductsActivity.this);
        queue.add(myReq);
    }


    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    businessItems = serviceResponse.getItems();
                    setAdapter(businessItems);
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericMyProductsActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }


    public void filter(final String text) {

        //Toast.makeText(getActivity(),text,Toast.LENGTH_LONG).show();

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // Clear the filter list
                    filterList.clear();

                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {
                        filterList.addAll(businessItems);
                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (BillItem item : businessItems) {

                            if (item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) {
                                // Adding Matched items
                                filterList.add(item);
                            } else if (item.getParentItem() != null && item.getParentItem().getName() != null && item.getParentItem().getName().toLowerCase().contains(text.toLowerCase())) {
                                filterList.add(item);
                            }

                        }
                    }

                    // Set on UI Thread
                    (GenericMyProductsActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setAdapter(filterList);

                        }
                    });
                } catch (Exception e) {
                    System.out.println("Error in filter contacts");
                    e.printStackTrace();
                }


            }
        }).start();

    }

    private void setAdapter(List<BillItem> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(GenericMyProductsActivity.this));
        mAdapter = new AddNewspaperAdapter(list, GenericMyProductsActivity.this);
        mAdapter.setParentActivity(GenericMyProductsActivity.this);
        mAdapter.setUserBusiness(user.getCurrentBusiness());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
