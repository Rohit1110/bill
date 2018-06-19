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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import adapters.CustomerSubcriptionAdapter;
import adapters.DeliveriesAdapter;
import model.CustomerSubscription;
import util.ServiceUtil;
import util.Utility;

public class AddSubcription extends Fragment {

    private List<CustomerSubscription> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private DeliveriesAdapter adapter;
    private Spinner sp;
    private TextView txtweekdays, customerName;
    private ImageView imgpause, imgdiscontinue;
    private BillUser customer;
    private ProgressDialog profileLoader;
    private ProgressDialog itemLoader;
    private Button fabAddCustomerItem;
    private List<BillItem> businessItems;


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
        fabAddCustomerItem = (Button) rootView.findViewById(R.id.fab_add_customer_item);
        fabAddCustomerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCustomerItem();
            }
        });


        customerName = (TextView) rootView.findViewById(R.id.txt_add_subscription_customer_name);
        if (customer != null) {
            customerName.setText(customer.getName());
        }
        return rootView;
    }

    private void addCustomerItem() {

        BillItem selectedItem = new BillItem();
        selectedItem.setQuantity(new BigDecimal(1));
        selectedItem.setParentItem((BillItem) Utility.findInStringList(businessItems, sp.getSelectedItem().toString()));
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(customer);
        request.setItem(selectedItem);
        profileLoader = Utility.getProgressDialogue("Saving..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerItem", saveItemListener(), ServiceUtil.createMyReqErrorListener(profileLoader, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);

    }

    private Response.Listener<String> saveItemListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                profileLoader.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlert(getContext(), "Saved successfully!", "Done");
                    loadCustomerProfile();
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }

            }

        };
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCustomerProfile();
        loadNewsPapers();

    }

    private void loadNewsPapers() {
        BillServiceRequest request = new BillServiceRequest();
        if (customer.getCurrentBusiness() == null) {
            BillUser vendor = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);
            if (vendor != null) {
                customer.setCurrentBusiness(vendor.getCurrentBusiness());
            }
        }
        request.setBusiness(customer.getCurrentBusiness());
        itemLoader = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("loadBusinessItems", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(profileLoader, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private void loadCustomerProfile() {

        BillServiceRequest request = new BillServiceRequest();
        BillUser user = new BillUser();
        if (customer.getCurrentSubscription() != null) {
            user.setId(customer.getCurrentSubscription().getId());
        } else {
            user.setId(customer.getId());
        }
        request.setUser(user);
        profileLoader = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getCustomerProfile", customerProfileLoader(), ServiceUtil.createMyReqErrorListener(profileLoader, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> customerProfileLoader() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                profileLoader.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (TextUtils.isEmpty(customerName.getText())) {
                        customerName.setText(serviceResponse.getUser().getName());
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(new CustomerSubcriptionAdapter(serviceResponse.getUser().getCurrentSubscription().getItems(), getActivity(), customer, getActivity()));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }

            }

        };
    }


    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                itemLoader.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    businessItems = serviceResponse.getItems();
                    List<String> strings = Utility.convertToStringArrayList(businessItems);
                    System.out.println("String list == " + strings);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, strings);
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
