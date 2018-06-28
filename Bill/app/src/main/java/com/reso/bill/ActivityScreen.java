package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillOrder;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.bo.domain.BillUserLog;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import adapters.CustomerActivityAdapter;
import adapters.CustomerLogActivityAdapter;
import model.BillCustomer;
import util.ServiceUtil;
import util.Utility;

public class ActivityScreen extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<BillCustomer> list;
    private ProgressDialog pDialog;
    private RadioButton orders, holiday;
    private Spinner months, years;
    private BillUser customer;
    private List<String> yearsList;
    private boolean firstLoad;
    private List<BillOrder> ordersList;
    private List<BillUserLog> log;

    public static ActivityScreen newInstance(BillUser customer) {
        ActivityScreen fragment = new ActivityScreen();
        fragment.customer = customer;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_screen, container, false);
        Utility.AppBarTitle("Customer Activities", getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_cust_list);
        orders = (RadioButton) rootView.findViewById(R.id.radio_orders);
        orders.setSelected(true);
        orders.setChecked(true);
        holiday = (RadioButton) rootView.findViewById(R.id.radio_holiday);
        months = (Spinner) rootView.findViewById(R.id.spinner_month);
        years = (Spinner) rootView.findViewById(R.id.spinner_year);
        yearsList = Utility.createYearsArray();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, yearsList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        years.setAdapter(dataAdapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.months_arrays));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        months.setAdapter(adapter);

        years.setSelection(yearsList.indexOf(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
        months.setSelection(Calendar.getInstance().get(Calendar.MONTH));

        years.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (firstLoad) {
                    loadActivity();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        months.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (firstLoad) {
                    loadActivity();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOrders();
            }
        });

        holiday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogs();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadActivity();

    }

    private void loadActivity() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(customer.getCurrentBusiness());
        BillInvoice invoice = new BillInvoice();
        invoice.setYear(new Integer(yearsList.get(yearsList.indexOf(years.getSelectedItem()))));
        invoice.setMonth(Arrays.asList(getResources().getStringArray(R.array.months_arrays)).indexOf(months.getSelectedItem()) + 1);
        request.setInvoice(invoice);
        request.setUser(customer);
        pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getCustomerActivity", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                firstLoad = true;
                System.out.println("## response:" + response);
                pDialog.dismiss();

                list = new ArrayList<>();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    ordersList = serviceResponse.getOrders();
                    log = serviceResponse.getLogs();
                    setOrders();
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }
            }

        };
    }

    private void setOrders() {
        if (ordersList == null) {
            ordersList = new ArrayList<>();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new CustomerActivityAdapter(ordersList, getActivity()));

    }

    private void setLogs() {
        if (log == null) {
            log = new ArrayList<>();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new CustomerLogActivityAdapter(log, getActivity()));
    }

}
