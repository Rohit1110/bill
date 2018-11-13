package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/16/2018.
 */

public class Settings extends Fragment {
    private Button saveSettings;
    private CheckBox hideBillDetails;
    private BillUser user;
    private ProgressDialog pDialog;


    public static Settings newInstance() {
        Settings fragment = new Settings();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Add Customer</font>"));
        Utility.AppBarTitle("Settings", getActivity());
        //layout = (LinearLayout) rootView.findViewById(R.id.layout_subscriptions);
        saveSettings = (Button) rootView.findViewById(R.id.btn_save_settings);
        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }


        });
        hideBillDetails = (CheckBox) rootView.findViewById(R.id.chk_settings_bill_hide);
        user = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);

        if(user.getCurrentBusiness().getShowBillDetails() != null && "N".equals(user.getCurrentBusiness().getShowBillDetails())) {
            hideBillDetails.setChecked(true);
        }

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    private void saveSettings() {
        BillServiceRequest request = new BillServiceRequest();

        BillUser requestUser = new BillUser();
        requestUser.setId(user.getId());
        requestUser.setPhone(user.getPhone());
        BillBusiness requestBusiness = new BillBusiness();
        requestBusiness.setId(user.getCurrentBusiness().getId());
        if(hideBillDetails.isChecked()) {
            requestBusiness.setShowBillDetails("N");
            user.getCurrentBusiness().setShowBillDetails("N");
        } else if (user.getCurrentBusiness().getShowBillDetails() != null) {
            requestBusiness.setShowBillDetails("Y");
            user.getCurrentBusiness().setShowBillDetails("Y");
        }
        requestUser.setCurrentBusiness(requestBusiness);
        request.setUser(requestUser);
        pDialog = Utility.getProgressDialogue("Saving ..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("updateUserProfile", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
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
                    Utility.createAlert(getContext(), "Settings updated successfully!", "Done");
                    Utility.writeObject(getActivity(), Utility.USER_KEY, user);
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }


}
