package edu.rosehulman.breadcrumb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by turnerrs on 1/25/2015.
 */
public class TripHistory extends Fragment implements View.OnClickListener {
    private ArrayList<Trip> trips;
    private TripDataAdapter dataAdapter;
    private TripRowAdapter rowAdapter;
    private final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    public static String KEY_ID = "KEY_ID";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_history, container, false);

        dataAdapter = new TripDataAdapter(getActivity());
        dataAdapter.open();

        trips = new ArrayList<Trip>();
        trips = dataAdapter.getAllTrips(trips);

        rowAdapter = new TripRowAdapter(view.getContext(), trips);

        ListView lsTrips = (ListView)view.findViewById(R.id.trip_history);
        lsTrips.setAdapter(rowAdapter);

        lsTrips.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Log.d(Constants.LOG_NAME, "Pressed trip from date: " + SIMPLE_FORMAT.format(trips.get(position).getStartDate().getTime()));
                Intent tripIntent = new Intent(view.getContext(), TripSummaryActivity.class);
                tripIntent.putExtra(KEY_ID, trips.get(position).getId());
                startActivity(tripIntent);
            }
        });

        lsTrips.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(Constants.LOG_NAME, "Delete Press on " + SIMPLE_FORMAT.format(trips.get(position).getStartDate().getTime()));
                showDelete(trips.get(position));
                return false;
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {

    }

    public void showDelete(final Trip trip){
        DialogFragment df = new DialogFragment(){
            @Override
            public Dialog onCreateDialog(Bundle b){
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getActivity());
                deleteBuilder.setTitle(R.string.delete_trip_title);
                deleteBuilder.setMessage(getString(R.string.delete_trip_message, SIMPLE_FORMAT.format(trip.getStartDate().getTime())));
                deleteBuilder.setNegativeButton(android.R.string.cancel, null);
                deleteBuilder.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                Log.d(Constants.LOG_NAME, SIMPLE_FORMAT.format(trip.getStartDate().getTime()) + " was deleted.");
                                dataAdapter.deleteTrip(trip);
                                trips.remove(trip);
                                rowAdapter.notifyDataSetChanged();
                            }
                        });
                return deleteBuilder.create();
            }
        };
        df.show(getFragmentManager(), "");
    }
}
