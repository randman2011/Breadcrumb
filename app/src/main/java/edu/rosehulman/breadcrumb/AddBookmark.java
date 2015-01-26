package edu.rosehulman.breadcrumb;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by turnerrs on 1/25/2015.
 */
public class AddBookmark extends Fragment implements View.OnClickListener {

    private ImageButton addImageButton;
    private LinearLayout imageSampler;
    private EditText bookmarkNameText;
    private EditText bookmarkDescriptionText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_add_bookmark, container, false);
        imageSampler = (LinearLayout)view.findViewById(R.id.image_sampler);
        bookmarkNameText = (EditText)view.findViewById(R.id.bookmark_name);
        bookmarkDescriptionText = (EditText)view.findViewById(R.id.bookmark_description);
        addImageButton = (ImageButton)view.findViewById(R.id.image_add_button);
        addImageButton.setOnClickListener(this);
        ((Button)view.findViewById(R.id.save_button)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.cancel_button)).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
