package com.reso.bill.generic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.EditInvoiceActivity;
import com.reso.bill.R;
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

public class GenericCustomerInvoicesActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_customer_invoices);

        customer = (BillUser) Utility.getIntentObject(BillUser.class, getIntent(), Utility.CUSTOMER_KEY);

        recyclerView = findViewById(R.id.recycler_view_customer_bill_details);

        /*sp = (Spinner) findViewById(R.id.spinner1);
        List<String> list = new ArrayList<String>();
        list.add("2018");
        list.add("2017");
        list.add("2016");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(GenericCustomerInvoicesActivity.this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(dataAdapter);*/

        if (customer == null) {
            finish();
            return;
        }

        //Toolbar toolbar = (Toolbar) findViewById(R.id.tabthree_toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/
        Utility.setActionBar("Bill by Year - " + customer.getName(), getSupportActionBar());
        //customerName = (TextView) findViewById(R.id.txt_customer_invoices_customer_name);
        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(GenericCustomerInvoicesActivity.this, recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {

                AlertDialog.Builder builder = Utility.SingleChoiceWithRadioButton(GenericCustomerInvoicesActivity.this);

                builder.setSingleChoiceItems(Utility.LIST_OPTIONS, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == Utility.LIST_OPT_DELETE) {
                            if (invoices != null && invoices.size() > 0 && position < invoices.size()) {
                                BillInvoice invoice = invoices.get(position);
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                                deleteInvoice(invoice);

                            }
                        }
                    }
                });

                *//*builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });*//*

                alertDialog = builder.create();
                alertDialog.show();

            }
        }));*/
        addInvoice = (Button) findViewById(R.id.fab_add_invoice);
        addInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.nextFragment(GenericCustomerInvoicesActivity.this, FragmentEditInvoice.newInstance(customer, new BillInvoice()));
                if (BillConstants.FRAMEWORK_GENERIC.equals(Utility.getFramework(user))) {
                    //customer.setCurrentInvoice(invoice);
                    //Utility.nextFragment((FragmentActivity) activity, GenericCreateBill.newInstance(customer));
                    startActivity(Utility.nextIntent(GenericCustomerInvoicesActivity.this, GenericCreateBillActivity.class, true));
                } else {

                    //Utility.nextFragment((FragmentActivity) activity, FragmentEditInvoice.newInstance(customer, invoice));
                    startActivity(Utility.nextIntent(GenericCustomerInvoicesActivity.this, EditInvoiceActivity.class, true, customer, Utility.CUSTOMER_KEY));
                }
            }
        });

        user = (BillUser) Utility.readObject(GenericCustomerInvoicesActivity.this, Utility.USER_KEY);

        //Show ADD NEW button only for recurring
        if (!BillConstants.FRAMEWORK_RECURRING.equals(Utility.getFramework(user))) {
            addInvoice.setVisibility(View.GONE);
        }
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
                //filter(newText);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, this);
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
        pDialog = Utility.getProgressDialogue("Loading..", GenericCustomerInvoicesActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("getCustomerInvoices", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(pDialog, GenericCustomerInvoicesActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(GenericCustomerInvoicesActivity.this);
        queue.add(myReq);
    }


    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    invoices = serviceResponse.getInvoices();
                    if (invoices != null && invoices.size() > 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(GenericCustomerInvoicesActivity.this));
                        CustomerInvoiceAdapter adapter = new CustomerInvoiceAdapter(invoices, GenericCustomerInvoicesActivity.this, customer);
                        adapter.setUser(user);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericCustomerInvoicesActivity.this, serviceResponse.getResponse(), "Error");
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
