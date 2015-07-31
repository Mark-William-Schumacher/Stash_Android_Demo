package com.markshoe.stashapp.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.markshoe.stashapp.Backpack;
import com.markshoe.stashapp.R;

/**
 * Created by shoe on 15-05-23.
 * Custom view that creates an icon and puts a highlighted shadow of the icon based on the sent in
 * highlighted color.
 * To use the class , instantiate the view and set the icon res id and setHighlightColour
 */
public class HighlightedDrawableTransaction extends View {
    private static final String LOG_TAG = HighlightedDrawableTransaction.class.getSimpleName();
    /* Icon for the item*/
    private Drawable mIcon ;
    /* HighlightColor */
    private int mHighlightColour;
    private int mIconId;
    private String mTitle;
    public long mItemId;
    Paint mPaint ;


    public HighlightedDrawableTransaction(Context context) { this(context, null); }
    public HighlightedDrawableTransaction(Context context, AttributeSet attrs) { this(context, attrs, 0); }
    public HighlightedDrawableTransaction(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ItemInGridView, 0, 0);
        setIconDrawable(a.getDrawable(R.styleable.ItemInGridView_android_drawable));
        mHighlightColour = a.getInteger(R.styleable.ItemInGridView_highlight_color, 0);
        a.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF9f9f9f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width = widthSize;
        int height = width;
        setMeasuredDimension(width, height);
    }

    protected void onDraw(Canvas canvas){

        mIcon.setBounds(0-5, 0+5, getMeasuredWidth()-5, getMeasuredHeight()+5);

        int red = (mHighlightColour & 0xFF0000) / 0xFFFF;
        int green = (mHighlightColour & 0xFF00) / 0xFF;
        int blue = mHighlightColour & 0xFF;
        float[] matrix = { 0, 0, 0, 0, red
                , 0, 0, 0, 0, green
                , 0, 0, 0, 0, blue
                , 0, 0, 0, 1, 0 };
        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        mIcon.setColorFilter(colorFilter);
        mIcon.setAlpha(190);
        mIcon.draw(canvas);

        mIcon.setBounds(0 , 0 , getMeasuredWidth(), getMeasuredHeight());
        int black = 0xFF000000;
        red = (black & 0xFF0000) / 0xFFFF;
        green = (black & 0xFF00) / 0xFF;
        blue = black & 0xFF;
        matrix = new float[]{ 0, 0, 0, 0, red
                , 0, 0, 0, 0, green
                , 0, 0, 0, 0, blue
                , 0, 0, 0, 1, 0 };
        ColorFilter colorFilter2 = new ColorMatrixColorFilter(matrix);
        mIcon.setColorFilter(colorFilter2);
        mIcon.setAlpha(255);
        mIcon.draw(canvas);





    }

    //
    // SETTERS AND GETTERS
    //
    public void setProperties(int resId, int highlightColour){
        setIcon(resId);
        setHighlightColour(highlightColour);
    }


    public void setIcon(int resId){
        setIconDrawable(getResources().getDrawable(resId));
        mIconId = resId;
        return;
    }
    public int getIconResId(){
        return mIconId;
    }

    private void setIconDrawable(Drawable d){
        if (d!=null){
            mIcon=d;
        }
    }

    public void setItem(Backpack.Item i){
        mItemId = i.itemID;
        setIcon(i.resId);
        setHighlightColour(i.color);
        setTitle(i.title);
    }

    public void setItem(long itemId, int resId, int color, String title){
        mItemId = itemId;
        setIcon(resId);
        setHighlightColour(color);
        setTitle(title);
    }

    public void setTitle(String title){
        mTitle = title;
    }
    public String getTitle(){
        return mTitle;
    }
    public int getHighlightColour(){
        return mHighlightColour;
    }
    public void setHighlightColour(int color){
        mHighlightColour = color;
    }
}
