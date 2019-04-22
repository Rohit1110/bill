package adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.DistributorBillDetailsActivity;
import com.reso.bill.R;
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

public class PurchaseInvoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    List items = new ArrayList<>();
    private BillUser customer;
    private BillUser user;
    private ProgressDialog pDialog;
    private BillInvoice currentInvoice;

    public PurchaseInvoiceAdapter(List<BillInvoice> items, Activity activity, BillUser customer) {
        this.items = items;
        this.activity = activity;
        this.customer = customer;
    }

    public void setUser(BillUser user) {
        this.user = user;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BillInvoice invoice = (BillInvoice) items.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;
        if (invoice.getMonth() != null && invoice.getYear() != null) {
            gholder.txtMonths.setText(BillConstants.MONTHS[invoice.getMonth() - 1] + " " + invoice.getYear());
        } else if (invoice.getInvoiceDate() != null) {
            gholder.txtMonths.setText(CommonUtils.convertDate(invoice.getInvoiceDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR));
        } else {
            gholder.txtMonths.setText("");
        }
        if (invoice.getPayable() != null) {
            gholder.txtAmount.setText(invoice.getPayable().toString() + "/-");
        }
        if (invoice.getStatus() != null && BillConstants.INVOICE_STATUS_PAID.equals(invoice.getStatus())) {
            //gholder.statusImg.setImageResource(R.drawable.ic_invoice_paid);
            showHelpfulToast(gholder.statusImg, "Invoice Paid");
            gholder.statusImg.setVisibility(View.VISIBLE);
            gholder.txt_paid_on.setVisibility(View.VISIBLE);
            gholder.txt_paid_on.setText("Paid on \n" + CommonUtils.convertDate(invoice.getPaidDate(), BillConstants.DATE_FORMAT_DISPLAY_NO_YEAR_TIME));
            gholder.btnPay.setVisibility(View.GONE);

        } else {
            gholder.statusImg.setVisibility(View.GONE);
            gholder.txt_paid_on.setVisibility(View.GONE);
        }

        gholder.bind(invoice);

    }

    private void payInvoice(BillInvoice invoice) {
        if (invoice == null || invoice.getId() == null || customer == null) {
            return;
        }
        invoice.setStatus(BillConstants.INVOICE_STATUS_PAID);
        invoice.setPaymentMode(BillConstants.PAYMENT_OFFLINE);
        invoice.setPaidAmount(invoice.getPayable());
        invoice.setPaymentMedium("CASH");
        //invoice.setPaidDate(new Date());
        //}
        BillServiceRequest request = new BillServiceRequest();
        request.setInvoice(invoice);
        //request.setUser(customer);
        pDialog = Utility.getProgressDialogue("Saving..", activity);
        StringRequest myReq = ServiceUtil.getStringRequest("updateBusinessInvoice", invoicePaidResponse(invoice), ServiceUtil.createMyReqErrorListener(pDialog, activity), request);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(myReq);
    }

    private Response.Listener<String> invoicePaidResponse(final BillInvoice invoice) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    //invoice.setStatus(BillConstants.PAID);
                    notifyDataSetChanged();
                    Utility.createAlert(activity, "Invoice paid successfully!", "Done");
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(activity, serviceResponse.getResponse(), "Error");
                }

            }

        };
    }

    private void deleteInvoice(BillInvoice invoice) {
        if (invoice == null || invoice.getId() == null || customer == null) {
            return;
        }
        invoice.setStatus(BillConstants.INVOICE_STATUS_DELETED);
        //}
        BillServiceRequest request = new BillServiceRequest();
        request.setInvoice(invoice);
        //request.setUser(customer);
        pDialog = Utility.getProgressDialogue("Deleting..", activity);
        StringRequest myReq = ServiceUtil.getStringRequest("updateBusinessInvoice", invoiceDeletionResponse(invoice), ServiceUtil.createMyReqErrorListener(pDialog, activity), request);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(myReq);
    }

    private Response.Listener<String> invoiceDeletionResponse(final BillInvoice invoice) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    items.remove(invoice);
                    notifyDataSetChanged();
                    Utility.createAlert(activity, "Invoice deleted successfully!", "Done");
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(activity, serviceResponse.getResponse(), "Error");
                }

            }

        };


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.row_purchase_invoice_details, parent, false);
        return new ViewHolder1(item);
    }

    class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtMonths, txtAmount, btnPay, txt_paid_on;
        private ImageView statusImg;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtMonths = itemView.findViewById(R.id.txt_months);
            txtAmount = itemView.findViewById(R.id.txt_amount);
            txt_paid_on = itemView.findViewById(R.id.txt_paid_on);
            statusImg = itemView.findViewById(R.id.status_img);
            btnPay = itemView.findViewById(R.id.btn_pay_purchase_invoice);
        }

        @Override
        public void onClick(View view) {

        }

        public void bind(final BillInvoice invoice) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*if (BillConstants.FRAMEWORK_GENERIC.equals(Utility.getFramework(user))) {
                        customer.setCurrentInvoice(invoice);
                        //Utility.nextFragment((FragmentActivity) activity, GenericCreateBill.newInstance(customer));
                        activity.startActivity(Utility.nextIntent(activity, GenericCreateBillActivity.class, true, customer, Utility.CUSTOMER_KEY));
                    } else {
                        //TODO Change to activity
                        //Utility.nextFragment((FragmentActivity) activity, FragmentEditInvoice.newInstance(customer, invoice));
                        Intent intent = Utility.nextIntent(activity, EditInvoiceActivity.class, true, invoice, Utility.INVOICE_KEY);
                        intent.putExtra(Utility.CUSTOMER_KEY, ServiceUtil.toJson(customer));
                        activity.startActivity(intent);
                    }*/

                    //customer.setCurrentInvoice(invoice);
                    activity.startActivity(Utility.nextIntent(activity, DistributorBillDetailsActivity.class, true, invoice, Utility.INVOICE_KEY));

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final CharSequence[] items = {"Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    builder.setTitle("Action for invoice #" + invoice.getId());
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            System.out.println("Selected option => " + item);
                            deleteInvoice(invoice);

                        }
                    });
                    builder.show();
                    return true;

                }
            });

            btnPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CharSequence[] items = {"Pay with Cash", "Pay with UPI"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    builder.setTitle("Pay invoice #" + invoice.getId());
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            System.out.println("Selected option => " + item);
                            if (item == 0) {
                                //Cash payment
                                payInvoice(invoice);
                            } else {
                                //Toast.makeText(activity, "UPI mode not available yet", Toast.LENGTH_LONG).show();
                                startUpi(invoice);
                            }

                        }
                    });
                    builder.show();

                }
            });

        }
    }

    private void showHelpfulToast(ImageView imageView, final String message) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public void startUpi(BillInvoice invoice) {
        try {
            String url = Utility.getUPIString(customer, invoice);
            if (TextUtils.isEmpty(url)) {
                Utility.createAlert(activity, "Invalid UPI address. Please ask payee to update a valid UPI address.", "Error");
                return;
            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            currentInvoice = invoice;
            System.out.println("Current Invoice is set => " + currentInvoice);
            Intent chooser = Intent.createChooser(intent, "Pay with...");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                activity.startActivityForResult(chooser, 1, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Utility.createAlert(activity, "No UPI application found! Please download a UPI app like BHIM or Google Pay", "Error");
        }

    }

    public BillInvoice getCurrentInvoice() {
        return currentInvoice;
    }
}
