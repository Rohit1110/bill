package com.reso.bill;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import adapters.AddNewspaperAdapter;
import adapters.ListTwoAdapter;
import model.ListTwo;
import model.SelectNewsPapers;

public class AddNewspapers extends Fragment {
    RecyclerView recyclerView;
    List<ListTwo> list = new ArrayList<>();
    LinearLayout layoutaddnewspaper;
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_newspapers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.backarrow);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddNewspapers.this,VendorDashBoard.class);
                startActivity(i);
            }
        });
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Drawable upArrow = getResources().getDrawable(R.mipmap.backarrow);

        //Collections.sort(items);

        recyclerView.setAdapter(new AddNewspaperAdapter(list));
        layoutaddnewspaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(AddNewspapers.this,SelectNewspaper.class);
                startActivity(i);
            }
        });


    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_add_newspapers, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Home</font>"));
        //recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/
        layoutaddnewspaper=(LinearLayout)rootView.findViewById(R.id.layout_addnewspaper);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_newspaper);
        layoutaddnewspaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectNewspaper fragment = new SelectNewspaper();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ListTwo ltwo = null;
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
            ltwo = new ListTwo();
            ltwo.setRates("4.50/pc +0.50(m)");
            ltwo.setImgUrl(R.drawable.loksatta);
            list.add(ltwo);

        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new AddNewspaperAdapter(list));
    }
}
