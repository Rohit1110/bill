package com.reso.bill;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.rns.web.billapp.service.util.CommonUtils;
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

public class PauseCustomerSubscription extends Fragment {
    private EditText txtfromdate, txttodate, txtselectdate, customerName;
    private ImageView itemIcon;
    private BillUser customer;
    private BillItem subscribedItem;
    private ProgressDialog pDialog;
    private Button pause;
    private TextView pausedDays;
    private Date fromDate, toDate;

    public static PauseCustomerSubscription newInstance(BillUser customer, BillItem customerSubscription) {
        PauseCustomerSubscription fragment = new PauseCustomerSubscription();
        fragment.customer = customer;
        fragment.subscribedItem = customerSubscription;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((Dashboard) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(getResources().getColor(R.color.md_black_1000));
        MenuItemCompat.setActionView(item, searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filter(newText);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //searchView.setMenuItem(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pause_time, container, false);
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Pause Times of India</font>"));

        Utility.AppBarTitle("Pause " + Utility.getItemName(subscribedItem), getActivity());

        txtfromdate = (EditText) rootView.findViewById(R.id.txt_from_date);
        txttodate = (EditText) rootView.findViewById(R.id.txt_to_date);
        txtfromdate.setFocusable(false);
        txttodate.setFocusable(false);
        txtfromdate.setClickable(true);
        txttodate.setClickable(true);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        //One day ahead of current by default
        fromDate = cal.getTime();
        toDate = cal.getTime();

        txtfromdate.setText(CommonUtils.convertDate(fromDate));
        //txtfromdate.setText((dd+1)+"-"+mm+"-"+yy);

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
                final int yyyy = yy;
                final int mon = mm;
                final int day = dd;
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
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis() + (1000 * 24 * 60 * 60));

                datePicker.show();

            }
        });

        txttodate.setText(CommonUtils.convertDate(toDate));
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
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis() + (1000 * 24 * 60 * 60));
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

        calculateNoOfDays();

        return rootView;
    }

    private void calculateNoOfDays() {
        if (toDate != null && fromDate != null) {
            if (toDate.getTime() < fromDate.getTime()) {
                //Utility.createAlert(getContext(), "Please select valid dates!", "Error");
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
            Utility.createAlert(getContext(), "To date cannot be less than From date!", "Error");
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