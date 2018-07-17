package com.reso.bill.components;

import android.view.View;

/**
 * Created by Rohit on 1/5/2018.
 */

public interface ClickListener {

    public void onClick (View view, int position);

    public void onLongClick (View view, int position);
}
