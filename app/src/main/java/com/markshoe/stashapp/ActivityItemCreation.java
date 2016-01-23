package com.markshoe.stashapp;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.markshoe.stashapp.CustomViews.HighlightedDrawable;
import com.markshoe.stashapp.data.BagContract;
import com.markshoe.stashapp.data.DbUtility;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shoe on 15-07-25.
 */
public class ActivityItemCreation extends Activity implements View.OnClickListener{
    final String LOG_TAG = this.getClass().getSimpleName();
    LayoutInflater mLayoutInflater;
    ViewGroup mContainer;
    final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    // Activity result key for camera
    static final int REQUEST_GET_ICON = 11112;

    // Creation of a new Item
    String mItemName = "";
    String mUuid = UUID.randomUUID().toString();
    Bitmap mImage = null;
    String mResName = "backpack";
    int mCurrentBag = 1;
    int mImageCode= BagContract.ItemEntry.NO_IMAGE;
    Uri mReturnUri = null;

    //
    boolean mCompleteItemActivity =false;
    boolean mCancelable = true;
    boolean mClickable = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_creation_activity);
        mContainer = (ViewGroup) findViewById(R.id.container);
        mLayoutInflater = getLayoutInflater();
        createScanView();
    }

    public View createScanView() {
        View tv = mLayoutInflater.inflate(R.layout.activity_item_creation_scan_view, null);
        mContainer.removeAllViews();
        mContainer.addView(tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToNameEntryView();
            }
        });
        return tv;
    }

    public void switchToNameEntryView(){
        // TODO Validate input on return
        View tv = mLayoutInflater.inflate(R.layout.activity_item_creation_name_view,null);
        ViewGroup vg = (ViewGroup) findViewById(R.id.container);
        vg.removeAllViews();
        vg.addView(tv);
        final MaterialEditText editText = (MaterialEditText) tv.findViewById(R.id.edit_text);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        editText.setTypeface(font);
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    mItemName = editText.getText().toString();
                    if (editText.getText().length() < 15) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        mContainer.animate().alpha(0).setDuration(150).setInterpolator(sAccelerator).withEndAction(new Runnable() {
                            public void run() {
                                switchToImagePickerView();
                                mContainer.animate().alpha(1).setDuration(300).setInterpolator(sDecelerator);
                            }
                        });
                    } else {
                        // INVALID
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void switchToImagePickerView() {
        mContainer.removeAllViews();
        View pickerView = mLayoutInflater.inflate(R.layout.activity_item_creation_image_picker_view, null);
        mContainer.addView(pickerView);
        pickerView.findViewById(R.id.take_picture).setOnClickListener(this);
        pickerView.findViewById(R.id.choose_icon).setOnClickListener(this);
        pickerView.findViewById(R.id.no_image).setOnClickListener(this);
    }

    /**
     * The activity returns with the photo.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            String photoPath = data.getStringExtra(CameraActivity.PHOTO_PATH_RESULT_KEY);
            mImage = CameraActivity.resizeImage(photoPath,300,300);
            mImageCode = BagContract.ItemEntry.REAL_IMAGE;
            switchToCompletedView();
        } else {
            // Back button pressed when in camera  view
        } if (requestCode == REQUEST_GET_ICON && resultCode == Activity.RESULT_OK){
            mResName = data.getStringExtra(ActivityIconChooser.ICON_NAME_RESULT_KEY);
            Log.e("Shoe",mResName);
            mImageCode = BagContract.ItemEntry.ICON_IMAGE;
            int resId = getResources().getIdentifier(mResName, "drawable", getPackageName());
            mImage = BitmapFactory.decodeResource(getResources(),resId);
            switchToCompletedView();
        } else {
            // Back button from icon view
        }
    }

    @Override
    public void onClick(View v) {
        if (!mClickable) return;
        mClickable = false;
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switchToCompletedView();
                    }
                }, 300);
                break;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mClickable = true;
    }

    public void switchToCompletedView() {
        // item_image_blob[9],item_image_code[8],item_res_name[7] item_color[6] date_description[5]
        // current_bag_key[4] item_description[3] item_name[2] tag_id[1] _id[0]
        mCancelable = false;
        mContainer.removeAllViews();
        final View completedView = mLayoutInflater.inflate(R.layout.activity_item_creation_completed_view, null);
        mContainer.addView(completedView);
        Uri uri = BagContract.ItemEntry.CONTENT_URI;
        ContentValues cv = BagContract.ItemEntry.createContentValues(mUuid, mItemName, mCurrentBag
                , "Default Desc", System.currentTimeMillis(), 0, mResName, mImageCode, DbUtility.getBytes(mImage));
        uri = getContentResolver().insert(uri, cv);
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        getContentResolver().notifyChange(BagContract.BagEntry.CONTENT_URI, null);
        c.moveToFirst();

        ((TextView) completedView.findViewById(R.id.item_name)).setText(c.getString(2));
        ((TextView)findViewById(R.id.activity_bar_title)).setText("Item created");
        Bitmap imageBitmap = DbUtility.getImage(c.getBlob(9));
        switch (c.getInt(8)){
            case BagContract.ItemEntry.REAL_IMAGE:
                ((CircleImageView) completedView.findViewById(R.id.circular_image_view))
                        .setImageBitmap(imageBitmap);
                break;
            case BagContract.ItemEntry.ICON_IMAGE:
                HighlightedDrawable hd = new HighlightedDrawable(this);
                hd.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                hd.setIconDrawable(new BitmapDrawable(getResources(), imageBitmap));
                hd.setTitle(c.getString(2));
                LinearLayout vg = (LinearLayout) findViewById(R.id.icon_frame);
                vg.removeViewAt(0);
                vg.addView(hd);
                break;
            case BagContract.ItemEntry.NO_IMAGE:
                HighlightedDrawable hdLetter = new HighlightedDrawable(this,null,HighlightedDrawable.LETTER_MODE);
                hdLetter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                hdLetter.setItem(-1,0,0,c.getString(2));
                LinearLayout vg2 = (LinearLayout) findViewById(R.id.icon_frame);
                vg2.removeViewAt(0);
                vg2.addView(hdLetter);
                break;
        }
        mCancelable = true;
        completedView.findViewById(R.id.progress_layout).animate().alpha(0).setDuration(100).setInterpolator(sAccelerator).withEndAction(new Runnable() {
            public void run() {
                completedView.findViewById(R.id.completed_layout).animate().alpha(1).setDuration(400).setInterpolator(sDecelerator);
            }
        });
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int width = getWindow().getDecorView().getWidth();
        final int height = getWindow().getDecorView().getHeight();
        final int x = (int) event.getX();
        final int y = (int) event.getY();

        if (x > 0 && y > 0 && x < width && y < height)
        {   // Touch Even Inside
            if (mCompleteItemActivity) finish();
        }
        else
        {   // Touch Even Outside
            if (mCancelable)
                finish();
        }
        return super.onTouchEvent(event);
    }
}