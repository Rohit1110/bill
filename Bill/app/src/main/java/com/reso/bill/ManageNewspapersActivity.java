package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.List;

import adapters.AddNewspaperAdapter;
import util.ServiceUtil;
import util.Utility;

public class ManageNewspapersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    //private List<ListTwo> list = new ArrayList<>();
    private List<BillItem> listtwo = new ArrayList<>();
    BillServiceResponse serviceResponse;
    private AddNewspaperAdapter mAdapter;
    private LinearLayout layoutaddnewspaper;
    private BillUser user;
    private ProgressDialog pDialog;
    private List<BillItem> businessItems;
    private Button add;
    private ImageView img;

    //private List<BillItem> = new ArrayList<>();
    List<BillItem> filterList = new ArrayList<BillItem>();
    List<BillItem> list = new ArrayList<BillItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_newspapers);

        Utility.setActionBar("Manage Newspapers", getSupportActionBar());
        //recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        // ManageNewspapersActivity.this.getActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_newspaper);
        img = (ImageView) findViewById(R.id.btn_paus);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Utility.nextFragment(ManageNewspapersActivity.this, PauseBusiness.newInstance());
                startActivity(Utility.nextIntent(ManageNewspapersActivity.this, PauseBusinessActivity.class, true));
            }
        });
        add = (Button) findViewById(R.id.btn_gn_add_product);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SelectNewspaper fragment = new SelectNewspaper();
                //fragment.setSelectedItems(businessItems);
                //Utility.nextFragment(ManageNewspapersActivity.this, fragment);
                BillBusiness currBusiness = new BillBusiness();
                currBusiness.setItems(businessItems);
                startActivity(Utility.nextIntent(ManageNewspapersActivity.this, SelectNewspaperActivity.class, true, currBusiness, Utility.BUSINESS_KEY));
            }
        });

        user = (BillUser) Utility.readObject(ManageNewspapersActivity.this, Utility.USER_KEY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, this);
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
    public void onResume() {
        super.onResume();

        if (user == null || user.getCurrentBusiness() == null) {
            Utility.createAlert(ManageNewspapersActivity.this, "Please complete your profile first!", "Error");
            return;
        }

        loadNewsPapers();

    }

    private void loadNewsPapers() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        pDialog = Utility.getProgressDialogue("Loading..", ManageNewspapersActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("loadBusinessItems", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, ManageNewspapersActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(ManageNewspapersActivity.this);
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
                    recyclerView.setLayoutManager(new LinearLayoutManager(ManageNewspapersActivity.this));
                    businessItems = new ArrayList<>();
                    businessItems = serviceResponse.getItems();
                    mAdapter = new AddNewspaperAdapter(businessItems, ManageNewspapersActivity.this);
                    recyclerView.setAdapter(mAdapter);
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(ManageNewspapersActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }


    public void filter(final String text) {

        //Toast.makeText(ManageNewspapersActivity.this,text,Toast.LENGTH_LONG).show();

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
                    (ManageNewspapersActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(ManageNewspapersActivity.this, "SSSSSSSSSSSSSSSSS", Toast.LENGTH_LONG).show();
                            recyclerView.setLayoutManager(new LinearLayoutManager(ManageNewspapersActivity.this));
                            mAdapter = new AddNewspaperAdapter(filterList, ManageNewspapersActivity.this);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();

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
