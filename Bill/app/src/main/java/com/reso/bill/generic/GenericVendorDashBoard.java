package com.reso.bill.generic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reso.bill.BillSummaryActivity;
import com.reso.bill.DailySummaryFragment;
import com.reso.bill.ManageNewspapersActivity;
import com.reso.bill.R;
import com.reso.bill.SettingsActivity;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.Date;

import util.Utility;

public class GenericVendorDashBoard extends Fragment {
    //Generic Layouts
    private ConstraintLayout layoutMyProducts;
    private ConstraintLayout layoutTransactions;
    private ConstraintLayout layoutBankInformation;
    //Recurring layouts
    private ConstraintLayout layout_total_orders, layout_manage_newspapers, layout_bill_summary, layout_settings;
    private TextView totalOrdersDate;
    private ConstraintLayout layoutCustomerGroups;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.generic_fragment_vendor_dashboard, container, false);
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Dashboard</font>"));
        Utility.AppBarTitle("Dashboard", getActivity());
        //recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/

        BillUser user = (BillUser) Utility.readObject(getActivity(), Utility.USER_KEY);

        layoutMyProducts = rootView.findViewById(R.id.layout_my_products);
        layoutTransactions = rootView.findViewById(R.id.layout_transactions);
        layoutBankInformation = rootView.findViewById(R.id.layout_bank_info);

        layoutMyProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*GenericMyProducts fragment = new GenericMyProducts();
                Utility.nextFragment(getActivity(), fragment);*/
                startActivity(Utility.nextIntent(getActivity(), GenericMyProductsActivity.class, true));
            }
        });


        layoutTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Utility.nextIntent(getActivity(), GenericTransactionsActivity.class, true));
            }
        });


        layoutBankInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Utility.nextIntent(getActivity(), GenericBankInfoDisplayActivity.class, false));
            }
        });


        //Newspaper specific
        layout_total_orders = rootView.findViewById(R.id.layout_totalorder);
        layout_manage_newspapers = rootView.findViewById(R.id.layout_managenewspaper);
        layout_bill_summary = rootView.findViewById(R.id.layout_bill_summary);
        layout_settings = rootView.findViewById(R.id.layout_settings);
        totalOrdersDate = rootView.findViewById(R.id.txt_vendor_total_orderdate);

        layoutCustomerGroups = rootView.findViewById(R.id.layout_customer_groups);

        totalOrdersDate.setText(CommonUtils.convertDate(new Date(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR));

        layout_manage_newspapers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*AddNewspapers fragment = new AddNewspapers();
                Utility.nextFragment(getActivity(), fragment);*/
                startActivity(Utility.nextIntent(getActivity(), ManageNewspapersActivity.class, true));
            }
        });

        layout_total_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), DailySummaryFragment.newInstance());
            }
        });


        layout_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.nextFragment(getActivity(), Settings.newInstance());
                startActivity(Utility.nextIntent(getActivity(), SettingsActivity.class, true));
            }
        });

        layout_bill_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.nextFragment(getActivity(), BillsSummary.newInstance());
                startActivity(Utility.nextIntent(getActivity(), BillSummaryActivity.class, true));

            }
        });

        layoutCustomerGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Utility.nextIntent(getActivity(), GenericCustomerGroupsActivity.class, true));
            }
        });

        if (BillConstants.FRAMEWORK_GENERIC.equals(Utility.getFramework(user))) {
            layout_total_orders.setVisibility(View.GONE);
            layout_manage_newspapers.setVisibility(View.GONE);
            layout_bill_summary.setVisibility(View.GONE);
            layout_settings.setVisibility(View.GONE);

        } else {
            layoutMyProducts.setVisibility(View.GONE);

        }

        return rootView;
    }
}
