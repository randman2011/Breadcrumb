package edu.rosehulman.breadcrumb;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class BookmarkSummaryActivity extends ActionBarActivity implements OnMapReadyCallback, View.OnClickListener, View.OnLongClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private BookmarkDataAdapter dataAdapter;
    private Bookmark bookmark;
    private MapFragment mapFragment;
    private HorizontalScrollView mScrollView;
    private LatLng coordinate;
    private ArrayList<Bitmap> photos;
    private LinearLayout photoView;
    private View imageSelected;

    public static final String KEY_BUNDLE_BOOKMARK_TITLE = "KEY_BUNDLE_BOOKMARK_TITLE";
    public static final String KEY_BUNDLE_BOOKMARK_DESCRIPTION = "KEY_BUNDLE_BOOKMARK_DESCRIPTION";
    public static final String KEY_BUNDLE_BOOKMARK_IMAGES = "KEY_BUNDLE_BOOKMARK_IMAGES";
    public static final String KEY_BUNDLE = "KEY_BUNDLE";
    public static final String KEY_BUNDLE_BOOKMARK_ID = "KEY_BUNDLE_BOOKMARK_ID";
    public static final int REQUEST_EDIT_BOOKMARK = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapFragment = MapFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.map_container_summary, mapFragment);
        ft.commit();

        setContentView(R.layout.activity_bookmark_summmary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        long bookmarkId = intent.getLongExtra(BookmarksList.KEY_ID, 0);

        dataAdapter = new BookmarkDataAdapter(this);
        dataAdapter.open();
        bookmark = dataAdapter.getBookmark(bookmarkId);
        GPSCoordinate coord = bookmark.getCoordinate();
        coordinate = new LatLng(coord.getLatitude(), coord.getLongitude());

        ((ImageButton)findViewById(R.id.fab_return_to_position)).setOnClickListener(this);
        ((ImageButton)findViewById(R.id.fab_get_directions)).setOnClickListener(this);

        ((TextView)findViewById(R.id.bookmark_title)).setText(bookmark.getTitle());

        SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        ((TextView)findViewById(R.id.last_visited)).setText(getString(R.string.bookmark_last_visited, simpleFormat.format(bookmark.getLastVisited().getTime())));


        photoView = (LinearLayout)findViewById(R.id.photo_view);
        HorizontalScrollView mScrollView = (HorizontalScrollView)findViewById(R.id.scroll_view);
        photos = bookmark.getBitmapFromUriStrings(this);
        int id = 0;

        for (Bitmap photo : photos){
            ImageButton image = new ImageButton(photoView.getContext());
            image.setImageBitmap(photo);
            image.setAdjustViewBounds(true);
            image.setId(id--);
            image.setBackgroundColor(getResources().getColor(R.color.transparent));
            image.setOnClickListener(this);
            image.setOnLongClickListener(this);
            photoView.addView(image);
        }


        ((TextView)findViewById(R.id.description)).setText(bookmark.getDescription());

        dataAdapter.close();

        setUpMapIfNeeded(mapFragment);


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
                b.putLong(KEY_BUNDLE_BOOKMARK_ID, bookmark.getId());
                intent.putExtra(KEY_BUNDLE, b);
                startActivityForResult(intent, REQUEST_EDIT_BOOKMARK);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_EDIT_BOOKMARK:
                this.recreate();
        }
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
        mMap.addMarker(new MarkerOptions().position(coordinate).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, Constants.MAP_ZOOM);
            mMap.animateCamera(yourLocation);
            setUpMap(coordinate, bookmark.getTitle());
        }
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId < 1) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(bookmark.getImageURIs().get(Math.abs(vId))), "image/*");
            startActivity(intent);
            return;
        }
        switch (vId){
            case R.id.fab_return_to_position:
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, Constants.MAP_ZOOM);
                mMap.animateCamera(yourLocation);
                break;
            case R.id.fab_get_directions:
                String uri = String.format("google.navigation:q=%f,%f", coordinate.latitude, coordinate.longitude);
                Uri gmmIntentUri = Uri.parse(uri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                break;
            default:

                // TODO Create custom dialog and add full size image to it

                break;
        }


    }

    @Override
    public boolean onLongClick(View v) {
        imageSelected = v;
        DialogFragment df = new DialogFragment(){
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.delete_image_message));
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImage();
                        dialog.dismiss();
                    }
                });
                return builder.create();
            }
        };
        df.show(getFragmentManager(), "");
        return true;
    }

    private void deleteImage() {
        if (imageSelected == null) {
            return;
        }
        int id = imageSelected.getId();
        photoView.removeView(imageSelected);
        photos.remove(Math.abs(id));
        ArrayList<String> uris = bookmark.getImageURIs();
        uris.remove(Math.abs(id));
        uris.trimToSize();
        bookmark.setImageURIs(uris);
        dataAdapter.updateBookmark(bookmark);
    }
}
