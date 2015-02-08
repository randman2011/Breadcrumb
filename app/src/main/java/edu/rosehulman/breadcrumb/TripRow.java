package edu.rosehulman.breadcrumb;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by watterlm on 1/26/2015.
 */
public class TripRow extends RelativeLayout {
    private TextView dateView;
    private TextView distanceView;

    public TripRow(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.trips_list_item, this);

        dateView = (TextView)findViewById(R.id.text_date);
        distanceView = (TextView)findViewById(R.id.text_distance);
    }

    public void setDateText(String date){
        dateView.setText(date);
    }

    public void setDistanceText(String distance){
        distanceView.setText(distance);
    }
}
