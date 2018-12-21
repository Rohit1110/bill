package adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        private TextView items, txtName, txtAddress;

        public RecViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_order_customer_name);
            txtAddress = itemView.findViewById(R.id.txt_order_customer_address);
            items = itemView.findViewById(R.id.txt_order_items);
        }

        private void bind(final BillCustomer customer) {
            final BillUser customerUser = customer.getUser();
            txtName.setText(customerUser.getName());
            txtAddress.setText(customerUser.getAddress());

            if (customerUser.getCurrentSubscription() != null && customerUser.getCurrentSubscription().getItems() != null && customerUser.getCurrentSubscription().getItems().size() > 0) {
                items.setText(Utility.getCustomerItemString(customerUser.getCurrentSubscription().getItems()));
            } else {
                items.setText("No subscriptions");
            }
        }
    }
}
