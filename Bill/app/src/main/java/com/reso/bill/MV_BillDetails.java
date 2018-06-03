package com.reso.bill;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import adapters.BillDetailsAdapter;
import model.Bill_details;

/**
 * Created by Rohit on 5/28/2018.
 */

public class MV_BillDetails extends Fragment {
    RecyclerView recyclerView;
    Spinner monthspinner;
    private List<Bill_details> list= new ArrayList<>();
    public static MV_BillDetails newInstance() {
        MV_BillDetails fragment = new MV_BillDetails();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mv_bill_details, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Bill Details111</font>"));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_bill);
        monthspinner= (Spinner)rootView.findViewById(R.id.spinner_months);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.months_arrays));
        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        //Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title")
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/
       monthspinner.setAdapter(adapter);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();

        Bill_details bill_details = new Bill_details();
        for (int i = 0; i < 15; i++) {
            System.out.println(i);

            bill_details.setDate("30/"+i+"/2018");

            list.add(bill_details);

        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Collections.sort(items);

        recyclerView.setAdapter(new BillDetailsAdapter(list));
    }
}
