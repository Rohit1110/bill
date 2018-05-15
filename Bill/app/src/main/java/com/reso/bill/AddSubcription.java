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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import adapters.CustomerSubcriptionAdapter;
import adapters.ListOneAdapter;
import model.CustomerSubscription;

public class AddSubcription extends Fragment {

    List<CustomerSubscription> list = new ArrayList<>();
    RecyclerView recyclerView;
    ListOneAdapter adapter;
    private Spinner sp;

    public static AddSubcription newInstance() {
        AddSubcription fragment = new AddSubcription();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_add_subcription, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Subscriptions</font>"));
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_newspaper_subcription);
        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/
        sp=(Spinner)rootView.findViewById(R.id.spinner_newspaper);
        List<String> list = new ArrayList<String>();
        list.add("Times of india");
        list.add("Loksatta");
        list.add("Sakal");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(dataAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        CustomerSubscription lone = new CustomerSubscription();
        for(int i =0; i<15;i++) {
            System.out.println(i);
            lone.setImgUrl(R.drawable.sakal);

            list.add(lone);
        }



      /*  adapter = new ListOneAdapter(list);
       adapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener()){


        }*/

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Collections.sort(items);

        recyclerView.setAdapter(new CustomerSubcriptionAdapter(list));
    }
}
