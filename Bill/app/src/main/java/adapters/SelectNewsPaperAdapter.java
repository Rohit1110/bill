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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reso.bill.R;

import java.util.List;

import model.ListTwo;
import model.Listone;
import model.SelectNewsPapers;

public class SelectNewsPaperAdapter extends RecyclerView.Adapter<SelectNewsPaperAdapter.RecViewHolder> {

    private List<SelectNewsPapers> list;

    public SelectNewsPaperAdapter(List<SelectNewsPapers> list) {
        this.list = list;
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.select_newpaper_row, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, final int position) {
        final SelectNewsPapers movie = list.get(position);

        holder.bind(movie,position);



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

        private ImageView imageView;
        private CheckBox checkBox;
        private TextView txtNewsPaperInfo;
        private View subItem;

        public RecViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.img_select_newspaper);
            checkBox=(CheckBox) itemView.findViewById(R.id.chkbox_selectnewspaper);
            subItem = itemView.findViewById(R.id.sub_item);
            txtNewsPaperInfo=(TextView)itemView.findViewById(R.id.sub_item_newspaperinfo);
            /*title = itemView.findViewById(R.id.item_title);
            genre = itemView.findViewById(R.id.sub_item_genre);
            year = itemView.findViewById(R.id.sub_item_year);
            subItem = itemView.findViewById(R.id.sub_item);*/
        }

        private void bind(SelectNewsPapers movie, final int pos) {
            boolean expanded = movie.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            imageView.setBackgroundResource(movie.getImgUrl());
            txtNewsPaperInfo.setText(movie.getNewsPaperInfo());
            checkBox.setChecked(list.get(pos).isSelected());
            checkBox.setTag(list.get(pos));

            //checkBox.setText(movie.getAddress());
            //year.setText("Year: " + movie.getYear());

            /* holder.contactCheck.setOnCheckedChangeListener(null);
        holder.contactCheck.setChecked(item.get(position).isSelected());

        holder.contactCheck.setTag(item.get(position));
        holder.contactCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                InviteContact contact = (InviteContact) cb.getTag();
                contact.setSelected(cb.isChecked());

                item.get(pos).setSelected(cb.isChecked());

                /*Toast.makeText(
                        v.getContext(),
                        "Clicked on Checkbox: " + cb.getText() + " is "
                                + cb.isChecked(), Toast.LENGTH_LONG).show();*/
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    SelectNewsPapers contact = (SelectNewsPapers) cb.getTag();
                    contact.setSelected(cb.isChecked());

                    list.get(pos).setSelected(cb.isChecked());

                Toast.makeText(
                        view.getContext(),
                        "Clicked on Checkbox: " + cb.getText() + " is "
                                + cb.isChecked(), Toast.LENGTH_LONG).show();

                }



            });
        }
    }
}
