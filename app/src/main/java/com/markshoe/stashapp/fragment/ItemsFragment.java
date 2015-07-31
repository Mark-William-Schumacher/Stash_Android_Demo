package com.markshoe.stashapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.support.v4.app.LoaderManager;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.markshoe.stashapp.BackpackCardAdapter;
import com.markshoe.stashapp.BackpackContentsActivity;
import com.markshoe.stashapp.CustomViews.HighlightedDrawable;
import com.markshoe.stashapp.ItemDetailsActivity;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.markshoe.stashapp.R;
import com.markshoe.stashapp.data.BagContract;


/**
 * Created by shoe on 15-06-09.
 */
public class ItemsFragment extends Fragment implements BackpackCardAdapter.BagListener, LoaderManager.LoaderCallbacks<Cursor> {
    final String LOG_TAG = this.getClass().getSimpleName();
    int ITEMS_LOADER = 0;
    LinearLayout mBackpackContainer;
    ObservableScrollView mScrollView;
    BackpackCardAdapter cardAdapter;
    MaterialViewPager mViewPager;


    public static ItemsFragment newInstance() {
        return new ItemsFragment();
    }


    public void onActivityCreated(Bundle savedIntanceState) {
        getLoaderManager().initLoader(ITEMS_LOADER, null, this);
        super.onActivityCreated(savedIntanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mScrollView = (ObservableScrollView) inflater.inflate(R.layout.fragment_items, container, false);
        mViewPager = (MaterialViewPager) getActivity().findViewById(R.id.materialViewPager);
        return mScrollView;
    }

    @Override
    public void itemClicked(View v, long bagId, String bagName) {

        int viewId = v.getId();
        if (viewId ==R.id.view_contents_button){
            Intent backpackContentsActivity = new Intent(getActivity(), BackpackContentsActivity.class);
            backpackContentsActivity.putExtra("BACKPACK_ID", bagId);
            backpackContentsActivity.putExtra("BAG_NAME", bagName);
            startActivity(backpackContentsActivity);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            mViewPager.getViewPager().setCurrentItem(1);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBackpackContainer = (LinearLayout) view.findViewById(R.id.backpack_container);
        cardAdapter = new BackpackCardAdapter(getActivity(),null,mBackpackContainer,this);
        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }


    /*
                        Loader Callback function
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = BagContract.BagEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                uri,
                null,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cardAdapter.updateCursor(cursor);
//        cardAdapter.updateCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        cardAdapter.updateCursor(null);
    }

    /*
                        End Loader Callback function
     */


}