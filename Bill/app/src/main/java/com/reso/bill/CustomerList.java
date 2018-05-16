package com.reso.bill;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import adapters.CustomerListAdapter;
import adapters.ListOneAdapter;
import model.Listone;

/**
 * Created by Rohit on 5/16/2018.
 */

public class CustomerList extends Fragment {
    List<Listone> list = new ArrayList<>();
    RecyclerView recyclerView;
    public static CustomerList newInstance() {
        CustomerList fragment = new CustomerList();
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.customer_list_main, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Home</font>"));
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_cust_list);
        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        Listone lone = new Listone();
        for(int i =0; i<15;i++) {
            System.out.println(i);
            lone.setName("Ajinkya kulkarni");
            lone.setAddress("B504, ABC apartments, Sadashiv peth, Pune 411010 ");
            list.add(lone);
        }



      /*  adapter = new ListOneAdapter(list);
       adapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener()){


        }*/

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Collections.sort(items);

        recyclerView.setAdapter(new CustomerListAdapter(list,getActivity()));
    }


}
