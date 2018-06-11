/*
package adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.reso.bill.R;

import java.util.ArrayList;
import java.util.List;

import model.Listone;

*/
/**
 * Created by Rohit on 5/10/2018.
 *//*


public class DeliveriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
List<BillCustomer> items = new ArrayList<BillCustomer>();

    public DeliveriesAdapter(List<BillCustomer> items) {
        this.items = items;
    }
    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView txtName;
        //private TextView time, name;
        View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtName=(TextView)itemView.findViewById(R.id.txt_name);

        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.row_customer_order, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BillCustomer listone =(BillCustomer)items.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;
        gholder.txtName.setText(listone.getName());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
*/


package adapters;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reso.bill.FragmentCustomerInvoices;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillUser;

import java.util.List;

import util.Utility;

public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesAdapter.RecViewHolder> {

    private List<BillUser> list;
    private Activity activity;

    public InvoicesAdapter(List<BillUser> list, Activity activity) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_customer_invoice_summary, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, final int position) {
        final BillUser customer = list.get(position);
        holder.bind(customer);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment((FragmentActivity) activity, FragmentCustomerInvoices.newInstance(customer));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class RecViewHolder extends RecyclerView.ViewHolder {

        private TextView amount;
        private TextView txtName;


        public RecViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_invoice_summary_customer_name);
            amount = (TextView) itemView.findViewById(R.id.txt_invoice_summary_amount);
        }

        private void bind(BillUser customerUser) {
            txtName.setText(customerUser.getName());
            if (customerUser.getCurrentInvoice() != null && customerUser.getCurrentInvoice().getAmount() != null) {
                amount.setText("INR " + customerUser.getCurrentInvoice().getAmount().toString());
            }

        }
    }
}
