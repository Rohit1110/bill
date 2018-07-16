package com.reso.bill;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapters.SelectNewsPaperAdapter;
import model.BillItemHolder;
import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 6/29/2018.
 */

public class DatePikerActivity extends Fragment {

    private RecyclerView recyclerView;
    private Button add;
    private BillUser user;
    private List<BillItemHolder> list = new ArrayList<>();
    //private List<BillItem> businessItems;
    private List<BillItem> selectedItems;
    private ProgressDialog pDialog;
    EditText txtfrom,txtto;
    private Date fromDate, toDate;


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
                //filter(newText);
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
        View rootView = inflater.inflate(R.layout.activity_datepicker, container, false);
        Utility.AppBarTitle("DatePicker",getActivity());

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_datepicker_list);
        txtfrom = (EditText)rootView.findViewById(R.id.txt_fromdate);
        txtfrom.setFocusable(false);
        txtfrom.setClickable(true);
        txtto = (EditText)rootView.findViewById(R.id.txt_todate);
        txtto.setFocusable(false);
        txtto.setClickable(true);


        add = (Button) rootView.findViewById(R.id.btn_save_date);
        user = (BillUser) Utility.readObject(getContext(), Utility.USER_KEY);
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        txtfrom.setText(yy+"-"+mm+"-"+(dd+1));
txtfrom.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        final int yyyy=yy;
        final int mon=mm;
        final int day=dd;
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                SimpleDateFormat sdf = new SimpleDateFormat(BillConstants.DATE_FORMAT);
                fromDate = null;
                try {
                    fromDate = sdf.parse(selectedDateString);
                    if (fromDate != null) {
                        txtfrom.setText(new SimpleDateFormat(BillConstants.DATE_FORMAT).format(fromDate));
                        //calculateNoOfDays();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //

            }
        }, yy, mm, dd);
        datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis()+(1000*24*60*60));

        datePicker.show();

    }
});
        txtto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                final int yyyy=yy;
                final int mon=mm;
                final int day=dd;
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                        SimpleDateFormat sdf = new SimpleDateFormat(BillConstants.DATE_FORMAT);
                        fromDate = null;
                        try {
                            fromDate = sdf.parse(selectedDateString);
                            if (fromDate != null) {
                                txtto.setText(new SimpleDateFormat(BillConstants.DATE_FORMAT).format(fromDate));
                                //calculateNoOfDays();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //

                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis()+(1000*24*60*60));

                datePicker.show();



            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (user == null || user.getCurrentBusiness() == null) {
            Utility.createAlert(getContext(), "Please complete your profile first!", "Error");
            return;
        }
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
                                        //txtSelectedItems.setText(txtSelectedItems.getText() + item.getName() + ",");
                                    }
                                }
                            }
                            holder.setItem(item);
                            list.add(holder);
                        }
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    SelectNewsPaperAdapter adapter = new SelectNewsPaperAdapter(list);
                    //adapter.setSelectedItems();
                    recyclerView.setAdapter(adapter);
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(getActivity(), serviceResponse.getResponse(), "Error");
                }


            }

        };
    }
}
