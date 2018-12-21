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
List<BillCustomer> users = new ArrayList<BillCustomer>();

    public DeliveriesAdapter(List<BillCustomer> users) {
        this.users = users;
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
        BillCustomer listone =(BillCustomer)users.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;
        gholder.txtName.setText(listone.getName());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
*/


package adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillOrder;
import com.rns.web.billapp.service.bo.domain.BillUserLog;

import java.util.List;

import util.Utility;

public class CustomerActivityAdapter extends RecyclerView.Adapter<CustomerActivityAdapter.RecViewHolder> {

    private List<BillOrder> list;
    private FragmentActivity context;
    private List<BillUserLog> logs;

    public CustomerActivityAdapter(List<BillOrder> list, FragmentActivity context) {
        this.list = list;
        this.context = context;
        //this.logs = logs;
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_activity_row, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, final int position) {
        final BillOrder movie = list.get(position);

        holder.bind(movie);


//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class RecViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtItemName;

        public RecViewHolder(View itemView) {
            super(itemView);
            txtName =  itemView.findViewById(R.id.txt_order_customer_name);
            txtItemName =  itemView.findViewById(R.id.txt_customer_row_item_name);

        }

        private void bind(final BillOrder order) {

            txtName.setText(order.getOrderDateString());
            txtItemName.setText("");
            if (order.getItems() != null && order.getItems().size() > 0) {
                txtItemName.setText(Utility.getCustomerItemString(order.getItems()));
            } else {
                txtItemName.setText("No subscriptions");
            }

        }

    }
}
