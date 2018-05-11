package adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reso.bill.R;

import java.util.ArrayList;
import java.util.List;

import model.ListTwo;
import model.Listone;

/**
 * Created by Rohit on 5/10/2018.
 */

public class ListTwoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
List<ListTwo> items = new ArrayList<ListTwo>();

    public ListTwoAdapter(List<ListTwo> items) {
        this.items = items;
    }
    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView txtName;
        //private TextView time, name;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtName=(TextView)itemView.findViewById(R.id.txt_name);

        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.list_two_row, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListTwo listTwo =(ListTwo)items.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;
        gholder.txtName.setText(listTwo.getName());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
