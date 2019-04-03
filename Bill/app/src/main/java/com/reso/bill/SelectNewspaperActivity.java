package com.reso.bill;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapters.SelectNewsPaperAdapter;
import model.BillItemHolder;
import util.ServiceUtil;
import util.Utility;

public class SelectNewspaperActivity extends AppCompatActivity {

    private List<BillItemHolder> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private BillUser user;
    private List<BillItem> selectedItems;
    private TextView txtSelectedItems;
    //private Button save;
    private List<BillItemHolder> filterList = new ArrayList<>();
    private CheckBox selectAll;
    private SelectNewsPaperAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_newspaper);

        Utility.setActionBar("Select newspapers", getSupportActionBar());
        BillBusiness currBusiness = (BillBusiness) Utility.getIntentObject(BillBusiness.class, getIntent(), Utility.BUSINESS_KEY);
        selectedItems = currBusiness.getItems();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_select_newspaper);
        //save = (Button) findViewById(R.id.btn_save_business_items);
        /*save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItems();
            }
        });*/
        txtSelectedItems = (TextView) findViewById(R.id.txt_selected_items);
        txtSelectedItems.setText("");
        txtSelectedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(SelectNewspaperActivity.this);
                View promptsView = li.inflate(R.layout.dialog_layout, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectNewspaperActivity.this);
                alertDialogBuilder.setView(promptsView);

                final TextView userInput = (TextView) promptsView.findViewById(R.id.textView1);
                ListView listView = (ListView) promptsView.findViewById(R.id.news_papers);
                //userInput.setText(txtSelectedItems.getText().toString());
                List<String> elephantList = Arrays.asList(txtSelectedItems.getText().toString().split(","));

                ArrayAdapter<String> mHistory = new ArrayAdapter<String>(SelectNewspaperActivity.this, android.R.layout.simple_list_item_1, elephantList);
                listView.setAdapter(mHistory);
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text

                    }
                });
                        /*.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });*/

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        selectAll = (CheckBox) findViewById(R.id.chk_select_all_newspapers);
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (adapter == null || adapter.getList() == null || adapter.getList().size() == 0) {
                    return;
                }
                if (b) {
                    for (BillItemHolder holder : adapter.getList()) {
                        selectItem(holder.getItem(), holder);
                    }
                } else {
                    for (BillItemHolder holder : adapter.getList()) {
                        deselectItem(holder.getItem(), holder);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //Back button click
                finish();
                return true;
            case Utility.MENU_ITEM_SAVE:
//                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();;
                saveItems();
                return true;
        }
        return false;

        //return Utility.backDefault(item, this);
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

        menu.add(Menu.NONE, Utility.MENU_ITEM_SAVE, Menu.NONE, "Save").setIcon(R.drawable.ic_check_blue_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        user = (BillUser) Utility.readObject(SelectNewspaperActivity.this, Utility.USER_KEY);

        loadParentItems();

    }

    private void loadParentItems() {
        BillServiceRequest request = new BillServiceRequest();
        if (user.getCurrentBusiness().getBusinessSector() == null) {
            request.setSector(ServiceUtil.NEWSPAPER_SECTOR);
        } else {
            request.setSector(user.getCurrentBusiness().getBusinessSector());
        }
        if (user != null && user.getCurrentBusiness() != null) {
            BillBusiness business = new BillBusiness();
            business.setId(user.getCurrentBusiness().getId());
            request.setBusiness(business);
        }
        pDialog = Utility.getProgressDialogue("Loading..", SelectNewspaperActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("loadSectorItems", getItemsListener(), ServiceUtil.createMyReqErrorListener(pDialog, SelectNewspaperActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(SelectNewspaperActivity.this);
        queue.add(myReq);
    }

    private void saveItems() {
        if (selectedItems == null) {
            selectedItems = new ArrayList<>();
        }
        for (BillItemHolder holder : list) {
            if (holder.isSelected()) {
                //Add if not already present
                if (findExistingItem(holder.getItem()) == null) {
                    BillItem businessItem = new BillItem();
                    businessItem.setParentItem(holder.getItem());
                    selectedItems.add(businessItem);
                }
            } else {
                //Deactivate if existing item
                BillItem existingItem = findExistingItem(holder.getItem());
                if (existingItem != null) {
                    existingItem.setStatus("D");//Deleted
                }
            }
        }
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        request.setItems(selectedItems);
        pDialog = Utility.getProgressDialogue("Saving..", SelectNewspaperActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateBusinessItem", saveItemsListener(), ServiceUtil.createMyReqErrorListener(pDialog, SelectNewspaperActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(SelectNewspaperActivity.this);
        queue.add(myReq);
    }

    private BillItem findExistingItem(BillItem parent) {
        if (selectedItems != null && selectedItems.size() > 0) {
            for (BillItem selectedItem : selectedItems) {
                if (parent.getId() == selectedItem.getParentItem().getId()) {
                    return selectedItem;
                }
            }
        }
        return null;
    }

    private Response.Listener<String> saveItemsListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    //Utility.nextFragment(SelectNewspaperActivity.this, new AddNewspapers());
                    finish();
                } else {
                    Utility.createAlert(SelectNewspaperActivity.this, "Error saving data!", "Error");
                }
            }
        };
    }

    private Response.Listener<String> getItemsListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (serviceResponse.getItems() != null) {
                        for (BillItem item : serviceResponse.getItems()) {
                            BillItemHolder holder = new BillItemHolder();
                            if (selectedItems != null && selectedItems.size() > 0) {
                                for (BillItem selectedItem : selectedItems) {
                                    if (item.getId() == selectedItem.getParentItem().getId()) {
                                        selectItem(item, holder);
                                    }
                                }
                            }
                            holder.setItem(item);
                            list.add(holder);
                        }
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(SelectNewspaperActivity.this));
                    adapter = new SelectNewsPaperAdapter(list);
                    adapter.setSelectedItems(txtSelectedItems);
                    recyclerView.setAdapter(adapter);
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(SelectNewspaperActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

    private void selectItem(BillItem item, BillItemHolder holder) {
        if (holder == null || item == null) {
            return;
        }
        holder.setSelected(true);
        txtSelectedItems.setText(txtSelectedItems.getText() + item.getName() + ",");
    }

    private void deselectItem(BillItem item, BillItemHolder holder) {
        if (holder == null || item == null) {
            return;
        }
        holder.setSelected(false);
        txtSelectedItems.setText(txtSelectedItems.getText().toString().replace(item.getName() + ",", ""));
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
                        for (BillItemHolder item : list) {
                            System.out.println("Get Name --->>> " + item.getItem().getName());
                            if (item.getItem().getName().toLowerCase().contains(text.toLowerCase()) /*|| comparePhone(item, text)*/) {
                                // Adding Matched items
                                filterList.add(item);
                            }

                        }
                    }

                    // Set on UI Thread
                    (SelectNewspaperActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Notify the List that the DataSet has changed...
                           /* adapter = new ContactListAdapter(SearchAppointmentActivity.this, filterList);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(SearchAppointmentActivity.this, 1);
                            recyclerView_contact.setLayoutManager(mLayoutManager);
                            recyclerView_contact.setAdapter(adapter);*/
                           /* recyclerView.setLayoutManager(new LinearLayoutManager(SelectNewspaperActivity.this));
                            adapter = new DeliveriesAdapter(filterList, SelectNewspaperActivity.this, user);
                            recyclerView.setAdapter(adapter);*/
                            recyclerView.setLayoutManager(new LinearLayoutManager(SelectNewspaperActivity.this));
                            SelectNewsPaperAdapter adapter = new SelectNewsPaperAdapter(filterList);
                            adapter.setSelectedItems(txtSelectedItems);
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
}
