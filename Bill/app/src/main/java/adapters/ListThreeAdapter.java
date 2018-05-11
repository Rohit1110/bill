package adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reso.bill.R;

import java.util.ArrayList;
import java.util.List;

import model.ListThree;
import model.Listone;

/**
 * Created by Rohit on 5/10/2018.
 */

public class ListThreeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
List<ListThree> items = new ArrayList<>();

    public ListThreeAdapter(List<ListThree> items) {
        this.items = items;
    }
    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView txtMonths,txtamount,txtstatus;
        ImageView iv;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtMonths=(TextView)itemView.findViewById(R.id.txt_months);
            txtamount=(TextView)itemView.findViewById(R.id.txt_amount);
            txtstatus= (TextView)itemView.findViewById(R.id.status);

        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.list_three_row, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListThree listthree =(ListThree)items.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;
        gholder.txtMonths.setText(listthree.getMonths());
        gholder.txtamount.setText(listthree.getAmount());
        gholder.txtstatus.setText(listthree.getStatus());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
