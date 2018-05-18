package com.reso.bill;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import model.Listone;

/**
 * Created by Rohit on 5/16/2018.
 */

public class CustomerInfo extends Fragment {
    FloatingActionButton fabsubscription;
    List<Listone> list = new ArrayList<>();
    LinearLayout layout;
    public static CustomerInfo newInstance() {
        CustomerInfo fragment = new CustomerInfo();
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.customer_info_main, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Add Customer</font>"));
        layout=(LinearLayout)rootView.findViewById(R.id.layout_subscriptions);
        fabsubscription=(FloatingActionButton)rootView.findViewById(R.id.fab_custinfo);
        fabsubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddSubcription fragment = new AddSubcription();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();

            }
        });
        //recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
       // Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/

       layout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               AddSubcription fragment = new AddSubcription();
               FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
               ft.replace(R.id.frame_layout, fragment);
               ft.commit();

           }
       });

        return rootView;
    }
}
