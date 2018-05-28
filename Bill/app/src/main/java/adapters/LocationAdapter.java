package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.reso.bill.R;
import com.rns.web.billapp.service.bo.domain.BillLocation;

import java.util.List;

/**
 * Created by Admin on 25/05/2018.
 */

public class LocationAdapter extends ArrayAdapter<BillLocation> {


    private Activity context;
    private List<BillLocation> locations;
    private ViewHolder holder;
    private String selectedLocations = "";

    public LocationAdapter(Context ctx, int txtViewResourceId, List<BillLocation> locations, Activity activity) {
        super(ctx, txtViewResourceId, locations);
        this.context = activity;
        this.locations = locations;
    }

    public void setLocations(List<BillLocation> locations) {
        this.locations = locations;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        /*View mySpinner = inflater.inflate(R.layout.spinner_multi_select, parent, false);
        TextView main_text = (TextView) mySpinner.findViewById(R.id.txt_area_name);
        main_text.setText(locations.get(position).getName());*/

        if (convertView == null) {
            holder = new ViewHolder();
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_multi_select, null, false);
            holder.locationName = convertView.findViewById(R.id.txt_area_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BillLocation selected = locations.get(position);
        //if(position != 0) {
        holder.locationName.setText(selected.getName());
        if(position != 0) {
            selected.setStatus("A");
            selectedLocations = selectedLocations + selected.getName() + ",";
        }

        System.out.println("Selected string =>" + selectedLocations);

        //}


        return convertView;
    }

    private class ViewHolder {
        private TextView locationName;
    }

}
