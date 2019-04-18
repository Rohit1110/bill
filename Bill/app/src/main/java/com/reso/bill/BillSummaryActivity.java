package com.reso.bill;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import model.BillFilter;
import util.ServiceUtil;
import util.Utility;

public class BillSummaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<BillCustomer> list;
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
    private Menu mainMenu;
    private BillFilter filter;
    private DialogInterface.OnDismissListener dismissListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_summary);

        Utility.setActionBar("Summary of your Invoices", getSupportActionBar());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_bills_summary);
        recyclerView.setLayoutManager(new LinearLayoutManager(BillSummaryActivity.this));

        months = (Spinner) findViewById(R.id.spinner_bill_summary_month);
        item = (Spinner) findViewById(R.id.spinner_bill_summary_item);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(BillSummaryActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.months_arrays));
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

        showSchemeOnly = (CheckBox) findViewById(R.id.chk_bill_summary_show_scheme);
        showSchemeOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (billAdapter != null) {
                    billAdapter.showOnlySchemeUsers(b);
                }
            }
        });

        editInvoice = (ImageView) findViewById(R.id.btn_edit_invoice_amount_multiple);
        editInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (item.getSelectedItemPosition() < 1) {
                    Utility.createAlert(BillSummaryActivity.this, "Please select a product to change first!", "Error");
                    return;
                }

                if (billAdapter == null || billAdapter.getUsers() == null) {
                    Utility.createAlert(BillSummaryActivity.this, "No bills to change!", "Error");
                    return;
                }

                final Dialog dialog = new Dialog(BillSummaryActivity.this);
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
                                if (showSchemeOnly.isChecked()) {
                                    requestedItem.setPriceType(BillConstants.FREQ_MONTHLY);
                                }
                                request.setItem(requestedItem);
                            }
                        }

                        pDialog = Utility.getProgressDialogue("Saving..", BillSummaryActivity.this);
                        StringRequest myReq = ServiceUtil.getStringRequest("updateInvoiceItems", saveMultipleItemsListener(), ServiceUtil.createMyReqErrorListener(pDialog, BillSummaryActivity.this), request);
                        RequestQueue queue = Volley.newRequestQueue(BillSummaryActivity.this);
                        queue.add(myReq);
                    }

                    private Response.Listener<String> saveMultipleItemsListener() {
                        return new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("## response:" + response);
                                //pDialog.dismiss();
                                Utility.dismiss(pDialog);
                                list = new ArrayList<>();
                                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                                    Utility.createAlert(BillSummaryActivity.this, "Saved succesfully!", "Done");
                                    dialog.dismiss();
                                    loadBillSummary();
                                } else {
                                    System.out.println("Error .." + serviceResponse.getResponse());
                                    Utility.createAlert(BillSummaryActivity.this, serviceResponse.getResponse(), "Error");
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
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

        menu.add(Menu.NONE, Utility.MENU_ITEM_FILTER, Menu.NONE, "Filter").setIcon(R.drawable.ic_action_filter_list_disabled).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        mainMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case Utility.MENU_ITEM_FILTER:
                System.out.println("FIlter called ...");
                if (filter == null) {
                    filter = new BillFilter(this, user);
                }
                filter.showFilterDialog();

                if (dismissListener == null) {
                    dismissListener = new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            mainMenu.getItem(1).setIcon(filter.getFilterIcon());
                            loadBillSummary();
                        }
                    };
                    filter.getDialog().setOnDismissListener(dismissListener);
                }

                mainMenu.getItem(1).setIcon(filter.getFilterIcon());
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    public void filter(final String text) {
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
                    (BillSummaryActivity.this).runOnUiThread(new Runnable() {
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


    @Override
    public void onResume() {
        super.onResume();
        //loadBillSummary();
        user = (BillUser) Utility.readObject(BillSummaryActivity.this, Utility.USER_KEY);
        if (user == null) {
            Utility.createAlert(BillSummaryActivity.this, "Error in profile. Close the app and try again!", "Error");
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
        if (filter != null) {
            request.setCustomerGroup(filter.getGroup());
        }
        loadInProgress = true;
        pDialog = Utility.getProgressDialogue("Loading..", BillSummaryActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("getBillSummary", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, BillSummaryActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(BillSummaryActivity.this);
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
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
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BillSummaryActivity.this, android.R.layout.simple_spinner_item, strings);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        item.setAdapter(dataAdapter);

                    }
                    if (bills != null && bills.size() > 0) {
                        billAdapter = new BillSummaryAdapter(bills, BillSummaryActivity.this);
                    } else {
                        billAdapter = new BillSummaryAdapter(new ArrayList<BillUser>(), BillSummaryActivity.this);
                    }

                    recyclerView.setAdapter(billAdapter);
                    billAdapter.showOnlySchemeUsers(showSchemeOnly.isChecked());

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(BillSummaryActivity.this, serviceResponse.getResponse(), "Error");
                }
            }

        };
    }


}
