package adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillServiceResponse;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.ServiceUtil;
import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class PurchaseInvoiceItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<BillItem> items = new ArrayList<>();
    private BillUser customer;
    private ProgressDialog pDialog;
    private BillUser selectedUser;
    private TextView billAmount;
    private boolean disableEdit;
    private BillInvoice returnInvoice;
    private BigDecimal soldAmount;
    private boolean showOptions;
    private TextView purchaseTotal;
    private TextView returnTotal;
    private Map<Integer, TextWatcher> returnQuantityWatchers = new HashMap<>();
    private Map<Integer, TextWatcher> purchaseQuantityWatchers = new HashMap<>();


    public void setPurchaseTotal(TextView purchaseTotal) {
        this.purchaseTotal = purchaseTotal;
    }

    public void setReturnTotal(TextView returnTotal) {
        this.returnTotal = returnTotal;
    }

    public void setShowOptions(boolean showOptions) {
        this.showOptions = showOptions;
        notifyDataSetChanged();
    }

    public boolean isShowOptions() {
        return showOptions;
    }

    public BillInvoice getReturnInvoice() {
        return returnInvoice;
    }

    public BigDecimal getSoldAmount() {
        return soldAmount;
    }

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

    public void setReturnInvoice(BillInvoice returnInvoice) {
        this.returnInvoice = returnInvoice;
    }

    private BillItem findReturnItem(BillItem distributorItem) {
        try {
            if (returnInvoice == null || returnInvoice.getInvoiceItems() == null | returnInvoice.getInvoiceItems().size() == 0 || distributorItem.getParentItemId() == null) {
                return null;
            }
            for (BillItem item : returnInvoice.getInvoiceItems()) {
                if (item.getParentItemId() != null && item.getParentItemId().intValue() == distributorItem.getParentItemId().intValue()) {
                    return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName, txtAmount; //
        private EditText txtQuantity, txtCostPrice;
        private EditText txtReturnQuantity, txtReturnCostPrice;
        private ConstraintLayout moreOptions;
        //private MyCustomEditTextListener returnQuantityWatcher;
        //private MyCustomEditTextListener purchaseQuantityWatcher;
        private boolean loadedOnce = false;

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

            //txtReturnAmount = (EditText) itemView.findViewById(R.id.txt_update_return_item_amount);
            txtReturnQuantity = (EditText) itemView.findViewById(R.id.txt_update_return_item_quantity);
            txtReturnCostPrice = (EditText) itemView.findViewById(R.id.txt_purchase_item_return_price);

            moreOptions = (ConstraintLayout) itemView.findViewById(R.id.layout_purchase_more_options);

            /*returnQuantityWatcher = new MyCustomEditTextListener("RQ");
            purchaseQuantityWatcher = new MyCustomEditTextListener("PQ");

            txtQuantity.addTextChangedListener(purchaseQuantityWatcher);
            txtReturnQuantity.addTextChangedListener(returnQuantityWatcher);*/


            txtQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    try {
                        final BillItem item = items.get(getAdapterPosition());
                        if (TextUtils.isEmpty(charSequence)) {
                            item.setQuantity(BigDecimal.ZERO);
                        } else {
                            item.setQuantity(new BigDecimal(charSequence.toString()));
                        }
                        calculatePurchaseItemCost(charSequence, txtCostPrice, txtAmount, txtReturnCostPrice, txtReturnQuantity);
                        calculateBillTotal();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            txtReturnQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        BillItem item = items.get(getAdapterPosition());
                        BillItem returnItem = findReturnItem(item);
                        if (TextUtils.isEmpty(charSequence)) {
                            returnItem.setQuantity(BigDecimal.ZERO);
                        } else {
                            returnItem.setQuantity(new BigDecimal(charSequence.toString()));
                        }
                        calculateReturnItemCost(charSequence, txtReturnCostPrice, txtAmount, calculateTotalCp(item));
                        calculateBillTotal();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            txtCostPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        BillItem item = items.get(getAdapterPosition());
                        if (TextUtils.isEmpty(charSequence)) {
                            return;
                        }
                        item.setPrice(new BigDecimal(charSequence.toString()));
                        calculatePurchaseItemCost(charSequence, txtQuantity, txtAmount, txtReturnCostPrice, txtReturnQuantity);
                        calculateBillTotal();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            /*parentView.purchaseQuantityWatcher.updatePosition(position);
            parentView.returnQuantityWatcher.updatePosition(position);*/

        /*if (returnQuantityWatchers.get(item.getId()) == null) {
            TextWatcher returnWatcher = getReturnWatcher(item, parentView);
            returnQuantityWatchers.put(item.getId(), returnWatcher);
            parentView.txtReturnQuantity.addTextChangedListener(returnWatcher);
            System.out.println("added watcher ... ");
        } else {
            System.out.println("Has watcher ...");
        }*/


            txtReturnCostPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (TextUtils.isEmpty(charSequence)) {
                            return;
                        }
                        BillItem item = items.get(getAdapterPosition());
                        BillItem returnItem = findReturnItem(item);
                        returnItem.setPrice(new BigDecimal(charSequence.toString()));
                        calculateReturnItemCost(charSequence, txtReturnQuantity, txtAmount, calculateTotalCp(item));
                        calculateBillTotal();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            //Focus listeners
            txtReturnQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    onFocusChanged(hasFocus, txtReturnQuantity);

                }
            });

            txtReturnCostPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    onFocusChanged(hasFocus, txtReturnCostPrice);
                }
            });

            txtQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    onFocusChanged(hasFocus, txtQuantity);

                }
            });

            txtCostPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    onFocusChanged(hasFocus, txtCostPrice);
                }
            });

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

        BillItem returnItem = findReturnItem(item);

        BigDecimal price = Utility.getCostPrice(item);

        //Loaded once

        if (item.getQuantity() == null) {
            item.setQuantity(new BigDecimal(0));
        }
        parentView.txtCostPrice.setText(Utility.getDecimalString(price));
        parentView.txtName.setText(Utility.getItemName(item));
        parentView.txtQuantity.setText(Utility.getDecimalString(item.getQuantity()));


        if (returnItem != null) {
            parentView.txtReturnCostPrice.setText(Utility.getDecimalString(returnItem.getCostPrice()));
            parentView.txtReturnQuantity.setText(Utility.getDecimalString(returnItem.getQuantity()));
            //calculateReturnItemCost();
        }

        /*imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(item);
            }
        });*/


        //Dynamic values
        if (price != null) {
            BigDecimal costPrice = price.multiply(item.getQuantity());
            if (returnItem != null && returnItem.getQuantity() != null) {
                BigDecimal returnItemCp = Utility.getCostPrice(returnItem);
                if (returnItemCp != null) {
                    costPrice = costPrice.subtract(returnItemCp.multiply(returnItem.getQuantity()));
                }
            }
            parentView.txtAmount.setText(Utility.getDecimalString(costPrice));
        }


        //Following things Load only once


        //parentView.txtAmount.setText("0");


        /*if (purchaseQuantityWatchers.get(item.getId()) == null) {
            TextWatcher quantityWatcher = getQuantityWatcher(item, parentView);
            parentView.txtQuantity.addTextChangedListener(quantityWatcher);
            purchaseQuantityWatchers.put(item.getId(), quantityWatcher);
            System.out.println("Added Quantity watcher ... ");
        } else {
            System.out.println("Has Quantity watcher ...");
        }*/


        if (disableEdit) {
            //parentView.imgDelete.setVisibility(View.GONE);
            parentView.txtCostPrice.setEnabled(false);
            parentView.txtQuantity.setEnabled(false);
        }


        if (!isShowOptions()) {
            parentView.moreOptions.setVisibility(View.GONE);
        } else {
            parentView.moreOptions.setVisibility(View.VISIBLE);
        }
        System.out.println("..... Binding " + parentView.txtName.getText() + " ... " + parentView.txtAmount.getText());
        parentView.loadedOnce = true;
    }

    @NonNull
    private TextWatcher getQuantityWatcher(final BillItem item, final ViewHolder1 parentView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (TextUtils.isEmpty(charSequence)) {
                        item.setQuantity(BigDecimal.ZERO);
                    } else {
                        item.setQuantity(new BigDecimal(charSequence.toString()));
                    }
                    calculatePurchaseItemCost(charSequence, parentView.txtCostPrice, parentView.txtAmount, parentView.txtReturnCostPrice, parentView.txtReturnQuantity);
                    calculateBillTotal();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }


    private void onFocusChanged(boolean hasFocus, EditText editText) {
        try {
            if (hasFocus) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    BigDecimal val = new BigDecimal(editText.getText().toString());
                    if (BigDecimal.ZERO.compareTo(val) == 0) {
                        editText.setText("");
                    }
                }
            } else {
                if (TextUtils.isEmpty(editText.getText())) {
                    editText.setText("0");
                }
            }
        } catch (Exception e) {

        }
    }

    private void calculatePurchaseItemCost(CharSequence charSequence, EditText amount, TextView txtTotal, EditText returnAmount, EditText returnQuantity) {
        try {
            if (TextUtils.isEmpty(charSequence)) {
                txtTotal.setText("0");
                return;
            }
            BigDecimal price = Utility.getDecimal(amount);
            BigDecimal quantityVal = new BigDecimal(charSequence.toString());
            if (price != null && price.compareTo(BigDecimal.ZERO) > 0 && quantityVal != null) {
                BigDecimal total = price.multiply(quantityVal);
                //item.setPrice(total);
                BigDecimal returnVal = BigDecimal.ZERO;
                BigDecimal returnAmountVal = Utility.getDecimal(returnAmount);
                BigDecimal returnQuantityVal = Utility.getDecimal(returnQuantity);
                if (returnAmountVal != null && returnQuantityVal != null) {
                    returnVal = returnAmountVal.multiply(returnQuantityVal);
                }
                txtTotal.setText(Utility.getDecimalString(total.subtract(returnVal)));
            }
            calculateBillTotal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateReturnItemCost(CharSequence charSequence, EditText amount, TextView txtTotal, BigDecimal totalCp) {
        try {
            if (TextUtils.isEmpty(charSequence)) {
                //txtTotal.setText("0");
                return;
            }
            BigDecimal price = Utility.getDecimal(amount);
            BigDecimal quantityVal = new BigDecimal(charSequence.toString());
            if (price != null && price.compareTo(BigDecimal.ZERO) > 0 && quantityVal != null) {
                BigDecimal total = price.multiply(quantityVal);
                //item.setPrice(total);
                //BigDecimal totalCp = Utility.getDecimal(txtTotal);

                if (totalCp != null) {
                    txtTotal.setText(Utility.getDecimalString(totalCp.subtract(total)));
                }

            }
            //billAmount.setText(Utility.getDecimal(billAmount).subtract());
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
        soldAmount = BigDecimal.ZERO;
        BigDecimal totalPurchase = BigDecimal.ZERO;
        BigDecimal totalReturns = BigDecimal.ZERO;

        for (BillItem item : items) {
            BigDecimal cp = calculateTotalCp(item);
            total = total.add(cp);
            totalPurchase = totalPurchase.add(cp);
            BillItem returnItem = findReturnItem(item);
            BigDecimal currentProfit = null;
            BigDecimal returnLoss = null;
            if (item.getQuantity() != null && item.getUnitSellingPrice() != null) {
                currentProfit = item.getQuantity().multiply(item.getUnitSellingPrice());
            }
            if (returnItem != null && returnItem.getQuantity() != null) {
                /*if (currentProfit != null) {
                    currentProfit = currentProfit.subtract(returnItem.getQuantity());
                }*/
                BigDecimal returnCp = Utility.getCostPrice(returnItem);
                if (returnCp != null) {
                    BigDecimal returnTotal = returnCp.multiply(returnItem.getQuantity());
                    totalReturns = totalReturns.add(returnTotal);
                    total = total.subtract(returnTotal);
                }
                if (returnItem.getUnitSellingPrice() != null) {
                    returnLoss = returnItem.getUnitSellingPrice().multiply(returnItem.getQuantity());
                }
            }
            if (currentProfit != null) {
                if (returnLoss != null) {
                    currentProfit = currentProfit.subtract(returnLoss);
                }
                soldAmount = soldAmount.add(currentProfit);
            }
        }
        billAmount.setText(Utility.getDecimalString(total));
        purchaseTotal.setText("Purchase total: " + Utility.getDecimalString(totalPurchase) + " /-");
        returnTotal.setText("Return total: " + Utility.getDecimalString(totalReturns) + " /-");
    }

    private BigDecimal calculateTotalCp(BillItem item) {
        try {
            return Utility.getCostPrice(item).multiply(item.getQuantity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    private class MyCustomEditTextListener implements TextWatcher {
        private Integer position;
        private String type;

        public void updatePosition(int position) {
            this.position = position;
        }

        public MyCustomEditTextListener(String type) {
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            //mDataset[position] = charSequence.toString();
            try {
                if (position == null) {
                    return;
                }
                BillItem item = items.get(position);
                System.out.println("Found item =>" + item.getId() + " AND " + item.getQuantity() + " val " + charSequence);
                BillItem returnItem = findReturnItem(item);
                if ("RQ".equals(type)) {
                    if (TextUtils.isEmpty(charSequence)) {
                        returnItem.setQuantity(BigDecimal.ZERO);
                    } else {
                        returnItem.setQuantity(new BigDecimal(charSequence.toString()));
                    }
                } else if ("PQ".equals(type)) {
                    if (TextUtils.isEmpty(charSequence)) {
                        item.setQuantity(BigDecimal.ZERO);
                    } else {
                        item.setQuantity(new BigDecimal(charSequence.toString()));
                    }
                }
                //calculateReturnItemCost(charSequence, parentView.txtReturnCostPrice, parentView.txtAmount, calculateTotalCp(item));
                calculateBillTotal();
                //notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

}
