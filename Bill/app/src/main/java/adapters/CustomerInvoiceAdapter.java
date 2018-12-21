package adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reso.bill.EditInvoiceActivity;
import com.reso.bill.R;
import com.reso.bill.generic.GenericCreateBillActivity;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class CustomerInvoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    List<BillInvoice> items = new ArrayList<>();
    private BillUser customer;
    private BillUser user;

    public CustomerInvoiceAdapter(List<BillInvoice> items, Activity activity, BillUser customer) {
        this.items = items;
        this.activity = activity;
        this.customer = customer;
    }

    public void setUser(BillUser user) {
        this.user = user;
    }

    class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtMonths, txtamount;
        ImageView statusImg;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtMonths = (TextView) itemView.findViewById(R.id.txt_months);
            txtamount = (TextView) itemView.findViewById(R.id.txt_amount);
            statusImg = (ImageView) itemView.findViewById(R.id.status_img);
        }

        @Override
        public void onClick(View view) {

        }

        public void bind(final BillInvoice invoice) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (BillConstants.FRAMEWORK_GENERIC.equals(Utility.getFramework(user))) {
                        customer.setCurrentInvoice(invoice);
                        //Utility.nextFragment((FragmentActivity) activity, GenericCreateBill.newInstance(customer));
                        activity.startActivity(Utility.nextIntent(activity, GenericCreateBillActivity.class, true, customer, Utility.CUSTOMER_KEY));
                    } else {
                        //TODO Change to activity
                        //Utility.nextFragment((FragmentActivity) activity, FragmentEditInvoice.newInstance(customer, invoice));
                        Intent intent = Utility.nextIntent(activity, EditInvoiceActivity.class, true, invoice, Utility.INVOICE_KEY);
                        intent.putExtra(Utility.CUSTOMER_KEY, ServiceUtil.toJson(customer));
                        activity.startActivity(intent);
                    }

                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.row_customer_bill_details, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BillInvoice invoice = (BillInvoice) items.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;
        if (invoice.getMonth() != null && invoice.getYear() != null) {
            gholder.txtMonths.setText(BillConstants.MONTHS[invoice.getMonth() - 1] + " " + invoice.getYear());
        } else if (invoice.getInvoiceDate() != null) {
            gholder.txtMonths.setText(CommonUtils.convertDate(invoice.getInvoiceDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR));
        } else {
            gholder.txtMonths.setText("");
        }
        if (invoice.getPayable() != null) {
            gholder.txtamount.setText(invoice.getPayable().toString() + "/-");
        }
        if (invoice.getStatus() != null && BillConstants.INVOICE_STATUS_PAID.equals(invoice.getStatus())) {
            gholder.statusImg.setImageResource(R.drawable.ic_invoice_paid);
            showHelpfulToast(gholder.statusImg, "Invoice Paid");
        } else if (invoice.getStatus() != null && "Failed".equals(invoice.getStatus())) {
            gholder.statusImg.setImageResource(R.drawable.ic_invoice_failed);
            showHelpfulToast(gholder.statusImg, "Invoice Failed");
        }else if (invoice.getStatus() != null && "Pending".equals(invoice.getStatus())) {
            gholder.statusImg.setImageResource(R.drawable.ic_invoice_pending);
            showHelpfulToast(gholder.statusImg, "Invoice Pending");
        }

        gholder.bind(invoice);

    }

    private void showHelpfulToast(ImageView imageView, final String message) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }


}
