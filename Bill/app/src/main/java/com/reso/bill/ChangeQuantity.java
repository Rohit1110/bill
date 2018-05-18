package com.reso.bill;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import util.Utility;

/**
 * Created by Rohit on 5/16/2018.
 */

public class ChangeQuantity extends Fragment {
    TextView txtselectdate;
    public static ChangeQuantity newInstance(){
        ChangeQuantity fragment = new ChangeQuantity();
        return  fragment;

    }

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_quantity, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Change Quantity</font>"));
        txtselectdate=(TextView)rootView.findViewById(R.id.txt_date_select);
        txtselectdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");*/
                //isoffday = false;
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date dates = null;
                        try {
                            dates = sdf.parse(selectedDateString);
                            if (dates != null) {

                                txtselectdate.setText(new SimpleDateFormat("dd MMM yyyy").format(dates));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //

                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePicker.show();

            }
        });

        return rootView;
    }
}
