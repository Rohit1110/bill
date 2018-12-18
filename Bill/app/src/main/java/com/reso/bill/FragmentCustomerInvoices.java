package com.reso.bill;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.reso.bill.components.ClickListener;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;

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
    private List<BillInvoice> invoices;
    private AlertDialog alertDialog;
    private BillUser user;

    public static FragmentCustomerInvoices newInstance(BillUser customer) {
        FragmentCustomerInvoices fragment = new FragmentCustomerInvoices();
        fragment.customer = customer;
        return fragment;
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
                //filter(newText);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        if (customer == null) {
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            return rootView;
        }

        //Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.tabthree_toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/
        getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Bill by Year - " + customer.getName() + "</font>"));
        Utility.AppBarTitle("Bill by Year - " + customer.getName(), getActivity());
        //customerName = (TextView) rootView.findViewById(R.id.txt_customer_invoices_customer_name);
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
                            if (invoices != null && invoices.size() > 0 && position < invoices.size()) {
                                BillInvoice invoice = invoices.get(position);
                                if(alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                                deleteInvoice(invoice);

                            }
                        }
                    }
                });

                /*builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });*/

                alertDialog = builder.create();
                alertDialog.show();

            }
        }));
        addInvoice = (Button) rootView.findViewById(R.id.fab_add_invoice);
        addInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), FragmentEditInvoice.newInstance(customer, new BillInvoice()));
            }
        });

        user = (BillUser) Utility.readObject(getActivity(), Utility.USER_KEY);

        //Show ADD NEW button only for recurring
        if(!BillConstants.FRAMEWORK_RECURRING.equals(Utility.getFramework(user))) {
            addInvoice.setVisibility(View.GONE);
        }

        return rootView;
    }

    private void deleteInvoice(BillInvoice invoice) {
        if (invoice == null || invoice.getId() == null || customer == null) {
            return;
        }
        invoice.setStatus(BillConstants.INVOICE_STATUS_DELETED);
        //}
        BillServiceRequest request = new BillServiceRequest();
        request.setInvoice(invoice);
        request.setUser(customer);
        pDialog = Utility.getProgressDialogue("Deleting..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerInvoice", invoiceDeletionResponse(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> invoiceDeletionResponse() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    loadCustomerInvoices();
                    Utility.createAlert(getContext(), "Invoice deleted successfully!", "Done");
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
                    invoices = serviceResponse.getInvoices();
                    if (invoices != null && invoices.size() > 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        CustomerInvoiceAdapter adapter = new CustomerInvoiceAdapter(invoices, getActivity(), customer);
                        adapter.setUser(user);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
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

