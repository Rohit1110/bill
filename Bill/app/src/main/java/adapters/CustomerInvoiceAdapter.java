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

import com.reso.bill.FragmentEditInvoice;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillInvoice;

import java.util.ArrayList;
import java.util.List;

import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class CustomerInvoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    List<BillInvoice> items = new ArrayList<>();
    private BillUser customer;

    public CustomerInvoiceAdapter(List<BillInvoice> items, Activity activity, BillUser customer) {
        this.items = items;
        this.activity = activity;
        this.customer = customer;
    }

    class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtMonths, txtamount, txtstatus;
        ImageView iv;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtMonths = (TextView) itemView.findViewById(R.id.txt_months);
            txtamount = (TextView) itemView.findViewById(R.id.txt_amount);
            txtstatus = (TextView) itemView.findViewById(R.id.status);

        }

        @Override
        public void onClick(View view) {

        }

        public void bind(final BillInvoice invoice) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.nextFragment((FragmentActivity) activity, FragmentEditInvoice.newInstance(customer, invoice));
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
        gholder.txtMonths.setText(invoice.getMonth() + " - " + invoice.getYear());
        if (invoice.getAmount() != null) {
            gholder.txtamount.setText(invoice.getAmount().toString());
        }
        gholder.txtstatus.setText(invoice.getStatus());
        gholder.bind(invoice);

    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }


}
