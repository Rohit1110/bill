package adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohit on 5/10/2018.
 */

public class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    List<BillUser> users = new ArrayList<>();
    private BillUser customer;

    public TransactionsAdapter(List<BillUser> items, Activity activity) {
        this.users = items;
        this.activity = activity;
        //this.customer = customer;
    }

    public void updateSearchList(List<BillUser> filterList) {
        this.users = filterList;
        notifyDataSetChanged();
    }

    class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName, txtAmount, txtDate, txtInvoiceRef;
        private ImageView imgStatus, imgPaymentMedium;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_txn_customer_name);
            txtAmount = (TextView) itemView.findViewById(R.id.txt_txn_amount);
            imgStatus = (ImageView) itemView.findViewById(R.id.img_txn_status);
            imgPaymentMedium = (ImageView) itemView.findViewById(R.id.img_txn_payment_medium);
            txtDate = (TextView) itemView.findViewById(R.id.txt_txn_date);
            txtInvoiceRef = (TextView) itemView.findViewById(R.id.txt_txn_invoice_ref);
        }

        @Override
        public void onClick(View view) {

        }

        public void bind(final BillUser invoice) {
            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.nextFragment((FragmentActivity) activity, FragmentEditInvoice.newInstance(customer, invoice));
                }
            });*/
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.row_transaction_details, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BillUser txn = (BillUser) users.get(position);
        ViewHolder1 view = (ViewHolder1) holder;
        view.txtName.setText(txn.getName());
        view.txtDate.setText(CommonUtils.convertDate(txn.getCurrentInvoice().getCreatedDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR));
        BillInvoice currentInvoice = txn.getCurrentInvoice();
        if (currentInvoice != null) {
            if (currentInvoice.getMonth() != null && currentInvoice.getYear() != null) {
                view.txtInvoiceRef.setText("Invoice for " + BillConstants.MONTHS[currentInvoice.getMonth() - 1] + " " + currentInvoice.getYear());
            } else {
                view.txtInvoiceRef.setText("Invoice #" + CommonUtils.getStringValue(currentInvoice.getId()));
            }
            view.txtAmount.setText("INR " + CommonUtils.getStringValue(currentInvoice.getAmount()));

            if (currentInvoice.getStatus() != null && BillConstants.INVOICE_STATUS_PAID.equals(currentInvoice.getStatus())) {
                view.imgStatus.setImageResource(R.drawable.ic_invoice_paid);
            } else if (currentInvoice.getStatus() != null && "Settled".equals(currentInvoice.getStatus())) {
                view.imgStatus.setImageResource(R.drawable.ic_txn_settled);
            } else if (currentInvoice.getStatus() != null && "Failed".equals(currentInvoice.getStatus())) {
                view.imgStatus.setImageResource(R.drawable.ic_invoice_failed);
            } else {
                view.imgStatus.setImageResource(R.drawable.ic_invoice_pending);
            }

            if (currentInvoice.getPaymentMedium() != null && currentInvoice.getPaymentMedium().equals("CASH")) {
                view.imgPaymentMedium.setImageResource(R.drawable.ic_payment_medium_cash);
            } else {
                view.imgPaymentMedium.setImageResource(R.drawable.ic_payment_medium_online);
            }

        }


        view.bind(txn);

    }

    @Override
    public int getItemCount() {
        if (users == null) {
            return 0;
        }
        return users.size();
    }


}