package adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.reso.bill.generic.GenericAddProductActivity;
import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.domain.BillServiceRequest;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.BillConstants;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class AddNewspaperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<BillItem> items = new ArrayList<BillItem>();
    private Context activity;
    private List<BillItem> contactList;
    private ProgressDialog pDialog;
    private Activity parentActivity;
    private BillBusiness userBusiness;

    public AddNewspaperAdapter(List<BillItem> items, Context context) {
        this.activity = context;
        this.items = items;
        this.contactList = items;
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    public void setUserBusiness(BillBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BillItem item = items.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;

        Integer itemId = item.getId();
        if (item.getParentItem() != null) {
            itemId = item.getParentItem().getId();
            Picasso.get().load(Utility.getItemImageURL(itemId)).into(gholder.image);
        } else {
            Picasso.get().load(Utility.getBusinessItemImageURL(itemId)).into(gholder.image);
        }

        if (item.getParentItem() == null) {
            gholder.name.setText(item.getName());
        } else {
            gholder.name.setText(item.getParentItem().getName());
        }
        if (item.getPrice() != null && item.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            gholder.price.setText(item.getPrice().toString());
            gholder.price.setVisibility(View.VISIBLE);
        }

        if (item.getParentItem() == null) {
            gholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Utility.nextFragment((FragmentActivity) activity, AddProduct.newInstance(item));
                    parentActivity.startActivity(Utility.nextIntent(parentActivity, GenericAddProductActivity.class, true, item, Utility.ITEM_KEY));
                }
            });

            gholder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final CharSequence[] items = {"Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    builder.setTitle("Actions for " + item.getName());
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            deleteItem(item);
                        }
                    });
                    builder.show();
                    return true;

                }
            });
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.add_newspaper_row, parent, false);
        return new ViewHolder1(item);
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        ImageView iv;
        private TextView price;


        public ViewHolder1(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_addnewspaper);
            //price =  itemView.findViewById(R.id.txt_newspaper_rates);
            name = itemView.findViewById(R.id.txt_add_subscription_item_name);
            price = itemView.findViewById(R.id.txt_item_price);
            //iv =  itemView.findViewById(R.id.btn_paus);

        }
    }

    private void deleteItem(BillItem item) {
        item.setStatus(BillConstants.STATUS_DELETED);
        BillServiceRequest request = new BillServiceRequest();
        List<BillItem> itemsList = new ArrayList<>();
        itemsList.add(item);
        request.setItems(itemsList);
        request.setBusiness(userBusiness);
        pDialog = Utility.getProgressDialogue("Deleting..", parentActivity);
        StringRequest myReq = ServiceUtil.getStringRequest("updateBusinessItem", deleteResponse(item), ServiceUtil.createMyReqErrorListener(pDialog, parentActivity), request);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(myReq);
    }

    private Response.Listener<String> deleteResponse(final BillItem item) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("## response:" + response);
                pDialog.dismiss();

                BillServiceResponse serviceResponse = (BillServiceResponse) ServiceUtil.fromJson(response, BillServiceResponse.class);
                if (serviceResponse != null && serviceResponse.getStatus() == 200) {
                    items.remove(item);
                    notifyDataSetChanged();
                    Utility.createAlert(activity, "Product deleted successfully!", "Done");
                } else {
                    System.out.println("Error .." + serviceResponse.getResponse());
                    Utility.createAlert(activity, serviceResponse.getResponse(), "Error");
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
