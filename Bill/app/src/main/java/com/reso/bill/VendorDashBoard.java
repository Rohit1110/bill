package com.reso.bill;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import util.Utility;

public class VendorDashBoard extends Fragment {
    LinearLayout layout_total_orders, layout_manage_newspapers, layout_accounting;

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
        layout_accounting = (LinearLayout) rootView.findViewById(R.id.layout_accounts);

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

        return rootView;
    }
}
