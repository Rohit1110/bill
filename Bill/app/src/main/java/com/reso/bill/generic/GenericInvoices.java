package com.reso.bill.generic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.MainActivity;
import com.reso.bill.R;
import com.reso.bill.components.ClickListener;
import com.reso.bill.components.RecycleClickListener;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.bo.domain.BillUserLog;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapters.generic.CompleteInvoicesAdapter;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/8/2018.
 */

public class GenericInvoices extends Fragment {

    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private BillUser user;
    private List<BillUser> users;
    private List<BillUser> filterList = new ArrayList<>();
    private Date date;
    private Spinner durations;
    //private Spinner year;
    private List<String> yearsList;
    private TextView txtNoPayments;
    private boolean screenLoaded = false;
    private CompleteInvoicesAdapter invoicesAdapter;

    public static GenericInvoices newInstance() {
        GenericInvoices fragment = new GenericInvoices();
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
        SearchView searchView = new SearchView(((GenericDashboard) getActivity()).getSupportActionBar().getThemedContext());
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
        View rootView = inflater.inflate(R.layout.generic_fragment_invoices, container, false);
        date = new Date();
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Invoice Summary</font>"));
        Utility.AppBarTitle("Payments", getActivity());
        recyclerView =  rootView.findViewById(R.id.recycler_update_invoice_items);

        durations =  rootView.findViewById(R.id.spn_txn_duration_filter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_basic_text_white, getResources().getStringArray(R.array.durations_arrays));
        durations.setAdapter(adapter);
        durations.setSelection(1);

        txtNoPayments =  rootView.findViewById(R.id.txt_txn_no_payments);

        user = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);

        durations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (screenLoaded) {
                    loadInvoices();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        recyclerView.addOnItemTouchListener(new RecycleClickListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        FloatingActionButton addNewBillFab = rootView.findViewById(R.id.addNewBillFab);
        addNewBillFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "FAB Tapped", Toast.LENGTH_SHORT).show();
                startActivity(Utility.nextIntent(getActivity(), GenericCreateBillActivity.class, true));
            }
        });

        return rootView;

    }


    @Override
    public void onResume() {
        super.onResume();
        screenLoaded = false;
        loadInvoices();

    }

    private void loadInvoices() {
        if (user == null || user.getCurrentBusiness() == null) {
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            return;
        }
        /*if (durations.getSelectedItemPosition() < 1) {
            Utility.createAlert(getActivity(), "Please select a durations!", "Error");
            return;
        }*/
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        BillUserLog log = new BillUserLog();
        if ("Today".equalsIgnoreCase(durations.getSelectedItem().toString())) {
            log.setFromDate(CommonUtils.startDate(new Date()));
            log.setToDate(CommonUtils.endDate(new Date()));
        } else if ("This week".equalsIgnoreCase(durations.getSelectedItem().toString())) {
            log.setFromDate(CommonUtils.getWeekFirstDate());
            log.setToDate(CommonUtils.getWeekLastDate());
        } else {
            int currentMonth = CommonUtils.getCalendarValue(new Date(), Calendar.MONTH) + 1;
            Integer currentYear = CommonUtils.getCalendarValue(new Date(), Calendar.YEAR);
            if ("This month".equalsIgnoreCase(durations.getSelectedItem().toString())) {
                log.setFromDate(CommonUtils.getMonthFirstDate(currentMonth, currentYear));
                log.setToDate(CommonUtils.getMonthLastDate(currentMonth, currentYear));
            } else if ("Last 6 months".equalsIgnoreCase(durations.getSelectedItem().toString())) {
                Date oldDate = CommonUtils.add(-6, new Date(), Calendar.MONTH);
                log.setFromDate(CommonUtils.getMonthFirstDate(CommonUtils.getCalendarValue(oldDate, Calendar.MONTH), CommonUtils.getCalendarValue(oldDate, Calendar.YEAR)));
                log.setToDate(CommonUtils.getMonthLastDate(currentMonth, currentYear));
            }
        }

        if(log.getFromDate() != null && log.getToDate() != null) {
            String title = "Payments " + CommonUtils.convertDate(log.getFromDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR);
            if(!"Today".equalsIgnoreCase(durations.getSelectedItem().toString())) {
                title = title + " - " +  CommonUtils.convertDate(log.getToDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR);
            }
            Utility.AppBarTitle(title, getActivity());
        }

        BillItem item = new BillItem();
        item.setChangeLog(log);
        if (user.getCurrentBusiness().getBusinessSector() != null) {
            request.setRequestType(user.getCurrentBusiness().getBusinessSector().getFramework());
        } else {
            request.setRequestType(BillConstants.FRAMEWORK_GENERIC);
        }
        request.setItem(item);
        pDialog = Utility.getProgressDialogue("Loading", getActivity());
        StringRequest myReq = ServiceUtil.getBusinessStringRequest("getAllInvoices", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();
                screenLoaded = true;
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    users = serviceResponse.getUsers();
                    if (users != null && users.size() > 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        invoicesAdapter = new CompleteInvoicesAdapter(users, getActivity());
                        recyclerView.setAdapter(invoicesAdapter);
                        txtNoPayments.setVisibility(View.GONE);
                    } else {
                        txtNoPayments.setVisibility(View.VISIBLE);
                        txtNoPayments.setText("No payments " + durations.getSelectedItem().toString().toLowerCase());
                        invoicesAdapter = new CompleteInvoicesAdapter(new ArrayList<BillUser>(), getActivity());
                        recyclerView.setAdapter(invoicesAdapter);
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }


    public void filter(final String text) {

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
                            invoicesAdapter.updateSearchList(filterList);
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Error in filter contacts");
                    e.printStackTrace();
                }


            }
        }).start();

    }

    private void deleteInvoice(BillInvoice invoice) {
        if (invoice == null || invoice.getId() == null) {
            return;
        }
        invoice.setStatus(BillConstants.INVOICE_STATUS_DELETED);
        //}
        BillServiceRequest request = new BillServiceRequest();
        request.setInvoice(invoice);
        pDialog = Utility.getProgressDialogue("Deleting..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerInvoice", invoiceDeletionResponse(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> invoiceDeletionResponse() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    loadInvoices();
                    Utility.createAlert(getContext(), "Invoice deleted successfully!", "Done");
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }

            }

        };


    }


}