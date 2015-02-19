package edu.rosehulman.breadcrumb;

import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class TripSummaryActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private TripDataAdapter dataAdapter;
    private Trip trip;
    private MapFragment mapFragment;
    private PolylineOptions lineOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapFragment = MapFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.map_container_summary, mapFragment);
        ft.commit();

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

        lineOptions = new PolylineOptions().width(3).color(Color.RED);
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

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated..
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded(MapFragment m) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(LatLng coordinate, String title) {
        mMap.addMarker(new MarkerOptions().position(coordinate).title(title));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            Log.d(Constants.LOG_NAME, "Map initiated and set to HYBRID");

            if (trip != null) {
                ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
                for (GPSCoordinate coord : trip.getCoordinates()) {
                    coordinates.add(new LatLng(coord.getLatitude(), coord.getLongitude()));
                }

                mMap.addPolyline(lineOptions.addAll(coordinates));

                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinates.get(0), Constants.MAP_ZOOM);
                mMap.animateCamera(yourLocation);

            }
        }
    }
}
