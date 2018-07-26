package com.reso.bill;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
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

import adapters.DeliveriesAdapter;
import adapters.InvoicesAdapter;
import model.BillCustomer;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/8/2018.
 */

public class FragmentInvoiceSummary extends Fragment {
    private List<BillCustomer> orders = new ArrayList<>();
    private List<BillCustomer> noOrders = new ArrayList<>();
    private RecyclerView recyclerView;
    private DeliveriesAdapter adapter;
    private ProgressDialog pDialog;
    private BillUser user;
    private List<BillUser> users;
    private Date date;
    private TextView noOrdersMessage;
    private RadioButton deliveries;
    private RadioButton noDeliveries;
    private TextView totalPendingCount;
    private List<BillUser> list = new ArrayList<>();
    private List<BillUser> filterList = new ArrayList<>();

    public static FragmentInvoiceSummary newInstance(BillUser user) {
        FragmentInvoiceSummary fragment = new FragmentInvoiceSummary();
        fragment.user = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((Dashboard) getActivity()).getSupportActionBar().getThemedContext());
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

        //searchView.setMenuItem(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invoice_summary, container, false);
        date = new Date();
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Invoice Summary</font>"));
        Utility.AppBarTitle("Pending Invoices", getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        /*deliveries = (RadioButton) rootView.findViewById(R.id.radio_deliveries);

        deliveries.setSelected(true);
        deliveries.setChecked(true);
        deliveries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDeliveriesListView(orders);
            }
        });

        noDeliveries = (RadioButton) rootView.findViewById(R.id.radio_no_deliveries);
        noDeliveries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDeliveriesListView(noOrders);
            }
        });

        noOrdersMessage = (TextView) rootView.findViewById(R.id.txt_no_orders);*/

        totalPendingCount = (TextView) rootView.findViewById(R.id.txt_total_pending_bills);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        loadInvoiceSummary();
    }

    private void loadInvoiceSummary() {
        if(user == null || user.getCurrentBusiness() == null) {
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            return;
        }
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        request.setRequestedDate(date);
        pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getInvoiceSummary", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                     users = serviceResponse.getUsers();
                    recyclerView.setAdapter(new InvoicesAdapter(users, getActivity()));
                    if(users != null) {
                        totalPendingCount.setText("Total pending bills - " + users.size());
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
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

                        /*hideicon = true;
                        invalidateOptionsMenu();*/

                        filterList.addAll(users);


                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (BillUser item : users) {
                            System.out.println("Get Name --->>> "+ item.getName());
                            if (item.getName().toLowerCase().contains(text.toLowerCase()) /*|| comparePhone(item, text)*/) {
                                // Adding Matched items
                                filterList.add(item);
                            }

                        }
                    }

                    // Set on UI Thread
                    (getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Notify the List that the DataSet has changed...
                           /* adapter = new ContactListAdapter(SearchAppointmentActivity.this, filterList);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(SearchAppointmentActivity.this, 1);
                            recyclerView_contact.setLayoutManager(mLayoutManager);
                            recyclerView_contact.setAdapter(adapter);*/
                           /* recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new DeliveriesAdapter(filterList, getActivity(), user);
                            recyclerView.setAdapter(adapter);*/
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(new InvoicesAdapter(filterList, getActivity()));
                            //recyclerView.setAdapter(adapter);


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