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

package adapters.generic;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.reso.bill.components.ItemMoveCallBack;
import com.reso.bill.generic.GenericCustomerProfileActivity;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillCustomerGroup;
import com.rns.web.billapp.service.bo.domain.BillSubscription;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.BillCustomer;
import util.ServiceUtil;
import util.Utility;

public class GroupCustomersAdapter extends RecyclerView.Adapter<GroupCustomersAdapter.RecViewHolder> implements ItemMoveCallBack.ItemTouchHelperContract {

    private List<BillCustomer> list;
    private Activity context;
    private BillUser currentUser;
    private BillCustomerGroup group;
    private BillBusiness business;

    public GroupCustomersAdapter(List<BillCustomer> list, Activity context, BillUser user) {
        this.list = list;
        this.context = context;
        this.currentUser = user;
    }

    public void setBusiness(BillBusiness business) {
        this.business = business;
    }

    public void setGroup(BillCustomerGroup group) {
        this.group = group;
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

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(GroupCustomersAdapter.RecViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.md_blue_grey_100));
        System.out.println("Selected ...");

    }

    @Override
    public void onRowClear(GroupCustomersAdapter.RecViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.WHITE);
        System.out.println("Cleared ...");
        //Update the new sequence in the backend
        updateSequence();
    }

    @Override
    public void onRowSwiped(RecViewHolder myViewHolder) {
        int adapterPosition = myViewHolder.getAdapterPosition();
        BillCustomer customerDeleted = list.get(adapterPosition);
        System.out.println("Row swiped ..." + adapterPosition + " .. " + customerDeleted.getUser().getName());
        list.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        //Calling service to update user info
        removeFromGroup(customerDeleted.getUser());
    }

    private void removeFromGroup(BillUser selectedUser) {
        if (selectedUser == null || (selectedUser.getId() == null && selectedUser.getCurrentSubscription().getId() == null)) {
            Utility.createAlert(context, "Customer does not exist!", "Error");
            return;
        }

        BillServiceRequest request = new BillServiceRequest();
        BillUser customer = new BillUser();
        BillSubscription subscription = new BillSubscription();
        subscription.setId(selectedUser.getId());
        subscription.setItems(selectedUser.getCurrentSubscription().getItems());
        customer.setName(selectedUser.getName());
        customer.setPhone(selectedUser.getPhone());
        customer.setCurrentSubscription(subscription);
        request.setUser(customer);

        /*BillServiceRequest request = new BillServiceRequest();
        BillUser customer = new BillUser();
        if (selectedUser.getCurrentSubscription().getId() != null) {
            customer.setId(selectedUser.getCurrentSubscription().getId());
        } else {
            customer.setId(selectedUser.getId());
        }
        BillSubscription subscription = new BillSubscription();
        subscription.setId(selectedUser.getCurrentSubscription().getId());
        //subscription.setItems(selectedUser.getCurrentSubscription().getItems());
        customer.setCurrentSubscription(subscription);
        request.setUser(customer);*/
        BillCustomerGroup customerGroup = new BillCustomerGroup();
        customerGroup.setId(0);
        request.setCustomerGroup(customerGroup);
        request.setBusiness(business);
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomer", customerSavedListener(customer), ServiceUtil.createMyReqErrorListener(null, context), request);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(myReq);
    }

    private Response.Listener<String> customerSavedListener(final BillUser customer) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Toast.makeText(context, "Removed successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(context, serviceResponse.getResponse(), "Error");
                }


            }
        };
    }

    private void updateSequence() {
        BillServiceRequest request = new BillServiceRequest();
        request.setCustomerGroup(group);
        //Create list sequence to send
        List<BillUser> sequencedUsers = new ArrayList<>();
        if (list != null && list.size() > 0) {

            Integer seq = 1;
            for (BillCustomer customer : list) {
                BillUser user = new BillUser();
                BillSubscription subscription = new BillSubscription();
                BillCustomerGroup customerGroup = new BillCustomerGroup();
                customerGroup.setId(group.getId());
                customerGroup.setSequenceNumber(seq);
                subscription.setGroup(customerGroup);
                user.setCurrentSubscription(subscription);
                if (customer.getUser().getCurrentSubscription() != null) {
                    user.setId(customer.getUser().getCurrentSubscription().getId());
                } else {
                    user.setId(customer.getUser().getId());
                }
                seq++;
                sequencedUsers.add(user);
            }
        }
        request.setUsers(sequencedUsers);
        StringRequest myReq = ServiceUtil.getStringRequest("updateGroupCustomers", customersLoaded("group"), ServiceUtil.createMyReqErrorListener(null, context), request);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(myReq);
    }

    private Response.Listener<String> customersLoaded(final String type) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println("## response:" + response);
                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(context, "Error while saving!" + serviceResponse.getResponse(), "Error");
                    //customerName.setada
                }
            }

        };
    }


}
