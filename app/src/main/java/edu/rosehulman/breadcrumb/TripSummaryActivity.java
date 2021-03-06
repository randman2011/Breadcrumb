package edu.rosehulman.breadcrumb;

import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class TripSummaryActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, View.OnClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private TripDataAdapter dataAdapter;
    private Trip trip;
    private MapFragment mapFragment;
    private PolylineOptions lineOptions;
    private FrameLayout mapContainer;
    private boolean useMetricUnits;
    private LatLng centerLocation;
    private LatLngBounds bounds;
    private ArrayList<LatLng> coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapFragment = MapFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.map_container_summary, mapFragment);
        ft.commit();

        mapContainer = (FrameLayout)findViewById(R.id.map_container_summary);

        setUpMapIfNeeded(mapFragment);

        useMetricUnits = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_key_metric_units), false);

        Intent intent = getIntent();
        long tripId = intent.getLongExtra(TripHistory.KEY_ID, 0);

        dataAdapter = new TripDataAdapter(this);
        dataAdapter.open();
        trip = dataAdapter.getTrip(tripId);

        SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        double distance = (useMetricUnits) ? trip.getDistance() : Trip.getMiles(trip.getDistance());
        String distanceLabel = (useMetricUnits) ? " " + getString(R.string.km) : " " + getString(R.string.miles);
        double speed = (useMetricUnits) ? trip.calculateAverageSpeed() : Trip.getMiles(trip.calculateAverageSpeed());
        String speedLabel = (useMetricUnits) ? " " + getString(R.string.kph) : " " + getString(R.string.mph);

        ((ImageButton)findViewById(R.id.fab_return_to_position)).setOnClickListener(this);

        ArrayList<String> tripStrings = new ArrayList<String>();
        tripStrings.add(getString(R.string.trip_summary_date, simpleFormat.format(trip.getStartDate().getTime())));
        tripStrings.add(getString(R.string.trip_summary_duration, trip.calculateDuration()));
        tripStrings.add(String.format(getString(R.string.trip_summary_distance), trip.getDistance(),distanceLabel));
        tripStrings.add(String.format(getString(R.string.trip_summary_average_speed), trip.calculateAverageSpeed(), speedLabel));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tripStrings);
        ((ListView)findViewById(R.id.trip_summary_listView)).setAdapter(adapter);

        lineOptions = new PolylineOptions().width(Constants.MAP_LINE_WIDTH).color(Color.RED);
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
            Log.d(Constants.LOG_NAME, "Map type: " + mMap.getMapType() + " Should be: " + GoogleMap.MAP_TYPE_HYBRID);

            mMap.setOnCameraChangeListener(this);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (trip != null) {
            coordinates = new ArrayList<LatLng>();
            ArrayList<Double> latitudes = new ArrayList<Double>();
            ArrayList<Double> longitudes = new ArrayList<Double>();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (GPSCoordinate coord : trip.getCoordinates()) {
                LatLng point = new LatLng(coord.getLatitude(), coord.getLongitude());
                coordinates.add(point);
                builder.include(point);
                latitudes.add(coord.getLatitude());
                longitudes.add(coord.getLongitude());
            }
            Log.d(Constants.LOG_NAME, "Number of points: " + coordinates.size());
            if (coordinates.size() > 1) {
                bounds = builder.build();
                mMap.addPolyline(lineOptions.addAll(coordinates));
                centerLocation = bounds.getCenter();
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngBounds(bounds, mapContainer.getWidth(), mapContainer.getHeight(), 50);
                mMap.addMarker(new MarkerOptions().position(coordinates.get(0)).title("End Location"));
                mMap.addMarker(new MarkerOptions().position(coordinates.get(coordinates.size() - 1)).title("Start Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                try {
                    mMap.animateCamera(yourLocation);
                } catch (Exception e) {
                    Log.e(Constants.LOG_NAME, e.getMessage());
                    Log.d(Constants.LOG_NAME, "Had to zoom to center.");
                    CameraUpdate newLocation = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.MAP_ZOOM);
                    mMap.animateCamera(newLocation);
                }
            } else if (coordinates.size() == 1) {
                bounds = null;
                mMap.addMarker(new MarkerOptions().position(coordinates.get(0)).title("Trip Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                centerLocation = coordinates.get(0);
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinates.get(0), Constants.MAP_ZOOM);
                mMap.animateCamera(yourLocation);
            }
        }
        mMap.setOnCameraChangeListener(null);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab_return_to_position:
                if (coordinates.size() > 1 && bounds != null) {
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngBounds(bounds, mapContainer.getWidth(), mapContainer.getHeight(), 50);
                    mMap.addMarker(new MarkerOptions().position(coordinates.get(0)).title("End Location"));
                    mMap.addMarker(new MarkerOptions().position(coordinates.get(coordinates.size() - 1)).title("Start Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    try {
                        mMap.animateCamera(yourLocation);
                    } catch (Exception e) {
                        Log.e(Constants.LOG_NAME, e.getMessage());
                        Log.d(Constants.LOG_NAME, "Had to zoom to center.");
                        CameraUpdate newLocation = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Constants.MAP_ZOOM);
                        mMap.animateCamera(newLocation);
                    }
                } else if (coordinates.size() == 1) {
                    mMap.addMarker(new MarkerOptions().position(coordinates.get(0)).title("Trip Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinates.get(0), Constants.MAP_ZOOM);
                    mMap.animateCamera(yourLocation);
                }
                return;
            default:
                Log.d(Constants.LOG_NAME, "No valid ID");
                break;
        }
    }
}
