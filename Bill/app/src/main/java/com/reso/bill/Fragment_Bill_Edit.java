package com.reso.bill;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.BillDetailsAdapter;
import adapters.BillDetailsEditAdapter;
import model.Bill_details;
import util.Utility;

/**
 * Created by Rohit on 5/28/2018.
 */

public class Fragment_Bill_Edit extends Fragment {
    RecyclerView recyclerView;
    //Spinner sp;
    String[] s = { "India ", "Arica", "India ", "Arica", "India ", "Arica",
            "India ", "Arica", "India ", "Arica" };
    Button btnpay;
    private List<Bill_details> list = new ArrayList<>();
    private Spinner monthspinner;

    public static Fragment_Bill_Edit newInstance(){
        Fragment_Bill_Edit fragment_bill_edit = new Fragment_Bill_Edit();
        return fragment_bill_edit;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mv_bill_details_edit, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#000000'>Bill Details</font>"));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_bill_two);
        btnpay=(Button)rootView.findViewById(R.id.btn_pay);
       /* final ArrayAdapter<String> adp = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, s);
        final Spinner sp = new Spinner(getActivity());
        sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sp.setAdapter(adp);*/
        monthspinner= (Spinner)rootView.findViewById(R.id.spn_months);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.months_arrays));

        monthspinner.setAdapter(adapter);
        btnpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(sp);
                builder.create().show();*/
            }
        });
        //sp=(Spinner)rootView.findViewById(R.id.spinner_status);

        //sp.setAdapter(adapter);
        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        //Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title")
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/

        return rootView;
    }

    private void showDialog() {
        SingleChoiceWithRadioButton();

    }
    AlertDialog alert;
    private void SingleChoiceWithRadioButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pay Status");
        View mView = getLayoutInflater().inflate(R.layout.alert_spinner,null);
        Spinner spinner = (Spinner)mView.findViewById(R.id.alert_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.paymode));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        builder.setView(mView);


       /* builder.setSingleChoiceItems(selectFruit, selectedElement, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedElement = which;
                //Toast.makeText(AppointmentsActivity.this, selectFruit[which]+":"+ which + " Selected", Toast.LENGTH_LONG).show();
                if (selectFruit[which] == "Todays Appointments") {
                    from = "today";
                    showcacel = false;
                    prepareAppointmentsList(Utility.formatDate(new Date(), Utility.DATE_FORMAT_USED));
                } else if (selectFruit[which] == "Show All Appointments") {
                    showcacel = false;
                    from = "all";
                    prepareAppointmentsList(null);

                } else if (selectFruit[which] == "Cancelled Appointments") {
                    showcacel = true;
                    from = "cancel";
                    prepareAppointmentsList(null);

                }
                //  dialog.dismiss();
            }
        });*/

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert = builder.create();
        alert.show();
    }



    @Override
    public void onResume() {
        super.onResume();

        Bill_details bill_details = new Bill_details();
        for (int i = 0; i <5; i++) {
            System.out.println(i);

            bill_details.setDate("Sakal");

            list.add(bill_details);

        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Collections.sort(items);

        recyclerView.setAdapter(new BillDetailsEditAdapter(list));
    }
}
