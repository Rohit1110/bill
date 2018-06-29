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
import com.squareup.picasso.Picasso;

import java.util.List;

import model.BillItemHolder;
import util.Utility;

public class DatePickerAdapter extends RecyclerView.Adapter<DatePickerAdapter.RecViewHolder> {

    private List<BillItemHolder> list;
    private TextView selectedItems;

    public DatePickerAdapter(List<BillItemHolder> list) {
        this.list = list;
    }

    public void setSelectedItems(TextView selectedItems) {
        this.selectedItems = selectedItems;
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_datepicker_row, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, final int position) {
        final BillItemHolder movie = list.get(position);

        holder.bind(movie, position);


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
        private TextView txtName;
        private View subItem;

        public RecViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_select_newspaper);
            checkBox = (CheckBox) itemView.findViewById(R.id.chkbox_selectnewspaper);
            subItem = itemView.findViewById(R.id.customer_details);
            txtNewsPaperInfo = (TextView) itemView.findViewById(R.id.sub_item_newspaperinfo);
            txtName = (TextView) itemView.findViewById(R.id.sub_item_name);

        }

        private void bind(BillItemHolder mainItem, final int pos) {
            boolean expanded = mainItem.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            //imageView.setImageURI(Uri.parse(ServiceUtil.ADMIN_URL + "getParentItemImage/" + ));

            Picasso.get().load(Utility.getItemImageURL(mainItem.getItem().getId())).into(imageView);

            txtNewsPaperInfo.setText(mainItem.getItem().getDescription());
            txtName.setText(mainItem.getItem().getName());
            checkBox.setChecked(list.get(pos).isSelected());
            checkBox.setTag(list.get(pos));

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    BillItemHolder selectedItem = (BillItemHolder) cb.getTag();
                    selectedItem.setSelected(cb.isChecked());
                    list.get(pos).setSelected(cb.isChecked());
                    Toast.makeText(view.getContext(), "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked(), Toast.LENGTH_SHORT).show();
                    if(selectedItem.isSelected()) {
                        selectedItems.setText(selectedItems.getText() + selectedItem.getItem().getName() + ",");
                    } else {
                        selectedItems.setText(selectedItems.getText().toString().replace(selectedItem.getItem().getName() + ",", ""));
                    }
                }


            });
        }
    }
}
