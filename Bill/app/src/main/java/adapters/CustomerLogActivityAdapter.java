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

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillUserLog;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.List;

public class CustomerLogActivityAdapter extends RecyclerView.Adapter<CustomerLogActivityAdapter.RecViewHolder> {

    private FragmentActivity context;
    private List<BillUserLog> list;

    public CustomerLogActivityAdapter(List<BillUserLog> list, FragmentActivity context) {
        this.list = list;
        this.context = context;
        //this.logs = logs;
    }

    public List<BillUserLog> getList() {
        return list;
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_activity_row, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, final int position) {
        final BillUserLog movie = list.get(position);

        holder.bind(movie);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*boolean expanded = movie.isExpanded();
                movie.setExpanded(!expanded);
                notifyItemChanged(position);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class RecViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtItemName;
        //private TextView txtAddress, txtViewprofile;
        private TextView year;
        //private View subItem;

        public RecViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_order_customer_name);
            txtItemName = (TextView) itemView.findViewById(R.id.txt_customer_row_item_name);
            /*txtAddress = (TextView) itemView.findViewById(R.id.txt_order_customer_address);
            subItem = itemView.findViewById(R.id.customer_details);
            txtViewprofile = (TextView) itemView.findViewById(R.id.btn_order_view_profile);*/

        }

        private void bind(final BillUserLog log) {

            txtName.setText(CommonUtils.convertDate(log.getFromDate(), "MMM dd") + " to " + CommonUtils.convertDate(log.getToDate(), "MMM dd") );
            txtItemName.setText("");
            if (log.getItem() != null) {
                txtItemName.setText(log.getItem().getName());
            }

        }

    }
}
