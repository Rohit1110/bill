package com.reso.bill;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
    private EditText amount, serviceCharge, credit, pending;
    private Button save;
    private Button send;
    private Spinner invoiceStatusSpinner;
    private List<String> statusList;
    private TextView payableAmount;

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
        getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Bill Details</font>"));
        Utility.AppBarTitle("Bill Details" ,getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_bill_two);
        //btnpay = (Button) rootView.findViewById(R.id.btn_pay);

        monthspinner = (Spinner) rootView.findViewById(R.id.spn_months);
        monthsArray = getResources().getStringArray(R.array.months_arrays);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_basic_text_white, monthsArray);
        monthspinner.setAdapter(adapter);

        yearsSpinner = (Spinner) rootView.findViewById(R.id.spn_year);
        yearsList = Utility.createYearsArray();
        yearsSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_basic_text_white, yearsList));

        /*btnpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });*/

        payableAmount = (TextView) rootView.findViewById(R.id.txt_invoice_payable);

        amount = (EditText) rootView.findViewById(R.id.et_bill_details_amount);
        amount.setEnabled(true);
        amount.addTextChangedListener(watcher);


        if (invoice.getAmount() != null) {
            amount.setText(invoice.getAmount().toString());
        }

        serviceCharge = (EditText) rootView.findViewById(R.id.et_bill_details_service_charge);
        serviceCharge.setEnabled(true);
        serviceCharge.addTextChangedListener(watcher);

        if (invoice.getServiceCharge() != null) {
            serviceCharge.setText(invoice.getServiceCharge().toString());
        } else if (customer.getServiceCharge() != null) {
            serviceCharge.setText(customer.getServiceCharge().toString());
        }

        save = (Button) rootView.findViewById(R.id.fab_save_invoice);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInvoice();
            }
        });

        send = (Button) rootView.findViewById(R.id.btn_send_invoice);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvoice();
            }
        });

        vendor = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);

        if (invoice.getYear() != null) {
            yearsSpinner.setSelection(yearsList.indexOf(invoice.getYear().toString()));
            yearsSpinner.setEnabled(false);
        }
        if (invoice.getMonth() != null) {
            monthspinner.setSelection(invoice.getMonth() - 1);
            monthspinner.setEnabled(false);
        }

        invoiceStatusSpinner = (Spinner) rootView.findViewById(R.id.spn_invoice_status);
        prepareStatuses();
        invoiceStatusSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_basic_text, statusList));
        if (invoice.getStatus() != null) {
            int position = statusList.indexOf(invoice.getStatus());
            if (position < 0) {
                invoiceStatusSpinner.setSelection(0);
            } else {
                invoiceStatusSpinner.setSelection(position);
            }
        }

        credit = (EditText) rootView.findViewById(R.id.et_bill_details_credit_amount);
        credit.addTextChangedListener(watcher);
        pending = (EditText) rootView.findViewById(R.id.et_bill_details_pending_amount);
        pending.addTextChangedListener(watcher);

        if (invoice.getCreditBalance() != null) {
            credit.setText(invoice.getCreditBalance().toString());
        }
        if (invoice.getPendingBalance() != null) {
            pending.setText(invoice.getPendingBalance().toString());
        }


        if (invoice.getAmount() != null) {
            payableAmount.setText(invoice.getAmount().toString());
            calculatePayable();
        } else {
            payableAmount.setText("0.00");
        }

        return rootView;
    }

    private void calculatePayable() {
        try {
            BigDecimal payableAmountValue = new BigDecimal(payableAmount.getText().toString());
            if (!TextUtils.isEmpty(amount.getText())) {
                payableAmountValue = new BigDecimal(amount.getText().toString());
            }
            if (!TextUtils.isEmpty(serviceCharge.getText())) {
                payableAmountValue = payableAmountValue.add(new BigDecimal(serviceCharge.getText().toString()));
            }
            if (!TextUtils.isEmpty(pending.getText())) {
                payableAmountValue = payableAmountValue.add(new BigDecimal(pending.getText().toString()));
            }
            if (!TextUtils.isEmpty(credit.getText())) {
                payableAmountValue = payableAmountValue.subtract(new BigDecimal(credit.getText().toString()));
            }
            payableAmount.setText(payableAmountValue.toString());
        } catch (Exception e) {
            System.err.println("Error in input ..");
            payableAmount.setText("0.00");
        }

    }

    private void prepareStatuses() {
        statusList = new ArrayList<>();
        statusList.add("Pending");
        statusList.add("Credit");
        statusList.add("Failed");
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


        if (customer.getCurrentSubscription() == null || customer.getCurrentSubscription().getItems() == null || customer.getCurrentSubscription().getItems().size() == 0) {
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
        BillDetailsEditAdapter adapter = new BillDetailsEditAdapter(customer.getCurrentSubscription().getItems(), getContext(), amount);
        recyclerView.setAdapter(adapter);
        adapter.setRecyclerView(recyclerView);
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

        if (serviceCharge.getText() != null && serviceCharge.getText().toString().trim().length() > 0) {
            invoice.setServiceCharge(new BigDecimal(serviceCharge.getText().toString()));
        }

        invoice.setStatus(invoiceStatusSpinner.getSelectedItem().toString());
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

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            calculatePayable();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private Integer getMonth() {
        int index = Arrays.asList(monthsArray).indexOf(monthspinner.getSelectedItem());
        if (index >= 0) {
            return (index + 1);
        }
        return null;
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

    private void sendInvoice() {
        if (invoice.getId() == null) {
            Utility.createAlert(getContext(), "Please save the invoice first!", "Error");
            return;
        }
        BillServiceRequest request = new BillServiceRequest();
        request.setRequestType("EMAIL");
        request.setInvoice(invoice);
        pDialog = Utility.getProgressDialogue("Sending Invoice..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("sendInvoice", invoiceSendResponse(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);

    }

    private Response.Listener<String> invoiceSendResponse() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();
                final BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    //Utility.createAlert(getContext(), "Invoice sent to customer successfully!", "Done");

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setTitle("Success");
                    alertDialogBuilder.setMessage("Invoice sent to customer via Email/ SMS successfully! Do you want to share it via WhatsApp?");
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            whatsappIntent.setType("text/plain");
                            whatsappIntent.setPackage("com.whatsapp");
                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, serviceResponse.getResponse());
                            try {
                                getActivity().startActivity(whatsappIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                System.out.println("No whatsapp!!!");
                            }
                        }
                    });
                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }

            }

        };
    }

}
