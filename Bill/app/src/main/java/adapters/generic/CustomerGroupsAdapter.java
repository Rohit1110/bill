package adapters.generic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.R;
import com.reso.bill.generic.GenericAddCustomerGroupActivity;
import com.reso.bill.generic.GenericGroupCustomersActivity;
import com.rns.web.billapp.service.bo.domain.BillCustomerGroup;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;

import java.util.ArrayList;
import java.util.List;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class CustomerGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    List<BillCustomerGroup> groups = new ArrayList<>();
    //    private BillUser customer;
    private ProgressDialog pDialog;
    private BillCustomerGroup selectedGroup;

    public CustomerGroupsAdapter(List<BillCustomerGroup> items, Activity activity) {
        this.groups = items;
        this.activity = activity;

    }

    public void updateSearchList(List<BillCustomerGroup> filterList) {
        this.groups = filterList;
        notifyDataSetChanged();
    }

    class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName, txtCount;
        private ImageView usersIcon, editIcon;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_group_name);
            txtCount = (TextView) itemView.findViewById(R.id.txt_group_customers_count);
            usersIcon = (ImageView) itemView.findViewById(R.id.img_users_icon);
            editIcon = (ImageView) itemView.findViewById(R.id.img_edit_customer_group);

        }

        @Override
        public void onClick(View view) {

        }

        public void bind(final BillCustomerGroup invoice) {
            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.nextFragment((FragmentActivity) activity, FragmentEditInvoice.newInstance(customer, invoice));
                }
            });*/
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.row_gn_customer_group, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BillCustomerGroup group = (BillCustomerGroup) groups.get(position);
        ViewHolder1 view = (ViewHolder1) holder;
        view.txtName.setText(group.getGroupName());
        //view.txtCount.setText(group.get);
        Utility.showHelpfulToast(view.usersIcon, "Count of customers in this group", activity);

        if (group.getNoOfCustomers() != null) {
            view.txtCount.setText(group.getNoOfCustomers().toString());
        }

        view.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(Utility.nextIntent(activity, GenericGroupCustomersActivity.class, true, group, Utility.GROUP_KEY));
            }
        });

        view.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] items = {"Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setTitle("Action for group " + group.getGroupName());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        System.out.println("Selected option => " + item);
                        selectedGroup = group;
                        deleteGroup();

                    }
                });
                builder.show();
                return true;
            }
        });

        view.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(Utility.nextIntent(activity, GenericAddCustomerGroupActivity.class, false, group, Utility.GROUP_KEY));
            }
        });

        view.bind(group);

    }


    @Override
    public int getItemCount() {
        if (groups == null) {
            return 0;
        }
        return groups.size();
    }

    private void deleteGroup() {
        if (selectedGroup == null || selectedGroup.getId() == null) {
            return;
        }
        BillServiceRequest request = new BillServiceRequest();
        selectedGroup.setStatus(BillConstants.STATUS_DELETED);
        request.setCustomerGroup(selectedGroup);
        pDialog = Utility.getProgressDialogue("Deleting..", activity);
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerGroup", invoiceDeletionResponse(), ServiceUtil.createMyReqErrorListener(pDialog, activity), request);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(myReq);
    }

    private Response.Listener<String> invoiceDeletionResponse() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    if (selectedGroup != null) {
                        groups.remove(selectedGroup);
                        notifyDataSetChanged();
                    }
                    Utility.createAlert(activity, "Group deleted successfully!", "Done");
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(activity, serviceResponse.getResponse(), "Error");
                }

            }

        };


    }


}
