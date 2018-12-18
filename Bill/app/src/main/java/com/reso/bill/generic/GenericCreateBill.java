package com.reso.bill.generic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.MainActivity;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import adapters.TransactionsAdapter;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/8/2018.
 */

public class GenericCreateBill extends Fragment {

    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private TransactionsAdapter adapter;
    private Button saveBill;
    private Button sendBill;
    private AutoCompleteTextView customerName;
    private EditText customerPhone;
    private EditText customerEmail;
    private BillInvoice invoice;
    private BillUser user;
    private EditText billAmount;
    private CheckBox cashPayment;
    private BillUser customer;
    private ImageView paidIcon;
    private TextView billPaidDetails;
    private List<BillUser> customers;
    private TextView billDetails;

    public static GenericCreateBill newInstance(BillUser user) {
        GenericCreateBill fragment = new GenericCreateBill();
        if (user != null) {
            fragment.invoice = user.getCurrentInvoice();
            fragment.customer = user;
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.share, menu);
        //MenuItem item = menu.findItem(R.id.action_search);

        //searchView.setMenuItem(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.generic_fragment_update_bill, container, false);
        Utility.AppBarTitle("Update Bill", getActivity());
        //recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_transactions);

        saveBill = (Button) rootView.findViewById(R.id.gn_btn_save_bill);

        saveBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBill();
            }
        });

        sendBill = (Button) rootView.findViewById(R.id.gn_btn_send_bill);
        sendBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCustomerBill();
            }
        });

        customerName = (AutoCompleteTextView) rootView.findViewById(R.id.et_product_name);
        customerEmail = (EditText) rootView.findViewById(R.id.et_customer_email);
        customerPhone = (EditText) rootView.findViewById(R.id.et_product_description);

        billAmount = (EditText) rootView.findViewById(R.id.et_customer_bill_amount);

        cashPayment = (CheckBox) rootView.findViewById(R.id.chk_gn_bill_offline_payment);
        paidIcon = (ImageView) rootView.findViewById(R.id.img_gn_delete_inv_item);
        billPaidDetails = (TextView) rootView.findViewById(R.id.txt_bill_paid_details);

        billDetails = (TextView) rootView.findViewById(R.id.btn_gn_bill_details);
        billDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillUser currCustomer = prepareInvoice(true);
                if (currCustomer == null || TextUtils.isEmpty(currCustomer.getPhone())) {
                    //Utility.createAlert(getActivity(), "Please enter phone number first!", "Error");
                    return;
                }
                if (invoice.getAmount() == null) {
                    invoice.setAmount(BigDecimal.ZERO);
                }
                if (invoice.getPayable() == null) {
                    invoice.setPayable(BigDecimal.ZERO);
                }
                currCustomer.setCurrentInvoice(invoice);
                Utility.nextFragment(getActivity(), GenericUpdateInvoiceItems.newInstance(currCustomer));
            }
        });

        user = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);

        customerName.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (customerName.getRight() - customerName.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Utility.checkcontactPermission(getActivity());
                        //identy = "aadhar";
                        // aadharNumber.setText(identy);
                        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        startActivityForResult(contactPickerIntent, Utility.RESULT_PICK_CONTACT);
                        return true;
                    }
                }
                return false;
            }
        });
        ;

        customerName.setThreshold(2);

        customerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (customers != null && customers.size() > 0) {
                        String selected = adapterView.getItemAtPosition(i).toString();
                        BillUser selectedCustomer = (BillUser) Utility.findInStringList(customers, selected);
                        //BillUser selectedCustomer = customers.get(i);
                        customerPhone.setText(selectedCustomer.getPhone());
                        if (!TextUtils.isEmpty(selectedCustomer.getEmail())) {
                            customerEmail.setText(selectedCustomer.getEmail());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error in setting customer .." + e);
                }

            }
        });

        Utility.changeDrawer(getActivity(), GenericInvoices.newInstance());

        return rootView;

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case Utility.RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }

        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String cname = null;
            Uri uri = data.getData();
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();

            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            phoneNo = cursor.getString(phoneIndex);
            cname = cursor.getString(nameIndex);

            customerName.setText(cname);
            customerPhone.setText(phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendCustomerBill() {
        if (invoice == null || invoice.getId() == null) {
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
                            //Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            Intent whatsappIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + customerPhone.getText().toString() + "?body=" + serviceResponse.getResponse()));
                            //whatsappIntent.setType("text/plain");
                            whatsappIntent.setPackage("com.whatsapp");
                            //whatsappIntent.putExtra(Intent.EXTRA_TEXT, serviceResponse.getResponse());
                            try {

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                String whatsappNumber = customerPhone.getText().toString();
                                if (whatsappNumber != null && !whatsappNumber.contains("+91")) {
                                    whatsappNumber = "+91" + whatsappNumber;
                                }
                                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + whatsappNumber + "&text=" + serviceResponse.getResponse()));

                                getActivity().startActivity(intent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                System.out.println("No whatsapp!!!" + ex);
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

    private void saveBill() {
        BillUser customer = prepareInvoice(false);
        if (customer == null) return;

        BillServiceRequest request = new BillServiceRequest();
        request.setInvoice(invoice);
        request.setUser(customer);
        request.setBusiness(user.getCurrentBusiness());
        pDialog = Utility.getProgressDialogue("Saving", getActivity());
        StringRequest myReq = ServiceUtil.getBusinessStringRequest("updateBill", billSavedListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    @Nullable
    private BillUser prepareInvoice(boolean ignoreBillAmount) {
        if (invoice == null) {
            invoice = new BillInvoice();
            invoice.setInvoiceDate(new Date());
        }
        if (customerPhone.getText() == null || customerPhone.getText().toString().length() < 10) {
            Utility.createAlert(getActivity(), "Please enter a valid phone number!", "Error");
            return null;
        }
        if (!ignoreBillAmount && (billAmount.getText() == null || billAmount.getText().toString().length() == 0)) {
            Utility.createAlert(getActivity(), "Please enter a valid bill amount!", "Error");
            return null;
        }
        invoice.setAmount(Utility.getDecimal(billAmount));
        if (cashPayment.isChecked()) {
            invoice.setStatus(BillConstants.INVOICE_STATUS_PAID);
            invoice.setPaidDate(new Date());
        }
        BillUser customer = new BillUser();
        customer.setPhone(customerPhone.getText().toString());
        if (customerEmail.getText() != null) {
            customer.setEmail(customerEmail.getText().toString());
        }
        if (customerName.getText() != null) {
            customer.setName(customerName.getText().toString());
        }
        return customer;
    }

    private Response.Listener<String> billSavedListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    String message = "";
                    if (invoice.getId() == null) {
                        message = " Now you can send the invoice via SMS/Email";
                    }
                    if (serviceResponse.getInvoice() != null) {
                        invoice = serviceResponse.getInvoice();
                        customerPhone.setEnabled(false);
                        prepareInvoice(invoice);
                    }
                    Utility.createAlert(getActivity(), "Invoice saved successfully!" + message, "Done");

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        //loadBillDetails();
        prepareInvoice(invoice, customer);
        if (invoice == null || invoice.getId() == null) {
            loadCustomers(); //For Suggestions
        }

    }

    private void loadCustomers() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        //pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getAllCustomers", customersLoaded(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> customersLoaded() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    customers = serviceResponse.getUsers();
                    if (customers != null && customers.size() > 0) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, Utility.convertToStringArrayList(customers));
                        customerName.setAdapter(adapter);
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    //customerName.setada
                }


            }

        };
    }

    private void loadBillDetails() {
        if (user == null || user.getCurrentBusiness() == null) {
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            return;
        }

        if (invoice == null || invoice.getId() == null) {
            return;
        }
        /*if (durations.getSelectedItemPosition() < 1) {
            Utility.createAlert(getActivity(), "Please select a durations!", "Error");
            return;
        }*/
        BillServiceRequest request = new BillServiceRequest();
        request.setRequestType("READONLY");
        request.setBusiness(user.getCurrentBusiness());
        request.setInvoice(invoice);
        pDialog = Utility.getProgressDialogue("Loading", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("sendInvoice", invoiceLoader(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> invoiceLoader() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    prepareInvoice(serviceResponse.getInvoice(), serviceResponse.getUser());
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_share:
                shareIt();
                return true;

        }
        return false;
    }

    private void shareIt() {
        if (invoice == null || TextUtils.isEmpty(invoice.getPaymentMessage())) {
            return;
        }
        //Toast.makeText(getActivity(),"Click",Toast.LENGTH_LONG).show();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, invoice.getPaymentMessage());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void prepareInvoice(BillInvoice invoice, BillUser user) {
        if (user != null) {
            customerName.setText(user.getName());
            customerEmail.setText(user.getEmail());
            customerPhone.setText(user.getPhone());
            customerPhone.setEnabled(false);
        }
        prepareInvoice(invoice);
    }

    private void prepareInvoice(BillInvoice inv) {
        if (inv != null) {
            billAmount.setText(Utility.getDecimalText(inv.getPayable()));
            if (inv.getStatus() != null && inv.getStatus().equalsIgnoreCase(BillConstants.INVOICE_STATUS_PAID)) {
                cashPayment.setVisibility(View.GONE);
                billPaidDetails.setVisibility(View.VISIBLE);
                paidIcon.setVisibility(View.VISIBLE);
                String paymentMode = "";
                if (inv.getPaymentType() != null) {
                    paymentMode = inv.getPaymentType();
                }
                billPaidDetails.setText("Paid");
                if (inv.getPaidDate() != null) {
                    billPaidDetails.setText("Paid " + paymentMode + " on " + CommonUtils.convertDate(inv.getPaidDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR_TIME));
                }
                //Disable all fields
                customerName.setEnabled(false);
                int disabledColor = R.color.md_blue_grey_200;
                customerName.setTextColor(getResources().getColor(disabledColor));
                customerPhone.setEnabled(false);
                customerPhone.setTextColor(getResources().getColor(disabledColor));
                customerEmail.setEnabled(false);
                customerEmail.setTextColor(getResources().getColor(disabledColor));
                billAmount.setEnabled(false);
                billAmount.setTextColor(getResources().getColor(disabledColor));

            }
        }
    }


}