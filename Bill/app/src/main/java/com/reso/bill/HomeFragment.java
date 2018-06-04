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

import adapters.ListOneAdapter;
import model.BillCustomer;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/8/2018.
 */

public class HomeFragment extends Fragment {
    List<BillCustomer> list = new ArrayList<>();
    RecyclerView recyclerView;
    ListOneAdapter adapter;
    private ProgressDialog pDialog;
    private BillUser user;
    private List<BillUser> users;
    private Date date;

    public static HomeFragment newInstance(BillUser user) {
        HomeFragment fragment = new HomeFragment();
        fragment.user = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_one, container, false);
        date = new Date();
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Deliveries " + CommonUtils.convertDate(date) + "</font>"));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        loadDeliveries();
    }

    private void loadDeliveries() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        request.setRequestedDate(date);
        pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getDeliveries", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
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
                    addUsers("A");
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

    private void addUsers(String status) {
        if (users != null && users.size() > 0) {
            for (BillUser user : users) {
                if(status != null && status.equals(user.getStatus())) {
                    list.add(new BillCustomer(user));
                }
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new ListOneAdapter(list));
        }
    }


}