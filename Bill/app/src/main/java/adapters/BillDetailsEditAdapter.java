package adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class BillDetailsEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private EditText billAmount;
    private List<BillItem> items = new ArrayList<>();
    private Context ctx;
    private RecyclerView listView;
    private boolean invoicePaid;

    public BillDetailsEditAdapter(List<BillItem> items, Context ctx, EditText amount) {
        this.items = items;
        this.ctx = ctx;
        this.billAmount = amount;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }

    public void setInvoicePaid(boolean invoicePaid) {
        this.invoicePaid = invoicePaid;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.listView = recyclerView;
    }


    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView txtqty, txtAmount;
        private TextView txtpaper;
        private ImageView delete;
        //private ImageView statusImg;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtpaper = (TextView) itemView.findViewById(R.id.txt_paper);
            txtqty = (TextView) itemView.findViewById(R.id.txt_paperqty);
            txtAmount = (TextView) itemView.findViewById(R.id.txt_paperbill);
            delete = (ImageView) itemView.findViewById(R.id.img_delete_inv_item);
            //statusImg = (ImageView) itemView.findViewById(R.id.edit_bill);
            /*txtqty.setFocusable(true);
            txtAmount.setFocusable(true);
            txtqty.setEnabled(false);
            txtAmount.setEnabled(false);*/
            txtqty.setText("0");
            txtAmount.setText("0");
        }


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.bill_detail_edit_row, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final BillItem item = (BillItem) items.get(position);
        final ViewHolder1 gholder = (ViewHolder1) holder;
        gholder.txtpaper.setText(Utility.getItemName(item));
        if (item.getQuantity() != null) {
            gholder.txtqty.setText(Utility.getDecimalString(item.getQuantity()));
        }
        if (item.getPrice() != null) {
            gholder.txtAmount.setText(Utility.getDecimalString(item.getPrice()));
            gholder.txtAmount.addTextChangedListener(watcher);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (invoicePaid) {
                    return;
                }

                final Dialog dialog = new Dialog(ctx);
                dialog.setContentView(R.layout.layout_edit_bill_item);
                dialog.setTitle(null);

                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.txt_dialog_invoice_item_name);
                text.setText(Utility.getItemName(item));
                /*ImageView image = (ImageView) dialog.findViewById(R.id.image);
                image.setImageResource(R.drawable.ic_launcher);*/

                final EditText quantity = (EditText) dialog.findViewById(R.id.et_bill_item_quantity);
                quantity.setText("0");
                final EditText amount = (EditText) dialog.findViewById(R.id.et_dialog_invoice_item_amount_multiple);
                amount.setText("0");

                if (item.getQuantity() != null) {
                    quantity.setText(Utility.getDecimalString(item.getQuantity()));
                }
                if (item.getPrice() != null) {
                    amount.setText(Utility.getDecimalString(item.getPrice()));
                }


                Button saveBillItem = (Button) dialog.findViewById(R.id.btn_dialog_save_invoice_item_multiple);
                // if button is clicked, close the custom dialog
                saveBillItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!Utility.validateDecimal(quantity)) {
                            quantity.setError("Invalid quantity!");
                            return;
                        }

                        if (!Utility.validateDecimal(amount)) {
                            quantity.setError("Invalid billAmount!");
                            return;
                        }

                        item.setQuantity(Utility.getDecimal(quantity));
                        item.setPrice(Utility.getDecimal(amount));
                        gholder.txtqty.setText(quantity.getText().toString());
                        gholder.txtAmount.setText(amount.getText().toString());

                        //Calculate total billAmount
                        calculateBillTotal();
                        dialog.dismiss();
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
        });

        gholder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(item);
            }
        });

    }

    private void calculateBillTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (BillItem item : items) {
            if (item.getPrice() != null) {
                total = total.add(item.getPrice());
            }
        }
        billAmount.setText(Utility.getDecimalString(total));
    }

    private void textEnabler(TextView txt, BillItem item, String type) {
        txt.setEnabled(!txt.isEnabled());
        if (!txt.isEnabled()) {
            if ("Q".equals(type)) {
                item.setQuantity(Utility.getDecimal(txt));
            } else {
                item.setPrice(Utility.getDecimal(txt));
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try {
                /*if (users != null && users.size() > 0) {
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    for (int position = 0; position < users.size(); position++) {
                        ViewHolder1 gholder = (ViewHolder1) listView.findViewHolderForAdapterPosition(position);
                        if (gholder != null && !TextUtils.isEmpty(gholder.txtAmount.getText())) {
                            totalAmount = totalAmount.add(new BigDecimal(gholder.txtAmount.getText().toString()));
                        }
                    }
                    billAmount.setText(totalAmount.toString());
                }*/
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error in edit bill row ..");
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public BigDecimal getPrice(int position) {
        ViewHolder1 gholder = (ViewHolder1) listView.findViewHolderForAdapterPosition(position);
        if (gholder != null && gholder.txtAmount != null) {
            return Utility.getDecimal(gholder.txtAmount);
        }
        return null;
    }

    public BigDecimal getQuantity(int position) {
        ViewHolder1 gholder = (ViewHolder1) listView.findViewHolderForAdapterPosition(position);
        if (gholder != null && gholder.txtqty != null) {
            return Utility.getDecimal(gholder.txtqty);
        }
        return null;
    }

    private void deleteItem(BillItem item) {
        items.remove(item);
        notifyDataSetChanged();
        calculateBillTotal();
    }


}
