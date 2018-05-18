package adapters;

import android.app.Activity;
import android.app.FragmentManager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reso.bill.ChangeQuantity;
import com.reso.bill.DaysToDeliver;
import com.reso.bill.DisContinue;
import com.reso.bill.PauseTime;
import com.reso.bill.R;

import java.util.ArrayList;
import java.util.List;

import model.CustomerSubscription;
import model.ListTwo;

/**
 * Created by Rohit on 5/10/2018.
 */

public class CustomerSubcriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    List<CustomerSubscription> items = new ArrayList<CustomerSubscription>();
Context activity;

    public CustomerSubcriptionAdapter(List<CustomerSubscription> items,Context activity) {
        this.items = items;
        this.activity=activity;
    }
    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView txtnewpaperqty;
        private ImageView newspaperimg;
        private  TextView txtweekdays;
        private ImageView imgpause,imgdiscontinue;
        //View appointmentindicator;

        public ViewHolder1(View itemView) {
            super(itemView);
            //txtName=(TextView)itemView.findViewById(R.id.txt_name);
            newspaperimg=(ImageView)itemView.findViewById(R.id.img_newspaper);
            txtnewpaperqty=(TextView)itemView.findViewById(R.id.txt_newspaperqty);
            txtweekdays=(TextView)itemView.findViewById(R.id.txt_weekdays);
            imgpause=(ImageView)itemView.findViewById(R.id.img_paus);
            imgdiscontinue=(ImageView)itemView.findViewById(R.id.img_cross);


        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.cust_subscription_row, parent, false);
        return new ViewHolder1(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CustomerSubscription customerSubscription =(CustomerSubscription)items.get(position);
        final ViewHolder1 gholder = (ViewHolder1) holder;
        gholder.newspaperimg.setBackgroundResource(customerSubscription.getImgUrl());
        gholder.txtnewpaperqty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeQuantity fragment = new ChangeQuantity();
                FragmentTransaction ft =((AppCompatActivity) activity).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();

            }
        });
        gholder.txtweekdays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DaysToDeliver fragment = new DaysToDeliver();
                FragmentTransaction ft = ((AppCompatActivity) activity).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();
            }
        });
        gholder.imgpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PauseTime fragment = new PauseTime();
                FragmentTransaction ft = ((AppCompatActivity) activity).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();

            }
        });
        gholder.imgdiscontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisContinue fragment = new DisContinue();
                FragmentTransaction ft = ((AppCompatActivity) activity).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.commit();

            }
        });
        //gholder.txtpcs.setText(customerSubscription.getNewspaperpcs());
        //qImageView.setBackgroundResource(R.drawable.thumbs_down);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
