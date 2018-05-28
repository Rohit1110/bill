package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.List;

import adapters.CustomerSubcriptionAdapter;
import adapters.ListOneAdapter;
import model.CustomerSubscription;
import util.ServiceUtil;
import util.Utility;

public class AddSubcription extends Fragment {

    private List<CustomerSubscription> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListOneAdapter adapter;
    private Spinner sp;
    private TextView txtchangequty, txtweekdays;
    private ImageView imgpause, imgdiscontinue;
    private BillUser customer;
    private ProgressDialog pDialog;

    public static AddSubcription newInstance(BillUser user) {
        AddSubcription fragment = new AddSubcription();
        fragment.customer = user;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_add_subcription, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Subscribed NewsPapers</font>"));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_newspaper_subcription);

        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        sp = (Spinner) rootView.findViewById(R.id.spinner_newspaper);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*CustomerSubscription lone = new CustomerSubscription();
        for (int i = 0; i < 15; i++) {
            System.out.println(i);
            lone.setImgUrl(R.drawable.sakal);

            list.add(lone);
        }*/
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new CustomerSubcriptionAdapter(customer.getCurrentSubscription().getItems(), getActivity()));
    }

    private void loadNewsPapers() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(customer.getCurrentBusiness());
        pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("loadBusinessItems", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
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
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Utility.convertToStringArrayList(serviceResponse.getItems()));
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(dataAdapter);
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }

            }

        };
    }


}
