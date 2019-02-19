package adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class PurchaseInvoiceItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    public PurchaseInvoiceItemsAdapter(List<BillItem> items, Activity activity, TextView billAmount) {
        this.items = items;
        this.activity = activity;
        this.billAmount = billAmount;
        //calculateBillTotal();
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
        private TextView txtName, txtAmount; //
        private EditText txtQuantity, txtCostPrice;
        //private ImageView imgDelete;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_update_inv_item_name);
            txtAmount = (TextView) itemView.findViewById(R.id.txt_update_inv_item_amount);
            //imgDelete = (ImageView) itemView.findViewById(R.id.img_gn_delete_inv_item);
            txtQuantity = (EditText) itemView.findViewById(R.id.txt_update_inv_item_quantity);
            txtCostPrice = (EditText) itemView.findViewById(R.id.txt_purchase_item_cost_price);
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
        View item = inflater.inflate(R.layout.row_purchase_invoice_item_update, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BillItem item = (BillItem) items.get(position);
        final ViewHolder1 parentView = (ViewHolder1) holder;

        parentView.txtName.setText(Utility.getItemName(item));

        parentView.txtQuantity.setText(Utility.getDecimalString(item.getQuantity()));
        /*parentView.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(item);
            }
        });*/

        if (item.getQuantity() == null) {
            item.setQuantity(new BigDecimal(0));
        }

        if (item.getPrice() != null) {
            parentView.txtCostPrice.setText(Utility.getDecimalString(item.getPrice()));
            parentView.txtAmount.setText(Utility.getDecimalString(item.getPrice().multiply(item.getQuantity())));
            System.out.println("The item price set as =>" + item.getPrice());
        } else if (item.getCostPrice() != null) {
            parentView.txtCostPrice.setText(Utility.getDecimalString(item.getCostPrice()));
            parentView.txtAmount.setText(Utility.getDecimalString(item.getCostPrice().multiply(item.getQuantity())));
            System.out.println("The item price set as =>" + item.getCostPrice());
        } else if (item.getParentItem() != null) {
            parentView.txtCostPrice.setText(Utility.getDecimalString(item.getParentItem().getCostPrice()));
            parentView.txtAmount.setText("0");
            System.out.println("The item price set as =>" + item.getCostPrice());
        }

        parentView.txtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    item.setQuantity(new BigDecimal(charSequence.toString()));
                    calculatePurchaseItemCost(charSequence, parentView.txtCostPrice, item, parentView.txtAmount);
                    calculateBillTotal();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        parentView.txtCostPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    item.setPrice(new BigDecimal(charSequence.toString()));
                    calculatePurchaseItemCost(charSequence, parentView.txtQuantity, item, parentView.txtAmount);
                    calculateBillTotal();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if (disableEdit) {
            //parentView.imgDelete.setVisibility(View.GONE);
            parentView.txtCostPrice.setEnabled(false);
            parentView.txtQuantity.setEnabled(false);
        }

        if (!disableEdit) {
            /*parentView.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.generic_dialog_edit_purchase_item);
                    dialog.setTitle("Edit details");

                    // set the custom dialog components - text, image and button
                    TextView text = (TextView) dialog.findViewById(R.id.txt_dialog_invoice_item_name);
                    text.setText(Utility.getItemName(item));
                *//*ImageView image = (ImageView) dialog.findViewById(R.id.image);
                image.setImageResource(R.drawable.ic_launcher);*//*
                    final TextView itemPayable = (TextView) dialog.findViewById(R.id.txt_purchase_item_total_cost);

                    final EditText amount = (EditText) dialog.findViewById(R.id.et_dialog_invoice_item_amount_multiple);
                    //amount.setText("0");
                    amount.setText(parentView.txtCostPrice.getText());

                    final EditText quantity = (EditText) dialog.findViewById(R.id.et_dialog_purchase_item_quantity);
                    quantity.setText(parentView.txtQuantity.getText());

                    TextWatcher purchaseItemWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            calculatePurchaseItemCost(charSequence, amount, itemPayable, item);

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    };

                    quantity.addTextChangedListener(purchaseItemWatcher);
                    amount.addTextChangedListener(purchaseItemWatcher);

                    if (item.getQuantity() != null) {
                        quantity.setText(Utility.getDecimalString(item.getQuantity()));
                    }


                    Button saveBillItem = (Button) dialog.findViewById(R.id.btn_dialog_save_invoice_item_multiple);
                    // if button is clicked, close the custom dialog
                    saveBillItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if (!Utility.validateDecimal(amount)) {
                                amount.setError("Invalid amount!");
                                return;
                            }

                            BigDecimal quantityVal = Utility.getDecimal(quantity);
                            item.setQuantity(quantityVal);
                            item.setPrice(Utility.getDecimal(amount));
                            //parentView.txtQuantity.setText(quantity.getValue());
                            //parentView.txtAmount.setText(amount.getText().toString());

                            //Calculate total billAmount
                            calculateBillTotal();
                            dialog.dismiss();
                            notifyDataSetChanged();
                        }
                    });

                    Button cancel = (Button) dialog.findViewById(R.id.btn_dialog_cancel_invoice_item_multiple);
                    // if button is clicked, close the custom dialog
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });*/

        }

        System.out.println("..... Binding " + parentView.txtName.getText() + " ... " + parentView.txtAmount.getText());
    }

    private void calculatePurchaseItemCost(CharSequence charSequence, EditText amount, BillItem item, TextView txtTotal) {
        try {
            if (TextUtils.isEmpty(charSequence)) {
                return;
            }
            BigDecimal price = Utility.getDecimal(amount);
            BigDecimal quantityVal = new BigDecimal(charSequence.toString());
            if (price != null && price.compareTo(BigDecimal.ZERO) > 0 && quantityVal != null) {
                BigDecimal total = price.multiply(quantityVal);
                //item.setPrice(total);
                txtTotal.setText(Utility.getDecimalString(total));
            }
            calculateBillTotal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculatePurchaseItemCost(CharSequence charSequence, EditText amount, TextView itemPayable, BillItem item) {
        try {
            if (TextUtils.isEmpty(charSequence)) {
                return;
            }
            BigDecimal price = Utility.getDecimal(amount);
            BigDecimal quantityVal = new BigDecimal(charSequence.toString());
            if (price != null && price.compareTo(BigDecimal.ZERO) > 0 && quantityVal != null) {
                BigDecimal total = price.multiply(quantityVal);
                itemPayable.setText("Paying " + total + " /- for " + Utility.getItemName(item));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void calculateBillTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (BillItem item : items) {
            if (item.getPrice() != null && item.getQuantity() != null) {
                total = total.add(item.getPrice().multiply(item.getQuantity()));
            }
        }
        billAmount.setText(Utility.getDecimalString(total));
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
