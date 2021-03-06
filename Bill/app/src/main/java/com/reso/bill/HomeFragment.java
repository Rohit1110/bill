package com.reso.bill;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillCache;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.DeliveriesAdapter;
import model.BillCustomer;
import model.BillFilter;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/8/2018.
 */

public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener {
    private List<BillCustomer> orders = new ArrayList<>();
    private List<BillCustomer> filterList = new ArrayList<>();
    private List<BillCustomer> noOrders = new ArrayList<>();
    private RecyclerView recyclerView;
    private DeliveriesAdapter adapter;
    private ProgressDialog pDialog;
    private BillUser user;
    private List<BillUser> users;
    private Date date;
    private TextView noOrdersMessage;
    private RadioButton deliveries;
    private RadioButton noDeliveries;
    private Menu fragmentMenu;
    private BillFilter filter;
    private DialogInterface.OnDismissListener dismissListener;
    //EditText search;

    public static HomeFragment newInstance(BillUser user) {
        HomeFragment fragment = new HomeFragment();
        fragment.user = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /* @Override
     public void onPrepareOptionsMenu(Menu menu) {
         MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
         SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
     }
 */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        menu.add(Menu.NONE, Utility.MENU_ITEM_SEARCH, 0, "Search").setIcon(R.drawable.ic_search_black_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem item = menu.getItem(0);

        //inflater.inflate(R.menu.search, menu);
        //MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView((Utility.castActivity(getActivity())).getSupportActionBar().getThemedContext());
        //MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
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
        menu.add(Menu.NONE, Utility.MENU_ITEM_FILTER, 1, "Filter").setIcon(R.drawable.ic_action_filter_list_disabled).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, Utility.MENU_ITEM_EXPORT, 2, "Export").setIcon(R.drawable.ic_action_picture_as_pdf).setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

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
                            loadDeliveries();
                        }
                    };
                    filter.getDialog().setOnDismissListener(dismissListener);
                }

                fragmentMenu.getItem(1).setIcon(filter.getFilterIcon());
                return true;
            case Utility.MENU_ITEM_EXPORT:
                try {
                    System.out.println("PDF clicked ...");
                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, Utility.MY_PERMISSIONS_REQUEST_CONTACTS);
                        return true;
                    }
                    String dateString = CommonUtils.convertDate(date);
                    Utility.exportPendingInvoices(filter, user, getActivity(), "date=" + dateString, "Deliveries-" + dateString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_one, container, false);
        date = new Date();
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Deliveries " + CommonUtils.convertDate(date) + "</font>"));
        Utility.AppBarTitle("Today's Deliveries", getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        deliveries = (RadioButton) rootView.findViewById(R.id.radio_deliveries);
        //search = (EditText) rootView.findViewById(R.id.edit_search_order);
        deliveries.setSelected(true);
        deliveries.setChecked(true);
        deliveries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDeliveriesListView(orders);
            }
        });

        noDeliveries = (RadioButton) rootView.findViewById(R.id.radio_no_deliveries);
        noDeliveries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDeliveriesListView(noOrders);
            }
        });
        /*search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                String text = search.getText().toString().toLowerCase(Locale.getDefault());

                filter(text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
        noOrdersMessage = (TextView) rootView.findViewById(R.id.txt_no_orders);
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        loadDeliveries();
    }

    private void loadDeliveries() {
        if (user == null) {
            Utility.createAlert(getContext(), "Profile not set correctly! Please login to the app again!", "Error");
            return;
        }

        //Load from cache
        users = BillCache.getDeliveries(getActivity());
        addUsers(false);

        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        request.setRequestedDate(date);

        if (filter != null) {
            request.setCustomerGroup(filter.getGroup());

        }

        //Removed to unblock the UI
        //pDialog = Utility.getProgressDialogue("Loading..", getActivity());

        StringRequest myReq = ServiceUtil.getStringRequest("getDeliveries", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    users = serviceResponse.getUsers();
                    if (filter == null) {
                        BillCache.addDeliveries(users, getActivity()); //Add to cache
                    }
                    addUsers(true);
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

    private void addUsers(boolean loaded) {
        orders = new ArrayList<>();
        if (users != null && users.size() > 0) {
            noOrdersMessage.setVisibility(View.GONE);
            for (BillUser user : users) {
                if (user.getCurrentSubscription() != null && user.getCurrentSubscription().getStatus() != null && user.getCurrentSubscription().getStatus().equals("D")) {
                    noOrders.add(new BillCustomer(user));
                } else {
                    orders.add(new BillCustomer(user));
                }
            }
            setDeliveriesListView(orders);
        } else if (!loaded) {
            noOrdersMessage.setVisibility(View.VISIBLE);
            noOrdersMessage.setText("Loading ...");
        } else {
            noOrdersMessage.setVisibility(View.VISIBLE);
            noOrdersMessage.setText("No orders today");
        }
    }

    private void setDeliveriesListView(List<BillCustomer> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DeliveriesAdapter(list, getActivity(), user);
        recyclerView.setAdapter(adapter);
    }

    public void filter(final String text) {

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

                        filterList.addAll(orders);


                    } else {
                        // Iterate in the original List and add it to filter list...
                        System.out.println(".......");
                        for (BillCustomer item : orders) {
                            BillUser customer = item.getUser();
                            if (customer.getName().toLowerCase().contains(text.toLowerCase()) /*|| comparePhone(item, text)*/) {
                                System.out.println("item added .. " + customer.getId());
                                if (notContains(filterList, item)) {
                                    // Adding Matched items
                                    filterList.add(item);
                                }

                            } else if (customer.getAddress() != null && customer.getAddress().toLowerCase().contains(text.toLowerCase())) {
                                if (notContains(filterList, item)) {
                                    filterList.add(item);
                                }
                            } else if (customer.getPhone() != null && customer.getPhone().toLowerCase().contains(text.toLowerCase())) {
                                if (notContains(filterList, item)) {
                                    filterList.add(item);
                                }
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
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new DeliveriesAdapter(filterList, getActivity(), user);
                            recyclerView.setAdapter(adapter);


                        }
                    });
                } catch (Exception e) {
                    System.out.println("Error in filter contacts");
                    e.printStackTrace();
                }


            }
        }).start();

    }

    private boolean notContains(List<BillCustomer> filterList, BillCustomer item) {
        if (item == null || filterList == null) {
            return false;
        }
        if (filterList != null && filterList.size() == 0) {
            return true;
        }
        for (BillCustomer customer : filterList) {
            if (customer.getUser().getId() == item.getUser().getId()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String dateString = CommonUtils.convertDate(date);
                    Utility.exportPendingInvoices(filter, user, getActivity(), "date=" + dateString, "Deliveries-" + dateString);
                } else {
                    //Utility.nextFragment(GenericCustomerInfoActivity.this, getNextFragment());
                    System.out.println("No permission!");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}