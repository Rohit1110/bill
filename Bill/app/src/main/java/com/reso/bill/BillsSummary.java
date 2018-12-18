package com.reso.bill;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.rns.web.billapp.service.util.BillConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import adapters.BillSummaryAdapter;
import model.BillCustomer;
import util.ServiceUtil;
import util.Utility;

public class BillsSummary extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<BillCustomer> list;
    private ProgressDialog pDialog;
    //private RadioButton orders, holiday;
    private Spinner months, item;
    private BillUser customer;
    private List<String> yearsList;
    private List<BillItem> items;
    private List<BillUser> bills;
    private BillUser user;
    private CheckBox showSchemeOnly;
    private BillSummaryAdapter billAdapter;
    private ImageView editInvoice;
    private boolean loadInProgress;
    //private boolean firstLoad;
    //private List<BillOrder> ordersList;
    //private List<BillUserLog> log;
    //private CustomerLogActivityAdapter logAdapter;
    //private boolean logs = false;

    public static BillsSummary newInstance() {
        BillsSummary fragment = new BillsSummary();
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
                filter(newText);
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

    public void filter(final String text) {
        //Toast.makeText(getActivity(),text,Toast.LENGTH_LONG).show();
        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (billAdapter == null || billAdapter.getUsers() == null) {
                        return;
                    }
                    final List<BillUser> filterList = new ArrayList<>();

                    // If there is no search value, then add all original list items to filter list
                    if (!TextUtils.isEmpty(text)) {
                        // Iterate in the original List and add it to filter list...
                        for (BillUser customer : billAdapter.getUsers()) {
                            System.out.println("Get Name --->>> " + customer.getName());
                            if (customer.getName() != null && customer.getName().toLowerCase().contains(text.toLowerCase()) /*|| comparePhone(item, text)*/) {
                                // Adding Matched items
                                filterList.add(customer);
                            } else if (customer.getAddress() != null && customer.getAddress().toLowerCase().contains(text.toLowerCase())) {
                                filterList.add(customer);
                            } else if (customer.getPhone() != null && customer.getPhone().toLowerCase().contains(text.toLowerCase())) {
                                filterList.add(customer);
                            }
                        }
                    } else {
                        if (billAdapter.getFilteredUsers() != null) {
                            filterList.addAll(billAdapter.getFilteredUsers());
                        } else {
                            filterList.addAll(billAdapter.getOriginals());
                        }

                    }
                    // Set on UI Thread
                    (getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            billAdapter.updateSearchList(filterList);
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Error in filter contacts");
                    e.printStackTrace();
                }


            }
        }).start();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bills_summary, container, false);
        Utility.AppBarTitle("Bills Summary", getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_bills_summary);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        months = (Spinner) rootView.findViewById(R.id.spinner_bill_summary_month);
        item = (Spinner) rootView.findViewById(R.id.spinner_bill_summary_item);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.months_arrays));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        months.setAdapter(adapter);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        if (currentMonth == 0) {
            currentMonth = 12;
        }
        months.setSelection(currentMonth);

        item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadBillSummary();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        months.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadBillSummary();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        showSchemeOnly = (CheckBox) rootView.findViewById(R.id.chk_bill_summary_show_scheme);
        showSchemeOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (billAdapter != null) {
                    billAdapter.showOnlySchemeUsers(b);
                }
            }
        });

        editInvoice = (ImageView) rootView.findViewById(R.id.btn_edit_invoice_amount_multiple);
        editInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (item.getSelectedItemPosition() < 1) {
                    Utility.createAlert(getActivity(), "Please select a product to change first!", "Error");
                    return;
                }

                if (billAdapter == null || billAdapter.getUsers() == null) {
                    Utility.createAlert(getActivity(), "No bills to change!", "Error");
                    return;
                }

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_edit_invoice_item_multiple);
                dialog.setTitle("Change amount bulk");

                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.txt_dialog_invoice_item_name);
                text.setText(item.getSelectedItem().toString());

                final EditText amount = (EditText) dialog.findViewById(R.id.et_dialog_invoice_item_amount_multiple);
                //amount.setText("0");


                TextView txtNote = (TextView) dialog.findViewById(R.id.txt_invoice_item_change_note);
                txtNote.setText("Changing the amount for " + item.getSelectedItem().toString() + " of " + billAdapter.getUsers().size() + " customers");


                Button saveBillItem = (Button) dialog.findViewById(R.id.btn_dialog_save_invoice_item_multiple);
                // if button is clicked, close the custom dialog
                saveBillItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(amount.getText())) {
                            amount.setError("Please enter the amount");
                            return;
                        }
                        BillServiceRequest request = new BillServiceRequest();
                        BillInvoice invoice = new BillInvoice();
                        invoice.setMonth(Arrays.asList(getResources().getStringArray(R.array.months_arrays)).indexOf(months.getSelectedItem()));
                        request.setInvoice(invoice);
                        BillItem requestedItem = new BillItem();
                        if (item.getSelectedItem() != null) {
                            BillItem selectedItem = (BillItem) Utility.findInStringList(items, item.getSelectedItem().toString());
                            if (selectedItem != null) {
                                requestedItem.setParentItemId(selectedItem.getId());
                                requestedItem.setPrice(Utility.getDecimal(amount));
                                if(showSchemeOnly.isChecked()) {
                                    requestedItem.setPriceType(BillConstants.FREQ_MONTHLY);
                                }
                                request.setItem(requestedItem);
                            }
                        }

                        pDialog = Utility.getProgressDialogue("Saving..", getActivity());
                        StringRequest myReq = ServiceUtil.getStringRequest("updateInvoiceItems", saveMultipleItemsListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
                        RequestQueue queue = Volley.newRequestQueue(getActivity());
                        queue.add(myReq);
                    }

                    private Response.Listener<String> saveMultipleItemsListener() {
                        return new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("## response:" + response);
                                pDialog.dismiss();

                                list = new ArrayList<>();
                                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                                    Utility.createAlert(getActivity(), "Saved succesfully!", "Done");
                                    dialog.dismiss();
                                    loadBillSummary();
                                } else {
                                    System.out.println("Error .." + serviceResponse.getResponse());
                                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                                }
                            }

                        };
                    }
                });

                Button cancel = (Button) dialog.findViewById(R.id.btn_dialog_cancel_invoice_item_multiple);
                // if button is clicked, close the custom dialog
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //loadBillSummary();
        user = (BillUser) Utility.readObject(getActivity(), Utility.USER_KEY);
        if (user == null) {
            Utility.createAlert(getActivity(), "Error in profile. Close the app and try again!", "Error");
        }
    }

    private void loadBillSummary() {
        if (user == null || loadInProgress) {
            return;
        }
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        BillInvoice invoice = new BillInvoice();
        invoice.setMonth(Arrays.asList(getResources().getStringArray(R.array.months_arrays)).indexOf(months.getSelectedItem()));
        request.setInvoice(invoice);
        BillItem requestedItem = new BillItem();
        if (item.getSelectedItem() != null) {
            BillItem selectedItem = (BillItem) Utility.findInStringList(items, item.getSelectedItem().toString());
            if (selectedItem != null) {
                requestedItem.setParentItemId(selectedItem.getParentItem().getId());
                request.setItem(requestedItem);
            }
        }
        loadInProgress = true;
        pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getBillSummary", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();
                loadInProgress = false;
                list = new ArrayList<>();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    items = serviceResponse.getItems();
                    bills = serviceResponse.getUsers();
                    if (items != null && item.getAdapter() == null) {
                        List<String> strings = new ArrayList<>();
                        strings.add("ALL");
                        strings.addAll(Utility.convertToStringArrayList(items));
                        System.out.println("String list == " + strings);
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, strings);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        item.setAdapter(dataAdapter);

                    }
                    if (bills != null && bills.size() > 0) {
                        billAdapter = new BillSummaryAdapter(bills, getActivity());
                    } else {
                        billAdapter = new BillSummaryAdapter(new ArrayList<BillUser>(), getActivity());
                    }

                    recyclerView.setAdapter(billAdapter);
                    billAdapter.showOnlySchemeUsers(showSchemeOnly.isChecked());

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }
            }

        };
    }


}
