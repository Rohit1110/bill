package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.reso.bill.PauseTime;
import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import util.Utility;

/**
 * Created by Rohit on 5/10/2018.
 */

public class AddNewspaperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<BillItem> items = new ArrayList<BillItem>();
    private Context activity;
    private List<BillItem> contactList;

    public AddNewspaperAdapter(List<BillItem> items, Context context) {
        this.activity = context;
        this.items = items;
        this.contactList=items;
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        //private TextView price;
        private ImageView image;
        private TextView name;
        ImageView iv;

        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.img_addnewspaper);
            //price = (TextView) itemView.findViewById(R.id.txt_newspaper_rates);
            name = (TextView) itemView.findViewById(R.id.txt_add_subscription_item_name);
            //iv = (ImageView) itemView.findViewById(R.id.btn_paus);

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.add_newspaper_row, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BillItem item = (BillItem) items.get(position);
        ViewHolder1 gholder = (ViewHolder1) holder;
        //gholder.image.setBackgroundResource();
        //gholder.image.setImageURI(Utility.getItemImageURL(item));

        //Utility.downloadImage(gholder.image, activity, Utility.getItemImageURL(item.getId()));

        Integer itemId = item.getId();
        if (item.getParentItem() != null) {
            itemId = item.getParentItem().getId();
        }
        Picasso.get().load(Utility.getItemImageURL(itemId)).into(gholder.image);

        if (item.getParentItem() == null) {
            gholder.name.setText(item.getName());
        } else {
            gholder.name.setText(item.getParentItem().getName());
        }
       /* gholder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PauseTime fragment = new PauseTime();
                Utility.nextFragment((FragmentActivity) activity, fragment);
            }
        });*/
        //gholder.price.setText(item.getPrice());
        //qImageView.setBackgroundResource(R.drawable.thumbs_down);

    }


    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }





}
