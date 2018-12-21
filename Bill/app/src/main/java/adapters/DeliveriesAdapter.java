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

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reso.bill.CustomerProfileFragment;
import com.reso.bill.R;
import com.reso.bill.generic.GenericCustomerProfileActivity;
import com.rns.web.billapp.service.bo.domain.BillUser;

import java.util.List;

import model.BillCustomer;
import util.Utility;

public class DeliveriesAdapter extends RecyclerView.Adapter<DeliveriesAdapter.RecViewHolder> {

    private List<BillCustomer> list;
    private Activity activity;
    private BillUser currentUser;

    public DeliveriesAdapter(List<BillCustomer> list, Activity activity, BillUser vendor) {
        this.list = list;
        this.activity = activity;
        this.currentUser = vendor;
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_customer_order, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, final int position) {
        final BillCustomer customer = list.get(position);
        holder.bind(customer);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*boolean expanded = customer.isExpanded();
                customer.setExpanded(!expanded);
                notifyItemChanged(position);*/

                /*CustomerProfileFragment fragment = CustomerProfileFragment.newInstance(customer.getUser());
                customer.getUser().setCurrentBusiness(currentUser.getCurrentBusiness());
                Utility.nextFragment((FragmentActivity) activity, fragment);*/

                activity.startActivity(Utility.nextIntent(activity, GenericCustomerProfileActivity.class, true, customer.getUser(), Utility.CUSTOMER_KEY));

            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class RecViewHolder extends RecyclerView.ViewHolder {

        private TextView viewProfile;
        private TextView items;
        private TextView txtName;
        private TextView txtAddress;
        private TextView year;
        private View subItem;

        public RecViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_order_customer_name);
            txtAddress = (TextView) itemView.findViewById(R.id.txt_order_customer_address);
            subItem = itemView.findViewById(R.id.customer_details);
            items = (TextView) itemView.findViewById(R.id.txt_order_items);
            viewProfile = (TextView) itemView.findViewById(R.id.btn_order_view_profile);
        }

        private void bind(final BillCustomer customer) {

            boolean expanded = customer.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            final BillUser customerUser = customer.getUser();
            txtName.setText(customerUser.getName());
            txtAddress.setText(customerUser.getAddress());

            if (customerUser.getCurrentSubscription() != null && customerUser.getCurrentSubscription().getItems() != null && customerUser.getCurrentSubscription().getItems().size() > 0) {
                items.setText(Utility.getCustomerItemString(customerUser.getCurrentSubscription().getItems()));
            } else {
                items.setText("No subscriptions");
            }

            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomerProfileFragment fragment = CustomerProfileFragment.newInstance(customerUser);
                    customerUser.setCurrentBusiness(currentUser.getCurrentBusiness());
                    Utility.nextFragment((FragmentActivity) activity, fragment);
                }
            });
        }
    }
}
