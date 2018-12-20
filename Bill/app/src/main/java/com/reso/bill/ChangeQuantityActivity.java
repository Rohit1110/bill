package com.reso.bill;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import util.ServiceUtil;
import util.Utility;

public class ChangeQuantityActivity extends AppCompatActivity {

    private TextView customerName;
    private ImageView subItemIcon;
    private EditText quantity, schemePrice;
    private BillUser customer;
    private BillItem subItem;
    private Button changeQuantity;
    private ProgressDialog pDialog;
    private CheckBox scheme;
    private EditText startDate, endDate;
    private Date schemeStart;
    private Date schemeEnd;
    private EditText paymentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_quantity);

        customer = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.CUSTOMER_KEY);
        subItem = (BillItem) Utility.getIntentObject(BillItem.class, getIntent(), Utility.ITEM_KEY);

        Utility.setActionBar("Change Quantity", getSupportActionBar());

        subItemIcon = (ImageView) findViewById(R.id.img_change_quantity_sub_item);
        quantity = (EditText) findViewById(R.id.et_change_quantity);
        changeQuantity = (Button) findViewById(R.id.btn_change_quantity);
        scheme = (CheckBox) findViewById(R.id.chk_subscription_scheme);
        schemePrice = (EditText) findViewById(R.id.et_scheme_price);
        startDate = (EditText) findViewById(R.id.et_scheme_start_date);
        endDate = (EditText) findViewById(R.id.et_scheme_end_date);
        paymentRef = (EditText) findViewById(R.id.et_scheme_payment_ref);
        //customerName.setText(customer.getName());
        Picasso.get().load(Utility.getItemImageURL(Utility.getRootItemId(subItem))).into(subItemIcon);
        quantity.setText("0");
        if (subItem.getQuantity() != null) {
            quantity.setText(Utility.getDecimalText(subItem.getQuantity()));
        }

        if (subItem.getPrice() != null) {
            scheme.setChecked(true);
            schemePrice.setVisibility(View.VISIBLE);
            schemePrice.setText(subItem.getPrice().toString());
            startDate.setVisibility(View.VISIBLE);
            startDate.setText(CommonUtils.convertDate(subItem.getSchemeStartDate()));
            endDate.setVisibility(View.VISIBLE);
            endDate.setText(CommonUtils.convertDate(subItem.getSchemeEndDate()));
            paymentRef.setText(subItem.getPaymentRef());
            paymentRef.setVisibility(View.VISIBLE);
        }

        scheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    schemePrice.setVisibility(View.VISIBLE);
                    schemePrice.setText("0.0");
                } else {
                    schemePrice.setVisibility(View.GONE);
                }
                startDate.setVisibility(schemePrice.getVisibility());
                endDate.setVisibility(schemePrice.getVisibility());
                paymentRef.setVisibility(schemePrice.getVisibility());
            }
        });

        changeQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity.getText().toString() == null || quantity.getText().toString().trim().length() == 0) {
                    Utility.createAlert(ChangeQuantityActivity.this, "Please select the quantity", "Error");
                    return;
                }
                subItem.setQuantity(new BigDecimal(quantity.getText().toString()));

                if (scheme.isChecked()) {
                    if (TextUtils.isEmpty(schemePrice.getText())) {
                        subItem.setPrice(BigDecimal.ZERO);
                        subItem.setPriceType(null);
                    } else {
                        subItem.setPriceType("MONTHLY");
                        subItem.setPrice(new BigDecimal(schemePrice.getText().toString()));
                    }

                }

                saveQuantity();
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareDatePicker("START");
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareDatePicker("END");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, this);
    }

    private void prepareDatePicker(final String type) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        final int yyyy = yy;
        final int mon = mm;
        final int day = dd;
        DatePickerDialog datePicker = new DatePickerDialog(ChangeQuantityActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                SimpleDateFormat sdf = new SimpleDateFormat(BillConstants.DATE_FORMAT);

                try {
                    if ("START".equals(type)) {
                        schemeStart = sdf.parse(selectedDateString);
                        if (schemeStart != null) {
                            startDate.setText(new SimpleDateFormat(BillConstants.DATE_FORMAT).format(schemeStart));
                        }
                    } else {
                        schemeEnd = sdf.parse(selectedDateString);
                        if (schemeEnd != null) {
                            endDate.setText(new SimpleDateFormat(BillConstants.DATE_FORMAT).format(schemeEnd));
                        }
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


    private void saveQuantity() {
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(customer);
        BillItem subscribedItem = new BillItem();
        subscribedItem.setId(subItem.getId());
        subscribedItem.setQuantity(subItem.getQuantity());
        subscribedItem.setPriceType(subItem.getPriceType());
        subscribedItem.setPrice(subItem.getPrice());
        subscribedItem.setSchemeStartDate(schemeStart);
        subscribedItem.setSchemeEndDate(schemeEnd);
        subscribedItem.setPaymentRef(paymentRef.getText().toString());
        request.setItem(subscribedItem);
        pDialog = Utility.getProgressDialogue("Saving..", ChangeQuantityActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerItem", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, ChangeQuantityActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(ChangeQuantityActivity.this);
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
                    Utility.createAlertWithActivityFinish(ChangeQuantityActivity.this, "Quantity updated successfully!", "Done", null, null, null, null);
                    //Utility.nextFragment(ChangeQuantityActivity.this, AddSubcription.newInstance(customer));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(ChangeQuantityActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }
}
