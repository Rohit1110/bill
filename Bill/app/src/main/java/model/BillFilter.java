package model;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillCustomerGroup;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.bo.domain.BillUserLog;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Admin on 22/01/2019.
 */

public class BillFilter {

    private BillCustomerGroup group;
    private List<BillCustomerGroup> groupsList;
    private Activity activity;
    private BillUser user;
    private Spinner groups;
    private Dialog dialog;
    private Date fromDate;
    private Date toDate;
    private Dialog dateDialog;

    public BillFilter(Activity activity, BillUser user) {
        this.activity = activity;
        this.user = user;
        loadCustomerGroups();
    }

    public BillFilter(Activity activity) {
        this.activity = activity;
    }


    public Dialog getDateDialog() {
        return dateDialog;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public BillCustomerGroup getGroup() {
        return group;
    }

    public void setGroup(BillCustomerGroup group) {
        this.group = group;
    }

    public void showFilterDialog() {
        if (dialog != null) {
            setGroupSpinner();
            dialog.show();
            return;
        }
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.generic_dialog_filter);
        dialog.setTitle("Filter");

        groups = dialog.findViewById(R.id.sp_customer_group_filter);

        setGroupSpinner();

        // set the custom dialog components - text, image and button
        Button save = (Button) dialog.findViewById(R.id.btn_dialog_save_filter);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                group = findGroup();
                dialog.dismiss();
            }
        });

        Button clear = (Button) dialog.findViewById(R.id.btn_dialog_clear_filter);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                group = null;
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private BillCustomerGroup findGroup() {
        if (groupsList != null && groupsList.size() > 0) {
            for (BillCustomerGroup grp : groupsList) {
                if (grp.getGroupName().equals(groups.getSelectedItem().toString())) {
                    return grp;
                }
            }
        }
        return null;
    }

    private void loadCustomerGroups() {
        BillServiceRequest request = new BillServiceRequest();
        request.setBusiness(user.getCurrentBusiness());
        //pDialog = Utility.getProgressDialogue("Loading ..", this);
        StringRequest myReq = ServiceUtil.getStringRequest("getCustomerGroups", loadGroupsResponse(), ServiceUtil.createMyReqErrorListener(null, activity), request);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(myReq);
    }

    private Response.Listener<String> loadGroupsResponse() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    groupsList = serviceResponse.getGroups();
                    setGroupSpinner();
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    //Utility.createAlert(GenericCustomerInfoActivity.this, serviceResponse.getResponse(), "Error");
                }

            }

        };
    }

    private void setGroupSpinner() {
        if (groupsList != null && groupsList.size() > 0 && groups != null) {
            List<String> customerGroupsList = new ArrayList<>();
            customerGroupsList.add("Show all groups");
            for (BillCustomerGroup grp : groupsList) {
                customerGroupsList.add(grp.getGroupName());
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, customerGroupsList); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            groups.setAdapter(spinnerArrayAdapter);
            if (group != null && group.getGroupName() != null) {
                int position = customerGroupsList.indexOf(group.getGroupName());
                if (position > 0) {
                    groups.setSelection(position);
                }
            }
        }
    }

    public int getFilterIcon() {
        if (group == null) {
            return R.drawable.ic_action_filter_list_disabled;
        }
        return R.drawable.ic_action_filter_list;
    }


    public BillUserLog getUserLogFromSpinner(Spinner durations) {
        BillUserLog log = new BillUserLog();
        if ("Today".equalsIgnoreCase(durations.getSelectedItem().toString())) {
            log.setFromDate(CommonUtils.startDate(new Date()));
            log.setToDate(CommonUtils.startDate(new Date()));
        } else if ("This week".equalsIgnoreCase(durations.getSelectedItem().toString())) {
            log.setFromDate(CommonUtils.getWeekFirstDate());
            log.setToDate(CommonUtils.getWeekLastDate());
        } else {
            int currentMonth = CommonUtils.getCalendarValue(new Date(), Calendar.MONTH) + 1;
            Integer currentYear = CommonUtils.getCalendarValue(new Date(), Calendar.YEAR);
            if ("This month".equalsIgnoreCase(durations.getSelectedItem().toString())) {
                log.setFromDate(CommonUtils.getMonthFirstDate(currentMonth, currentYear));
                log.setToDate(CommonUtils.getMonthLastDate(currentMonth, currentYear));
            } else if ("Last 6 months".equalsIgnoreCase(durations.getSelectedItem().toString())) {
                Date oldDate = CommonUtils.add(-6, new Date(), Calendar.MONTH);
                log.setFromDate(CommonUtils.getMonthFirstDate(CommonUtils.getCalendarValue(oldDate, Calendar.MONTH), CommonUtils.getCalendarValue(oldDate, Calendar.YEAR)));
                log.setToDate(CommonUtils.getMonthLastDate(currentMonth, currentYear));
            } else if ("Custom".equalsIgnoreCase(durations.getSelectedItem().toString())) {
                log.setFromDate(fromDate);
                log.setToDate(toDate);
            }
        }
        return log;
    }

    public void showDateFilter() {

        dateDialog = new Dialog(activity);
        dateDialog.setContentView(R.layout.generic_dialog_date_filter);
        dateDialog.setTitle("Custom date range");

        // set the custom dateDialog components - text, image and button
        Button save = (Button) dateDialog.findViewById(R.id.btn_dialog_save_date_filter);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateDialog.dismiss();
            }
        });

        Button clear = (Button) dateDialog.findViewById(R.id.btn_dialog_clear_date_filter);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateDialog.dismiss();
            }
        });

        final Calendar cal = Calendar.getInstance();
        fromDate = cal.getTime();
        toDate = cal.getTime();

        final EditText txtfrom = dateDialog.findViewById(R.id.txt_from_date_filter);
        final EditText txtto = dateDialog.findViewById(R.id.txt_to_date_filter);

        txtfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = cal;
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                final int yyyy = yy;
                final int mon = mm;
                final int day = dd;
                DatePickerDialog datePicker = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                        SimpleDateFormat sdf = new SimpleDateFormat(BillConstants.DATE_FORMAT);
                        fromDate = null;
                        try {
                            fromDate = sdf.parse(selectedDateString);
                            if (fromDate != null) {
                                txtfrom.setText(new SimpleDateFormat(BillConstants.DATE_FORMAT).format(fromDate));

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //

                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis() + (1000 * 24 * 60 * 60));

                datePicker.show();

            }
        });
        txtto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar calendar = cal;
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                final int yyyy = yy;
                final int mon = mm;
                final int day = dd;
                DatePickerDialog datePicker = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                        SimpleDateFormat sdf = new SimpleDateFormat(BillConstants.DATE_FORMAT);
                        toDate = null;
                        try {
                            toDate = sdf.parse(selectedDateString);
                            if (toDate != null) {
                                txtto.setText(new SimpleDateFormat(BillConstants.DATE_FORMAT).format(fromDate));

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //

                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis() + (1000 * 24 * 60 * 60));

                datePicker.show();


            }
        });

        txtfrom.setText(CommonUtils.convertDate(fromDate));
        txtto.setText(CommonUtils.convertDate(toDate));


        dateDialog.show();
    }
}
