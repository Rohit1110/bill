package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
    private List<BillItem> items = new ArrayList<>();
    private Context ctx;

    public BillDetailsEditAdapter(List<BillItem> items, Context ctx) {
        this.items = items;
        this.ctx = ctx;
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        private EditText txtqty, txtAmount;
        private TextView txtpaper;
        private ImageView iv;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtpaper = (TextView) itemView.findViewById(R.id.txt_paper);
            txtqty = (EditText) itemView.findViewById(R.id.txt_paperqty);
            txtAmount = (EditText) itemView.findViewById(R.id.txt_paperbill);
            iv = (ImageView) itemView.findViewById(R.id.edit_bill);
            txtqty.setFocusable(true);
            txtAmount.setFocusable(true);
            txtqty.setEnabled(false);
            txtAmount.setEnabled(false);
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
        }

        /*gholder.txtqty.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                textEnabler(gholder.txtqty, item, "Q");
                return true;
            }
        });

        gholder.txtqty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textEnabler(gholder.txtqty, item, "Q");
            }
        });

        gholder.txtAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textEnabler(gholder.txtAmoun);
                //gholder.txtpaper.setTextColor();
            }
        }); */

        gholder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textEnabler(gholder.txtqty, item, "Q");
                textEnabler(gholder.txtAmount, item, "A");
                if(gholder.txtqty.isEnabled()) {
                    gholder.txtpaper.setTextColor(ctx.getResources().getColor(R.color.buttonColor));
                } else {
                    gholder.txtpaper.setTextColor(ctx.getResources().getColor(R.color.accent));
                }
                /*if (gholder.txtqty.isEnabled() == false) {
                    gholder.txtqty.setEnabled(true);
                    //gholder.txtqty.setFocusable(true);
                    gholder.txtpaper.setFocusable(false);
                } else {
                    gholder.txtqty.setEnabled(false);
                    gholder.txtqty.setFocusable(false);
                }

                if (gholder.txtAmount.isEnabled() == false) {
                    gholder.txtAmount.setEnabled(true);
                    gholder.txtpaper.setFocusable(false);
                } else {
                    gholder.txtAmount.setEnabled(false);
                    gholder.txtAmount.setFocusable(false);
                }*/
                //gholder.txtAmount.setEnabled(true);

            }
        });

    }

    private void textEnabler(TextView txt, BillItem item, String type) {
        txt.setEnabled(!txt.isEnabled());
        if(!txt.isEnabled()) {
            if("Q".equals(type)) {
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
}
