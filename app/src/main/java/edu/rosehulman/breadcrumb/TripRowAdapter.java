package edu.rosehulman.breadcrumb;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by watterlm on 1/26/2015.
 */
public class TripRowAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Trip> trips;
    private final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("MM/dd/yyyy h:mm aaa", Locale.US);
    private boolean useMetricUnits;


    public TripRowAdapter(Context context, ArrayList<Trip> trips){
        this.mContext = context;
        this.trips = trips;
        this.useMetricUnits = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getBoolean(App.getContext().getString(R.string.pref_key_metric_units), false);
    }
    @Override
    public int getCount() {
        return trips.size();
    }

    @Override
    public Object getItem(int position) {
        return trips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TripRow view = null;
        if (convertView == null){
            view = new TripRow(mContext);
        }else{
            view = (TripRow) convertView;
        }

        view.setDateText(SIMPLE_FORMAT.format(trips.get(position).getStartDate().getTime()));

        String distanceText = mContext.getString(R.string.distance_formater, trips.get(position).getDistance());
        distanceText += " " + ((useMetricUnits) ? mContext.getString(R.string.km) : mContext.getString(R.string.miles));
        view.setDistanceText(distanceText);
        return view;
    }
}
