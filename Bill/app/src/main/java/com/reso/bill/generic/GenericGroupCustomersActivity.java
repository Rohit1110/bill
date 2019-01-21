package com.reso.bill.generic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.reso.bill.components.ItemMoveCallBack;
import com.rns.web.billapp.service.bo.domain.BillCustomerGroup;
import com.rns.web.billapp.service.bo.domain.BillSubscription;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.List;

import adapters.generic.GroupCustomersAdapter;
import model.BillCustomer;
import util.ServiceUtil;
import util.Utility;

public class GenericGroupCustomersActivity extends AppCompatActivity {

    private Button addProduct;
    private BillUser selectedUser;
    private List<BillUser> groupCustomers;
    private AutoCompleteTextView customerName;
    private BillUser user;
    private BillCustomerGroup group;
    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private List<BillUser> allCustomers;
    private GroupCustomersAdapter groupCustomerAdapter;
    private List<BillUser> filterList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_group_customers);


        user = (BillUser) Utility.readObject(GenericGroupCustomersActivity.this, Utility.USER_KEY);

        group = (BillCustomerGroup) Utility.getIntentObject(BillCustomerGroup.class, getIntent(), Utility.GROUP_KEY);

        Utility.setActionBar("Group customers", getSupportActionBar());

        addProduct = (Button) findViewById(R.id.gn_btn_add_customer_to_group);


        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (selectedUser != null) {
                        addCustomer();
                    } else {
                        Utility.createAlert(GenericGroupCustomersActivity.this, "This customer does not exist!", "Error");
                    }
                } catch (Exception e) {

                } finally {
                    selectedUser = null;
                }

            }
        });

        customerName = (AutoCompleteTextView) findViewById(R.id.et_group_customer_name);

        customerName.setThreshold(2);

        customerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (allCustomers != null && allCustomers.size() > 0) {
                        String selected = adapterView.getItemAtPosition(i).toString();
                        selectedUser = (BillUser) Utility.findInStringList(allCustomers, selected);
                        //BillUser selectedCustomer = items.get(i);

                    }
                } catch (Exception e) {
                    System.out.println("Error in setting customer .." + e);
                }

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_group_customers);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(Menu.NONE, Utility.MENU_ITEM_SAVE, Menu.NONE, "Save").setIcon(R.drawable.ic_check_blue_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Utility.backDefault(item, this);
    }


    private boolean isAlreadyAdded(BillUser selected) {
        for (BillUser customer : groupCustomers) {
            if (customer.getId() != null && selected.getId() != null && customer.getId() == selected.getId()) {
                Utility.createAlert(this, "Customer is already added to this group", "Error");
                return true;
            }
        }
        return false;
    }


    private void addCustomer() {
        if (selectedUser == null || selectedUser.getId() == null) {
            Utility.createAlert(this, "Customer does not exist!", "Error");
            return;
        }
        if (isAlreadyAdded(selectedUser)) {
            return;
        }
        BillServiceRequest request = new BillServiceRequest();
        BillUser customer = new BillUser();
        BillSubscription subscription = new BillSubscription();
        subscription.setId(selectedUser.getId());
        subscription.setItems(selectedUser.getCurrentSubscription().getItems());
        customer.setName(selectedUser.getName());
        customer.setPhone(selectedUser.getPhone());
        customer.setCurrentSubscription(subscription);
        request.setUser(customer);
        request.setCustomerGroup(group);
        request.setBusiness(user.getCurrentBusiness());
        pDialog = Utility.getProgressDialogue("Saving", this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomer", customerSavedListener(customer), ServiceUtil.createMyReqErrorListener(pDialog, this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
    }

    private Response.Listener<String> customerSavedListener(final BillUser customer) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (groupCustomers == null) {
                        groupCustomers = new ArrayList<>();
                    }
                    groupCustomers.add(customer);
                    groupCustomerAdapter.updateList(groupCustomers);
                    allCustomers.remove(customer);
                    setAllCustomers(allCustomers);
                    customerName.setText("");
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(GenericGroupCustomersActivity.this, serviceResponse.getResponse(), "Error");
                }


            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();

        loadAllCustomers();
        loadGroupCustomers(); //For Suggestions}

    }

    private void loadAllCustomers() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        //pDialog = Utility.getProgressDialogue("Loading..", GenericUpdateInvoiceItemsActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("getAllCustomers", customersLoaded("all"), ServiceUtil.createMyReqErrorListener(pDialog, this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
    }

    private void loadGroupCustomers() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        request.setCustomerGroup(group);
        pDialog = Utility.getProgressDialogue("Loading..", this);
        StringRequest myReq = ServiceUtil.getStringRequest("getAllCustomers", customersLoaded("group"), ServiceUtil.createMyReqErrorListener(pDialog, this), request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
    }

    private Response.Listener<String> customersLoaded(final String type) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (pDialog != null && "group".equals(type)) {
                    pDialog.dismiss();
                }
                System.out.println("## response:" + response);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if ("group".equals(type)) {


                        recyclerView.setLayoutManager(new LinearLayoutManager(GenericGroupCustomersActivity.this));
                        groupCustomerAdapter = new GroupCustomersAdapter(new ArrayList<BillCustomer>(), GenericGroupCustomersActivity.this, user);
                        groupCustomerAdapter.setGroup(group);
                        groupCustomers = serviceResponse.getUsers();
                        groupCustomerAdapter.setUsers(groupCustomers);

                        //To add drag n drop functionality
                        ItemTouchHelper.Callback callback = new ItemMoveCallBack(groupCustomerAdapter);
                        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                        touchHelper.attachToRecyclerView(recyclerView);

                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(groupCustomerAdapter);
                    } else {
                        setAllCustomers(serviceResponse.getUsers());
                    }
                    filterAllCustomers();
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    //customerName.setada
                }
            }

        };
    }

    private void setAllCustomers(List<BillUser> users) {
        allCustomers = users;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GenericGroupCustomersActivity.this, android.R.layout.select_dialog_item, Utility.convertToStringArrayList(allCustomers));
        customerName.setAdapter(adapter);
    }

    private void filterAllCustomers() {
        if (groupCustomers != null && allCustomers != null && groupCustomers.size() > 0 && allCustomers.size() > 0) {
            List<BillUser> filteredAll = new ArrayList<>();
            for (BillUser customer : allCustomers) {
                boolean added = false;
                for (BillUser groupCustomer : groupCustomers) {
                    if (customer.getId() == groupCustomer.getId()) {
                        added = true;
                        break; //Already added
                    }
                }
                if (!added) {
                    filteredAll.add(customer);
                }
            }
            setAllCustomers(filteredAll);
        }
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

                        filterList.addAll(groupCustomers);


                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (BillUser customer : groupCustomers) {
                            if (customer.getName().toLowerCase().contains(text.toLowerCase()) /*|| comparePhone(customer, text)*/) {
                                // Adding Matched items
                                filterList.add(customer);
                            } else if (customer.getPhone().toLowerCase().contains(text.toLowerCase())) {
                                filterList.add(customer);
                            }

                        }
                    }

                    // Set on UI Thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Notify the List that the DataSet has changed...
                            groupCustomerAdapter.updateList(filterList);
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
