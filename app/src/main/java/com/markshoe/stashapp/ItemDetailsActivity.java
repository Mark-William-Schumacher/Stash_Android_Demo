package com.markshoe.stashapp;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by shoe on 15-06-13.
 */
public class ItemDetailsActivity extends Activity {
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 500;
    ColorDrawable mBackground;
    int mLeftDelta;
    int mTopDelta;
    float mWidthScale;
    float mHeightScale;
    private ImageView mImageView;
    private View mDescription;
    private View mBackDrop;
    private FrameLayout mTopLevelLayout;
    private int mOriginalOrientation;
    private GradientDrawable mGd;
    private View mDetailLayout;
    boolean mRequestedLocation; // used for exit animation choise
    boolean mItemNameChanged;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mTopLevelLayout = (FrameLayout) findViewById(R.id.topLevelLayout);
        mDescription =  findViewById(R.id.description);
        mBackDrop = findViewById(R.id.backdropColor);
        Button getLocationButton  = (Button) findViewById(R.id.get_location_button);
        Button changeIconButton  = (Button) findViewById(R.id.change_icon_button);
        ImageView backArrow =(ImageView) findViewById(R.id.back_arrow);
        final MaterialEditText editText = (MaterialEditText) findViewById(R.id.edit_text);


        // Retrieve the data we need for the picture/description to display and
        // the thumbnail to animate it from
        Bundle bundle = getIntent().getExtras();
        int resId = bundle.getInt("iconId");
        final int thumbnailTop = bundle.getInt("top");
        final int thumbnailLeft = bundle.getInt("left");
        final int color = bundle.getInt("color");
        final int thumbnailWidth = bundle.getInt("iconWidth");
        final int thumbnailHeight = bundle.getInt("iconHeight");
        mOriginalOrientation = bundle.getInt("orientation");
        mImageView.setImageDrawable(getResources().getDrawable(resId));


        // Set bundle inputs
        mGd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {color,0xFFFFFFFF});
        mGd.setCornerRadius(0f);
        mBackDrop.setBackground(mGd);
        mBackground = new ColorDrawable(Color.WHITE);
        mTopLevelLayout.setBackground(mBackground); // transparent background needs to fade in to a white color
        editText.setText(bundle.getString("title"), TextView.BufferType.EDITABLE);
        editText.setPrimaryColor(color); // Looks nice on select
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mItemNameChanged = true;
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // set onclick listeners
        ItemDetailsClickListener mListener = new ItemDetailsClickListener();
        getLocationButton.setOnClickListener(mListener);
        changeIconButton.setOnClickListener(mListener);
        backArrow.setOnClickListener(mListener);

        // Only run the animation if we're coming from the parent activity, not if
        // we're recreated automatically by the window manager (e.g., device rotation)
        if (savedInstanceState == null) {
            ViewTreeObserver observer = mImageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    mImageView.getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) thumbnailWidth / mImageView.getWidth();
                    mHeightScale = (float) thumbnailHeight / mImageView.getHeight();
                    runEnterAnimation();
                    return true;
                }
            });
        }
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location, colorizing it in parallel. In parallel, the background of the
     * activity is fading in. When the pictue is in place, the text description
     * drops down.
     */
    public void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION * 1);

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        mImageView.setPivotX(0);
        mImageView.setPivotY(0);
        mImageView.setScaleX(mWidthScale);
        mImageView.setScaleY(mHeightScale);
        mImageView.setTranslationX(mLeftDelta);
        mImageView.setTranslationY(mTopDelta);

        // We'll fade the text in later
        mDescription.setAlpha(0);
        // Animate scale and translation to go from thumbnail to full size
        mImageView.animate().setDuration(duration).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).
                withEndAction(new Runnable() {
                    public void run() {
                        // Animate the description in after the image animation
                        // is done. Slide and fade the text in from underneath
                        // the picture.
                        mDescription.setTranslationX(-mDescription.getWidth());
                        mDescription.animate().setDuration(duration / 2).
                                translationX(0).alpha(1).
                                setInterpolator(sDecelerator);
                    }
                });


        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnim.setDuration(duration);
        bgAnim.start();

        ObjectAnimator backDropanim = ObjectAnimator.ofInt(mGd, "alpha", 0, 255);
        backDropanim.setDuration(duration);
        backDropanim.start();
    }

    /**
     * The exit animation is basically a reverse of the enter animation, except that if
     * the orientation has changed we simply scale the picture back into the center of
     * the screen.
     *
     * @param endAction This action gets run after the animation completes (this is
     * when we actually switch activities)
     */
    public void runExitAnimation(final Runnable endAction) {
        final long duration = (long) (ANIM_DURATION * 1);

        // No need to set initial values for the reverse animation; the image is at the
        // starting size/location that we want to start from. Just animate to the
        // thumbnail size/location that we retrieved earlier

        // Caveat: configuration change invalidates thumbnail positions; just animate
        // the scale around the center. Also, fade it out since it won't match up with
        // whatever's actually in the center
        final boolean fadeOut;
        if (getResources().getConfiguration().orientation != mOriginalOrientation || mRequestedLocation) {
            mImageView.setPivotX(mImageView.getWidth() / 2);
            mImageView.setPivotY(mImageView.getHeight() / 2);
            mLeftDelta = 0;
            mTopDelta = 0;
            fadeOut = true;
        } else {
            fadeOut = false;
        }

        ObjectAnimator bgAnim = ObjectAnimator.ofFloat(mBackground, "alpha", 0.8f, 0);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();

        ObjectAnimator backDropanim = ObjectAnimator.ofInt(mGd, "alpha", 255, 0);
        backDropanim.setDuration(ANIM_DURATION);
        backDropanim.start();


        // First, slide/fade text out of the way
        mDescription.animate().translationX(-mDescription.getWidth()).alpha(0).
                setDuration(duration / 2).setInterpolator(sAccelerator).
                withEndAction(new Runnable() {
                    public void run() {
                        // Animate image back to thumbnail size/location
                        mImageView.animate().setDuration(duration).
                                scaleX(mWidthScale).scaleY(mHeightScale).
                                translationX(mLeftDelta).translationY(mTopDelta).
                                withEndAction(endAction);
                        if (fadeOut) {
                            mImageView.animate().alpha(0);
                        }
                        // Fade out background
                        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
                        bgAnim.setDuration(duration);
                        bgAnim.start();

                        // Animate a color filter to take the image back to grayscale,
                        // in parallel with the image scaling and moving into place.
                        ObjectAnimator colorizer =
                                ObjectAnimator.ofFloat(ItemDetailsActivity.this,
                                        "saturation", 1, 0);
                        colorizer.setDuration(duration);
                        colorizer.start();
                    }
                });

    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it is complete.
     */
    @Override
    public void onBackPressed() {
        runExitAnimation(new Runnable() {
            public void run() {
                // *Now* go ahead and exit the activity
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }



    private class ItemDetailsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.get_location_button){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", 1);
                setResult(Activity.RESULT_OK, returnIntent);
                mRequestedLocation = true;
                runExitAnimation(new Runnable() {
                    public void run() {
                        // *Now* go ahead and exit the activity
                        finish();
                    }
                });
            }
            if (id == R.id.back_arrow){
                onBackPressed();
            }
        }
    }
}

