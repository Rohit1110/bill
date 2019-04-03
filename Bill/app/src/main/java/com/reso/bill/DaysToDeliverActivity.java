package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class DaysToDeliverActivity extends AppCompatActivity {

    private TextView txtmon, txttue, txtwed, txtthu, txtfri, txtsat, txtsun;
    private String selectedDays = "";
    private BillUser customer;
    private BillItem subscribedItem;
    private ProgressDialog pDialog;
    private Button save;
    private TextView txtSelectedDays;
    private ImageView itemIcon;
    //private Button saveDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_to_deliver);

        customer = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.CUSTOMER_KEY);
        subscribedItem = (BillItem) Utility.getIntentObject(BillItem.class, getIntent(), Utility.ITEM_KEY);

        Utility.setActionBar("Days To Deliver", getSupportActionBar());
        txtmon = (TextView) findViewById(R.id.txt_mon);
        txttue = (TextView) findViewById(R.id.txt_tue);
        txtwed = (TextView) findViewById(R.id.txt_wed);
        txtthu = (TextView) findViewById(R.id.txt_thu);
        txtfri = (TextView) findViewById(R.id.txt_fri);
        txtsat = (TextView) findViewById(R.id.txt_sat);
        txtsun = (TextView) findViewById(R.id.txt_sun);

        txtmon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDay(txtmon, "2");
            }
        });
        txttue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDay(txttue, "3");
            }
        });
        txtwed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDay(txtwed, "4");
            }
        });
        txtthu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDay(txtthu, "5");
            }
        });
        txtfri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDay(txtfri, "6");
            }
        });
        txtsat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDay(txtsat, "7");
            }
        });
        txtsun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDay(txtsun, "1");
            }
        });

        //customerName = (TextView) findViewById(R.id.txt_delivery_days_customer_name);
        itemIcon = (ImageView) findViewById(R.id.img_delivery_days_item_icon);
        txtSelectedDays = (TextView) findViewById(R.id.txt_selected_days);
        //saveDays = (Button) findViewById(R.id.fab_save_days);

        //customerName.setText(customer.getName());
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

        /*saveDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDays();
            }
        });*/
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
                saveDays();
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {

        Picasso.get().load(Utility.getItemImageURL(Utility.getRootItemId(subscribedItem))).into(itemIcon);
        super.onResume();
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
        return getResources().getColor(R.color.drewerbg);
    }

    private void saveDays() {
        if (selectedDays.trim().length() == 0) {
            Utility.createAlert(DaysToDeliverActivity.this, "Please select at least one day for delivery!", "Error");
            return;
        }
        BillItem subItem = new BillItem();
        subItem.setId(subscribedItem.getId());
        subItem.setWeekDays(selectedDays);
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(customer);
        request.setItem(subItem);
        pDialog = Utility.getProgressDialogue("Saving..", DaysToDeliverActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerItem", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, DaysToDeliverActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(DaysToDeliverActivity.this);
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
                    Utility.createAlertWithActivityFinish(DaysToDeliverActivity.this, "Delivery days updated successfully!", "Done", null, null, null, null);
                    //Utility.nextFragment(DaysToDeliverActivity.this, AddSubcription.newInstance(customer));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(DaysToDeliverActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }
}
