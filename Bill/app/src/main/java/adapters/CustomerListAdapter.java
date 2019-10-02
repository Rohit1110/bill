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
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
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

    public void updateCustomerList(final List<BillCustomer> customers) {
        //setUsers(newList);

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new CustomerCallback(customers, list));

        //result.
        result.dispatchUpdatesTo(this);

        this.list = customers;
        //notifyDataSetChanged();
    }

    public Filter getFilter(final List<BillCustomer> mainList) {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence text) {
                List<BillCustomer> filtered = new ArrayList<>();
                if (TextUtils.isEmpty(text)) {
                    if (list.size() == mainList.size()) {
                        return null;
                    }
                    filtered = mainList;
                } else {
                    // Iterate in the original List and add it to filter list...
                    for (BillCustomer item : list) {
                        if (item.getUser() == null) {
                            continue;
                        }
                        System.out.println("Comparing " + item.getUser().getName() + " with " + text);
                        if (item.getUser().getName() != null && item.getUser().getName().toLowerCase().contains(text.toString().toLowerCase()) /*|| comparePhone(item, text)*/) {
                            // Adding Matched items
                            filtered.add(item);
                        } else if (item.getUser().getPhone() != null && item.getUser().getPhone().toLowerCase().contains(text.toString().toLowerCase())) {
                            filtered.add(item);
                        }

                    }
                }
                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults == null) {
                    return;
                }


                DiffUtil.DiffResult result = DiffUtil.calculateDiff(new CustomerCallback((List<BillCustomer>) filterResults.values, list));

                //result.
                result.dispatchUpdatesTo(CustomerListAdapter.this);

                list = (List<BillCustomer>) filterResults.values;
                //notifyDataSetChanged();
            }
        };
    }

    class CustomerCallback extends DiffUtil.Callback {

        private List<BillCustomer> newList;
        private List<BillCustomer> oldList;


        public CustomerCallback(List<BillCustomer> nw, List<BillCustomer> old) {
            this.newList = nw;
            this.oldList = old;
        }


        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            BillCustomer oldItem = oldList.get(oldItemPosition);
            BillCustomer newItem = newList.get(newItemPosition);
            if (newItem != null && oldItem != null) {
                if (newItem.getUser() != null && oldItem.getUser() != null && newItem.getUser().getId().intValue() == oldItem.getUser().getId().intValue()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            BillCustomer oldItem = oldList.get(oldItemPosition);
            BillCustomer newItem = newList.get(newItemPosition);
            if (newItem != null && oldItem != null) {
                if (newItem.getUser() != null && oldItem.getUser() != null && newItem.getUser().getId().intValue() == oldItem.getUser().getId().intValue()) {
                    return true;
                }
            }
            return false;
        }
    }

    ;

}
