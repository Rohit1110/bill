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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapters.TransactionsAdapter;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/8/2018.
 */

public class FragmentTransactions extends Fragment {

    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private BillUser user;
    private List<BillUser> users;
    private List<BillUser> filterList = new ArrayList<>();
    private Date date;
    private Button requestTransactions;
    private TransactionsAdapter adapter;
    private Button clear;
    private Spinner month;
    private Spinner year;
    private List<String> yearsList;

    public static FragmentTransactions newInstance() {
        FragmentTransactions fragment = new FragmentTransactions();
        //fragment.user = user;*/
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
        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);
        date = new Date();
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Invoice Summary</font>"));
        Utility.AppBarTitle("Transactions", getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_update_invoice_items);

        requestTransactions = (Button) rootView.findViewById(R.id.btn_get_transactions);

        requestTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTransactions();
            }
        });

        month = (Spinner) rootView.findViewById(R.id.spn_txn_month);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_basic_text_white, getResources().getStringArray(R.array.months_arrays));
        month.setAdapter(adapter);
        month.setSelection(Calendar.getInstance().get(Calendar.MONTH) + 1);

        year = (Spinner) rootView.findViewById(R.id.spn_txn_year);
        yearsList = Utility.createYearsArray();
        year.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_basic_text_white, yearsList));
        year.setSelection(yearsList.indexOf(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));

        user = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);

        return rootView;

    }


    @Override
    public void onResume() {
        super.onResume();
        loadTransactions();
    }

    private void loadTransactions() {
        if (user == null || user.getCurrentBusiness() == null) {
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            return;
        }
        if (month.getSelectedItemPosition() < 1 || year.getSelectedItemPosition() < 1) {
            Utility.createAlert(getActivity(), "Please select a month and year!", "Error");
            return;
        }
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        BillInvoice currInvoice = new BillInvoice();
        currInvoice.setMonth(month.getSelectedItemPosition());
        currInvoice.setYear(Integer.parseInt(year.getSelectedItem().toString()));
        request.setInvoice(currInvoice);
        pDialog = Utility.getProgressDialogue("Loading", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getTransactions", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
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
                    users = serviceResponse.getUsers();
                    if (users != null && users.size() > 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        adapter = new TransactionsAdapter(users, getActivity());
                        recyclerView.setAdapter(adapter);
                    } else {
                        recyclerView.setAdapter(new TransactionsAdapter(new ArrayList<BillUser>(), getActivity()));
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
                        for (BillUser customer : users) {
                            System.out.println("Get Name --->>> " + customer.getName());
                            if (customer.getName() != null && customer.getName().toLowerCase().contains(text.toLowerCase()) /*|| comparePhone(item, text)*/) {
                                // Adding Matched items
                                filterList.add(customer);
                            }

                        }
                    }

                    // Set on UI Thread
                    (getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.updateSearchList(filterList);
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