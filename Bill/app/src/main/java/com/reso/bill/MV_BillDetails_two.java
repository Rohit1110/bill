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

import java.util.ArrayList;
import java.util.List;

import adapters.BillDetailsAdapter;
import model.Bill_details;

/**
 * Created by Rohit on 5/28/2018.
 */

public class MV_BillDetails_two extends Fragment {
    RecyclerView recyclerView;
    private List<Bill_details> list= new ArrayList<>();
    public static MV_BillDetails_two newInstance() {
        MV_BillDetails_two fragment = new MV_BillDetails_two();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mv_bill_details_two, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Bill Details</font>"));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_bill_two);
        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        //Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title")
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();

        Bill_details bill_details = new Bill_details();
        for (int i = 0; i <5; i++) {
            System.out.println(i);

            bill_details.setDate("Sakal");

            list.add(bill_details);

        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Collections.sort(items);

        recyclerView.setAdapter(new BillDetailsAdapter(list));
    }
}
