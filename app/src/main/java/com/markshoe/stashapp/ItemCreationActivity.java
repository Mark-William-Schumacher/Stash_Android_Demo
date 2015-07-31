package com.markshoe.stashapp;

import android.animation.LayoutTransition;
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
import android.text.format.DateFormat;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.markshoe.stashapp.CustomViews.HighlightedDrawable;
import com.markshoe.stashapp.data.BagContract;
import com.markshoe.stashapp.data.DbUtility;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shoe on 15-07-25.
 */
public class ItemCreationActivity extends Activity implements View.OnClickListener{
    final String LOG_TAG = this.getClass().getSimpleName();
    LayoutInflater mLayoutInflater;
    ViewGroup mContainer;
    final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    View pickerView;
    // Activity result key for camera
    static final int REQUEST_TAKE_PHOTO = 11111;
    static final int REQUEST_GET_ICON = 11112;

    // Storage for camera image URI components
    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private final static String CAPTURED_PHOTO_URI_KEY = "mCapturedImageURI";

    // Required for camera operations in order to save the image file on resume.
    private String mCurrentPhotoPath = null;
    private Uri mCapturedImageURI = null;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_creation_activity);
        mContainer = (ViewGroup) findViewById(R.id.container);
        mLayoutInflater = getLayoutInflater();
        pickerView = mLayoutInflater.inflate(R.layout.activity_item_creation_image_picker_view, null);
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
        mContainer.addView(pickerView);
        pickerView.findViewById(R.id.take_picture).setOnClickListener(this);
        pickerView.findViewById(R.id.choose_icon).setOnClickListener(this);
        pickerView.findViewById(R.id.no_image).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.take_picture:
                dispatchTakePictureIntent();
                break;
            case R.id.choose_icon:
                Intent iconChooserIntent = new Intent(this, IconChooserActivity.class);
                startActivityForResult(iconChooserIntent, REQUEST_GET_ICON);
                break;
            case R.id.no_image:
                mImageCode = BagContract.ItemEntry.NO_IMAGE;
                switchToCompletedView();
                break;
        }
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

        switch (c.getInt(8)){
            case BagContract.ItemEntry.REAL_IMAGE:
                ((CircleImageView) completedView.findViewById(R.id.circular_image_view))
                        .setImageBitmap(DbUtility.getImage(c.getBlob(9)));
                break;
            case BagContract.ItemEntry.ICON_IMAGE:
                int resId = getResources().getIdentifier(c.getString(7), "drawable", getPackageName());
                if (resId == 0){
                    resId = getResources().getIdentifier("backpack", "drawable", getPackageName());
                }
                HighlightedDrawable hd = new HighlightedDrawable(this);
                hd.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                hd.setItem(-1, resId,0,c.getString(2));
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


    /**
     * Start the camera by dispatching a camera intent.
     */
    protected void dispatchTakePictureIntent() {

        // Check if there is a camera.
        Context context = this;
        PackageManager packageManager = context.getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
            Toast.makeText(context, "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Camera exists? Then proceed...
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go.
            // If you don't do this, you may get a crash in some devices.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast toast = Toast.makeText(this, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = Uri.fromFile(photoFile);
                setCapturedImageURI(fileUri);
                setCurrentPhotoPath(fileUri.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCapturedImageURI());
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    /**
     * Creates the image file to which the image must be saved.
     * @return
     * @throws IOException
     */
    protected File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        setCurrentPhotoPath("file:" + image.getAbsolutePath());
        return image;
    }


    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

    public Uri getCapturedImageURI() {
        return mCapturedImageURI;
    }

    public void setCapturedImageURI(Uri mCapturedImageURI) {
        this.mCapturedImageURI = mCapturedImageURI;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mCurrentPhotoPath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath);
        }
        if (mCapturedImageURI != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_URI_KEY, mCapturedImageURI.toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_URI_KEY)) {
            mCapturedImageURI = Uri.parse(savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * The activity returns with the photo.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            addPhotoToGallery();
            // Show the full sized image.
            mImage = resizeImage(getCurrentPhotoPath(),300,300);
            mImageCode = BagContract.ItemEntry.REAL_IMAGE;
            switchToCompletedView();
        } else {
            // Back button pressed when in camera  view
        } if (requestCode == REQUEST_GET_ICON && resultCode == Activity.RESULT_OK){
            mResName = data.getStringExtra(IconChooserActivity.ICON_NAME_RESULT_KEY);
            Log.e("Shoe",mResName);
            mImageCode = BagContract.ItemEntry.ICON_IMAGE;
            switchToCompletedView();
        } else {

            // Back button from icon view
        }

    }


    /**
     * Scale the photo down and fit it to our image views.
     *
     * "Drastically increases performance" to set images using this technique.
     * Read more:http://developer.android.com/training/camera/photobasics.html
     */
    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        Log.e("H/W", String.valueOf(targetH) +" , "+ String.valueOf(targetW));

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        mImage = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(mImage);
    }

    /**
     * Resize imgage to hight and width
     */
    private Bitmap resizeImage(String imagePath, int width, int height) {
        // Get the dimensions of the View
        int targetW = width;
        int targetH = height;

        Log.e("H/W", String.valueOf(targetH) +" , "+ String.valueOf(targetW));

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(imagePath, bmOptions);
    }

    /**
     * Add the picture to the photo gallery.
     * Must be called on all camera images or they will
     * disappear once taken.
     */
    protected void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(getCurrentPhotoPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }
}