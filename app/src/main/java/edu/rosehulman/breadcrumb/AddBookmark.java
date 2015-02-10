package edu.rosehulman.breadcrumb;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by turnerrs on 1/25/2015.
 */
public class AddBookmark extends ActionBarActivity implements View.OnClickListener {

    private int numAddedImages = 0;
    private static final int MAX_NUMBER_OF_IMAGES = 10;
    private ArrayList<Bitmap> imageBitmaps;
    private ArrayList<String> imageLocations;

    private ImageButton imageView1;
    private ImageButton imageView2;
    private LinearLayout imageSampler;
    private EditText bookmarkNameText;
    private EditText bookmarkDescriptionText;
    private static final int KEY_PHOTO_SELECT = 20;
    private BookmarkDataAdapter bookmarkAdapter;
    private GPSLocationManager locManager;
    private GPSCoordinate coordinate;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_bookmark);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageSampler = (LinearLayout)findViewById(R.id.image_sampler);
        bookmarkNameText = (EditText)findViewById(R.id.bookmark_name);
        bookmarkDescriptionText = (EditText)findViewById(R.id.bookmark_description);
        ((ImageButton)findViewById(R.id.image_add_button)).setOnClickListener(this);
        btnSave = ((Button)findViewById(R.id.save_button));
        btnSave.setOnClickListener(this);
        ((Button)findViewById(R.id.cancel_button)).setOnClickListener(this);
        imageView1 = (ImageButton)findViewById(R.id.imageView1);
        imageView2 = (ImageButton)findViewById(R.id.imageView2);
        imageBitmaps = new ArrayList<Bitmap>();
        imageLocations = new ArrayList<String>();
        bookmarkAdapter = new BookmarkDataAdapter(this);
        bookmarkAdapter.open();
        locManager = new GPSLocationManager(this);
        coordinate = locManager.getGPSCoordinate();
        locManager.endTracking();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                setResult(RESULT_OK);
                finish();
                //((MainActivity)getActivity()).closeFragment((Fragment)this);
                return;
                // TODO Send signal to MainActivity to close this in FragmentManager

            case R.id.save_button:
                addBookmark();
                setResult(RESULT_OK);
                finish();
                //((MainActivity)getActivity()).closeFragment((Fragment)this);
                // TODO Send signal to MainActivity to close this in FragmentManager
                return;
            case R.id.image_add_button:
                createIntent();
                return;
        }
    }

    private Uri outputFileUri;

    private void createIntent() {
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        String fname = null;
        try {
            fname = File.createTempFile("IMG_", ".jpg").getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(photoPickerIntent, KEY_PHOTO_SELECT);

        final Intent chooserIntent = Intent.createChooser(photoPickerIntent, "Select Source");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, KEY_PHOTO_SELECT);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case KEY_PHOTO_SELECT:
                if (resultCode == Activity.RESULT_OK) {
                    Uri image;
                    if (data == null) {
                        image = outputFileUri;
                    } else {
                        image = data.getData();
                    }
                    imageLocations.add(image.toString());
                    InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(image);
                    } catch (FileNotFoundException e) {
                        //Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show();
                        return;
                    }
                    imageBitmaps.add(BitmapFactory.decodeStream(imageStream));
                    numAddedImages++;

                    if (numAddedImages > 1) {
                        imageView1.setImageBitmap(imageBitmaps.get(imageBitmaps.size() - 2));
                        imageView1.setVisibility(View.VISIBLE);
                        imageView2.setImageBitmap(imageBitmaps.get(imageBitmaps.size() - 1));
                        imageView2.setVisibility(View.VISIBLE);
                    } else if (numAddedImages == 1) {
                        imageView2.setImageBitmap(imageBitmaps.get(imageBitmaps.size() - 1));
                        imageView2.setVisibility(View.VISIBLE);
                        imageView1.setVisibility(View.GONE);
                    } else {
                        imageView1.setVisibility(View.GONE);
                        imageView2.setVisibility(View.GONE);
                    }
                    imageView1.invalidate();
                    imageView2.invalidate();

                }
        }
    }

    private void addBookmark() {
        String title = ((EditText)findViewById(R.id.bookmark_name)).getText().toString();
        String description = ((EditText)findViewById(R.id.bookmark_description)).getText().toString();
        Calendar lastVisited = Calendar.getInstance();
        Bookmark bookmark = new Bookmark(title, description, coordinate, lastVisited);
        bookmark.setImageURIs(imageLocations);
        bookmarkAdapter.addBookmark(bookmark);
    }
}
