package com.reso.bill.generic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.reso.bill.DailySummaryActivity;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillUser;

import util.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenericNewDashboard.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenericNewDashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenericNewDashboard extends Fragment {

    private BillUser user;
    private LinearLayout paymentReportLayout,totalNewspaperOrdersLinearLayout;

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

        //Today's Newspaper Total Orders view interaction
        totalNewspaperOrdersLinearLayout = rootView.findViewById(R.id.totalNewspaperOrdersLinearLayout);
        totalNewspaperOrdersLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Utility.nextIntent(getActivity(), DailySummaryActivity.class, true));
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
