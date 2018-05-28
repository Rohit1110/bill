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

import model.ListTwo;

/**
 * Created by Rohit on 5/10/2018.
 */

public class ListTwoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
List<ListTwo> items = new ArrayList<ListTwo>();

    public ListTwoAdapter(List<ListTwo> items) {
        this.items = items;
    }
    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView txtpcs;
        private ImageView newspaperimg;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            //txtName=(TextView)itemView.findViewById(R.id.txt_name);
            newspaperimg=(ImageView)itemView.findViewById(R.id.img_newspaper);
            txtpcs=(TextView)itemView.findViewById(R.id.txt_newspaper_pcs);


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
        gholder.newspaperimg.setBackgroundResource(listTwo.getImgUrl());
        gholder.txtpcs.setText(listTwo.getNewspaperpcs());
        //qImageView.setBackgroundResource(R.drawable.thumbs_down);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
