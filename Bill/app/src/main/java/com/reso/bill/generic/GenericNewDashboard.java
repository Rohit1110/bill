package com.reso.bill.generic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.BillSummaryActivity;
import com.reso.bill.CustomerList;
import com.reso.bill.DailySummaryActivity;
import com.reso.bill.DistributorsActivity;
import com.reso.bill.FragmentInvoiceSummary;
import com.reso.bill.HelpActivity;
import com.reso.bill.HomeFragment;
import com.reso.bill.ManageNewspapersActivity;
import com.reso.bill.R;
import com.reso.bill.SettingsActivity;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillPaymentSummary;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;

import util.FirebaseUtil;
import util.ServiceUtil;
import util.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GenericNewDashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenericNewDashboard extends Fragment {

    private BillUser user;
    private static Fragment fragment = null;
    private BillPaymentSummary summary;
    private TextView timeSaved, moneySaved, totalRevenue, monthlyRevenue, pendingBills, pendingBillAmount, monthlyCollection;
    private TextView noOfCustomersNumTextView, monthlyCollectionAmtTextView;


    public GenericNewDashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameter.
     */
    // TODO: Rename and change types and number of parameters
    public static GenericNewDashboard newInstance(BillUser user, BillPaymentSummary summary) {
        GenericNewDashboard fragment = new GenericNewDashboard();
        fragment.user = user;
        fragment.summary = summary;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utility.AppBarTitle("Dashboard", getActivity());

        View rootView = inflater.inflate(R.layout.fragment_generic_new_dashboard, container, false);

        CardView createNewInvoiceCardView, pendingInvoicesCardView, manageCustomersCardView, allTransactionsCardView;

        LinearLayout viewAllInvoicesLinearLayout, todaysDeliveriesLinearLayout, totalNewspaperOrdersLinearLayout, purchasesWithDistributorsLinearLayout, summaryOfInvoicesLinearLayout, paymentReportLayout, bankInformationLinearLayout, manageCustomerGroupsLinearLayout, settingsLinearLayout, termsLinearLayout, privacyPolicyLinearLayout, aboutUsLinearLayout, helpLinearLayout, productsLayout;

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

        //Terms view interaction
        termsLinearLayout = rootView.findViewById(R.id.termsLinearLayout);
        termsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(Utility.nextIntent(getActivity(), SettingsActivity.class, true));
                Intent termsWebViewIntent = new Intent(getActivity(), HelpActivity.class);
                // Adding URL, title string in the intent and starting it
                termsWebViewIntent.putExtra("URL", "https://payperbill.in/terms.html");
                termsWebViewIntent.putExtra("ACTIVITY_TITLE", "Terms");
                startActivity(termsWebViewIntent);
            }
        });

        //Privacy Policy view interaction
        privacyPolicyLinearLayout = rootView.findViewById(R.id.privacyPolicyLinearLayout);
        privacyPolicyLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(Utility.nextIntent(getActivity(), SettingsActivity.class, true));
                Intent privacyPolicyWebViewIntent = new Intent(getActivity(), HelpActivity.class);
                // Adding URL, title string in the intent and starting it
                privacyPolicyWebViewIntent.putExtra("URL", "https://payperbill.in/privacy.html");
                privacyPolicyWebViewIntent.putExtra("ACTIVITY_TITLE", "Privacy Policy");
                startActivity(privacyPolicyWebViewIntent);
            }
        });

        //About Us view interaction
        aboutUsLinearLayout = rootView.findViewById(R.id.aboutUsLinearLayout);
        aboutUsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(Utility.nextIntent(getActivity(), SettingsActivity.class, true));
                Intent aboutUsWebViewIntent = new Intent(getActivity(), HelpActivity.class);
                // Adding URL, title string in the intent and starting it
                aboutUsWebViewIntent.putExtra("URL", "https://payperbill.in/aboutUs.html");
                aboutUsWebViewIntent.putExtra("ACTIVITY_TITLE", "About Us");
                startActivity(aboutUsWebViewIntent);
            }
        });

        //Help view interaction
        helpLinearLayout = rootView.findViewById(R.id.helpLinearLayout);
        helpLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(Utility.nextIntent(getActivity(), SettingsActivity.class, true));
                Intent helpWebViewIntent = new Intent(getActivity(), HelpActivity.class);
                // Adding URL, title string in the intent and starting it
                helpWebViewIntent.putExtra("URL", "https://payperbill.in/help/");
                helpWebViewIntent.putExtra("ACTIVITY_TITLE", "Help");
                startActivity(helpWebViewIntent);
            }
        });

        //Products/ My newspapers layout
        productsLayout = rootView.findViewById(R.id.productCatalogueLayout);
        productsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNewspaper(user)) {
                    startActivity(Utility.nextIntent(getActivity(), ManageNewspapersActivity.class, true));
                } else {
                    startActivity(Utility.nextIntent(getActivity(), GenericMyProductsActivity.class, true));
                }

            }
        });

        TextView productsTitle = rootView.findViewById(R.id.productCatalogueTextView);


        if (!BillConstants.FRAMEWORK_RECURRING.equals(Utility.getFramework(user))) {
            totalNewspaperOrdersLinearLayout.setVisibility(View.GONE);
            purchasesWithDistributorsLinearLayout.setVisibility(View.GONE);
            todaysDeliveriesLinearLayout.setVisibility(View.GONE);
            settingsLinearLayout.setVisibility(View.GONE);
            summaryOfInvoicesLinearLayout.setVisibility(View.GONE);
        }
        if (Utility.isNewspaper(user)) {
            productsTitle.setText("Manage Newspapers");
        }

        viewAllInvoicesLinearLayout.setVisibility(View.GONE);

        timeSaved = rootView.findViewById(R.id.txtTimeSaved);
        moneySaved = rootView.findViewById(R.id.txtMoneySaved);
        totalRevenue = rootView.findViewById(R.id.txtTotalRevenue);
        pendingBills = rootView.findViewById(R.id.pendingBillsTextView);
        pendingBillAmount = rootView.findViewById(R.id.pendingBillsAmtTextView);
        monthlyCollectionAmtTextView = rootView.findViewById(R.id.monthlyCollectionAmtTextView);


        noOfCustomersNumTextView = rootView.findViewById(R.id.noOfCustomersNumTextView);

        setValues();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setValues() {
        if (summary != null) {
            timeSaved.setText(summary.getTimeSaved());
            moneySaved.setText(getActivity().getString(R.string.rupee) + summary.getMoneySaved());
            totalRevenue.setText(getActivity().getString(R.string.rupee) + summary.getTotalCollection());
            if (summary.getPendingInvoices() != null) {
                pendingBills.setText(summary.getPendingInvoices().toString() + " Pending Invoices");
            }
            pendingBillAmount.setText(getActivity().getString(R.string.rupee) + summary.getPendingTotal());
            monthlyCollectionAmtTextView.setText(getActivity().getString(R.string.rupee) + summary.getMonthlyCollection());
            noOfCustomersNumTextView.setText(String.valueOf(summary.getTotalCustomers()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();

    }

    void loadProfile() {

        //pDialog = Utility.getProgressDialogue("Loading Profile", MainActivity.this);
        BillServiceRequest request = new BillServiceRequest();
        BillUser requestUser = new BillUser();
        requestUser.setDeviceId(FirebaseUtil.getToken());
        requestUser.setPhone(user.getPhone());
        BillBusiness currBusiness = new BillBusiness();
        currBusiness.setId(user.getCurrentBusiness().getId());
        request.setBusiness(currBusiness);
        request.setRequestType("DASHBOARD");
        request.setUser(requestUser);
        StringRequest myReq = ServiceUtil.getStringRequest("loadProfile", createMyReqSuccessListener(), ServiceUtil.createMyReqErrorListener(null, getActivity()), request);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
//                pDialog.dismiss();
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200 && serviceResponse.getDashboard() != null) {
                    summary = serviceResponse.getDashboard();
                    setValues();
                }
            }
        };
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
