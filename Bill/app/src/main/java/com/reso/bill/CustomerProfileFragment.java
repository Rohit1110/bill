package com.reso.bill;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import model.ListThree;

/**
 * Created by Rohit on 5/8/2018.
 */

public class CustomerProfileFragment extends Fragment {
    private RecyclerView recyclerView;

    private List<ListThree> list = new ArrayList<>();

    public static CustomerProfileFragment newInstance() {
        CustomerProfileFragment fragment = new CustomerProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_profile, container, false);
        //recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_bill_details);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        /*Dashboard dashboard = (Dashboard) getActivity();
        dashboard.setTitle("Customer Information");*/

        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //recyclerView.setAdapter(new ListThreeAdapter(list));
    }
}