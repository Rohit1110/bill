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


public class ListOneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
List<BillCustomer> items = new ArrayList<BillCustomer>();

    public ListOneAdapter(List<BillCustomer> items) {
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
        View item = inflater.inflate(R.layout.list_one_row, parent, false);
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

import com.reso.bill.CustomerProfileFragment;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;

import java.util.List;

import model.BillCustomer;
import util.Utility;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.RecViewHolder> {

    private List<BillCustomer> list;
    private FragmentActivity context;

    public CustomerListAdapter(List<BillCustomer> list, FragmentActivity context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_list_row, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, final int position) {
        final BillCustomer movie = list.get(position);

        holder.bind(movie);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean expanded = movie.isExpanded();
                movie.setExpanded(!expanded);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class RecViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtItemName;
        private TextView txtAddress, txtViewprofile;
        private TextView year;
        private View subItem;

        public RecViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtItemName = (TextView) itemView.findViewById(R.id.txt_customer_row_item_name);
            txtAddress = (TextView) itemView.findViewById(R.id.sub_item_address);
            subItem = itemView.findViewById(R.id.customer_details);
            txtViewprofile = (TextView) itemView.findViewById(R.id.sub_item_viewprofile);

        }

        private void bind(final BillCustomer customer) {
            boolean expanded = customer.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            final BillUser customerUser = customer.getUser();
            txtName.setText(customerUser.getName());
            txtAddress.setText(customerUser.getAddress());
            //year.setText("Year: " + customer.getYear());
            txtViewprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.nextFragment(context, CustomerProfileFragment.newInstance(customerUser));
                }
            });

            txtItemName.setText("");
            if (customerUser.getCurrentSubscription() != null && customerUser.getCurrentSubscription().getItems() != null && customerUser.getCurrentSubscription().getItems().size() > 0) {
                for (BillItem subscribed : customerUser.getCurrentSubscription().getItems()) {
                    txtItemName.setText(txtItemName.getText().toString().concat(subscribed.getName() + " | "));
                }
            } else {
                txtItemName.setText("No subscriptions");
            }

        }
    }
}
