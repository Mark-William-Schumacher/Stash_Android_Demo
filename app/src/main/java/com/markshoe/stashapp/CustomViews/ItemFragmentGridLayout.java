package com.markshoe.stashapp.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shoe on 15-06-09.
 * Variable number of columns for the density of the screen the views will be a minimum of 60dp
 * per cell. From there they are scaled up to perfectly fit the column.
 *
 * Based loosely on:
 * https://github.com/devunwired/custom-view-examples/blob/master/app/src/main/java/com/example/customview/widget/BoxGridLayout.java
 * Tutorial at:
 * https://newcircle.com/s/post/1663/tutorial_enhancing_android_ui_with_custom_views_dave_smith_video
 */
public class ItemFragmentGridLayout extends ViewGroup {
    int MIN_DP_FOR_CELL = 60;
    int PADDING_BETWEEN_CELLS = 8; // In dp , TODO change this to a attrs input later
    int mTruncatedColNumber;
    int mExpandedHeight = 0;
    int mPadding;
    boolean mGridLinesOn = false;
    List<int[]> mLinesToDraw = new ArrayList<>();
    Paint mPaint;

    public ItemFragmentGridLayout(Context context) {
        this(context,null,0);
    }

    public ItemFragmentGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemFragmentGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0x889f9f9f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(3);
    }

    public void setExpandedHeight(int height){ mExpandedHeight = height;}
    public int getExpandedHeight(){ return mExpandedHeight;}

    /**
     * Here we don't really care about the width and height measure spec, we know that our height
     * spec will always be wrap content, or exact == 0 dp when we collapse the view
     *   Width is always fill parent
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {

        // Getting the width per grid cell
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int density = (int) getResources().getDisplayMetrics().density;
        int dipsWidth = (int) (width / density);
        mPadding = (int) (PADDING_BETWEEN_CELLS*density);
        mTruncatedColNumber = (dipsWidth-PADDING_BETWEEN_CELLS) / (MIN_DP_FOR_CELL+PADDING_BETWEEN_CELLS);
        int numRows = ((getChildCount()-1) / mTruncatedColNumber) + 1;
        int widthPerItem = ((width-mPadding)/mTruncatedColNumber)-mPadding;

        //width / mTruncatedColNumber;
        int widthSpec = MeasureSpec.makeMeasureSpec(widthPerItem, MeasureSpec.EXACTLY);

        // Sending the created width into the child views with the make measure spec
        measureChildren(widthSpec, widthSpec);
        mLinesToDraw = new ArrayList<>();

        int ExpandableHeight;
        if (getChildAt(0) != null)
            ExpandableHeight = ((getChildAt(0).getMeasuredHeight()+mPadding) * numRows+1)+mPadding;
        else
            ExpandableHeight =0;
        // Set Measure Dimenstion must call this
        setMeasuredDimension(width,ExpandableHeight);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        invalidate();
    }


    @Override
    protected void onLayout(boolean c, int l , int t, int r, int b){
        //super.onLayout(c,l,t,r,b);
        int row, col,left,top,shift;
        for (int i = 0; i<getChildCount(); i++){
            row = i / mTruncatedColNumber;
            col = i % mTruncatedColNumber;
            View child = getChildAt(i);
            left = ((child.getMeasuredWidth())+mPadding)*col+mPadding;
            top = ((child.getMeasuredHeight())+mPadding)*row+mPadding;
            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());


            int w = child.getMeasuredWidth();
            int h = child.getMeasuredHeight();
            if (mGridLinesOn)
                addLines(w,h,left,top,row,col);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (mGridLinesOn) {
            for (int[] lines : mLinesToDraw) {
                canvas.drawLine(lines[0], lines[1], lines[2], lines[3], mPaint);
            }
        }
    }

    /**
     * Add lines to the background of the grid
     */
    private void addLines(int w, int h,int left, int top, int row, int col){
        if (row==0 && col ==0){
            mLinesToDraw.add((new int[]{left - (mPadding / 2), top+(mPadding/2), left - (mPadding / 2), h + top - mPadding}));
            mLinesToDraw.add((new int[]{left+(mPadding/2), top - (mPadding / 2), left + w - mPadding, top - (mPadding / 2)}));
        }else {
            if (col == 0)
                mLinesToDraw.add((new int[]{left - (mPadding / 2), top, left - (mPadding / 2), h + top - mPadding}));
            if (row == 0)
                mLinesToDraw.add((new int[]{left, top - (mPadding / 2), left + w - mPadding, top - (mPadding / 2)}));
        }

        mLinesToDraw.add((new int[]{left+w,top,left+w,h+top-mPadding}));
        mLinesToDraw.add((new int[]{left,h+top,left+w-mPadding,h+top}));
    }
}
