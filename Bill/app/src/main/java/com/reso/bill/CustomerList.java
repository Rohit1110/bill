package com.reso.bill;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.components.ClickListener;
import com.reso.bill.generic.GenericCustomerInfoActivity;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillCache;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.CustomerListAdapter;
import model.BillCustomer;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/16/2018.
 */

public class CustomerList extends Fragment {
    private List<BillCustomer> list = new ArrayList<>();
    private List<BillCustomer> filterList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private BillUser user;
    private AlertDialog alertDialog;
    private CustomerListAdapter adapter;

    public static CustomerList newInstance() {
        CustomerList fragment = new CustomerList();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.customer_list_main, container, false);
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Customer List</font>"));
        Utility.AppBarTitle("Customer List", getActivity());
        recyclerView = rootView.findViewById(R.id.recycler_view_cust_list);

        FloatingActionButton addNewCustomerFab = rootView.findViewById(R.id.btn_add_customer_group);
        addNewCustomerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(getActivity(), GenericCustomerInfoActivity.class, true));
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder builder = Utility.SingleChoiceWithRadioButton(getActivity());
                builder.setSingleChoiceItems(Utility.LIST_OPTIONS, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == Utility.LIST_OPT_DELETE) {
                            List<BillCustomer> customerList = adapter.getList();
                            if (customerList != null && customerList.size() > 0 && position < customerList.size()) {
                                BillCustomer billCustomer = customerList.get(position);
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                                deleteCustomer(billCustomer);
                            }
                        }
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        }));

        return rootView;
    }

    private void deleteCustomer(BillCustomer billCustomer) {
        if (billCustomer != null && billCustomer.getUser() != null) {
            BillServiceRequest request = new BillServiceRequest();
            billCustomer.getUser().setStatus(BillConstants.STATUS_DELETED);
            request.setUser(billCustomer.getUser());
            pDialog = Utility.getProgressDialogue("Deleting customer..", getActivity());
            StringRequest myReq = ServiceUtil.getStringRequest("updateCustomer", deleteSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(myReq);
        }
    }

    private Response.Listener<String> deleteSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    loadCustomers();
                    Utility.createAlert(getContext(), "Customer deleted successfully!", "Done");
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

        user = (BillUser) Utility.readObject(getActivity(), Utility.USER_KEY);
        loadCustomers();

    }

    private void loadCustomers() {

        Date date = new Date();
        //Load from Cache
        List<BillUser> customers = BillCache.getCustomers(getActivity());
        setCustomerList(customers);
        Log.d("CustomerList", ".. Ended redering .." + (new Date().getTime() - date.getTime()));

        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        //pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("getAllCustomers", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
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
                    //Add customers to cache
                    BillCache.addCustomers(serviceResponse.getUsers(), getActivity());
                    setCustomerList(serviceResponse.getUsers());
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

    private void setCustomerList(List<BillUser> users) {
        list = new ArrayList<>();
        if (users != null && users.size() > 0) {
            for (BillUser user : users) {
                list.add(new BillCustomer(user));
            /*    count++;
                if (count > 10) {
                    break;
                }*/
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CustomerListAdapter(list, getActivity(), user);
        recyclerView.setHasFixedSize(true);
        /*recyclerView.setItemViewCacheSize(50);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);*/
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

                        filterList.addAll(list);


                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (BillCustomer item : list) {
                            if (item.getUser().getName().toLowerCase().contains(text.toLowerCase()) /*|| comparePhone(item, text)*/) {
                                // Adding Matched items
                                filterList.add(item);
                            } else if (item.getUser().getPhone().toLowerCase().contains(text.toLowerCase())) {
                                filterList.add(item);
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
                            adapter = new CustomerListAdapter(filterList, getActivity(), user);
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


    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
