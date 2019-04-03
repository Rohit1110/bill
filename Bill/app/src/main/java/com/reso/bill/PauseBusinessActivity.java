package com.reso.bill;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.bo.domain.BillUserLog;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import adapters.PauseItemAdapter;
import model.BillItemHolder;
import util.ServiceUtil;
import util.Utility;

public class PauseBusinessActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    //private Button pause;
    private BillUser user;
    private List<BillItemHolder> list = new ArrayList<>();
    //private List<BillItem> businessItems;
    private List<BillItem> selectedItems;
    private ProgressDialog pDialog;
    private EditText txtfrom, txtto;
    private Date fromDate, toDate;
    private TextView pausedDays;
    private List<BillItem> parentItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause_business);

        Utility.setActionBar("Pause Business", getSupportActionBar());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_datepicker_list);
        txtfrom = (EditText) findViewById(R.id.txt_from_date);
        txtfrom.setFocusable(false);
        txtfrom.setClickable(true);
        txtto = (EditText) findViewById(R.id.txt_to_date);
        txtto.setFocusable(false);
        txtto.setClickable(true);

        pausedDays = (TextView) findViewById(R.id.txt_pause_delivery_business_days);
        //pause = (Button) findViewById(R.id.btn_pause_business_items);
        user = (BillUser) Utility.readObject(PauseBusinessActivity.this, Utility.USER_KEY);
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        txtfrom.setText(yy + "-" + mm + "-" + (dd + 1));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        //One day ahead of current by default
        fromDate = cal.getTime();
        toDate = cal.getTime();

        txtfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                final int yyyy = yy;
                final int mon = mm;
                final int day = dd;
                DatePickerDialog datePicker = new DatePickerDialog(PauseBusinessActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                        SimpleDateFormat sdf = new SimpleDateFormat(BillConstants.DATE_FORMAT);
                        fromDate = null;
                        try {
                            fromDate = sdf.parse(selectedDateString);
                            if (fromDate != null) {
                                txtfrom.setText(new SimpleDateFormat(BillConstants.DATE_FORMAT).format(fromDate));
                                calculateNoOfDays();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //

                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis() + (1000 * 24 * 60 * 60));

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
                final int yyyy = yy;
                final int mon = mm;
                final int day = dd;
                DatePickerDialog datePicker = new DatePickerDialog(PauseBusinessActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                        SimpleDateFormat sdf = new SimpleDateFormat(BillConstants.DATE_FORMAT);
                        toDate = null;
                        try {
                            toDate = sdf.parse(selectedDateString);
                            if (toDate != null) {
                                txtto.setText(new SimpleDateFormat(BillConstants.DATE_FORMAT).format(fromDate));
                                calculateNoOfDays();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //

                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis() + (1000 * 24 * 60 * 60));

                datePicker.show();


            }
        });

        txtfrom.setText(CommonUtils.convertDate(fromDate));
        txtto.setText(CommonUtils.convertDate(toDate));

        calculateNoOfDays();

        /*pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseItems();
            }
        });*/
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
                pauseItems();
                return true;
        }
        return false;
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

        menu.add(Menu.NONE, Utility.MENU_ITEM_SAVE, Menu.NONE, "Save").setIcon(R.drawable.ic_check_blue_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    private void calculateNoOfDays() {
        if (toDate != null && fromDate != null) {
            if (toDate.getTime() < fromDate.getTime()) {
                //Utility.createAlert(PauseBusinessActivity.this, "Please select valid dates!", "Error");
                return;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(toDate.getTime() - fromDate.getTime());
            pausedDays.setText("Pause delivery for - " + String.valueOf(TimeUnit.DAYS.convert(toDate.getTime() - fromDate.getTime(), TimeUnit.MILLISECONDS) + 1) + " day(s)");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user == null || user.getCurrentBusiness() == null) {
            Utility.createAlert(PauseBusinessActivity.this, "Please complete your profile first!", "Error");
            return;
        }
        loadBusinessItems();
    }


    private void loadBusinessItems() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        pDialog = Utility.getProgressDialogue("Loading..", PauseBusinessActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("loadBusinessItems", getItemsListener(), ServiceUtil.createMyReqErrorListener(pDialog, PauseBusinessActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(PauseBusinessActivity.this);
        queue.add(myReq);
    }

    private void pauseItems() {
        selectedItems = new ArrayList<>();
        for (BillItemHolder holder : list) {
            if (holder.isSelected()) {
                //Add if not already present
                if (findExistingItem(holder.getItem()) == null) {
                    BillItem businessItem = new BillItem();
                    businessItem.setParentItem(holder.getItem());
                    selectedItems.add(businessItem);
                }
            }
        }
        String noOfItems = "all";
        if (selectedItems.size() == 0) {
            selectedItems = parentItems;
        } else {
            noOfItems = "" + selectedItems.size();
        }
        new AlertDialog.Builder(PauseBusinessActivity.this).
                setTitle("Warning").
                setMessage("Are you sure you want to pause  " + noOfItems + " of your items?").
                setIcon(android.R.drawable.ic_dialog_alert).
                setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        pauseConfirm();
                    }
                }).setNegativeButton(android.R.string.no, null).show();


    }

    private void pauseConfirm() {
        if (selectedItems == null || selectedItems.size() == 0) {
            Utility.createAlert(PauseBusinessActivity.this, "No items to pause. Please add items to your business first.", "Error");
            return;
        }
        if (fromDate == null || toDate == null) {
            Utility.createAlert(PauseBusinessActivity.this, "Please select From and To fromDate!", "Error");
            return;
        }
        if (toDate.getTime() < fromDate.getTime()) {
            Utility.createAlert(PauseBusinessActivity.this, "To date cannot be less than From date!", "Error");
            return;
        }

        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        BillUserLog log = new BillUserLog();
        log.setFromDate(fromDate);
        log.setToDate(toDate);
        for (BillItem item : selectedItems) {
            System.out.println("B Item == " + item.getId());
            item.setQuantity(BigDecimal.ZERO);
            item.setChangeLog(log);
        }
        request.setItems(selectedItems);

        pDialog = Utility.getProgressDialogue("Saving..", PauseBusinessActivity.this);
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerItemTemp", pauseItemsListener(), ServiceUtil.createMyReqErrorListener(pDialog, PauseBusinessActivity.this), request);
        RequestQueue queue = Volley.newRequestQueue(PauseBusinessActivity.this);
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

    private Response.Listener<String> pauseItemsListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                //pDialog.dismiss();
                Utility.dismiss(pDialog);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlertWithActivityFinish(PauseBusinessActivity.this, "Updated successfully!", "Done", null, null, null, null);
                } else {
                    Utility.createAlert(PauseBusinessActivity.this, "Error saving data!", "Error");
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
                            holder.setItem(item);
                            list.add(holder);
                            parentItems.add(item);
                        }
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(PauseBusinessActivity.this));
                    PauseItemAdapter adapter = new PauseItemAdapter(list);
                    //adapter.setSelectedItems();
                    recyclerView.setAdapter(adapter);
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(PauseBusinessActivity.this, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }
}
