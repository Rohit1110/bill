package adapters.generic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.reso.bill.generic.GenericCreateBill;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class CompleteInvoicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    List<BillUser> users = new ArrayList<>();
    private BillUser customer;
    private ProgressDialog pDialog;
    private BillUser selectedUser;

    public CompleteInvoicesAdapter(List<BillUser> items, Activity activity) {
        this.users = items;
        this.activity = activity;
        //this.customer = customer;
    }

    public void updateSearchList(List<BillUser> filterList) {
        this.users = filterList;
        notifyDataSetChanged();
    }

    class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName, txtAmount, txtDate, txtInvoiceRef;
        private ImageView imgStatus;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_txn_customer_name);
            txtAmount = (TextView) itemView.findViewById(R.id.txt_txn_amount);
            imgStatus = (ImageView) itemView.findViewById(R.id.img_txn_status);
            txtDate = (TextView) itemView.findViewById(R.id.txt_txn_date);
            txtInvoiceRef = (TextView) itemView.findViewById(R.id.txt_txn_invoice_ref);
        }

        @Override
        public void onClick(View view) {

        }

        public void bind(final BillUser invoice) {
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
        View item = inflater.inflate(R.layout.row_complete_invoice, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BillUser txn = (BillUser) users.get(position);
        ViewHolder1 view = (ViewHolder1) holder;
        if (!TextUtils.isEmpty(txn.getName())) {
            view.txtName.setText(txn.getName());
        } else {
            view.txtName.setText(txn.getPhone());
        }
        view.txtDate.setText(CommonUtils.convertDate(txn.getCurrentInvoice().getInvoiceDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR_TIME));
        final BillInvoice currentInvoice = txn.getCurrentInvoice();
        if (currentInvoice != null) {
            if (currentInvoice.getMonth() != null && currentInvoice.getYear() != null) {
                view.txtInvoiceRef.setText("Invoice for " + BillConstants.MONTHS[currentInvoice.getMonth() - 1] + " " + currentInvoice.getYear());
            } else {
                view.txtInvoiceRef.setText("Invoice #" + CommonUtils.getStringValue(currentInvoice.getId()));
            }
            view.txtAmount.setText("INR " + CommonUtils.getStringValue(currentInvoice.getAmount()));

            if (currentInvoice.getStatus() != null && BillConstants.INVOICE_STATUS_PAID.equals(currentInvoice.getStatus())) {
                view.imgStatus.setImageResource(R.drawable.ic_invoice_paid);
            } else if (currentInvoice.getStatus() != null && "Settled".equals(currentInvoice.getStatus())) {
                view.imgStatus.setImageResource(R.drawable.ic_txn_settled);
            } else if (currentInvoice.getStatus() != null && "Failed".equals(currentInvoice.getStatus())) {
                view.imgStatus.setImageResource(R.drawable.ic_invoice_failed);
            } else {
                view.imgStatus.setImageResource(R.drawable.ic_invoice_pending);
            }

        }

        view.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment((FragmentActivity) activity, GenericCreateBill.newInstance(txn));
            }
        });

        view.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] items = {"Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setTitle("Action for invoice #" + currentInvoice.getId());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        System.out.println("Selected option => " + item);
                        selectedUser = txn;
                        deleteInvoice();

                    }
                });
                builder.show();
                return true;
            }
        });

        view.bind(txn);

    }

    @Override
    public int getItemCount() {
        if (users == null) {
            return 0;
        }
        return users.size();
    }

    private void deleteInvoice() {
        if (selectedUser == null || selectedUser.getCurrentInvoice() == null || selectedUser.getCurrentInvoice().getId() == null) {
            return;
        }
        selectedUser.getCurrentInvoice().setStatus(BillConstants.INVOICE_STATUS_DELETED);
        BillServiceRequest request = new BillServiceRequest();
        request.setInvoice(selectedUser.getCurrentInvoice());
        pDialog = Utility.getProgressDialogue("Deleting..", activity);
        StringRequest myReq = ServiceUtil.getBusinessStringRequest("updateBill", invoiceDeletionResponse(), ServiceUtil.createMyReqErrorListener(pDialog, activity), request);
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
                    if (selectedUser != null) {
                        users.remove(selectedUser);
                        notifyDataSetChanged();
                    }
                    Utility.createAlert(activity, "Invoice deleted successfully!", "Done");
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(activity, serviceResponse.getResponse(), "Error");
                }

            }

        };


    }


}
