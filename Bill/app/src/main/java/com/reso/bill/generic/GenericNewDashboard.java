package com.reso.bill.generic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.reso.bill.BillSummaryActivity;
import com.reso.bill.CustomerList;
import com.reso.bill.DailySummaryActivity;
import com.reso.bill.DistributorsActivity;
import com.reso.bill.FragmentInvoiceSummary;
import com.reso.bill.HomeFragment;
import com.reso.bill.R;
import com.reso.bill.SettingsActivity;
import com.rns.web.billapp.service.bo.domain.BillUser;

import util.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GenericNewDashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenericNewDashboard extends Fragment {

    private BillUser user;
    private static Fragment fragment = null;


    public GenericNewDashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameter.
     */
    // TODO: Rename and change types and number of parameters
    public static GenericNewDashboard newInstance(BillUser user) {
        GenericNewDashboard fragment = new GenericNewDashboard();
        fragment.user = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utility.AppBarTitle("Dashboard", getActivity());

        View rootView = inflater.inflate(R.layout.fragment_generic_new_dashboard, container, false);

        CardView createNewInvoiceCardView,pendingInvoicesCardView, manageCustomersCardView, allTransactionsCardView;
        LinearLayout viewAllInvoicesLinearLayout, todaysDeliveriesLinearLayout, totalNewspaperOrdersLinearLayout, purchasesWithDistributorsLinearLayout, summaryOfInvoicesLinearLayout, paymentReportLayout, bankInformationLinearLayout, manageCustomerGroupsLinearLayout, settingsLinearLayout;

        //Create New Invoice Card View interaction
        createNewInvoiceCardView = rootView.findViewById(R.id.createNewInvoiceCardView);
        createNewInvoiceCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(getActivity(), GenericCreateEditInvoiceActivity.class, true));
            }
        });

        //Pending Invoices Card View interaction
        pendingInvoicesCardView = rootView.findViewById(R.id.pendingInvoicesCardView);
        pendingInvoicesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = FragmentInvoiceSummary.newInstance(user);
                Utility.nextFragment(getActivity(), fragment);
            }
        });

        //Manage Customers Card View interaction
        manageCustomersCardView = rootView.findViewById(R.id.manageCustomersCardView);
        manageCustomersCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new CustomerList();
                Utility.nextFragment(getActivity(), fragment);
            }
        });

        //All Transactions Card View interaction
        allTransactionsCardView = rootView.findViewById(R.id.allTransactionsCardView);
        allTransactionsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(getActivity(), GenericTransactionsActivity.class, true));
            }
        });

        //View All Invoices view interaction
        viewAllInvoicesLinearLayout = rootView.findViewById(R.id.viewAllInvoicesLinearLayout);
        viewAllInvoicesLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = GenericInvoices.newInstance();
                Utility.nextFragment(getActivity(), fragment);
            }
        });

        //Today's Deliveries view interaction
        todaysDeliveriesLinearLayout = rootView.findViewById(R.id.todaysDeliveriesLinearLayout);
        todaysDeliveriesLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = HomeFragment.newInstance(user);
                Utility.nextFragment(getActivity(), fragment);
            }
        });

        //Today's Newspaper Total Orders view interaction
        totalNewspaperOrdersLinearLayout = rootView.findViewById(R.id.totalNewspaperOrdersLinearLayout);
        totalNewspaperOrdersLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(getActivity(), DailySummaryActivity.class, true));
            }
        });

        //Your Purchases with Distributors view interaction
        purchasesWithDistributorsLinearLayout = rootView.findViewById(R.id.purchasesWithDistributorsLinearLayout);
        purchasesWithDistributorsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(getActivity(), DistributorsActivity.class, true));
            }
        });

        //Summary of Your Invoices view interaction
        summaryOfInvoicesLinearLayout = rootView.findViewById(R.id.summaryOfInvoicesLinearLayout);
        summaryOfInvoicesLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(getActivity(), BillSummaryActivity.class, true));
            }
        });

        //Invoice Payment Report view interaction
        paymentReportLayout = rootView.findViewById(R.id.paymentReportLayout);
        paymentReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(getActivity(), GenericQuickReportActivity.class, true));
            }
        });

        //Bank Information view interaction
        bankInformationLinearLayout = rootView.findViewById(R.id.bankInformationLinearLayout);
        bankInformationLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(getActivity(), GenericBankInfoDisplayActivity.class, false));
            }
        });

        //Manage Groups of Customers view interaction
        manageCustomerGroupsLinearLayout = rootView.findViewById(R.id.manageCustomerGroupsLinearLayout);
        manageCustomerGroupsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(getActivity(), GenericCustomerGroupsActivity.class, true));
            }
        });

        //Settings of Customers view interaction
        settingsLinearLayout = rootView.findViewById(R.id.settingsLinearLayout);
        settingsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(getActivity(), SettingsActivity.class, true));
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
