package edu.rosehulman.breadcrumb;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by watterlm on 1/26/2015.
 */
public class TripRowAdapter extends BaseAdapter {
    private Context mContext;
    private int mNumRows;
    private ArrayList<Trip> trips;

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

        view.setDateText("00/00/00");
        view.setDistanceeText("00.0 Miles");
        return view;
    }
}
