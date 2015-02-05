package edu.rosehulman.breadcrumb;

import android.app.Activity;
import android.app.FragmentManager;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by watterlm on 1/25/2015.
 */
public class TripTracking extends Fragment implements View.OnClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private FragmentActivity mContext;
    private GPSLocationManager locManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_trip_tracking, container, false);
        ((Button) v.findViewById(R.id.trip_control)).setOnClickListener(this);
        ((ImageButton)v.findViewById(R.id.fab_add_bookmark)).setOnClickListener(this);

        locManager = new GPSLocationManager(getActivity());
        setUpMapIfNeeded();
        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trip_control:

                return;
            case R.id.fab_add_bookmark:
                Toast.makeText(getActivity(), "FAB pressed", Toast.LENGTH_SHORT).show();
                Fragment fragment = new AddBookmark();

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                return;
        }
    }

    @Override
    public void onAttach(Activity activity){
        mContext = (FragmentActivity) activity;
        super.onAttach(activity);
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
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) mContext.getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                Location location = locManager.getCurrentLocation();
                locManager.endTracking();
                LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 5);
                mMap.animateCamera(yourLocation);

                setUpMap(coordinate);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(LatLng coordinate) {
        mMap.addMarker(new MarkerOptions().position(coordinate).title("Marker"));
    }
}
