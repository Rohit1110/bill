package com.reso.bill;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.VendorItemPayablesAdapter;
import adapters.VendorItemSummaryAdapter;
import model.BillFilter;
import model.ListTwo;
import util.ServiceUtil;
import util.Utility;

public class DailySummaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ListTwo> list = new ArrayList<>();
    private BillUser user;
    private ProgressDialog pDialog;
    private Date date;
    private TextView totalProfit, totalCost;
    //private Button switchView;
    private boolean distributorView;
    private Menu fragmentMenu;
    private BillFilter filter;
    private DialogInterface.OnDismissListener dismissListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_summary);

        date = new Date();
        Utility.setActionBar("Total Orders - " + CommonUtils.convertDate(date, Utility.DATE_FORMAT_DISPLAY), getSupportActionBar());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_order);
        //DailySummaryActivity.this.setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Total Orders - " + CommonUtils.convertDate(date) + "</font>"));

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);
        totalProfit = findViewById(R.id.txt_daily_total_profit);
        totalCost = findViewById(R.id.txt_daily_total_cost);
        /*switchView = findViewById(R.id.btn_daily_summary_switch_view);
        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (distributorView) {
                    distributorView = false;
                    loadDailySummary(null);
                } else {
                    distributorView = true;
                    loadDailySummary("Distributor");
                }
            }
        });*/

        //Utility.AppBarTitle(, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        /*menu.add(Menu.NONE, Utility.MENU_ITEM_SEARCH, 0, "Search").setIcon(R.drawable.ic_search_black_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem item = menu.getItem(0);

        //getMenuInflater().inflate(R.menu.search, menu);
        //MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        //MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
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

        menu.add(Menu.NONE, Utility.MENU_ITEM_FILTER, 1, "Filter").setIcon(R.drawable.ic_action_filter_list_disabled).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, Utility.MENU_ITEM_EXPORT, 2, "Export").setIcon(R.drawable.ic_action_picture_as_pdf).setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        fragmentMenu = menu;
        return true;
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
                            fragmentMenu.getItem(0).setIcon(filter.getFilterIcon());
                            loadDailySummary(null);
                        }
                    };
                    filter.getDialog().setOnDismissListener(dismissListener);
                }

                fragmentMenu.getItem(0).setIcon(filter.getFilterIcon());
                return true;
            case android.R.id.home:
                //Back button click
                finish();
                return true;
            case Utility.MENU_ITEM_EXPORT:
                try {
                    System.out.println("PDF clicked ...");
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, Utility.MY_PERMISSIONS_REQUEST_CONTACTS);
                        return true;
                    }
                    String dateString = CommonUtils.convertDate(date);
                    Utility.exportPendingInvoices(filter, user, this, "date=" + dateString, "Daily orders-" + dateString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (user == null || user.getCurrentBusiness() == null) {
            Utility.createAlert(this, "Please complete your profile information first!", "Error");
            return;
        }

        loadDailySummary(null);

    }

    private void loadDailySummary(String requestType) {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        request.setRequestedDate(date);
        request.setRequestType(requestType);
        if (filter != null) {
            request.setCustomerGroup(filter.getGroup());
        }
        pDialog = Utility.getProgressDialogue("Loading..", DailySummaryActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("getOrderSummary", createMyReqSuccessListener(requestType), ServiceUtil.createMyReqErrorListener(pDialog, DailySummaryActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(DailySummaryActivity.this);
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener(final String requestType) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (requestType != null && "Distributor".equals(requestType)) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(DailySummaryActivity.this));
                        recyclerView.setAdapter(new VendorItemPayablesAdapter(serviceResponse.getUsers()));
                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(DailySummaryActivity.this));
                        recyclerView.setAdapter(new VendorItemSummaryAdapter(serviceResponse.getItems()));
                    }

                    if (serviceResponse.getInvoice() != null) {
                        if (serviceResponse.getInvoice().getAmount() != null) {
                            totalProfit.setText("Total sold  " + serviceResponse.getInvoice().getAmount().toString() + " /-");
                        }
                        if (serviceResponse.getInvoice().getPayable() != null) {
                            totalCost.setText("Total Paid  " + serviceResponse.getInvoice().getPayable().toString() + " /-");
                        }
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(DailySummaryActivity.this, serviceResponse.getResponse(), "Error");
                }
            }

        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String dateString = CommonUtils.convertDate(date);
                    Utility.exportPendingInvoices(filter, user, this, "date=" + dateString, "Daily orders-" + dateString);
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
