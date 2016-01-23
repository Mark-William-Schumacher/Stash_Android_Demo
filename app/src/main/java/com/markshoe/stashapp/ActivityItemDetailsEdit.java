package com.markshoe.stashapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.markshoe.stashapp.CustomViews.HighlightedDrawable;
import com.markshoe.stashapp.data.BagContract;
import com.markshoe.stashapp.data.DbUtility;

/**
 * Created by shoe on 15-07-31.
 */
public class ActivityItemDetailsEdit extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener  {
    public static String ITEM_ID_KEY = "item_id_key";
    public static String CHANGE_OCCURED = "change_occured";
    public static int ITEM_LOADER = 0;

    private long mItemId ;
    EditText et;
    ImageView iv;
    HighlightedDrawable hd;

    String mPhotoPath;
    String mResName;
    int mImageCode;
    Bitmap mImage;
    byte[] mByteArray;

    //TODO Orientation change we need to save the photopath , and the image code
    // then on reorientation change we reload the bitmap using the path .

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details_edit);
        findViewById(R.id.item_name);
        mItemId = getIntent().getLongExtra(ITEM_ID_KEY, 0);
        getSupportLoaderManager().initLoader(ITEM_LOADER, null, this);
        Uri itemUri = BagContract.ItemEntry.buildItemUriById(mItemId);
        getContentResolver().query(itemUri,null,null,null,null);
        findViewById(R.id.take_picture).setOnClickListener(this);
        findViewById(R.id.choose_icon).setOnClickListener(this);
        findViewById(R.id.no_image).setOnClickListener(this);
        findViewById(R.id.okay).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        et = (EditText) findViewById(R.id.edit_text);
        iv = (ImageView) findViewById(R.id.circular_image_view);
        hd = (HighlightedDrawable) findViewById(R.id.highlighted_drawable_profile);
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
                Intent data = new Intent();
                data.putExtra(CHANGE_OCCURED, false);
                ActivityItemDetailsEdit.this.setResult(RESULT_OK, data);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri itemUri = BagContract.ItemEntry.buildItemUriById(mItemId);
        return new CursorLoader(this, itemUri, null, null, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // [0]_id [1] tag_id [2] item_name [3]item_description [4]current_bag_key [5]date_description
        // [6]item_color [7]item_res_name [8]item_image_code [9]item_image_blob
        //getLoaderManager().destroyLoader(ITEM_LOADER);
        if (data!= null && data.moveToFirst()){
            mResName = data.getString(2);  // Shouldn't matter
            mImageCode = data.getInt(8);
            mByteArray = data.getBlob(9);
            Drawable d = DbUtility.getDrawableFromByteArray(this, mByteArray);
            et.setText(data.getString(2));
            if (mImageCode == BagContract.ItemEntry.NO_IMAGE){
                hd.setAlpha(1f);
                iv.setAlpha(0f);
                hd.setTitle(data.getString(2));
                hd.setMode(HighlightedDrawable.LETTER_MODE);
            }else if (mImageCode == BagContract.ItemEntry.REAL_IMAGE){
                hd.setAlpha(0f);
                iv.setAlpha(1f);
                iv.setImageDrawable(d);
            } else if (mImageCode == BagContract.ItemEntry.ICON_IMAGE){
                hd.setMode(HighlightedDrawable.STANDARD_MODE);
                hd.setAlpha(1f);
                iv.setAlpha(0f);
                hd.setIconDrawable(d);
            }
        }
    }


    @Override
    public void onClick(View v) {
        Intent data = new Intent();
        switch (v.getId()){
            case R.id.take_picture:
                Intent cameraPictureIntent = new Intent(this, CameraActivity.class);
                startActivityForResult(cameraPictureIntent, CameraActivity.REQUEST_TAKE_PHOTO);
                break;
            case R.id.choose_icon:
                Intent iconChooserIntent = new Intent(this, ActivityIconChooser.class);
                startActivityForResult(iconChooserIntent, ActivityIconChooser.REQUEST_GET_ICON);
                break;
            case R.id.no_image:
                mImageCode = BagContract.ItemEntry.NO_IMAGE;
                hd.setAlpha(1f);
                iv.setAlpha(0f);
                hd.setMode(HighlightedDrawable.LETTER_MODE);
                break;
            case R.id.okay:
                Uri updateUri = BagContract.ItemEntry.buildItemUriById(mItemId);
                ContentValues args = new ContentValues();
                args.put(BagContract.ItemEntry.COLUMN_IMAGE_CODE, mImageCode);
                args.put(BagContract.ItemEntry.COLUMN_BLOB_IMAGE, mByteArray);
                args.put(BagContract.ItemEntry.COLUMN_NAME, et.getText().toString());
                getContentResolver().update(updateUri, args, null, null);
                data.putExtra(CHANGE_OCCURED, true);
                ActivityItemDetailsEdit.this.setResult(RESULT_OK, data);
                finish();
                break;
            case R.id.cancel:
                data.putExtra(CHANGE_OCCURED, false);
                ActivityItemDetailsEdit.this.setResult(RESULT_OK, data);
                finish();
                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            mPhotoPath = data.getStringExtra(CameraActivity.PHOTO_PATH_RESULT_KEY);
            mImage = CameraActivity.resizeImage(mPhotoPath, 300, 300);
            mImageCode = BagContract.ItemEntry.REAL_IMAGE;
            mByteArray = DbUtility.getBytes(mImage);
            iv.setImageDrawable(new BitmapDrawable(getResources(), mImage));
            hd.setAlpha(0f);
            iv.setAlpha(1f);
        } else {
            // Back button pressed when in camera  view
        } if (requestCode == ActivityIconChooser.REQUEST_GET_ICON && resultCode == Activity.RESULT_OK){
            mResName = data.getStringExtra(ActivityIconChooser.ICON_NAME_RESULT_KEY);
            mImageCode = BagContract.ItemEntry.ICON_IMAGE;
            int resId = getResources().getIdentifier(mResName, "drawable", getPackageName());
            mImage = BitmapFactory.decodeResource(getResources(), resId);
            mByteArray = DbUtility.getBytes(mImage);
            hd.setIconDrawable(new BitmapDrawable(getResources(),mImage));
            hd.setAlpha(1f);
            hd.setMode(HighlightedDrawable.STANDARD_MODE);
            iv.setAlpha(0f);
        } else {
            // Back button from icon view
        }
    }


}
