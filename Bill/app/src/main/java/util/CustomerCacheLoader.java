package util;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.rns.web.billapp.service.bo.domain.BillUser;
import com.rns.web.billapp.service.domain.BillCache;

import java.util.ArrayList;
import java.util.List;

import adapters.CustomerListAdapter;
import model.BillCustomer;

public class CustomerCacheLoader extends AsyncTask<Void, Void, Void> {

    private RecyclerView recyclerView;
    private Activity ctx;
    private List<BillCustomer> list;
    private CustomerListAdapter adapter;
    private TextView totalCustomersCount;

    public CustomerCacheLoader(RecyclerView recyclerView, Activity ctx) {

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<BillUser> customers = BillCache.getCustomers(ctx);
        list = new ArrayList<>();
        if (customers != null && customers.size() > 0) {
            for (BillUser user : customers) {
                list.add(new BillCustomer(user));

            }
        }

        //adapter = new CustomerListAdapter(list, ctx, user);
        //recyclerView.setHasFixedSize(true);
        /*recyclerView.setItemViewCacheSize(50);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);*/


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        recyclerView.setAdapter(adapter);
        totalCustomersCount.setText("Total customers - " + list.size());
    }
}
