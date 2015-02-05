package edu.rosehulman.breadcrumb;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;

/**
 * Created by turnerrs on 1/25/2015.
 */
public class AddBookmark extends Fragment implements View.OnClickListener {

    private int numAddedImages = 0;
    private static final int MAX_NUMBER_OF_IMAGES = 10;
    private Bitmap[] imageBitmaps;
    private ImageView imageView1;
    private ImageView imageView2;
    private LinearLayout imageSampler;
    private EditText bookmarkNameText;
    private EditText bookmarkDescriptionText;
    private static final int KEY_PHOTO_SELECT = 20;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_add_bookmark, container, false);
        imageSampler = (LinearLayout)view.findViewById(R.id.image_sampler);
        bookmarkNameText = (EditText)view.findViewById(R.id.bookmark_name);
        bookmarkDescriptionText = (EditText)view.findViewById(R.id.bookmark_description);
        ((ImageButton)view.findViewById(R.id.image_add_button)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.save_button)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.cancel_button)).setOnClickListener(this);
        // TODO make type ImageButton to allow deleting
        imageView1 = (ImageView)view.findViewById(R.id.imageView1);
        imageView2 = (ImageView)view.findViewById(R.id.imageView2);
        imageBitmaps = new Bitmap[MAX_NUMBER_OF_IMAGES];

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                // Send signal to MainActivity to close this in FragmentManager
                return;
            case R.id.save_button:
                // Get GPS coordinates
                // Save bookmark to database
                return;
            case R.id.image_add_button:
                // Open up image picker
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, KEY_PHOTO_SELECT);
                return;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case KEY_PHOTO_SELECT:
                if (resultCode == Activity.RESULT_OK) {
                    Uri image = data.getData();
                    InputStream imageStream;
                    try {
                        imageStream = this.getActivity().getContentResolver().openInputStream(image);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this.getActivity(), "File not found", Toast.LENGTH_LONG);
                        return;
                    }
                    imageBitmaps[numAddedImages] = BitmapFactory.decodeStream(imageStream);
                    numAddedImages++;

                    if (numAddedImages > 1) {
                        imageView1.setImageBitmap(imageBitmaps[numAddedImages-2]);
                        imageView1.setVisibility(View.VISIBLE);
                        imageView2.setImageBitmap(imageBitmaps[numAddedImages-1]);
                        imageView2.setVisibility(View.VISIBLE);
                    } else if (numAddedImages == 1) {
                        imageView2.setImageBitmap(imageBitmaps[numAddedImages-1]);
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
}
