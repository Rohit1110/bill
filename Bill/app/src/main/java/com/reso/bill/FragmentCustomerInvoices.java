package com.reso.bill;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillInvoice;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.List;

import adapters.CustomerInvoiceAdapter;
import model.ListThree;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/8/2018.
 */

public class FragmentCustomerInvoices extends Fragment {
    private RecyclerView recyclerView;
    private Spinner sp;
    private List<ListThree> list = new ArrayList<>();
    private BillUser customer;
    //private TextView customerName;
    private ProgressDialog pDialog;
    private Button addInvoice;

    public static FragmentCustomerInvoices newInstance(BillUser customer) {
        FragmentCustomerInvoices fragment = new FragmentCustomerInvoices();
        fragment.customer = customer;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_invoices, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_customer_bill_details);
        sp = (Spinner) rootView.findViewById(R.id.spinner1);
        List<String> list = new ArrayList<String>();
        list.add("2018");
        list.add("2017");
        list.add("2016");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(dataAdapter);

        //Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.tabthree_toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Bill by Year - " + customer.getName() + "</font>"));

        //customerName = (TextView) rootView.findViewById(R.id.txt_customer_invoices_customer_name);
        addInvoice = (Button) rootView.findViewById(R.id.fab_add_invoice);
        addInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), FragmentEditInvoice.newInstance(customer, new BillInvoice()));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //customerName.setText(customer.getName());
        loadCustomerInvoices();

    }

    private void loadCustomerInvoices() {
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(customer);
        pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getCustomerInvoices", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
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
                    if (serviceResponse.getInvoices() != null && serviceResponse.getInvoices().size() > 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        CustomerInvoiceAdapter adapter = new CustomerInvoiceAdapter(serviceResponse.getInvoices(), getActivity(), customer);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

}

