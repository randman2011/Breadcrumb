package edu.rosehulman.breadcrumb;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by turnerrs on 1/25/2015.
 */
public class BookmarksList extends Fragment implements View.OnClickListener {
    private ArrayList<Bookmark> bookmarks;
    private BookmarkDataAdapter dataAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks_list, container, false);
        dataAdapter = new BookmarkDataAdapter(getActivity());
        dataAdapter.open();

        bookmarks = new ArrayList<Bookmark>();
        bookmarks = dataAdapter.getAllBookmarks(bookmarks);
        //bookmarks.add(new Bookmark("Rose-Hulman Creek", "Cool spot down by the SRC.", new GPSCoordinate(39.484343, -87.328597), Calendar.getInstance()));
        //bookmarks.add(new Bookmark("Secret Place", "My secret place off the trails near campus. Gotta go down to the creek", new GPSCoordinate(39.483421, -87.335351), Calendar.getInstance()));

        ListView lsBookmarks = (ListView)view.findViewById(R.id.bookmarks_list_listView);
        lsBookmarks.setAdapter(new BookmarkRowAdapter(view.getContext(), bookmarks));

        lsBookmarks.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Log.d(Constants.LOG_NAME, bookmarks.get(position).getTitle());
                Intent bookmarkIntent = new Intent(view.getContext(), BookmarkSummaryActivity.class);
                startActivity(bookmarkIntent);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
