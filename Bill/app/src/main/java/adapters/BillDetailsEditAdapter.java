package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohit on 5/10/2018.
 */

public class BillDetailsEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private EditText amount;
    private List<BillItem> items = new ArrayList<>();
    private Context ctx;
    private RecyclerView listView;

    public BillDetailsEditAdapter(List<BillItem> items, Context ctx, EditText amount) {
        this.items = items;
        this.ctx = ctx;
        this.amount = amount;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.listView = recyclerView;
    }


    class ViewHolder1 extends RecyclerView.ViewHolder {
        private EditText txtqty, txtAmount;
        private TextView txtpaper;
        //private ImageView statusImg;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtpaper = (TextView) itemView.findViewById(R.id.txt_paper);
            txtqty = (EditText) itemView.findViewById(R.id.txt_paperqty);
            txtAmount = (EditText) itemView.findViewById(R.id.txt_paperbill);
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
        gholder.txtpaper.setText(item.getParentItem().getName());
        if (item.getQuantity() != null) {
            gholder.txtqty.setText(item.getQuantity().toString());
        }
        if (item.getPrice() != null) {
            gholder.txtAmount.setText(item.getPrice().toString());
            gholder.txtAmount.addTextChangedListener(watcher);
        }


    }

    private void textEnabler(TextView txt, BillItem item, String type) {
        txt.setEnabled(!txt.isEnabled());
        if (!txt.isEnabled()) {
            if ("Q".equals(type)) {
                item.setQuantity(new BigDecimal(txt.getText().toString()));
            } else {
                item.setPrice(new BigDecimal(txt.getText().toString()));
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
                if(items != null && items.size() > 0) {
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    for(int position = 0; position < items.size(); position++) {
                        ViewHolder1 gholder = (ViewHolder1) listView.findViewHolderForAdapterPosition(position);
                        if(gholder != null && !TextUtils.isEmpty(gholder.txtAmount.getText())) {
                            totalAmount = totalAmount.add(new BigDecimal(gholder.txtAmount.getText().toString()));
                        }
                    }
                    amount.setText(totalAmount.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error in edit bill row ..");
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


}
