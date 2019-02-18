package com.reso.bill;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.VendorItemPayablesAdapter;
import model.BillFilter;
import model.ListTwo;
import util.ServiceUtil;
import util.Utility;

public class DistributorsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ListTwo> list = new ArrayList<>();
    private BillUser user;
    private ProgressDialog pDialog;
    private Date date;
    private TextView totalProfit, totalCost;
    private Button switchView;
    private boolean distributorView;
    private Menu fragmentMenu;
    private BillFilter filter;
    private DialogInterface.OnDismissListener dismissListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributors);

        Utility.setActionBar("My Distributors", getSupportActionBar());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_order);
        //DailySummaryActivity.this.setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Total Orders - " + CommonUtils.convertDate(date) + "</font>"));

        user = (BillUser) Utility.readObject(this, Utility.USER_KEY);

        /*totalProfit = findViewById(R.id.txt_daily_total_profit);
        totalCost = findViewById(R.id.txt_daily_total_cost);
        switchView = findViewById(R.id.btn_daily_summary_switch_view);
        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDistributors();
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDistributors();
    }

    private void loadDistributors() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        /*if (filter != null) {
            request.setCustomerGroup(filter.getGroup());
        }*/
        pDialog = Utility.getProgressDialogue("Loading..", this);
        StringRequest myReq = ServiceUtil.getStringRequest("getDistributors", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    //if (requestType != null && "Distributor".equals(requestType)) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(DistributorsActivity.this));
                    VendorItemPayablesAdapter adapter = new VendorItemPayablesAdapter(serviceResponse.getUsers());
                    adapter.setActivity(DistributorsActivity.this);
                    recyclerView.setAdapter(adapter);
                    if (serviceResponse.getInvoice() != null) {
                        //TODO Total payable
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(DistributorsActivity.this, serviceResponse.getResponse(), "Error");
                }
            }

        };
    }
}
