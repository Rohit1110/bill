/*
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

import model.Listone;

*/
/**
 * Created by Rohit on 5/10/2018.
 *//*


public class ListOneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
List<Listone> items = new ArrayList<Listone>();

    public ListOneAdapter(List<Listone> items) {
        this.items = items;
    }
    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView txtName;
        //private TextView time, name;
        View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            txtName=(TextView)itemView.findViewById(R.id.txt_name);

        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.list_one_row, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Listone listone =(Listone)items.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;
        gholder.txtName.setText(listone.getName());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
*/


package adapters;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reso.bill.CustomerProfileFragment;
import com.reso.bill.R;

import java.util.List;

import model.Listone;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.RecViewHolder> {

    private List<Listone> list;
    Activity context;

    public CustomerListAdapter(List<Listone> list,Activity context) {
        this.list = list;
        this.context=context;
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.cust_list_row, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, final int position) {
        final Listone movie = list.get(position);

        holder.bind(movie);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean expanded = movie.isExpanded();
                movie.setExpanded(!expanded);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class RecViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private TextView txtAddress,txtViewprofile;
        private TextView year;
        private View subItem;

        public RecViewHolder(View itemView) {
            super(itemView);
            txtName=(TextView)itemView.findViewById(R.id.txt_name);
            txtAddress=(TextView)itemView.findViewById(R.id.sub_item_address);
            subItem = itemView.findViewById(R.id.sub_item);
            txtViewprofile=(TextView)itemView.findViewById(R.id.sub_item_viewprofile);
            /*title = itemView.findViewById(R.id.item_title);
            genre = itemView.findViewById(R.id.sub_item_genre);
            year = itemView.findViewById(R.id.sub_item_year);
            subItem = itemView.findViewById(R.id.sub_item);*/
        }

        private void bind(Listone movie) {
            boolean expanded = movie.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            txtName.setText(movie.getName());
            txtAddress.setText(movie.getAddress());
            //year.setText("Year: " + movie.getYear());
            txtViewprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
