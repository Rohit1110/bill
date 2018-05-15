package com.reso.bill;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import adapters.ListOneAdapter;
import adapters.SelectNewsPaperAdapter;
import model.ListTwo;
import model.Listone;
import model.SelectNewsPapers;

public class SelectNewspaper extends AppCompatActivity {
    List<SelectNewsPapers> list = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
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

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Drawable upArrow = getResources().getDrawable(R.mipmap.backarrow);

        SelectNewsPapers listTwo = new SelectNewsPapers();
        for(int i =0; i<5;i++) {
            System.out.println(i);
            listTwo.setImgUrl(R.drawable.loksatta);
            listTwo.setNewsPaperInfo("Loksatta  |  Marathi  |  Daily  |  INR 5.00 \n The Times of India is an Indian English-language daily newspaper owned by The Times Group. ");
            list.add(listTwo);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Collections.sort(items);

        recyclerView.setAdapter(new SelectNewsPaperAdapter(list));
    }

}
