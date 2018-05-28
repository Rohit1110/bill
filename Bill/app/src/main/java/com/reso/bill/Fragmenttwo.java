package com.reso.bill;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import adapters.ListTwoAdapter;
import model.ListTwo;

/**
 * Created by Rohit on 5/8/2018.
 */

public class Fragmenttwo extends Fragment {
    private RecyclerView recyclerView;
    List<ListTwo> list = new ArrayList<>();


    public static Fragmenttwo newInstance() {
        Fragmenttwo fragment = new Fragmenttwo();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_two, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_order);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Total Orders</font>"));

       // Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.tabtwo_toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/
        return rootView;


    }

    @Override
    public void onResume() {
        super.onResume();

        ListTwo ltwo = null;
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
            ltwo = new ListTwo();
            ltwo.setName((i + " Rohit Jadhav"));
            ltwo.setImgUrl(R.drawable.loksatta);
            ltwo.setNewspaperpcs("245 pcs");
            list.add(ltwo);

        }





        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Collections.sort(items);

        recyclerView.setAdapter(new ListTwoAdapter(list));
    }
}