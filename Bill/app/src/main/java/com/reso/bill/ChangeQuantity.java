package com.reso.bill;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Rohit on 5/16/2018.
 */

public class ChangeQuantity extends Fragment {
    //TextView txtselectdate;
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

    public static ChangeQuantity newInstance(BillUser customer, BillItem subItem) {
        ChangeQuantity fragment = new ChangeQuantity();
        fragment.customer = customer;
        fragment.subItem = subItem;
        return fragment;

    }

    private RecyclerView recyclerView;

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
        SearchView searchView = new SearchView((Utility.castActivity(getActivity())).getSupportActionBar().getThemedContext());
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
        View rootView = inflater.inflate(R.layout.fragment_change_quantity, container, false);
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Change Quantity</font>"));
        Utility.AppBarTitle("Change Quantity", getActivity());
        //customerName = (TextView) rootView.findViewById(R.id.txt_change_quantity_customer_name);
        subItemIcon = (ImageView) rootView.findViewById(R.id.img_change_quantity_sub_item);
        quantity = (EditText) rootView.findViewById(R.id.et_change_quantity);
        changeQuantity = (Button) rootView.findViewById(R.id.btn_change_quantity);
        scheme = (CheckBox) rootView.findViewById(R.id.chk_subscription_scheme);
        schemePrice = (EditText) rootView.findViewById(R.id.et_scheme_price);
        startDate = (EditText) rootView.findViewById(R.id.et_scheme_start_date);
        endDate = (EditText) rootView.findViewById(R.id.et_scheme_end_date);
        paymentRef = (EditText) rootView.findViewById(R.id.et_scheme_payment_ref);
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
                    Utility.createAlert(getContext(), "Please select the quantity", "Error");
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

        return rootView;
    }

    private void prepareDatePicker(final String type) {
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
                    Utility.createAlert(getContext(), "Quantity updated successfully!", "Done");
                    Utility.nextFragment(getActivity(), AddSubcription.newInstance(customer));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

}
