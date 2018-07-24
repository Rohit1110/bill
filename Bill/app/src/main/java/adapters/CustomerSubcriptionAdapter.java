package adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reso.bill.ChangeQuantity;
import com.reso.bill.DaysToDeliver;
import com.reso.bill.PauseTime;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.util.ArrayList;
import java.util.List;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class CustomerSubcriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity parent;
    private TextView txtName;
    private List<BillItem> items = new ArrayList<BillItem>();
    private Context activity;
    private BillUser customer;
    private ProgressDialog pDialog;

    public CustomerSubcriptionAdapter(List<BillItem> items, Context activity, BillUser customer, Activity parent) {
        this.items = items;
        this.activity = activity;
        this.customer = customer;
        this.parent = parent;
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView txtnewpaperqty;
        private ImageView newspaperimg;
        private TextView txtweekdays;
        private ImageView imgpause, imgdiscontinue;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_customer_subscription_item_name);
            newspaperimg = (ImageView) itemView.findViewById(R.id.img_customer_subscription_item_icon);
            txtnewpaperqty = (TextView) itemView.findViewById(R.id.txt_newspaperqty);
            txtweekdays = (TextView) itemView.findViewById(R.id.txt_weekdays);
            imgpause = (ImageView) itemView.findViewById(R.id.img_paus);
            imgdiscontinue = (ImageView) itemView.findViewById(R.id.img_cross);


        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.cust_subscription_row, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BillItem customerSubscription = (BillItem) items.get(position);
        final ViewHolder1 gholder = (ViewHolder1) holder;
        //gholder.newspaperimg.setImageURI(Utility.getItemImageURL(customerSubscription.getParentItemId()));

        String weekDays = customerSubscription.getWeekDays();
        if (weekDays == null) {
            weekDays = customerSubscription.getParentItem().getWeekDays();
        }

        if (weekDays != null && weekDays.contains(",")) {
            String[] days = weekDays.split(",");
            String txtWeekDays = gholder.txtweekdays.getText().toString();
            for (String day : days) {
                if (day.contains("1")) {
                    txtWeekDays = txtWeekDays.replace("su", "<font color='" + Utility.COLOR_BLUE + "'>su</font>");
                } else if (day.contains("2")) {
                    txtWeekDays = txtWeekDays.replace("mo", "<font color='" + Utility.COLOR_BLUE + "'>mo</font>");
                } else if (day.contains("3")) {
                    txtWeekDays = txtWeekDays.replace("tu", "<font color='" + Utility.COLOR_BLUE + "'>tu</font>");
                } else if (day.contains("4")) {
                    txtWeekDays = txtWeekDays.replace("we", "<font color='" + Utility.COLOR_BLUE + "'>we</font>");
                } else if (day.contains("5")) {
                    txtWeekDays = txtWeekDays.replace("th", "<font color='" + Utility.COLOR_BLUE + "'>th</font>");
                } else if (day.contains("6")) {
                    txtWeekDays = txtWeekDays.replace("fr", "<font color='" + Utility.COLOR_BLUE + "'>fr</font>");
                } else if (day.contains("7")) {
                    txtWeekDays = txtWeekDays.replace("sa", "<font color='" + Utility.COLOR_BLUE + "'>sa</font>");
                }
            }
            System.out.println("Week Days ==>" + txtWeekDays);
            gholder.txtweekdays.setText(Html.fromHtml(txtWeekDays));
        }


        Utility.downloadImage(gholder.newspaperimg, activity, Utility.getItemImageURL(customerSubscription.getParentItemId()));
        gholder.txtnewpaperqty.setText("0");

        if (customerSubscription.getQuantity() != null) {
            gholder.txtnewpaperqty.setText(customerSubscription.getQuantity().toString());
        }

        gholder.txtnewpaperqty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment((FragmentActivity) activity, ChangeQuantity.newInstance(customer, customerSubscription));

            }
        });
        gholder.txtweekdays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment((FragmentActivity) activity, DaysToDeliver.newInstance(customer, customerSubscription));
            }
        });
        gholder.imgpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.nextFragment((FragmentActivity) activity, PauseTime.newInstance(customer, customerSubscription));

            }
        });
        gholder.imgdiscontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity);
                alertDialogBuilder.setTitle("Suspend subscription");
                alertDialogBuilder.setMessage("Are you sure you want to suspend this subscription?");
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        suspend(customerSubscription);
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        if (customerSubscription.getParentItem() != null) {
            txtName.setText(customerSubscription.getParentItem().getName());
        } else {
            txtName.setText(customerSubscription.getName());
        }

        //gholder.txtpcs.setText(customerSubscription.getNewspaperpcs());
        //qImageView.setBackgroundResource(R.drawable.thumbs_down);

    }

    private void suspend(BillItem customerSubscription) {
        BillServiceRequest request = new BillServiceRequest();
        request.setUser(customer);
        customerSubscription.setStatus("D");
        request.setItem(customerSubscription);
        pDialog = Utility.getProgressDialogue("Saving..", parent);
        StringRequest myReq = ServiceUtil.getStringRequest("updateCustomerItem", createMyReqSuccessListener(customerSubscription), ServiceUtil.createMyReqErrorListener(pDialog, parent), request);
        RequestQueue queue = Volley.newRequestQueue(parent);
        queue.add(myReq);
    }



    private Response.Listener<String> createMyReqSuccessListener(final BillItem customerSubscription) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    Utility.createAlert(activity, "Subscription suspended successfully!", "Done");
                    int position = Utility.indexOf(items, customerSubscription);
                    if(position >= 0) {
                        items.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, items.size());
                        System.out.println("Item Removed ..." + position);
                    }


                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(parent, serviceResponse.getResponse(), "Error");
                }


            }

        };
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }
}
