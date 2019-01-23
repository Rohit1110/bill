package com.reso.bill;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by Rohit on 5/8/2018.
 */

public class DailySummaryFragment extends Fragment {
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


    public static DailySummaryFragment newInstance() {
        DailySummaryFragment fragment = new DailySummaryFragment();
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView((Utility.castActivity(getActivity())).getSupportActionBar().getThemedContext());
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
        });

        menu.add(Menu.NONE, Utility.MENU_ITEM_FILTER, Menu.NONE, "Filter").setIcon(R.drawable.ic_action_filter_list_disabled).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        fragmentMenu = menu;
        //searchView.setMenuItem(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Utility.MENU_ITEM_FILTER:
                System.out.println("FIlter called ...");
                if (filter == null) {
                    filter = new BillFilter(getActivity(), user);
                }
                filter.showFilterDialog();

                if (dismissListener == null) {
                    dismissListener = new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            fragmentMenu.getItem(1).setIcon(filter.getFilterIcon());
                            loadDailySummary(null);
                        }
                    };
                    filter.getDialog().setOnDismissListener(dismissListener);
                }

                fragmentMenu.getItem(1).setIcon(filter.getFilterIcon());
                return true;
        }
        return true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_two, container, false);
        date = new Date();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_order);
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Total Orders - " + CommonUtils.convertDate(date) + "</font>"));
        Utility.AppBarTitle("Total Orders - " + CommonUtils.convertDate(date, Utility.DATE_FORMAT_DISPLAY), getActivity());
        user = (BillUser) Utility.readObject(getActivity(), Utility.USER_KEY);
        totalProfit = rootView.findViewById(R.id.txt_daily_total_profit);
        totalCost = rootView.findViewById(R.id.txt_daily_total_cost);
        switchView = rootView.findViewById(R.id.btn_daily_summary_switch_view);
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
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (user == null || user.getCurrentBusiness() == null) {
            Utility.createAlert(getContext(), "Please complete your profile information first!", "Error");
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
        pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getOrderSummary", createMyReqSuccessListener(requestType), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener(final String requestType) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (requestType != null && "Distributor".equals(requestType)) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(new VendorItemPayablesAdapter(serviceResponse.getUsers()));
                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }
            }

        };
    }

}