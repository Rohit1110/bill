package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


    public static FragmentInvoiceSummary newInstance(BillUser user) {
        FragmentInvoiceSummary fragment = new FragmentInvoiceSummary();
        fragment.user = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invoice_summary, container, false);
        date = new Date();
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Invoice Summary</font>"));
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
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        loadInvoiceSummary();
    }

    private void loadInvoiceSummary() {
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
                    recyclerView.setAdapter(new InvoicesAdapter(serviceResponse.getUsers(), getActivity()));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }


}