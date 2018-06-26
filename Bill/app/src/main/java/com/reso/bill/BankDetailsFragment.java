package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillFinancialDetails;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Admin on 26/05/2018.
 */

public class BankDetailsFragment extends Fragment {

    private Button fabsubscription;
    private EditText bankName, accountNumber, ifscCode, address, accountHolder;
    private BillUser user;
    private ProgressDialog pDialog;

    public static CustomerInfo newInstance() {
        CustomerInfo fragment = new CustomerInfo();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bank_details, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Bank Details</font>"));
        fabsubscription = (Button) rootView.findViewById(R.id.fab_bank_details);
        fabsubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*AddSubcription fragment = new AddSubcription();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();*/
                if (user == null) {
                    user = new BillUser();
                }

                if (user.getFinancialDetails() == null) {
                    user.setFinancialDetails(new BillFinancialDetails());
                }
                if (!bankName.getText().toString().equals("") && !accountNumber.getText().toString().equals("") && !address.getText().toString().equals("") && !ifscCode.getText().toString().equals("")) {
                    if(accountNumber.getText().toString().length() < 10){
                        accountNumber.setError("Enter valid A/C number");
                    } else if(ifscCode.getText().toString().length() < 11&& ifscCode.getText().toString().length() > 11){
                        ifscCode.setError("enter valid ifsc code");
                    }else {
                        user.getFinancialDetails().setBankName(bankName.getText().toString());
                        user.getFinancialDetails().setAccountNumber(accountNumber.getText().toString());
                        user.getFinancialDetails().setBankAddress(address.getText().toString());
                        user.getFinancialDetails().setIfscCode(ifscCode.getText().toString());
                        user.getFinancialDetails().setAccountHolderName(accountHolder.getText().toString());
                        saveBankDetails();

                    }
                }

            }
        });

        bankName = (EditText) rootView.findViewById(R.id.et_bank_name);
        ifscCode = (EditText) rootView.findViewById(R.id.et_ifsc_code);
        accountNumber = (EditText) rootView.findViewById(R.id.et_account_number);
        address = (EditText) rootView.findViewById(R.id.et_bank_address);
        accountHolder = (EditText) rootView.findViewById(R.id.et_account_holder);

        user = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);
        if (user != null && user.getFinancialDetails() != null) {
            bankName.setText(user.getFinancialDetails().getBankName());
            ifscCode.setText(user.getFinancialDetails().getIfscCode());
            accountNumber.setText(user.getFinancialDetails().getAccountNumber());
            address.setText(user.getFinancialDetails().getBankAddress());
            accountHolder.setText(user.getFinancialDetails().getAccountHolderName());
        }

        return rootView;
    }

    private void saveBankDetails() {
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(user);

        pDialog = Utility.getProgressDialogue("Saving Bank Details..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("updateBankDetails", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
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
                    Utility.createAlert(getContext(), "Bank details updated successfully!", "Done");
                    Utility.writeObject(getContext(), Utility.USER_KEY, user);
                    Utility.nextFragment(getActivity(), HomeFragment.newInstance(user));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }


}
