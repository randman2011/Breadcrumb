package edu.rosehulman.breadcrumb;

import android.content.Context;
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
    private int mNumRows;
    private ArrayList<Trip> trips;
    private final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);


    public TripRowAdapter(Context context, ArrayList<Trip> trips){
        this.mContext = context;
        this.mNumRows = trips.size();
        this.trips = trips;
    }
    @Override
    public int getCount() {
        return mNumRows;
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

        view.setDateText(SIMPLE_FORMAT.format(trips.get(position).getStartDate().getTime()) + "  : " + trips.get(position).getId());

        String distanceText = mContext.getString(R.string.distance_formater, trips.get(position).getDistance());
        distanceText += " " + mContext.getString(R.string.km);
        //TODO: Add case for miles
        view.setDistanceText(distanceText);
        return view;
    }

    public void remove(){
        mNumRows--;
    }
}
