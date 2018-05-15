package com.reso.bill;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VendorDashBoard extends AppCompatActivity {
    LinearLayout layout_total_orders,layout_manage_newspapers,layout_accounting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_dash_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.backarrow);
        layout_total_orders=(LinearLayout)findViewById(R.id.layout_totalorder);
        layout_manage_newspapers=(LinearLayout)findViewById(R.id.layout_managenewspaper);
        layout_accounting=(LinearLayout)findViewById(R.id.layout_accounts);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VendorDashBoard.this,Main3Activity.class);
                startActivity(i);
            }
        });

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

       layout_total_orders.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(VendorDashBoard.this,AddNewspapers.class);
               startActivity(i);
           }
       });
        layout_manage_newspapers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VendorDashBoard.this,AddNewspapers.class);
                startActivity(i);
            }
        });
        layout_accounting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VendorDashBoard.this,AddNewspapers.class);
                startActivity(i);
            }
        });
    }

}
