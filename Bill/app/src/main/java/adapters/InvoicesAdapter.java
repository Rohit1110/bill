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
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reso.bill.R;
import com.reso.bill.generic.GenericCustomerProfileActivity;
import com.rns.web.billapp.service.bo.domain.BillUser;

import java.util.ArrayList;
import java.util.List;

import util.Utility;

public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesAdapter.RecViewHolder> {

    private List<BillUser> list;
    private Activity activity;
    private boolean isMultiSelect;
    private List<BillUser> selectedCustomers = new ArrayList<>();
    private Button clear;

    public InvoicesAdapter(List<BillUser> list, Activity activity) {
        this.activity = activity;
        this.list = list;
    }

    public void setMultiSelect(boolean multiSelect) {
        isMultiSelect = multiSelect;
    }

    public List<BillUser> getSelectedCustomers() {
        return selectedCustomers;
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_customer_invoice_summary, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecViewHolder holder, final int position) {
        final BillUser customer = list.get(position);

        holder.bind(customer);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMultiSelect) {
                    holder.selected.setChecked(!holder.selected.isChecked());
                } else {
                    //Utility.nextFragment((FragmentActivity) activity, CustomerProfileFragment.newInstance(customer));
                    activity.startActivity(Utility.nextIntent(activity, GenericCustomerProfileActivity.class, true, customer, Utility.CUSTOMER_KEY));
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isMultiSelect) {
                    setMultiSelect(true);
                    holder.selected.setChecked(true);
                    notifyDataSetChanged();
                    System.out.println("Long clicked ..");
                    if (clear != null) {
                        clear.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });
        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean selected) {
                if (selected) {
                    int index = findCustomer(customer, selectedCustomers);
                    if (index < 0) {
                        selectedCustomers.add(customer);
                    }
                } else {
                    int index = findCustomer(customer, selectedCustomers);
                    if (index >= 0) {
                        selectedCustomers.remove(index);
                    }
                }
                System.out.println("Checked changed ..." + selected + " list - " + selectedCustomers);
            }
        });
        multiSelect(holder.selected);

        int index = findCustomer(customer, selectedCustomers);
        if (index >= 0) {
            holder.selected.setChecked(true);
            System.out.println("Customer found .. " + customer.getName());
        } else {
            holder.selected.setChecked(false);
            System.out.println("Customer Not found .. " + customer.getName());
        }

    }

    private int findCustomer(BillUser customer, List<BillUser> list) {
        if (list.size() == 0) {
            return -1;
        }
        Integer i = 0;
        for (i = 0; i < list.size(); i++) {
            BillUser cust = list.get(i);
            if (cust.getId().intValue() == customer.getId().intValue()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void clearSelected() {
        setMultiSelect(false);
        selectedCustomers.clear();
        notifyDataSetChanged();
        if (clear != null) {
            clear.setVisibility(View.GONE);
        }
    }

    public void setClearButton(Button clear) {
        this.clear = clear;
    }

    public void updateSearchList(List<BillUser> filterList) {
        if (filterList == null || filterList.size() == 0) {
            list = new ArrayList<>();
        } else {
            list = filterList;
        }
        /*for (BillUser user : list) {
            boolean found = false;
            for (BillUser filtered : filterList) {
                if (user.getId().intValue() == filtered.getId().intValue()) {
                    user.setVisible(true);
                    System.out.println("User visible .." + user.getName());
                    found = true;
                    break;
                }
            }
            if(!found) {
                user.setVisible(false);
            }
        }*/
        notifyDataSetChanged();
    }

    public class RecViewHolder extends RecyclerView.ViewHolder {

        //private ImageView status;
        private TextView amount;
        private TextView txtName;
        private CheckBox selected;
        private TextView txtAddress;
        private ImageView reminderCount;
        private TextView reminderCountVal;

        public RecViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_invoice_summary_customer_name);
            amount = (TextView) itemView.findViewById(R.id.txt_invoice_summary_amount);
            //status = (ImageView) itemView.findViewById(R.id.img_status_invoice_summary);
            selected = (CheckBox) itemView.findViewById(R.id.chkbox_invoices_select_customer);
            txtAddress = (TextView) itemView.findViewById(R.id.txt_invoice_summary_cust_address);
            reminderCount = (ImageView) itemView.findViewById(R.id.img_reminder_count);
            reminderCountVal = (TextView) itemView.findViewById(R.id.txt_reminder_count);
        }

        private void bind(final BillUser customerUser) {
            //customerUser.setVisible(true);
            txtName.setText(customerUser.getName());
            if (TextUtils.isEmpty(txtName.getText())) {
                txtName.setText(customerUser.getPhone());
            }
            txtAddress.setText(customerUser.getAddress());
            //status.setImageResource(R.drawable.ic_invoice_pending);
            if (customerUser.getCurrentInvoice() != null && customerUser.getCurrentInvoice().getPayable() != null) {
                amount.setText(customerUser.getCurrentInvoice().getPayable().toString() + "/-");
                /*if(customerUser.getCurrentInvoice().getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    status.setImageResource(R.drawable.ic_invoice_pending);
                }*/
                if (customerUser.getCurrentInvoice().getNoOfReminders() != null && customerUser.getCurrentInvoice().getNoOfReminders() > 0) {
                    reminderCountVal.setText(customerUser.getCurrentInvoice().getNoOfReminders().toString());
                    reminderCount.setVisibility(View.VISIBLE);
                    reminderCountVal.setVisibility(View.VISIBLE);
                } else {
                    reminderCount.setVisibility(View.GONE);
                    reminderCountVal.setVisibility(View.GONE);
                }
            }

            reminderCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    remindCountHelp(customerUser);
                }
            });

            reminderCountVal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    remindCountHelp(customerUser);
                }
            });

            multiSelect(selected);
        }


    }

    private void remindCountHelp(BillUser customerUser) {
        if (customerUser != null && customerUser.getCurrentInvoice() != null && customerUser.getCurrentInvoice().getNoOfReminders() != null) {
            Toast.makeText(activity,  "You have sent " + customerUser.getCurrentInvoice().getNoOfReminders() + " reminders to " + customerUser.getName() + " so far", Toast.LENGTH_LONG).show();
        }
    }

    private void multiSelect(CheckBox selected) {
        if (isMultiSelect) {
            selected.setVisibility(View.VISIBLE);
        } else {
            selected.setVisibility(View.GONE);
        }
    }
}
