package com.reso.bill;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import util.Utility;

/**
 * Created by Rohit on 5/16/2018.
 */

public class DaysToDeliver extends Fragment implements View.OnClickListener {
TextView txtmon,txttue,txtwed,txtthu,txtfri,txtsat,txtsun;
    public static DaysToDeliver newInstance(){
        DaysToDeliver fragment = new DaysToDeliver();
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_days_todeliver, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Days To Deliver</font>"));
        txtmon=(TextView)rootView.findViewById(R.id.txt_mon);
        txttue=(TextView)rootView.findViewById(R.id.txt_tue);
        txtwed=(TextView)rootView.findViewById(R.id.txt_wed);
        txtthu=(TextView)rootView.findViewById(R.id.txt_thu);
        txtfri=(TextView)rootView.findViewById(R.id.txt_fri);
        txtsat=(TextView)rootView.findViewById(R.id.txt_sat);
        txtsun=(TextView)rootView.findViewById(R.id.txt_sun);

        txtmon.setOnClickListener(this);
        txttue.setOnClickListener(this);
        txtwed.setOnClickListener(this);
        txtthu.setOnClickListener(this);
        txtfri.setOnClickListener(this);
        txtsat.setOnClickListener(this);
        txtsun.setOnClickListener(this);



        return rootView;

    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.txt_mon) {
           txtmon.setTextColor(getResources().getColor(R.color.red));
        }


        if (view.getId() == R.id.txt_tue) {
            txttue.setTextColor(getResources().getColor(R.color.red));
        }
        if (view.getId() == R.id.txt_wed) {
            txtwed.setTextColor(getResources().getColor(R.color.red));
        }
        if (view.getId() == R.id.txt_thu) {
            txtthu.setTextColor(getResources().getColor(R.color.red));
        }
        if (view.getId() == R.id.txt_fri) {
            txtfri.setTextColor(getResources().getColor(R.color.red));
        }
        if (view.getId() == R.id.txt_sat) {
            txtsat.setTextColor(getResources().getColor(R.color.red));
        }
        if (view.getId() == R.id.txt_sun) {
            txtsun.setTextColor(getResources().getColor(R.color.red));
        }
    }
}
