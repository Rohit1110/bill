package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.squareup.picasso.Picasso;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/16/2018.
 */

public class DaysToDeliver extends Fragment implements View.OnClickListener {
    private TextView txtmon, txttue, txtwed, txtthu, txtfri, txtsat, txtsun;
    private String selectedDays = "";
    private BillUser customer;
    private BillItem subscribedItem;
    private ProgressDialog pDialog;
    private Button save;
    private TextView txtSelectedDays, customerName;
    private ImageView itemIcon;
    private Button saveDays;

    public static DaysToDeliver newInstance(BillUser customer, BillItem item) {
        DaysToDeliver fragment = new DaysToDeliver();
        fragment.customer = customer;
        fragment.subscribedItem = item;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_days_todeliver, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Days To Deliver</font>"));
        txtmon = (TextView) rootView.findViewById(R.id.txt_mon);
        txttue = (TextView) rootView.findViewById(R.id.txt_tue);
        txtwed = (TextView) rootView.findViewById(R.id.txt_wed);
        txtthu = (TextView) rootView.findViewById(R.id.txt_thu);
        txtfri = (TextView) rootView.findViewById(R.id.txt_fri);
        txtsat = (TextView) rootView.findViewById(R.id.txt_sat);
        txtsun = (TextView) rootView.findViewById(R.id.txt_sun);

        txtmon.setOnClickListener(this);
        txttue.setOnClickListener(this);
        txtwed.setOnClickListener(this);
        txtthu.setOnClickListener(this);
        txtfri.setOnClickListener(this);
        txtsat.setOnClickListener(this);
        txtsun.setOnClickListener(this);

        customerName = (TextView) rootView.findViewById(R.id.txt_delivery_days_customer_name);
        itemIcon = (ImageView) rootView.findViewById(R.id.img_delivery_days_item_icon);
        txtSelectedDays = (TextView) rootView.findViewById(R.id.txt_selected_days);
        saveDays = (Button) rootView.findViewById(R.id.fab_save_days);

        customerName.setText(customer.getName());
        Integer itemId = null;
        itemId = Utility.getRootItemId(subscribedItem);
        txtSelectedDays.setText("Days to deliver - ");

        selectedDays = subscribedItem.getWeekDays();
        if (selectedDays == null) {
            selectedDays = "";
        }

        if (selectedDays.trim().length() > 0) {
            initDay(txtsun, "1");
            initDay(txtmon, "2");
            initDay(txttue, "3");
            initDay(txtwed, "4");
            initDay(txtthu, "5");
            initDay(txtfri, "6");
            initDay(txtsat, "7");
        }

        saveDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDays();
            }
        });

        return rootView;

    }

    @Override
    public void onResume() {

        Picasso.get().load(Utility.getItemImageURL(Utility.getRootItemId(subscribedItem))).into(itemIcon);
        super.onResume();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.txt_mon) {
            setDay(txtmon, "2");
        }
        if (view.getId() == R.id.txt_tue) {
            setDay(txttue, "3");
        }
        if (view.getId() == R.id.txt_wed) {
            setDay(txtwed, "4");
        }
        if (view.getId() == R.id.txt_thu) {
            setDay(txtthu, "5");
        }
        if (view.getId() == R.id.txt_fri) {
            setDay(txtfri, "6");
        }
        if (view.getId() == R.id.txt_sat) {
            setDay(txtsat, "7");
        }
        if (view.getId() == R.id.txt_sun) {
            setDay(txtsun, "1");
        }
    }

    private void setDay(TextView txtDay, String day) {
        if (selectedDays.contains(day)) {
            txtDay.setTextColor(unSelectedDayColor());
            selectedDays = selectedDays.replace(day + ",", "");
            selectedDays = selectedDays.replace(day, "");
            txtSelectedDays.setText(txtSelectedDays.getText().toString().replace(getDayName(day) + ",", ""));
        } else {
            selectDay(txtDay, day);
            selectedDays = selectedDays.concat(day + ",");
        }
    }

    private void initDay(TextView txt, String day) {
        if (selectedDays.contains(day)) {
            selectDay(txt, day);
        }
    }

    private void selectDay(TextView txtDay, String day) {
        txtDay.setTextColor(selectedDayColor());
        txtSelectedDays.setText(txtSelectedDays.getText().toString().concat(getDayName(day) + ","));
    }

    private String getDayName(String dayValue) {
        if (dayValue == null) {
            return "";
        }
        if (dayValue.equals("1")) {
            return "Sunday";
        }
        if (dayValue.equals("2")) {
            return "Monday";
        }
        if (dayValue.equals("3")) {
            return "Tuesday";
        }
        if (dayValue.equals("4")) {
            return "Wednesday";
        }
        if (dayValue.equals("5")) {
            return "Thursday";
        }
        if (dayValue.equals("6")) {
            return "Friday";
        }
        if (dayValue.equals("7")) {
            return "Saturday";
        }
        return "";
    }


    private int selectedDayColor() {
        return getResources().getColor(R.color.buttonColor);
    }

    private int unSelectedDayColor() {
        return getResources().getColor(R.color.white);
    }

    private void saveDays() {
        if (selectedDays.trim().length() == 0) {
            Utility.createAlert(getContext(), "Please select at least one day for delivery!", "Error");
            return;
        }
        BillItem subItem = new BillItem();
        subItem.setId(subscribedItem.getId());
        subItem.setWeekDays(selectedDays);
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(customer);
        request.setItem(subItem);
        pDialog = Utility.getProgressDialogue("Saving..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerItem", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }


    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlert(getContext(), "Delivery days updated successfully!", "Done");
                    Utility.nextFragment(getActivity(), AddSubcription.newInstance(customer));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }
}
