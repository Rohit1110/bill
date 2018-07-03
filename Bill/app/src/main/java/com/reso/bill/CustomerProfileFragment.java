package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import model.ListThree;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/8/2018.
 */

public class CustomerProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private BillUser selectedCustomer;
    private TextView name, email, phone, address, billsDue, lastPaid;
    private View manageSubscriptions,viewactivity;
    private TextView editProfile, billDetails;


    private List<ListThree> list = new ArrayList<>();
    private ProgressDialog pDialog;

    public static CustomerProfileFragment newInstance(BillUser customer) {
        CustomerProfileFragment fragment = new CustomerProfileFragment();
        fragment.selectedCustomer = customer;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_profile, container, false);
        //recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_bill_details);
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Customer Profile</font>"));
        Utility.AppBarTitle("Customer Profile",getActivity());
        name = (TextView) rootView.findViewById(R.id.txt_profile_customer_name);
        email = (TextView) rootView.findViewById(R.id.txt_profile_customer_email);
        phone = (TextView) rootView.findViewById(R.id.txt_profile_customer_phone);
        address = (TextView) rootView.findViewById(R.id.txt_profile_customer_address);
        editProfile = (TextView) rootView.findViewById(R.id.btn_edit_profile);
        billsDue = (TextView) rootView.findViewById(R.id.txt_profile_bills_due);
        lastPaid = (TextView) rootView.findViewById(R.id.txt_profile_last_paid_bill);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), CustomerInfo.newInstance(selectedCustomer));
            }
        });

        manageSubscriptions = (View) rootView.findViewById(R.id.layout_profile_customer_subscriptions);

        manageSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), AddSubcription.newInstance(selectedCustomer));
            }
        });

        billDetails = (TextView) rootView.findViewById(R.id.btn_view_customer_bills);

        billDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), FragmentCustomerInvoices.newInstance(selectedCustomer));
            }
        });
        viewactivity= (View) rootView.findViewById(R.id.layout_profile_customer_activities);
        viewactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), ActivityScreen.newInstance(selectedCustomer));
            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (selectedCustomer != null) {
            name.setText(selectedCustomer.getName());
            email.setText(selectedCustomer.getEmail());
            phone.setText(selectedCustomer.getPhone());
            address.setText(selectedCustomer.getAddress());
        } else {
            Utility.createAlert(getContext(), "No Customer selected", "Error");
        }

        loadCustomerProfile();

    }

    //
    private void loadCustomerProfile() {
        BillServiceRequest request = new BillServiceRequest();
        BillUser user = new BillUser();
        if (selectedCustomer.getCurrentSubscription() != null) {
            user.setId(selectedCustomer.getCurrentSubscription().getId());
        } else {
            user.setId(selectedCustomer.getId());
        }
        request.setUser(user);
        pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getCustomerProfile", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
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
                if (serviceResponse != null && serviceResponse.getStatus() == 200 && serviceResponse.getUser() != null && serviceResponse.getUser().getCurrentSubscription() != null) {
                    billsDue.setText(serviceResponse.getUser().getCurrentSubscription().getBillsDue() + " Bills Due");
                    if(serviceResponse.getUser().getCurrentSubscription().getLastBillPaid() != null) {
                        lastPaid.setText("Last paid on " + CommonUtils.convertDate(serviceResponse.getUser().getCurrentSubscription().getLastBillPaid()));
                    } else {
                        lastPaid.setText("No payment found");
                    }
                    selectedCustomer = serviceResponse.getUser();
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }
}