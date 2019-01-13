package adapters.generic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.volley.Response;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceResponse;
import com.rns.web.billapp.service.util.CommonUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class GenericInvoiceItemsDisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    List<BillItem> items = new ArrayList<>();
    private BillUser customer;
    private ProgressDialog pDialog;
    private BillUser selectedUser;
    private TextView billAmount;
    private boolean disableEdit;

    public void setDisableEdit(boolean disableEdit) {
        this.disableEdit = disableEdit;
    }

    public GenericInvoiceItemsDisplayAdapter(List<BillItem> items, Activity activity, TextView billAmount) {
        this.items = items;
        this.activity = activity;
        this.billAmount = billAmount;
        calculateBillTotal();
        //this.customer = customer;
    }

    public void updateSearchList(List<BillItem> filterList) {
        this.items = filterList;
        notifyDataSetChanged();
        calculateBillTotal();
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
        notifyDataSetChanged();
        calculateBillTotal();
    }

    class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName, txtAmount, txtQuantity;
        //private ImageView imgDelete;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_update_inv_item_name);
            txtAmount = (TextView) itemView.findViewById(R.id.txt_update_inv_item_amount);
            //imgDelete = (ImageView) itemView.findViewById(R.id.img_gn_delete_inv_item);
            txtQuantity = (TextView) itemView.findViewById(R.id.txt_update_inv_item_quantity);

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
        View item = inflater.inflate(R.layout.row_gn_invoice_item_display, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BillItem item = (BillItem) items.get(position);
        final ViewHolder1 parentView = (ViewHolder1) holder;

        parentView.txtName.setText(Utility.getItemName(item));
        parentView.txtAmount.setText(Utility.getDecimalString(item.getPrice()));
        parentView.txtQuantity.setText(CommonUtils.getStringValue(item.getQuantity()));
        /*parentView.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(item);
            }
        });

        if(disableEdit) {
            parentView.imgDelete.setVisibility(View.GONE);
        }*/


    }

    private void calculateBillTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (BillItem item : items) {
            if (item.getPrice() != null) {
                total = total.add(item.getPrice());
            }
        }
        billAmount.setText("Total = " + Utility.getDecimalString(total));
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    private void deleteItem(BillItem item) {
        items.remove(item);
        notifyDataSetChanged();
        calculateBillTotal();
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
                        items.remove(selectedUser);
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

    public static void setNumberPickerTextColor(NumberPicker numberPicker, int color) {

        try {
            Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
            selectorWheelPaintField.setAccessible(true);
            ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
        } catch (NoSuchFieldException e) {
            Log.w("ERROR", e);
        } catch (IllegalAccessException e) {
            Log.w("ERROR", e);
        } catch (IllegalArgumentException e) {
            Log.w("ERROR", e);
        }

        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) ((EditText) child).setTextColor(color);
        }
        numberPicker.invalidate();
    }

}
