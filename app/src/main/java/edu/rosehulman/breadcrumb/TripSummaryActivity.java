package edu.rosehulman.breadcrumb;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class TripSummaryActivity extends ActionBarActivity {
    private TripDataAdapter dataAdapter;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        long tripId = intent.getLongExtra(TripHistory.KEY_ID, 0);

        dataAdapter = new TripDataAdapter(this);
        dataAdapter.open();
        trip = dataAdapter.getTrip(tripId);

        SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        ArrayList<String> tripStrings = new ArrayList<String>();
        tripStrings.add(getString(R.string.trip_summary_date, simpleFormat.format(trip.getStartDate().getTime())));
        tripStrings.add(getString(R.string.trip_summary_duration, trip.calculateDuration()));
        tripStrings.add(String.format(getString(R.string.trip_summary_distance), trip.getDistance()," km"));
        //TODO:Add case for miles
        tripStrings.add(String.format(getString(R.string.trip_summary_average_speed), trip.calculateAverageSpeed(), " kmph"));
        // TODO: Add case for miles

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tripStrings);
        ((ListView)findViewById(R.id.trip_summary_listView)).setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
