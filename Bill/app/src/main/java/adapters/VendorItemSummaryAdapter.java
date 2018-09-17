package adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class VendorItemSummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<BillItem> items = new ArrayList<BillItem>();

    public VendorItemSummaryAdapter(List<BillItem> items) {
        this.items = items;
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView txtpcs;
        private ImageView newspaperimg;
        private TextView txtName;
        private TextView txtCp;
        private TextView txtSp;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            //txtName=(TextView)itemView.findViewById(R.id.txt_name);
            newspaperimg = (ImageView) itemView.findViewById(R.id.img_daily_summary_item_icon);
            txtpcs = (TextView) itemView.findViewById(R.id.txt_daily_pcs);
            txtName = (TextView) itemView.findViewById(R.id.txt_daily_summary_item_name);
            txtCp = (TextView) itemView.findViewById(R.id.txt_daily_payment);
            txtSp = (TextView) itemView.findViewById(R.id.txt_daily_profit);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.row_daily_summary_item, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BillItem item = (BillItem) items.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;

        Integer itemId = item.getId();
        if (item.getParentItem() != null) {
            itemId = item.getParentItem().getId();
        }
        Picasso.get().load(Utility.getItemImageURL(itemId)).into(gholder.newspaperimg);
        if (item.getQuantity() != null) {
            String qty = item.getQuantity().toBigInteger().toString();
            //gholder.txtpcs.setText(item.getQuantity().toString());
            gholder.txtpcs.setText("Qty  " + qty);
        }

        if(item.getCostPrice() != null && item.getUnitCostPrice() != null) {
            gholder.txtCp.setText("Pay  " + item.getCostPrice() + " @ " + item.getUnitCostPrice() + " /unit");
        }

        if(item.getPrice() != null && item.getUnitSellingPrice() != null) {
            gholder.txtSp.setText("Get  " + item.getPrice() + " @ " + item.getUnitSellingPrice() + " /unit");
        }

        String name = item.getName();
        if(item.getParentItem() != null) {
            name = item.getParentItem().getName();
        }
        gholder.txtName.setText(name);

    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }
}
