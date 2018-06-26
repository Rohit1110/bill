package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.List;

import adapters.CustomerActivityAdapter;
import adapters.CustomerListAdapter;
import model.BillCustomer;
import util.ServiceUtil;
import util.Utility;

public class ActivityScreen extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<BillCustomer> list;
    private BillUser user;
    private ProgressDialog pDialog;
    RadioButton orders,holiday;
    Spinner months,years;

    public static ActivityScreen newInstance() {
        ActivityScreen fragment = new ActivityScreen();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_screen, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Customer Activity</font>"));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_cust_list);
        orders = (RadioButton)rootView.findViewById(R.id.radio_orders);
        orders.setSelected(true);
        orders.setChecked(true);
        holiday = (RadioButton)rootView.findViewById(R.id.radio_holiday);
        months=(Spinner)rootView.findViewById(R.id.spinner_month);
        years = (Spinner)rootView.findViewById(R.id.spinner_year);
        List<String> ylist = new ArrayList<String>();
        ylist.add("2018");
        ylist.add("2017");
        ylist.add("2016");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,ylist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        years.setAdapter(dataAdapter);
        //addcust=(Button)rootView.findViewById(R.id.fab_addcustomer);
        //layout = (LinearLayout) rootView.findViewById(R.id.layout_add_cust);
        List<String> mlist = new ArrayList<String>();
        mlist.add("Jan");
        mlist.add("Feb");
        mlist.add("Mar");
        mlist.add("Apr");
        mlist.add("Jun");
        mlist.add("Jul");
        mlist.add("Aug");
        mlist.add("Sep");
        mlist.add("Oct");
        mlist.add("Nov");
        mlist.add("Dec");



        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        months.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        user = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);
        loadCustomers();

    }

    private void loadCustomers() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getAllCustomers", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                list = new ArrayList<>();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (serviceResponse.getUsers() != null && serviceResponse.getUsers().size() > 0) {
                        for (BillUser user : serviceResponse.getUsers()) {
                            list.add(new BillCustomer(user));
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(new CustomerActivityAdapter(list, getActivity(), user));
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

}
