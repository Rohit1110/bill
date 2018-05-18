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

import adapters.ListOneAdapter;
import adapters.SelectNewsPaperAdapter;
import model.ListTwo;
import model.Listone;
import model.SelectNewsPapers;

public class SelectNewspaper extends Fragment {
    List<SelectNewsPapers> list = new ArrayList<>();
    RecyclerView recyclerView;

/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_newspaper);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.backarrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SelectNewspaper.this,AddNewspapers.class);
                startActivity(i);
            }
        });
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view_select_newspaper);

        *//*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*//*

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Drawable upArrow = getResources().getDrawable(R.mipmap.backarrow);


    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_select_newspaper, container, false);
        //getActivity().setTitle(Html.fromHtml("<font color='#000000'>Home</font>"));
        recyclerView=(RecyclerView)rootView.findViewById(R.id.recycler_view_select_newspaper);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SelectNewsPapers listTwo = new SelectNewsPapers();
        for(int i =0; i<5;i++) {
            System.out.println(i);
            listTwo.setImgUrl(R.drawable.loksatta);
            listTwo.setNewsPaperInfo("Loksatta  |  Marathi  |  Daily  |  INR 5.00 \n The Times of India is an Indian English-language daily newspaper owned by The Times Group. ");
            list.add(listTwo);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Collections.sort(items);

        recyclerView.setAdapter(new SelectNewsPaperAdapter(list));
    }
}
