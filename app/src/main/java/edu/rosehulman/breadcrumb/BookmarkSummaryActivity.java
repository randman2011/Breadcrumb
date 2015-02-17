package edu.rosehulman.breadcrumb;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class BookmarkSummaryActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private BookmarkDataAdapter dataAdapter;
    private Bookmark bookmark;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapFragment = MapFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.map_container_summary, mapFragment);
        ft.commit();

        setContentView(R.layout.activity_bookmark_summmary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpMapIfNeeded(mapFragment);

        Intent intent = getIntent();
        long bookmarkId = intent.getLongExtra(BookmarksList.KEY_ID, 0);

        dataAdapter = new BookmarkDataAdapter(this);
        dataAdapter.open();
        bookmark = dataAdapter.getBookmark(bookmarkId);
        GPSCoordinate coord = bookmark.getCoordinate();
        LatLng coordinate = coord.toLatLong();

        if (mMap != null){
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, Constants.MAP_ZOOM);
            mMap.animateCamera(yourLocation);
            setUpMap(coordinate, bookmark.getTitle());
        } else {
            Toast.makeText(this, "Map is null", Toast.LENGTH_SHORT).show();
        }

        ((TextView)findViewById(R.id.bookmark_title)).setText(bookmark.getTitle());

        SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        ((TextView)findViewById(R.id.last_visited)).setText(getString(R.string.bookmark_last_visited, simpleFormat.format(bookmark.getLastVisited().getTime())));

        ArrayList<Bitmap> photos = bookmark.getBitmapFromUriStrings(this);
        LinearLayout photoView = (LinearLayout)findViewById(R.id.photo_view);

        for (Bitmap photo : photos){
            ImageView image = new ImageView(photoView.getContext());
            image.setImageBitmap(photo);
            image.setAdjustViewBounds(true);
            photoView.addView(image);
        }

        dataAdapter.close();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bookmark_summmary2, menu);
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
    private void setUpMapIfNeeded(MapFragment mapFragment) {
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
        }
    }
}
