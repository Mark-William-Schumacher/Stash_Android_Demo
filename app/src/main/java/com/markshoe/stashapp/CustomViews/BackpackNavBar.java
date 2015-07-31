package com.markshoe.stashapp.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.markshoe.stashapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shoe on 15-07-09.
 */
public class BackpackNavBar extends RelativeLayout implements View.OnClickListener{
    private BackpackNavBarListener mBpListener;
    Context mContext;
    LayoutInflater mInflater;
    int mBaseColor;
    int mHighlightedColor;
    ColorFilter colorFilter;
    List<ImageView> al = new ArrayList<>();
    List<Drawable> dl = new ArrayList<>();
    int currentIcon = 0;
    HashMap<View, Integer> viewToIdMap = new HashMap<>();

    public BackpackNavBar(Context context){this(context, null);}
    public BackpackNavBar(Context context, AttributeSet attrs){this(context, attrs, 0);}
    public BackpackNavBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.nav_bar_layout, this, true);
        ImageView gridView = (ImageView) findViewById(R.id.griddrawable);
        ImageView lis = (ImageView) findViewById(R.id.listdrawable);
        ImageView local = (ImageView) findViewById(R.id.localdrawable);

        mContext = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BackpackNavBar, 0, 0);
        Drawable d1 = a.getDrawable(R.styleable.BackpackNavBar_drawable1);
        Drawable d2 = a.getDrawable(R.styleable.BackpackNavBar_drawable2);
        Drawable d3 = a.getDrawable(R.styleable.BackpackNavBar_drawable3);
        int selected = a.getInt(R.styleable.BackpackNavBar_selected, 0);
        dl.add( d1 );
        dl.add( d2 );
        dl.add( d3 );
        al.add(gridView);
        al.add(lis);
        al.add(local);
        (al.get(0)).setImageDrawable(dl.get(0));
        (al.get(1)).setImageDrawable(dl.get(1));
        (al.get(2)).setImageDrawable(dl.get(2));


        mBaseColor = a.getInt(R.styleable.BackpackNavBar_baseColor, Color.BLACK);
        mHighlightedColor = a.getInt(R.styleable.BackpackNavBar_highlightedColor, Color.RED);
        int red = (mHighlightedColor & 0xFF0000) / 0xFFFF;
        int green = (mHighlightedColor & 0xFF00) / 0xFF;
        int blue = mHighlightedColor & 0xFF;
        float[] matrix = { 0, 0, 0, 0, red
                , 0, 0, 0, 0, green
                , 0, 0, 0, 0, blue
                , 0, 0, 0, 1, 0};
        colorFilter = new ColorMatrixColorFilter(matrix);
        a.recycle();


        viewToIdMap.put(findViewById(R.id.grid), 0);
        findViewById(R.id.grid).setOnClickListener(this);
        viewToIdMap.put(findViewById(R.id.list), 1);
        findViewById(R.id.list).setOnClickListener(this);
        viewToIdMap.put(findViewById(R.id.local), 2);
        findViewById(R.id.local).setOnClickListener(this);
    }

    public void setNavBarListener(BackpackNavBarListener listener){
        mBpListener = listener;
    }

    public void setClicked(int j){
        Log.e("Shoe",String.valueOf(j));
        for (int i = 0 ; i < al.size(); i++ ){
            if (i == j){
                dl.get(i).setColorFilter(colorFilter);
                al.get(i).setImageDrawable(dl.get(i));
                currentIcon = i;
            } else{
                dl.get(i).setColorFilter(mBaseColor, PorterDuff.Mode.SRC_ATOP);
                al.get(i).setImageDrawable(dl.get(i));
            }
        }
    }
    @Override
    public void onClick(View v) {
        setClicked(viewToIdMap.get(v));
        mBpListener.navButtonClicked(v);

    }

    public interface BackpackNavBarListener{
        public void navButtonClicked(View view);
    }
}