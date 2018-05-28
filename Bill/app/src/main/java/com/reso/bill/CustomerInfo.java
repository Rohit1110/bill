package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillLocation;
import com.rns.web.billapp.service.bo.domain.BillSubscription;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import model.BillCustomer;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/16/2018.
 */

public class CustomerInfo extends Fragment {
    private FloatingActionButton fabsubscription;
    private List<BillCustomer> list = new ArrayList<>();
    private LinearLayout layout;
    private BillUser customer;
    private EditText name, contact, email, address, serviceCharge;
    private Spinner areas;
    private BillUser user;
    private ProgressDialog pDialog;

    public static CustomerInfo newInstance(BillUser selectedCustomer) {
        CustomerInfo fragment = new CustomerInfo();
        if(selectedCustomer != null) {
            fragment.setCustomer(selectedCustomer);
        }
        return fragment;
    }

    public void setCustomer(BillUser customer) {
        this.customer = customer;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.customer_info_main, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Add Customer</font>"));
        //layout = (LinearLayout) rootView.findViewById(R.id.layout_subscriptions);
        fabsubscription = (FloatingActionButton) rootView.findViewById(R.id.fab_save_customer);
        fabsubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillSubscription subscription = null;
                if(customer == null) {
                    customer = new BillUser();
                    subscription = new BillSubscription();
                } else {
                    subscription = customer.getCurrentSubscription();
                }
                customer.setName(name.getText().toString());
                customer.setEmail(email.getText().toString());
                customer.setPhone(contact.getText().toString());
                customer.setAddress(address.getText().toString());
                subscription.setServiceCharge(new BigDecimal(serviceCharge.getText().toString()));
                if(areas.getSelectedItem() == null || areas.getSelectedItem().toString().trim().length() == 0) {
                    Utility.createAlert(getContext(), "Please select a location!", "Error");
                    return;
                }
                subscription.setArea(findArea());
                customer.setCurrentSubscription(subscription);
                saveCustomer();
            }
        });

        /*layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddSubcription fragment = new AddSubcription();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();

            }
        });*/

        name = (EditText) rootView.findViewById(R.id.et_customer_name);
        email = (EditText) rootView.findViewById(R.id.et_customer_email);
        contact = (EditText) rootView.findViewById(R.id.et_customer_phone);
        address = (EditText) rootView.findViewById(R.id.et_customer_address);
        areas = (Spinner) rootView.findViewById(R.id.sp_customer_area);
        serviceCharge = (EditText) rootView.findViewById(R.id.et_customer_service_charge);

        user = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);

        return rootView;
    }

    private BillLocation findArea() {
        if(user != null && user.getCurrentBusiness() != null && user.getCurrentBusiness().getBusinessLocations() != null) {
            for(BillLocation loc: user.getCurrentBusiness().getBusinessLocations()) {
                if(loc.getName().equals(areas.getSelectedItem().toString())) {
                    return loc;
                }
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLocations();
    }

    private void loadLocations() {
        if (user != null && user.getCurrentBusiness() != null && user.getCurrentBusiness().getBusinessLocations() != null) {
            List<String> locationsList = new ArrayList<>();
            for (BillLocation loc : user.getCurrentBusiness().getBusinessLocations()) {
                locationsList.add(loc.getName());
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, locationsList); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            areas.setAdapter(spinnerArrayAdapter);
        }
    }

    private void saveCustomer() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        request.setUser(customer);
        pDialog = Utility.getProgressDialogue("Saving customer..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomer", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
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
                    Utility.createAlert(getContext(), "Customer details updated successfully!", "Done");
                    Utility.nextFragment(getActivity(), new CustomerList());
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }


}
