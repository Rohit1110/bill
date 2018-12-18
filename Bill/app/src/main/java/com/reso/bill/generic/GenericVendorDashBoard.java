package com.reso.bill.generic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.reso.bill.FragmentTransactions;
import com.reso.bill.R;

import util.Utility;

public class GenericVendorDashBoard extends Fragment {
    private LinearLayout layoutMyProducts;
    private LinearLayout layoutTransactions;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.generic_fragment_vendor_dashboard, container, false);
        //getActivity().setTitle(Html.fromHtml("<font color='#343F4B' size = 24 >Dashboard</font>"));
        Utility.AppBarTitle("Dashboard ", getActivity());
        //recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
       /* toolbar.setTitle("Title");
        toolbar.setNavigationIcon(R.mipmap.backarrow);*/

        layoutMyProducts = (LinearLayout) rootView.findViewById(R.id.layout_my_products);
        layoutTransactions = (LinearLayout) rootView.findViewById(R.id.layout_transactions);

        layoutMyProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*GenericMyProducts fragment = new GenericMyProducts();
                Utility.nextFragment(getActivity(), fragment);*/
                startActivity(Utility.nextIntent(getActivity(), GenericMyProductsActivity.class));
            }
        });


        layoutTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment(getActivity(), FragmentTransactions.newInstance());
            }
        });


        return rootView;
    }
}
