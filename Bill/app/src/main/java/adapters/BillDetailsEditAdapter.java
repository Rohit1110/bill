package adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.reso.bill.R;

import java.util.ArrayList;
import java.util.List;

import model.Bill_details;

/**
 * Created by Rohit on 5/10/2018.
 */

public class BillDetailsEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
List<Bill_details> items = new ArrayList<>();

    public BillDetailsEditAdapter(List<Bill_details> items) {
        this.items = items;
    }
    class ViewHolder1 extends RecyclerView.ViewHolder {
        private EditText txtdate;
        ImageView iv;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtdate=(EditText)itemView.findViewById(R.id.txt_date);

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Bill_details listthree =(Bill_details)items.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;
        gholder.txtdate.setText(listthree.getDate());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
