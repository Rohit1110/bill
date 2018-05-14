package com.reso.bill;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class VendorRegistration extends AppCompatActivity {
    FloatingActionButton rgister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vender_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.vtoolbar);
        setSupportActionBar(toolbar);
        rgister=(FloatingActionButton)findViewById(R.id.fab_vregister);


       /* getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);*/
      /*  if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Drawable upArrow = getResources().getDrawable(R.mipmap.backarrow);
        /*upArrow.setColorFilter(Color.parseColor(""), PorterDuff.Mode.SRC_ATOP);*/
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        rgister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(getApplicationContext(),VendorDashBoard.class);
               startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
