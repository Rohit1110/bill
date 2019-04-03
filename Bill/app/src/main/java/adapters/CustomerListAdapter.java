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
 */

package adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reso.bill.R;
import com.reso.bill.generic.GenericCustomerProfileActivity;
import com.rns.web.billapp.service.bo.domain.BillUser;

import java.util.ArrayList;
import java.util.List;

import model.BillCustomer;
import util.Utility;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.RecViewHolder> {

    private List<BillCustomer> list;
    private FragmentActivity context;
    private BillUser currentUser;

    public CustomerListAdapter(List<BillCustomer> list, FragmentActivity context, BillUser user) {
        this.list = list;
        this.context = context;
        this.currentUser = user;
    }

    public List<BillCustomer> getList() {
        return list;
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

                movie.getUser().setCurrentBusiness(currentUser.getCurrentBusiness());
                context.startActivity(Utility.nextIntent(context, GenericCustomerProfileActivity.class, true, movie.getUser(), Utility.CUSTOMER_KEY));
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
            txtName = (TextView) itemView.findViewById(R.id.txt_order_customer_name);
            txtItemName = (TextView) itemView.findViewById(R.id.txt_customer_row_item_name);


        }

        private void bind(final BillCustomer customer) {

            final BillUser customerUser = customer.getUser();
            txtName.setText(customerUser.getName());
            if (TextUtils.isEmpty(txtName.getText())) {
                txtName.setText(customerUser.getPhone());
            }

            txtItemName.setText("");
            if (customerUser.getCurrentSubscription() != null && customerUser.getCurrentSubscription().getItems() != null && customerUser.getCurrentSubscription().getItems().size() > 0) {
                txtItemName.setText(Utility.getCustomerItemString(customerUser.getCurrentSubscription().getItems()));
            } else {
                txtItemName.setText("No subscriptions");
            }

        }

    }

    public void setUsers(List<BillUser> users) {
        if (users == null || users.size() == 0) {
            return;
        }
        list = new ArrayList<>();
        for (BillUser user : users) {
            BillCustomer customer = new BillCustomer();
            customer.setUser(user);
            list.add(customer);
        }
    }

    public void updateList(List<BillUser> users) {
        setUsers(users);
        notifyDataSetChanged();
    }

    public void updateCustomerList(List<BillCustomer> customers) {
        //setUsers(customers);
        this.list = customers;
        notifyDataSetChanged();
    }

}
