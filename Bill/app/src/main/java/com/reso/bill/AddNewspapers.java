package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.List;

import adapters.AddNewspaperAdapter;
import model.ListTwo;
import util.ServiceUtil;
import util.Utility;

public class AddNewspapers extends Fragment {
    private RecyclerView recyclerView;
    private List<ListTwo> list = new ArrayList<>();
    private LinearLayout layoutaddnewspaper;
    private BillUser user;
    private ProgressDialog pDialog;
    private List<BillItem> businessItems;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_add_newspapers, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Add Newspapers</font>"));
        //recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/
        layoutaddnewspaper = (LinearLayout) rootView.findViewById(R.id.layout_addnewspaper);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_newspaper);
        layoutaddnewspaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectNewspaper fragment = new SelectNewspaper();
                fragment.setSelectedItems(businessItems);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();
            }
        });



        user = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (user == null || user.getCurrentBusiness() == null) {
            Utility.createAlert(getContext(), "Please complete your profile first!", "Error");
            return;
        }

        loadNewsPapers();

    }

    private void loadNewsPapers() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
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
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    businessItems = serviceResponse.getItems();
                    recyclerView.setAdapter(new AddNewspaperAdapter(serviceResponse.getItems(), getContext()));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }
}
