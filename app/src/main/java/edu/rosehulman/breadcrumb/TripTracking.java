package edu.rosehulman.breadcrumb;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by watterlm on 1/25/2015.
 */

public class TripTracking extends Fragment implements View.OnClickListener, OnMapReadyCallback, LocationListener {

    public static String KEY_LONG = "KEY_LONG";
    public static String KEY_LAT = "KEY_LAT";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LocationManager locManager;
    private String locationProvider;
    private Button tripControl;
    private Trip trip;
    private TripDataAdapter tripAdapter;
    private MapFragment mapFragment;
    private static View staticView;
    private boolean is_tracking = false;
    private PolylineOptions lineOptions;
    private MarkerOptions markerOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trip_tracking, container, false);

        mapFragment = MapFragment.newInstance();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add (R.id.map_container, mapFragment);
        ft.commit();

        tripControl = ((Button)v.findViewById(R.id.trip_control));
        tripControl.setOnClickListener(this);
        ((ImageButton) v.findViewById(R.id.fab_add_bookmark)).setOnClickListener(this);

        this.locationProvider = LocationManager.GPS_PROVIDER;
        // Acquire a reference to the system Location Manager
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Register the listener with the Location Manager to receive location updates
        locManager.requestLocationUpdates(locationProvider, 0, 0, this);

        tripAdapter = new TripDataAdapter(getActivity());
        tripAdapter.open();
        setUpMapIfNeeded(mapFragment);

        lineOptions = new PolylineOptions().width(3).color(Color.RED);
        markerOptions = new MarkerOptions();
        return v;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trip_control:
                if (!is_tracking) {//tripControl.getText().equals(getString(R.string.start_trip))){
                    tripControl.setText(R.string.stop_trip);
                    is_tracking = true;
                    startTracking();
                } else {
                    is_tracking = false;
                    tripControl.setText(R.string.start_trip);
                    endTracking();
                    if (trip != null) {
                        List<LatLng> coors = new ArrayList<LatLng>();
                        for (GPSCoordinate coordinate : trip.getCoordinates()) {
                            coors.add(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()));
                        }
                        trip.setEndDate(Calendar.getInstance());
                        long tripId = tripAdapter.addTrip(trip);
                        Intent intent = new Intent(getActivity(), TripSummaryActivity.class);
                        intent.putExtra(TripHistory.KEY_ID, tripId);
                        startActivity(intent);

                    }
                }

                return;
            case R.id.fab_add_bookmark:
                //((MainActivity)getActivity()).replaceFragment(getString(R.string.menu_add_bookmark));
                Intent intent = new Intent(getActivity(), AddBookmark.class);
                LatLng coord = getCurrentLocation();
                intent.putExtra(KEY_LONG, coord.longitude);
                intent.putExtra(KEY_LAT, coord.latitude);
                startActivity(intent);
                return;
        }

        }

        @Override
        public void onAttach (Activity activity){
            //mContext = (Context) activity;
            super.onAttach(activity);
        }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
        private void setUpMap (LatLng coordinate){
            mMap.addMarker(new MarkerOptions().position(coordinate).title("Your Location"));
        }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap(LatLng coordinate)} once when {@link #mMap} is not null.
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            LatLng coordinate =  getCurrentLocation();
            endTracking();
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, Constants.MAP_ZOOM);
            mMap.animateCamera(yourLocation);

            setUpMap(coordinate);
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        if (trip != null) {
            trip.addCoordinate(new GPSCoordinate(location.getLatitude(), location.getLongitude()));
            mMap.addPolyline(lineOptions.add(new LatLng(location.getLatitude(), location.getLongitude())));
        }
        mMap.addMarker(markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location"));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public LatLng getCurrentLocation() {
        Location location = locManager.getLastKnownLocation(locationProvider);
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public Trip endTracking() {
        locManager.removeUpdates(this);
        return trip;
    }

    public void startTracking() {
        locManager.requestLocationUpdates(locationProvider, 0, 0, this);
        this.trip = new Trip();
    }
}