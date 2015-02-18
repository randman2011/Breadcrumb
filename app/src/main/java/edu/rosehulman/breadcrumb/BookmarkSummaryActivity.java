package edu.rosehulman.breadcrumb;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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


public class BookmarkSummaryActivity extends ActionBarActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private BookmarkDataAdapter dataAdapter;
    private Bookmark bookmark;
    private MapFragment mapFragment;
    private HorizontalScrollView mScrollView;

    public static final String KEY_BUNDLE_BOOKMARK_TITLE = "KEY_BUNDLE_BOOKMARK_TITLE";
    public static final String KEY_BUNDLE_BOOKMARK_DESCRIPTION = "KEY_BUNDLE_BOOKMARK_DESCRIPTION";
    public static final String KEY_BUNDLE_BOOKMARK_IMAGES = "KEY_BUNDLE_BOOKMARK_IMAGES";
    public static final String KEY_BUNDLE = "KEY_BUNDLE";
    public static final String KEY_BUNDLE_BOOKMARK_LATITUDE = "KEY_BUNDLE_BOOKMARK_LATITUDE";
    public static final String KEY_BUNDLE_BOOKMARK_LONGITUDE = "KEY_BUNDLE_BOOKMARK_LONGITUDE";

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
        LatLng coordinate = new LatLng(coord.getLatitude(), coord.getLongitude());

        if (mMap != null){
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, Constants.MAP_ZOOM);
            mMap.animateCamera(yourLocation);
            setUpMap(coordinate, bookmark.getTitle());
        }

        ((TextView)findViewById(R.id.bookmark_title)).setText(bookmark.getTitle());

        SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        ((TextView)findViewById(R.id.last_visited)).setText(getString(R.string.bookmark_last_visited, simpleFormat.format(bookmark.getLastVisited().getTime())));


        LinearLayout photoView = (LinearLayout)findViewById(R.id.photo_view);
        HorizontalScrollView mScrollView = (HorizontalScrollView)findViewById(R.id.scroll_view);
        ArrayList<Bitmap> photos = bookmark.getBitmapFromUriStrings(this);

        for (Bitmap photo : photos){
            ImageButton image = new ImageButton(photoView.getContext());
            image.setImageBitmap(photo);
            image.setAdjustViewBounds(true);
            //image.setBackground(new ColorDrawable(R.color.background));
            image.setOnClickListener(this);
            photoView.addView(image);
        }


        ((TextView)findViewById(R.id.description)).setText(bookmark.getDescription());

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
        switch (item.getItemId()) {
            case R.id.edit_bookmark:
                Intent intent = new Intent(this, AddBookmark.class);
                Bundle b = new Bundle();
                b.putStringArrayList(KEY_BUNDLE_BOOKMARK_IMAGES, bookmark.getImageURIs());
                b.putString(KEY_BUNDLE_BOOKMARK_TITLE, bookmark.getTitle());
                b.putString(KEY_BUNDLE_BOOKMARK_DESCRIPTION, bookmark.getDescription());
                intent.putExtra(KEY_BUNDLE, b);
                startActivity(intent);
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
     * This is where we can add markers or lines, add listeners or move the camera.
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

    @Override
    public void onClick(View v) {
        // TODO load the full size image
    }
}
