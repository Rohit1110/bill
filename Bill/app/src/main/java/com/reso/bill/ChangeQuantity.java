package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

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


    public static ChangeQuantity newInstance(BillUser customer, BillItem subItem) {
        ChangeQuantity fragment = new ChangeQuantity();
        fragment.customer = customer;
        fragment.subItem = subItem;
        return fragment;

    }

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_quantity, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Change Quantity</font>"));

        customerName = (TextView) rootView.findViewById(R.id.txt_change_quantity_customer_name);
        subItemIcon = (ImageView) rootView.findViewById(R.id.img_change_quantity_sub_item);
        quantity = (EditText) rootView.findViewById(R.id.et_change_quantity);
        changeQuantity = (Button) rootView.findViewById(R.id.btn_change_quantity);
        scheme = (CheckBox) rootView.findViewById(R.id.chk_subscription_scheme);
        schemePrice = (EditText) rootView.findViewById(R.id.et_scheme_price);

        customerName.setText(customer.getName());
        Picasso.get().load(Utility.getItemImageURL(Utility.getRootItemId(subItem))).into(subItemIcon);
        quantity.setText("0");
        if (subItem.getQuantity() != null) {
            quantity.setText(subItem.getQuantity().toString());
        }

        if(subItem.getPrice() != null) {
            scheme.setChecked(true);
            schemePrice.setVisibility(View.VISIBLE);
            schemePrice.setText(subItem.getPrice().toString());
        }

        scheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    schemePrice.setVisibility(View.VISIBLE);
                    schemePrice.setText("0.0");
                } else {
                    schemePrice.setVisibility(View.GONE);
                }
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
                    if(TextUtils.isEmpty(schemePrice.getText())) {
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

        return rootView;
    }


    private void saveQuantity() {
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(customer);
        BillItem subscribedItem = new BillItem();
        subscribedItem.setId(subItem.getId());
        subscribedItem.setQuantity(subItem.getQuantity());
        subscribedItem.setPriceType(subItem.getPriceType());
        subscribedItem.setPrice(subItem.getPrice());
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
