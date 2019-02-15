package com.reso.bill;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.InvoicesAdapter;
import model.BillCustomer;
import model.BillFilter;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/8/2018.
 */

public class FragmentInvoiceSummary extends Fragment {
    private List<BillCustomer> orders = new ArrayList<>();
    private List<BillCustomer> noOrders = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private BillUser user;
    private List<BillUser> users;
    private Date date;
    private TextView noOrdersMessage;
    private RadioButton deliveries;
    private RadioButton noDeliveries;
    private TextView totalPendingCount;
    private List<BillUser> list = new ArrayList<>();
    private List<BillUser> filterList = new ArrayList<>();
    private Button sendReminder;
    private InvoicesAdapter adapter;
    private Button clear;
    private Menu fragmentMenu;
    private BillFilter filter;
    private DialogInterface.OnDismissListener dismissListener;
    private BillUser currentUser;

    public static FragmentInvoiceSummary newInstance(BillUser user) {
        FragmentInvoiceSummary fragment = new FragmentInvoiceSummary();
        fragment.user = user;
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

        menu.add(Menu.NONE, Utility.MENU_ITEM_FILTER, Menu.NONE, "Filter").setIcon(R.drawable.ic_action_filter_list_disabled).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        fragmentMenu = menu;
        //searchView.setMenuItem(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Utility.MENU_ITEM_FILTER:
                System.out.println("FIlter called ...");
                if (filter == null) {
                    filter = new BillFilter(getActivity(), user);
                }
                filter.showFilterDialog();

                if (dismissListener == null) {
                    dismissListener = new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            fragmentMenu.getItem(1).setIcon(filter.getFilterIcon());
                            loadInvoiceSummary(null);
                        }
                    };
                    filter.getDialog().setOnDismissListener(dismissListener);
                }

                fragmentMenu.getItem(1).setIcon(filter.getFilterIcon());
                return true;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invoice_summary, container, false);
        date = new Date();
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Invoice Summary</font>"));
        Utility.AppBarTitle("Pending Invoices", getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);


        sendReminder = (Button) rootView.findViewById(R.id.btn_send_reminders);

        sendReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noOfCustomers = "all";
                if (adapter.getSelectedCustomers() != null && adapter.getSelectedCustomers().size() > 0) {
                    noOfCustomers = "" + adapter.getSelectedCustomers().size();
                }
                new AlertDialog.Builder(getActivity()).
                        setTitle("Warning").
                        setMessage("Are you sure you want to send SMS/Email reminders to " + noOfCustomers + " of your customers?").
                        setIcon(android.R.drawable.ic_dialog_alert).
                        setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                sendReminders();
                            }
                        }).setNegativeButton(android.R.string.no, null).show();
            }
        });

        totalPendingCount = (TextView) rootView.findViewById(R.id.txt_total_pending_bills);

        clear = (Button) rootView.findViewById(R.id.btn_customers_invoices_clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clearSelected();
            }
        });

        return rootView;

    }

    private void sendReminders() {
        loadInvoiceSummary("EMAIL");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            currentUser = adapter.getCurrentUser();
        }
        //if (users == null || users.size() == 0) {
        loadInvoiceSummary(null);
        //}
    }

    private void loadInvoiceSummary(String type) {
        if (user == null || user.getCurrentBusiness() == null) {
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            return;
        }
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        request.setRequestedDate(date);
        String title = "Loading..";
        if (type != null) {
            request.setRequestType(type);
            if (adapter.getSelectedCustomers() != null && adapter.getSelectedCustomers().size() > 0) {
                request.setUsers(adapter.getSelectedCustomers());
            }
            title = "Sending reminders ..";
        }
        if (filter != null) {
            request.setCustomerGroup(filter.getGroup());
        }
        pDialog = Utility.getProgressDialogue(title, getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getInvoiceSummary", createMyReqSuccessListener(type), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener(final String requestType) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (requestType == null) {
                        users = serviceResponse.getUsers();
                        if (users != null && users.size() > 0) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new InvoicesAdapter(users, getActivity());
                            adapter.setClearButton(clear);
                            recyclerView.setAdapter(adapter);
                            totalPendingCount.setText("Total pending bills - " + users.size());
                            Integer index = getCurrentUserPosition();
                            if (index != null) {
                                recyclerView.scrollToPosition(index);
                                adapter.setCurrentUser(currentUser);
                            }

                        }
                    } else {
                        Utility.createAlert(getActivity(), "Sent the reminders successfully!", "Done");
                    }

                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

    private Integer getCurrentUserPosition() {
        if (currentUser == null || users == null | users.size() == 0) {
            return null;
        }
        int index = 0;
        for (BillUser user : users) {
            if (user.getId().intValue() == currentUser.getId().intValue()) {
                System.out.println("Current user found ... " + user.getName());
                return index;
            }
            index++;
        }
        return null;
    }


    public void filter(final String text) {

        //Toast.makeText(getActivity(),text,Toast.LENGTH_LONG).show();

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // Clear the filter list
                    filterList.clear();

                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {

                        /*hideicon = true;
                        invalidateOptionsMenu();*/

                        filterList.addAll(users);


                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (BillUser customer : users) {
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
                    }

                    // Set on UI Thread
                    (getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Notify the List that the DataSet has changed...
                           /* adapter = new ContactListAdapter(SearchAppointmentActivity.this, filterList);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(SearchAppointmentActivity.this, 1);
                            recyclerView_contact.setLayoutManager(mLayoutManager);
                            recyclerView_contact.setAdapter(adapter);*/
                           /* recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new DeliveriesAdapter(filterList, getActivity(), user);
                            recyclerView.setAdapter(adapter);*/

                            /*recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(new InvoicesAdapter(filterList, getActivity()));*/

                            adapter.updateSearchList(filterList);

                            //recyclerView.setAdapter(adapter);


                        }
                    });
                } catch (Exception e) {
                    System.out.println("Error in filter contacts");
                    e.printStackTrace();
                }


            }
        }).start();

    }


}