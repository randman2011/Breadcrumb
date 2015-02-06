package edu.rosehulman.breadcrumb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import static android.widget.ListView.*;

/**
 * Created by turnerrs on 1/25/2015.
 */
public class BookmarksList extends Fragment implements View.OnClickListener {
    private ArrayList<Bookmark> bookmarks;
    private BookmarkDataAdapter dataAdapter;
    private BookmarkRowAdapter rowAdapter;

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
        rowAdapter = new BookmarkRowAdapter(view.getContext(), bookmarks);
        ListView lsBookmarks = (ListView)view.findViewById(R.id.bookmarks_list_listView);
        lsBookmarks.setAdapter(rowAdapter);

        lsBookmarks.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Log.d(Constants.LOG_NAME, bookmarks.get(position).getTitle());
                Intent bookmarkIntent = new Intent(view.getContext(), BookmarkSummaryActivity.class);
                startActivity(bookmarkIntent);
            }
        });

        lsBookmarks.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(Constants.LOG_NAME, "Delete Press on " + bookmarks.get(position).getTitle());
                showDelete(bookmarks.get(position));
                return false;
            }
        });


        return view;
    }

    @Override
    public void onClick(View v) {

    }

    public void showDelete(final Bookmark bookmark){
        DialogFragment df = new DialogFragment(){
            @Override
            public Dialog onCreateDialog(Bundle b){
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getActivity());
                deleteBuilder.setTitle(R.string.delete_bookmark_title);
                deleteBuilder.setMessage(getString(R.string.delete_bookmark_message, bookmark.getTitle()));
                deleteBuilder.setNegativeButton(android.R.string.cancel, null);
                deleteBuilder.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                Log.d(Constants.LOG_NAME, bookmark.getTitle() + " was deleted.");
                                dataAdapter.deleteBookmark(bookmark);
                                bookmarks.remove(bookmark);
                                rowAdapter.remove();
                                rowAdapter.notifyDataSetChanged();
                            }
                        });
                return deleteBuilder.create();
            }
        };
        df.show(getFragmentManager(), "");
    }
}
