package com.reso.bill;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import adapters.ListThreeAdapter;
import adapters.ListTwoAdapter;
import model.ListThree;
import model.ListTwo;

/**
 * Created by Rohit on 5/8/2018.
 */

public class FragmentThree extends Fragment {
    private RecyclerView recyclerView;

    List<ListThree> list = new ArrayList<>();

    public static FragmentThree newInstance() {
        FragmentThree fragment = new FragmentThree();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*ListThree lthree = new ListThree();
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
            lthree.setName(i + " Rohit Jadhav");
            list.add(lthree);

        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Collections.sort(items);

        recyclerView.setAdapter(new ListThreeAdapter(list));*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_three, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_bill_details);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ListThree lthree = new ListThree();
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
            lthree.setMonths("Months " + i);
            lthree.setAmount("INR 320.00");
            lthree.setStatus("Pending");
            list.add(lthree);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Collections.sort(items);

        recyclerView.setAdapter(new ListThreeAdapter(list));
    }
}