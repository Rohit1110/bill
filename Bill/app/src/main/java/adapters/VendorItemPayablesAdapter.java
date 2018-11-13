package adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class VendorItemPayablesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<BillUser> users = new ArrayList<BillUser>();

    public VendorItemPayablesAdapter(List<BillUser> items) {
        this.users = items;
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView txtpcs;
        //private ImageView newspaperimg;
        private TextView txtName;
        private TextView txtCp;
        private TextView txtSp;
        private TextView items;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            //txtName=(TextView)itemView.findViewById(R.id.txt_name);
            txtpcs = (TextView) itemView.findViewById(R.id.txt_daily_pcs);
            txtName = (TextView) itemView.findViewById(R.id.txt_daily_payable_business_name);
            txtCp = (TextView) itemView.findViewById(R.id.txt_daily_business_payable);
            txtSp = (TextView) itemView.findViewById(R.id.txt_daily_business_profit);
            items = (TextView) itemView.findViewById(R.id.txt_payable_items);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.row_daily_payable, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BillUser user = (BillUser) users.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;

        BillInvoice invoice = user.getCurrentInvoice();
        if (invoice != null) {
            gholder.txtSp.setText("Get  " + CommonUtils.getStringValue(invoice.getAmount()) + " /- ");
            gholder.txtCp.setText("Pay  " + CommonUtils.getStringValue(invoice.getPayable()) + " /-");
        }
        if (user.getCurrentBusiness() != null) {
            gholder.txtName.setText(user.getCurrentBusiness().getName());
            if (user.getCurrentBusiness().getItems() != null) {
                StringBuilder itemList = new StringBuilder();
                for (BillItem item : user.getCurrentBusiness().getItems()) {
                    itemList.append(CommonUtils.getStringValue(item.getQuantity())).append(" ").append(Utility.getItemName(item)).append(" | ");
                }
                gholder.items.setText(itemList);
            } else {
                gholder.items.setText("");
            }

        }

    }

    @Override
    public int getItemCount() {
        if (users == null) {
            return 0;
        }
        return users.size();
    }
}
