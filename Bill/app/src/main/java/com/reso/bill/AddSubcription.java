package com.reso.bill;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
    TextView txtchangequty,txtweekdays;
    ImageView imgpause,imgdiscontinue;

    public static AddSubcription newInstance() {
        AddSubcription fragment = new AddSubcription();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_add_subcription, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Subscribed NewsPapers</font>"));
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_newspaper_subcription);
        /*txtchangequty=(TextView)rootView.findViewById(R.id.txt_newspaperqty);
        txtweekdays=(TextView)rootView.findViewById(R.id.txt_weekdays);
        imgpause=(ImageView)rootView.findViewById(R.id.img_paus);
        imgdiscontinue=(ImageView)rootView.findViewById(R.id.img_cross);
        txtchangequty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeQuantity fragment = new ChangeQuantity();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();
            }
        });
        txtweekdays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DaysToDeliver fragment = new DaysToDeliver();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();
            }
        });
        imgpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PauseTime fragment = new PauseTime();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();

            }
        });
        imgdiscontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisContinue fragment = new DisContinue();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();

            }
        });*/
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

        recyclerView.setAdapter(new CustomerSubcriptionAdapter(list,getActivity()));
    }
}
