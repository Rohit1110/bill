package com.reso.bill;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.bo.domain.BillUserLog;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/16/2018.
 */

public class PauseTime extends Fragment {
    private EditText txtfromdate, txttodate, txtselectdate, customerName;
    private ImageView itemIcon;
    private BillUser customer;
    private BillItem subscribedItem;
    private ProgressDialog pDialog;
    private Button pause;
    private TextView pausedDays;
    private Date fromDate, toDate;

    public static PauseTime newInstance(BillUser customer, BillItem customerSubscription) {
        PauseTime fragment = new PauseTime();
        fragment.customer = customer;
        fragment.subscribedItem = customerSubscription;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pause_time, container, false);
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Pause Times of India</font>"));
        Utility.AppBarTitle("Pause Times of India "  ,getActivity());
        txtfromdate = (EditText) rootView.findViewById(R.id.txt_from_date);
        txttodate = (EditText) rootView.findViewById(R.id.txt_to_date);
        txtfromdate.setFocusable(false);
        txttodate.setFocusable(false);
     txtfromdate.setClickable(true);
        txttodate.setClickable(true);

        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
            //txtfromdate.setText(yy+"-"+mm+"-"+(dd+1));
        txtfromdate.setText((dd+1)+"-"+mm+"-"+yy);

        txtfromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");*/
                //isoffday = false;
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                final int yyyy=yy;
                final int mon=mm;
                final int day=dd;
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                        SimpleDateFormat sdf = new SimpleDateFormat(BillConstants.DATE_FORMAT);
                        fromDate = null;
                        try {
                            fromDate = sdf.parse(selectedDateString);
                            if (fromDate != null) {
                                txtfromdate.setText(new SimpleDateFormat(BillConstants.DATE_FORMAT).format(fromDate));
                                calculateNoOfDays();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //

                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis()+(1000*24*60*60));

                datePicker.show();

            }
        });

        txttodate.setOnClickListener(new View.OnClickListener() {
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
                        SimpleDateFormat sdf = new SimpleDateFormat(BillConstants.DATE_FORMAT);
                        toDate = null;
                        try {
                            toDate = sdf.parse(selectedDateString);
                            if (toDate != null) {
                                txttodate.setText(new SimpleDateFormat(BillConstants.DATE_FORMAT).format(toDate));
                                calculateNoOfDays();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //

                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis()+(1000*24*60*60));
                datePicker.show();

            }
        });

        pause = (Button) rootView.findViewById(R.id.fab_pause_time);


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseDelivery();
            }
        });

        //customerName = (TextView) rootView.findViewById(R.id.txt_pause_delivery_customer_name);
        //customerName.setText(customer.getName());

        itemIcon = (ImageView) rootView.findViewById(R.id.img_pause_delivery_item_icon);
        Picasso.get().load(Utility.getItemImageURL(Utility.getRootItemId(subscribedItem))).into(itemIcon);

        pausedDays = (TextView) rootView.findViewById(R.id.txt_paused_days);

        pausedDays.setText("Pause delivery for - 0 day(s)");

        return rootView;
    }

    private void calculateNoOfDays() {
        if (toDate != null && fromDate != null) {
            if (toDate.getTime() < fromDate.getTime()) {
                Utility.createAlert(getContext(), "Please select valid dates!", "Error");
                return;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(toDate.getTime() - fromDate.getTime());
            pausedDays.setText("Pause delivery for - " + String.valueOf(TimeUnit.DAYS.convert(toDate.getTime() - fromDate.getTime(), TimeUnit.MILLISECONDS) + 1) + " day(s)");
        }
    }

    private long getMinDate() {
        return System.currentTimeMillis() - 1000;
    }

    private void pauseDelivery() {
        if (fromDate == null || toDate == null) {
            Utility.createAlert(getContext(), "Please select From and To fromDate!", "Error");
            return;
        }
        if (toDate.getTime() < fromDate.getTime()) {
            Utility.createAlert(getContext(), "Please select valid dates!", "Error");
            return;
        }

        BillServiceRequest request = new BillServiceRequest();
        request.setUser(customer);
        BillItem subItem = new BillItem();
        subItem.setId(subscribedItem.getId());
        subItem.setQuantity(BigDecimal.ZERO);
        BillUserLog log = new BillUserLog();
        log.setFromDate(fromDate);
        log.setToDate(toDate);
        subItem.setChangeLog(log);
        request.setItem(subItem);
        pDialog = Utility.getProgressDialogue("Saving..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerItemTemp", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
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
                    Utility.createAlert(getContext(), "Paused delivery successfully!", "Done");
                    Utility.nextFragment(getActivity(), AddSubcription.newInstance(customer));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }
}
