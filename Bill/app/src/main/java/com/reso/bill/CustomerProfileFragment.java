package com.reso.bill;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rns.web.billapp.service.bo.domain.BillUser;

import java.util.ArrayList;
import java.util.List;

import model.ListThree;
import util.Utility;

/**
 * Created by Rohit on 5/8/2018.
 */

public class CustomerProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private BillUser selectedCustomer;
    private TextView name, email, phone, address;
    private View manageSubscriptions;
    private TextView editProfile;

    private List<ListThree> list = new ArrayList<>();

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

        name = (TextView) rootView.findViewById(R.id.txt_profile_customer_name);
        email = (TextView) rootView.findViewById(R.id.txt_profile_customer_email);
        phone = (TextView) rootView.findViewById(R.id.txt_profile_customer_phone);
        address = (TextView) rootView.findViewById(R.id.txt_profile_customer_address);
        editProfile = (TextView) rootView.findViewById(R.id.btn_edit_profile);

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

    }
}