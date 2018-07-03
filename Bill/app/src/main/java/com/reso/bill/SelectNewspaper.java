package com.reso.bill;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.List;

import adapters.DeliveriesAdapter;
import adapters.SelectNewsPaperAdapter;
import model.BillCustomer;
import model.BillItemHolder;
import util.ServiceUtil;
import util.Utility;

public class SelectNewspaper extends Fragment {
    private List<BillItemHolder> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressDialog pDialog;
    private BillUser user;
    private List<BillItem> selectedItems;
    private TextView txtSelectedItems;
    private Button save;
    private List<BillItemHolder> filterList= new ArrayList<>();

    public void setSelectedItems(List<BillItem> selectedItems) {
        this.selectedItems = selectedItems;
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
        SearchView searchView = new SearchView(((Dashboard) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        ((EditText)  searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setTextColor(getResources().getColor(R.color.md_black_1000));
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
                                      }
        );

        //searchView.setMenuItem(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_select_newspaper, container, false);
        //getActivity().setTitle(Html.fromHtml("<font color='#000000'>Home</font>"));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_select_newspaper);
        save = (Button) rootView.findViewById(R.id.btn_save_business_items);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItems();
            }
        });
        txtSelectedItems = (TextView) rootView.findViewById(R.id.txt_selected_items);
        txtSelectedItems.setText("");
        txtSelectedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.dialog_layout, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());
                alertDialogBuilder.setView(promptsView);
                final TextView userInput = (TextView) promptsView
                        .findViewById(R.id.textView1);
                userInput.setText(txtSelectedItems.getText().toString());
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
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
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        user = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);

        loadParentItems();

    }

    private void loadParentItems() {
        BillServiceRequest request = new BillServiceRequest();
        request.setSector(ServiceUtil.NEWSPAPER_SECTOR);
        pDialog = Utility.getProgressDialogue("Loading..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("loadSectorItems", getItemsListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private void saveItems() {
        if(selectedItems == null) {
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
        pDialog = Utility.getProgressDialogue("Saving..", getActivity());
        StringRequest myReq = ServiceUtil.getStringRequest("updateBusinessItem", saveItemsListener(), ServiceUtil.createMyReqErrorListener(pDialog, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                pDialog.dismiss();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.nextFragment(getActivity(), new AddNewspapers());
                } else {
                    Utility.createAlert(getContext(), "Error saving data!","Error");
                }
            }
        };
    }

    private Response.Listener<String> getItemsListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (serviceResponse.getItems() != null) {
                        for (BillItem item : serviceResponse.getItems()) {
                            BillItemHolder holder = new BillItemHolder();
                            if (selectedItems != null && selectedItems.size() > 0) {
                                for (BillItem selectedItem : selectedItems) {
                                    if (item.getId() == selectedItem.getParentItem().getId()) {
                                        holder.setSelected(true);
                                        txtSelectedItems.setText(txtSelectedItems.getText() + item.getName() + ",");
                                    }
                                }
                            }
                            holder.setItem(item);
                            list.add(holder);
                        }
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    SelectNewsPaperAdapter adapter = new SelectNewsPaperAdapter(list);
                    adapter.setSelectedItems(txtSelectedItems);
                    recyclerView.setAdapter(adapter);
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
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

                        filterList.addAll(list);


                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (BillItemHolder item : list) {
                            System.out.println("Get Name --->>> "+ item.getItem().getName());
                            if (item.getItem().getName().toLowerCase().contains(text.toLowerCase()) /*|| comparePhone(item, text)*/) {
                                // Adding Matched items
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
                           /* recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new DeliveriesAdapter(filterList, getActivity(), user);
                            recyclerView.setAdapter(adapter);*/
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
