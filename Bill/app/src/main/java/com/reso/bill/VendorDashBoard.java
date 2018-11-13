package com.reso.bill;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.Date;

import util.Utility;

public class VendorDashBoard extends Fragment {
    private LinearLayout layout_total_orders, layout_manage_newspapers, layout_accounting;
    private LinearLayout layout_transactions;
    private LinearLayout layout_settings;
    private TextView totalOrdersDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_vendor_dash_board, container, false);
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Dashboard</font>"));
        Utility.AppBarTitle("Dashboard "  ,getActivity());
        //recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/

        layout_total_orders = (LinearLayout) rootView.findViewById(R.id.layout_totalorder);
        layout_manage_newspapers = (LinearLayout) rootView.findViewById(R.id.layout_managenewspaper);
        layout_accounting = (LinearLayout) rootView.findViewById(R.id.layout_bill_summary);
        layout_transactions = (LinearLayout) rootView.findViewById(R.id.layout_transactions);
        layout_settings = (LinearLayout) rootView.findViewById(R.id.layout_settings);
        totalOrdersDate = (TextView) rootView.findViewById(R.id.txt_vendor_total_orderdate);

        totalOrdersDate.setText(CommonUtils.convertDate(new Date(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR));

        layout_manage_newspapers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewspapers fragment = new AddNewspapers();
                Utility.nextFragment(getActivity(), fragment);
            }
        });

        layout_total_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), DailySummaryFragment.newInstance());
            }
        });

        layout_transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), FragmentTransactions.newInstance());
            }
        });

        layout_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), Settings.newInstance());
            }
        });

        layout_accounting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), BillsSummary.newInstance());
            }
        });

        return rootView;
    }
}
