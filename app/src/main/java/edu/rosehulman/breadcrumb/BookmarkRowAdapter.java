package edu.rosehulman.breadcrumb;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by watterlm on 1/26/2015.
 */
public class BookmarkRowAdapter extends BaseAdapter{
    private Context mContext;
    private int mNumRows;
    private ArrayList<Bookmark> bookmarks;

    public BookmarkRowAdapter(Context context, ArrayList<Bookmark> bookmarks){
        this.mContext = context;
        this.mNumRows = bookmarks.size();
        this.bookmarks = bookmarks;
    }

    @Override
    public int getCount() {
        return mNumRows;
    }

    @Override
    public Object getItem(int position) {
        return bookmarks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = null;

        if (convertView == null){
            view = new TextView(this.mContext);
        }else{
            view = (TextView) convertView;
        }

        view.setText(bookmarks.get(position).getTitle());
        view.setTextSize(24);
        return view;
    }
}
