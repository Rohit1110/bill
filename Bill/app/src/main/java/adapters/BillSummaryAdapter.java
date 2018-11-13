package adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reso.bill.CustomerProfileFragment;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class BillSummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<BillUser> users = new ArrayList<>();
    private BillUser customer;
    private List<BillUser> originals;
    private List<BillUser> filteredUsers;

    public BillSummaryAdapter(List<BillUser> items, Activity activity) {
        this.users = items;
        this.activity = activity;
        this.originals = items;
        //this.customer = customer;
    }

    public void updateSearchList(List<BillUser> filterList) {
        this.users = filterList;
        notifyDataSetChanged();
    }

    public void showOnlySchemeUsers(boolean showOnlySchemeUsers) {
        if (showOnlySchemeUsers) {
            if (originals != null && originals.size() > 0) {
                filteredUsers = new ArrayList<>();
                for (BillUser user : originals) {
                    BillInvoice currentInvoice = user.getCurrentInvoice();
                    if (currentInvoice != null) {
                        if (currentInvoice.getInvoiceItems() != null && currentInvoice.getInvoiceItems().size() > 0) {
                            BillItem invoiceItem = currentInvoice.getInvoiceItems().get(0);
                            if (invoiceItem.getPaymentRef() != null) {
                                filteredUsers.add(user);
                            }
                        }
                    }
                }
                this.users = filteredUsers;
            }
        } else {
            this.users = originals;
            this.filteredUsers = null;
        }
        notifyDataSetChanged();
    }

    public List<BillUser> getOriginals() {
        return originals;
    }

    public List<BillUser> getFilteredUsers() {
        return filteredUsers;
    }

    public List<BillUser> getUsers() {
        return this.users;
    }

    class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName, txtAmount, details, quantity;
        private ImageView imgStatus;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_bill_summary_customer_name);
            txtAmount = (TextView) itemView.findViewById(R.id.txt_bill_summary_amount);
            imgStatus = (ImageView) itemView.findViewById(R.id.img_bill_summary_status);
            //imgPaymentMedium = (ImageView) itemView.findViewById(R.id.img_txn_payment_medium);
            details = (TextView) itemView.findViewById(R.id.txt_bill_summary_details);
            quantity = (TextView) itemView.findViewById(R.id.txt_bill_summary_quantity);
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
        View item = inflater.inflate(R.layout.row_bill_summary, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BillUser txn = (BillUser) users.get(position);
        ViewHolder1 view = (ViewHolder1) holder;
        view.txtName.setText(txn.getName());
        view.details.setText("");
        BillInvoice currentInvoice = txn.getCurrentInvoice();
        if (currentInvoice != null) {
            if (currentInvoice.getInvoiceItems() != null && currentInvoice.getInvoiceItems().size() > 0) {
                BillItem invoiceItem = currentInvoice.getInvoiceItems().get(0);
                view.quantity.setText("Qty " + CommonUtils.getStringValue(invoiceItem.getQuantity()));
                if (invoiceItem.getSchemeEndDate() != null) {
                    view.details.setText("Scheme till " + CommonUtils.convertDate(invoiceItem.getSchemeEndDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR));
                } else if (invoiceItem.getSchemeStartDate() != null) {
                    view.details.setText("Scheme from " + CommonUtils.convertDate(invoiceItem.getSchemeEndDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR));
                } else if (invoiceItem.getPaymentRef() != null) {
                    view.details.setText("Scheme availed");
                }
            } else {
                view.quantity.setText(/*"Invoice #" + CommonUtils.getStringValue(currentInvoice.getId())*/"");
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.nextFragment((FragmentActivity) activity, CustomerProfileFragment.newInstance(txn));
                }
            });


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
