package com.markshoe.stashapp;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.astuetz.PagerSlidingTabStrip.IconTabProvider;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.markshoe.stashapp.fragment.ItemsFragment;
import com.markshoe.stashapp.fragment.MapsFragment;
import com.markshoe.stashapp.fragment.TransactionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shoe
 *
 */
public class IconViewPagerAdapter extends FragmentStatePagerAdapter
/**
 * PAGERSLIDINGTABSTIPS
 */
        implements IconTabProvider
{
    // Declare the number of ViewPager pages
//  final int PAGE_COUNT = 5;
    final int PAGE_COUNT = 3;
    Context mContext;
    MaterialViewPager mViewPager;
    int oldPosition = -1;
    private final List<StateDrawable> ICON_STATES = new ArrayList<StateDrawable>();


    /**
     * @param fm
     */
    public IconViewPagerAdapter(FragmentManager fm, Context c, MaterialViewPager m) {
        super(fm);
        mContext = c;
        mViewPager = m;
        int normal =  mContext.getResources().getColor(R.color.grey_900);
        int highlighted = mContext.getResources().getColor(R.color.primary_dark);
        Drawable bags = c.getResources().getDrawable(R.drawable.bags_slidingtab_thin);
        Drawable maps = c.getResources().getDrawable(R.drawable.maps_slidingtab_thin);
        Drawable logs = c.getResources().getDrawable(R.drawable.logs_slidingtab_thin);
        createSelector(bags,highlighted, new Drawable[] {maps,logs}, normal);
        createSelector(maps,highlighted, new Drawable[] {bags,logs}, normal);
        createSelector(logs, highlighted, new Drawable[]{maps, bags}, normal);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ItemsFragment.newInstance();
            case 1:
                return MapsFragment.newInstance();
            case 2:
                return TransactionFragment.newInstance();
            default:
                return ItemsFragment.newInstance();
        }
    }

    /**
     * PAGERSLIDINGTABSTRIPS
     */
    @Override
    public Drawable getPageIconDrawable(int position) {
        return ICON_STATES.get(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        //only if position changed
        if (position == oldPosition)
            return;
        oldPosition = position;

        int color = 0;
        Drawable d = null;
        String url="";
        switch (position) {
            case 0:
                d = mContext.getResources().getDrawable(R.drawable.skyline2);
                url = "https://www.thelongeststay.com/media/catalog/product/cache/5/image/9df78eab33525d08d6e5fb8d27136e95/n/e/new_york_sykline_wall_clock.jpg";
                color = mContext.getResources().getColor(R.color.white);// 0xFFFFFF;//mContext.getResources().getColor(Co);
                break;//0x2F97BA;
            case 1:
                d = mContext.getResources().getDrawable(R.drawable.skyline2);
                url = "https://s-media-cache-ak0.pinimg.com/736x/34/ac/e5/34ace5629b83681632bf38dd13c40ca7.jpg";
                color = mContext.getResources().getColor(R.color.white);
                break;
            case 2:
                d = mContext.getResources().getDrawable(R.drawable.skyline2);
                url = "http://www.clker.com/cliparts/z/a/T/u/F/3/large-gray-city-skyline-md.png";
                color = mContext.getResources().getColor(R.color.white);
                break;
        }

        final int fadeDuration = 400;
        PagerSlidingTabStrip s = mViewPager.getPagerTitleStrip();
        mViewPager.setColor(color, fadeDuration);
        //mViewPager.setImageDrawable(d, fadeDuration);
        //mViewPager.setImageUrl(url,400);


    }


    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Bags";
            case 1:
                return "Locations";
            case 2:
                return "Logs";
        }
        return "";
    }


    public void createSelector(Drawable d, int highlighted, Drawable[] other, int normal){
        int red = (highlighted & 0xFF0000) / 0xFFFF;
        int green = (highlighted & 0xFF00) / 0xFF;
        int blue = highlighted & 0xFF;
        float[] matrix = { 0, 0, 0, 0, red
                , 0, 0, 0, 0, green
                , 0, 0, 0, 0, blue
                , 0, 0, 0, 1, 0 };
        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        StateDrawable states = new StateDrawable(new Drawable[] { d }, colorFilter, other,normal);
        ICON_STATES.add(states);
    }

    public class StateDrawable extends LayerDrawable {
        public ColorFilter colorFilter;
        public Drawable d;
        public Drawable[] s ;
        public int normal;

        public StateDrawable(Drawable[] layers,ColorFilter colorFilter,Drawable[] other, int normal) {
            super(layers);
            this.d=layers[0];
            s = other;
            this.normal = normal;
            this.colorFilter = colorFilter;
        }

        @Override
        protected boolean onStateChange(int[] states) {
            for (int state : states) {
                if (state == android.R.attr.state_selected) {
                    d.setColorFilter(colorFilter);
                    s[0].setColorFilter(normal, PorterDuff.Mode.SRC_ATOP);
                    s[1].setColorFilter(normal, PorterDuff.Mode.SRC_ATOP);
                }
            }
            return super.onStateChange(states);
        }

        @Override
        public boolean isStateful() {
            return true;
        }

    }
}