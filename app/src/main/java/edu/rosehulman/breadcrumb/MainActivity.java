package edu.rosehulman.breadcrumb;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ListView mDrawerList;
    private String[] mMenuItems;
    private Fragment tripTrackingFrag;
    private String pageBeforeSettings;
    private static final int SETTING_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tripTrackingFrag = new TripTracking();

        mMenuItems = getResources().getStringArray(R.array.menu_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuItems));

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectItem(position);
                Log.d(Constants.LOG_NAME, "Drawer list item clicked");
            }
        });

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        selectItem(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        /// / Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    /** Swaps fragments in the main content view */
    public void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        replaceFragment(mMenuItems[position]);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuItems[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void replaceFragment(String selectedItem) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = null;
        if (selectedItem.equals(getString(R.string.menu_bookmark))) {
            fragment = new BookmarksList();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Log.d(Constants.LOG_NAME, "Bookmark Summary selected");
            pageBeforeSettings = getString(R.string.menu_bookmark);
        } else if (selectedItem.equals(getString(R.string.menu_tracking))) {
            tripTrackingFrag = new TripTracking();
            fragment = tripTrackingFrag;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Log.d(Constants.LOG_NAME, "Tracking selected");
            pageBeforeSettings = getString(R.string.menu_tracking);
        } else if (selectedItem.equals(getString(R.string.menu_trip_history))) {
            fragment = new TripHistory();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Log.d(Constants.LOG_NAME, "Trip History selected");
            pageBeforeSettings = getString(R.string.menu_trip_history);
        } else if (selectedItem.equals(getString(R.string.menu_exit))) {
            Log.d(Constants.LOG_NAME, "Finish selected");
            finish();
            return;
        } else if (selectedItem.equals(getString(R.string.action_settings))) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Log.d(Constants.LOG_NAME, "Settings selected");
            startActivityForResult(settingsIntent, SETTING_REQUEST_CODE);
            return;
        } else if (selectedItem.equals(getString(R.string.menu_add_bookmark))) {
            Intent addBookmarkIntent = new Intent(this, AddBookmark.class);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Log.d(Constants.LOG_NAME, "Add Bookmark selected");
            pageBeforeSettings = getString(R.string.menu_add_bookmark);
            startActivity(addBookmarkIntent);
            return;
        }
        if (fragment == null) {
            Log.e(Constants.LOG_NAME, "Fragment null. Selected item is " + selectedItem + ". Returning...");
            return;
        }
        ft.replace(R.id.content_frame, fragment);

        ft.commit();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See which child activity is calling us back.
        switch (requestCode) {
            case SETTING_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(Constants.LOG_NAME, "Result ok!");
                    refreshAfterSettings();
                } else {
                    Log.d(Constants.LOG_NAME, "Result not okay.  User hit back without a button:");
                    refreshAfterSettings();
                }
                break;
            default:
                Log.d(Constants.LOG_NAME, "Unknown result code");
                break;
        }
    }

    public void refreshAfterSettings(){
        ArrayList<String> items = new ArrayList<String>(Arrays.asList(mMenuItems));
        int index = items.indexOf(pageBeforeSettings);
        mDrawerList.setItemChecked(index, true);
        if (pageBeforeSettings == getString(R.string.menu_trip_history)){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fragment = new TripHistory();
            ft.replace(R.id.content_frame, fragment);

            ft.commit();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        // TODO
//        mTitle = title;
//        getFragmentManager().setTitle(mTitle);
    }
}
