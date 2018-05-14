package com.reso.bill;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import adapters.AddNewspaperAdapter;
import adapters.ListTwoAdapter;
import model.ListTwo;

public class AddNewspapers extends AppCompatActivity {
    RecyclerView recyclerView;
    List<ListTwo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_newspapers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_newspaper);
        ListTwo ltwo = null;
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
            ltwo = new ListTwo();
            ltwo.setRates("4.50/pc +0.50(m)");
            ltwo.setImgUrl(R.drawable.loksatta);
            list.add(ltwo);

        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Collections.sort(items);

        recyclerView.setAdapter(new AddNewspaperAdapter(list));


    }

}
