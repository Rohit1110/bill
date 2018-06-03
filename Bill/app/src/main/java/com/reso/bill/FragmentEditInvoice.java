package com.reso.bill;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillInvoice;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import adapters.BillDetailsEditAdapter;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/28/2018.
 */

public class FragmentEditInvoice extends Fragment {
    private RecyclerView recyclerView;
    private Button btnpay;
    private Spinner monthspinner;
    private BillUser customer;
    private BillInvoice invoice;
    private BillUser vendor;
    private ProgressDialog pDialog;
    private String[] monthsArray;
    private List<String> yearsList;
    private Spinner yearsSpinner;
    private EditText amount;
    private FloatingActionButton save;

    public static FragmentEditInvoice newInstance(BillUser customer, BillInvoice invoice) {
        FragmentEditInvoice fragment = new FragmentEditInvoice();
        fragment.customer = customer;
        fragment.invoice = invoice;
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mv_bill_details_edit, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Bill Details</font>"));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_bill_two);
        btnpay = (Button) rootView.findViewById(R.id.btn_pay);

        monthspinner = (Spinner) rootView.findViewById(R.id.spn_months);
        monthsArray = getResources().getStringArray(R.array.months_arrays);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, monthsArray);
        monthspinner.setAdapter(adapter);

        yearsSpinner = (Spinner) rootView.findViewById(R.id.spn_year);
        createYearsArray();
        yearsSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, yearsList));

        btnpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });

        amount = (EditText) rootView.findViewById(R.id.et_bill_details_amount);
        amount.setEnabled(true);
        amount.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                amount.setEnabled(!amount.isEnabled());
                return true;
            }
        });

        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount.setEnabled(!amount.isEnabled());
            }
        });

        if (invoice.getAmount() != null) {
            amount.setText(invoice.getAmount().toString());
        }

        save = (FloatingActionButton) rootView.findViewById(R.id.fab_save_invoice);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInvoice();
            }
        });

        vendor = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);
        return rootView;
    }

    private void showDialog() {
        SingleChoiceWithRadioButton();

    }

    AlertDialog alert;

    private void SingleChoiceWithRadioButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pay Status");
        View mView = getLayoutInflater().inflate(R.layout.alert_spinner, null);
        Spinner spinner = (Spinner) mView.findViewById(R.id.alert_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.paymode));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        builder.setView(mView);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert = builder.create();
        alert.show();
    }


    @Override
    public void onResume() {
        super.onResume();


        if (customer.getCurrentSubscription().getItems() == null || customer.getCurrentSubscription().getItems().size() == 0) {
            Utility.createAlert(getContext(), "Add products to this customer first!", "Error");
            return;
        }

        for (BillItem item : customer.getCurrentSubscription().getItems()) {
            if (invoice.getInvoiceItems() != null) {
                for (BillItem invoiceItem : invoice.getInvoiceItems()) {
                    if (invoiceItem.getParentItemId() == item.getId()) {
                        item.setQuantity(invoiceItem.getQuantity());
                        item.setPrice(invoiceItem.getPrice());
                    }
                }
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new BillDetailsEditAdapter(customer.getCurrentSubscription().getItems(), getContext()));
    }

    private void saveInvoice() {
        if (invoice.getYear() == null || invoice.getMonth() == null) {
            invoice.setMonth(getMonth());
            if (yearsSpinner.getSelectedItem() != null && yearsSpinner.getSelectedItem().toString().trim().length() > 0) {
                invoice.setYear(new Integer(yearsSpinner.getSelectedItem().toString()));
            }
            if (invoice.getMonth() == null || invoice.getYear() == null) {
                Utility.createAlert(getContext(), "Please select month and year for the invoice", "Error");
                return;
            }
        }
        //if (invoice.getAmount() == null) {
        if (amount.getText() == null || amount.getText().toString().trim().length() == 0) {
            Utility.createAlert(getContext(), "Please enter invoice amount", "Error");
            return;
        }
        invoice.setAmount(new BigDecimal(amount.getText().toString()));
        //}
        BillServiceRequest request = new BillServiceRequest();
        invoice.setInvoiceItems(getInvoiceItems());
        request.setInvoice(invoice);
        request.setUser(customer);
        pDialog = Utility.getProgressDialogue("Saving..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerInvoice", customerProfileLoader(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Integer getMonth() {
        int index = Arrays.asList(monthsArray).indexOf(monthspinner.getSelectedItem());
        if (index >= 0) {
            return (index + 1);
        }
        return null;
    }

    private void createYearsArray() {
        //+- 2 years
        Calendar cal = Calendar.getInstance();
        yearsList = new ArrayList<>();
        cal.add(Calendar.YEAR, -1);
        yearsList.add(String.valueOf(cal.get(Calendar.YEAR)));
        cal.add(Calendar.YEAR, -1);
        yearsList.add(String.valueOf(cal.get(Calendar.YEAR)));
        cal.add(Calendar.YEAR, 2);
        yearsList.add(String.valueOf(cal.get(Calendar.YEAR)));
        /*cal.add(Calendar.YEAR, 1);
        yearsList.add(String.valueOf(cal.get(Calendar.YEAR)));
        cal.add(Calendar.YEAR, 1);
        yearsList.add(String.valueOf(cal.get(Calendar.YEAR)));*/

    }

    private List<BillItem> getInvoiceItems() {
        List<BillItem> invoiceItems = new ArrayList<>();
        for (BillItem item : customer.getCurrentSubscription().getItems()) {
            if (item.getPrice() != null) {
                invoiceItems.add(item);
            }
        }
        return invoiceItems;
    }

    private Response.Listener<String> customerProfileLoader() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlert(getContext(), "Invoice updated successfully!", "Done");
                    Utility.nextFragment(getActivity(), FragmentCustomerInvoices.newInstance(customer));
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }

            }

        };
    }
}
