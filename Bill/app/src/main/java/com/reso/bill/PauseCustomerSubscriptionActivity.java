package com.reso.bill;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class PauseCustomerSubscriptionActivity extends AppCompatActivity {

    private EditText txtfromdate, txttodate, txtselectdate, customerName;
    private ImageView itemIcon;
    private BillUser customer;
    private BillItem subscribedItem;
    private ProgressDialog pDialog;
    //private Button pause;
    private TextView pausedDays;
    private Date fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause_customer_subscription);

        customer = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.CUSTOMER_KEY);
        subscribedItem = (BillItem) Utility.getIntentObject(BillItem.class, getIntent(), Utility.ITEM_KEY);

        Utility.setActionBar("Pause " + Utility.getItemName(subscribedItem), getSupportActionBar());

        txtfromdate = (EditText) findViewById(R.id.txt_from_date);
        txttodate = (EditText) findViewById(R.id.txt_to_date);
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
                DatePickerDialog datePicker = new DatePickerDialog(PauseCustomerSubscriptionActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                DatePickerDialog datePicker = new DatePickerDialog(PauseCustomerSubscriptionActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        /*pause = (Button) findViewById(R.id.fab_pause_time);


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseDelivery();
            }
        });*/

        //customerName = (TextView) findViewById(R.id.txt_pause_delivery_customer_name);
        //customerName.setText(customer.getName());

        itemIcon = (ImageView) findViewById(R.id.img_pause_delivery_item_icon);
        Picasso.get().load(Utility.getItemImageURL(Utility.getRootItemId(subscribedItem))).into(itemIcon);

        pausedDays = (TextView) findViewById(R.id.txt_paused_days);

        pausedDays.setText("Pause delivery for - 0 day(s)");

        calculateNoOfDays();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Utility.MENU_ITEM_SAVE, Menu.NONE, "Save").setIcon(R.drawable.ic_check_blue_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Back button click
                finish();
                return true;
            case Utility.MENU_ITEM_SAVE:
//                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();;
                pauseDelivery();
                return true;
        }
        return false;
        //return Utility.backDefault(item, this);
    }

    private void calculateNoOfDays() {
        if (toDate != null && fromDate != null) {
            if (toDate.getTime() < fromDate.getTime()) {
                //Utility.createAlert(PauseCustomerSubscriptionActivity.this, "Please select valid dates!", "Error");
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
            Utility.createAlert(PauseCustomerSubscriptionActivity.this, "Please select From and To fromDate!", "Error");
            return;
        }
        if (toDate.getTime() < fromDate.getTime()) {
            Utility.createAlert(PauseCustomerSubscriptionActivity.this, "To date cannot be less than From date!", "Error");
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
        pDialog = Utility.getProgressDialogue("Saving..", PauseCustomerSubscriptionActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerItemTemp", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, PauseCustomerSubscriptionActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(PauseCustomerSubscriptionActivity.this);
        queue.add(myReq);
    }


    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlertWithActivityFinish(PauseCustomerSubscriptionActivity.this, "Paused delivery successfully!", "Done", null, null, null, null);
                    //Utility.nextFragment(PauseCustomerSubscriptionActivity.this, AddSubcription.newInstance(customer));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(PauseCustomerSubscriptionActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }
}
