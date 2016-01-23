package com.markshoe.stashapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.markshoe.stashapp.CustomViews.HighlightedDrawable;
import com.markshoe.stashapp.data.BagContract;
import com.markshoe.stashapp.data.DbUtility;

/**
 * Created by shoe on 15-07-31.
 */
public class ActivityItemDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener  {
    public static String ITEM_ID_KEY = "item_id_key";
    private long mItemId;
    int ITEM_LOADER = 0;
    Cursor mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Item Details");
        }

        Bundle bundle = getIntent().getExtras();
        mItemId = bundle.getLong(ITEM_ID_KEY);
        getSupportLoaderManager().initLoader(ITEM_LOADER,null,this);

        findViewById(R.id.view_location_banner).setOnClickListener(this);
        findViewById(R.id.edit_relative_layout).setOnClickListener(this);
        findViewById(R.id.alarm_relative_layout).setOnClickListener(this);
        findViewById(R.id.scan_color_relative_layout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.edit_relative_layout:
                Intent activityItemDetailsEditIntent = new Intent(this, ActivityItemDetailsEdit.class);
                activityItemDetailsEditIntent.putExtra(ActivityItemDetailsEdit.ITEM_ID_KEY,mItemId);
                startActivity(activityItemDetailsEditIntent);
                break;
            case R.id.alarm_relative_layout:
                break;
            case R.id.scan_color_relative_layout:
                break;
            case R.id.view_location_banner:
                Log.e("viewBanner","viewBanner");
                break;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri itemUri = BagContract.ItemEntry.buildItemUriById(mItemId);
        return new CursorLoader(this, itemUri, null, null, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // [0]_id [1] tag_id [2] item_name [3]item_description [4]current_bag_key [5]date_description
        // [6]item_color [7]item_res_name [8]item_image_code [9]item_image_blob

        Log.e("CALL","CALL");
        if (data == mData) return; // Stupid android
        Log.e("CALL2","CALL2");
        mData = data;
        TextView itemName = (TextView) findViewById(R.id.item_name);
        HighlightedDrawable hdProfile = (HighlightedDrawable) findViewById(R.id.highlighted_drawable_profile);
        ImageView cImageProfile = (ImageView) findViewById(R.id.profile_image);

        if (data!= null && data.moveToFirst()){
            int imageCode = data.getInt(8);
            Drawable d = DbUtility.getDrawableFromByteArray(this, data.getBlob(9));
            itemName.setText(data.getString(2));
            if (imageCode ==BagContract.ItemEntry.NO_IMAGE){
                hdProfile.setAlpha(1f);
                cImageProfile.setAlpha(0f);
                hdProfile.setTitle(data.getString(2));
                hdProfile.setMode(HighlightedDrawable.LETTER_MODE);
            }
            else if (imageCode == BagContract.ItemEntry.REAL_IMAGE){
                hdProfile.setAlpha(0f);
                cImageProfile.setAlpha(1f);
                cImageProfile.setImageDrawable(d);
            }  else if (imageCode == BagContract.ItemEntry.ICON_IMAGE){
                hdProfile.setAlpha(1f);
                cImageProfile.setAlpha(0f);
                hdProfile.setIconDrawable(d);
                hdProfile.setMode(HighlightedDrawable.STANDARD_MODE);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_item_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.modify_backpack:
                return true;
            case R.id.action_delete:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
