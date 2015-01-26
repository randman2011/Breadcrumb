package edu.rosehulman.breadcrumb;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by turnerrs on 1/25/2015.
 */
public class TripHistory extends Fragment implements View.OnClickListener {
    private ArrayList<Trip> trips;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_trip_history, container, false);

        trips = new ArrayList<Trip>();
        trips.add(new Trip(Calendar.getInstance()));
        Calendar newDate = Calendar.getInstance();
        newDate.add(Calendar.DAY_OF_MONTH, -2);
        trips.add(new Trip(newDate));

        ListView lsTrips = (ListView)view.findViewById(R.id.trip_history);
        lsTrips.setAdapter(new TripRowAdapter(view.getContext(), trips));

        lsTrips.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Log.d(Constants.constants.LOG_NAME, trips.get(position).getStartDate().toString());
                Intent tripIntent = new Intent(view.getContext(), TripSummaryActivity.class);
                startActivity(tripIntent);
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
