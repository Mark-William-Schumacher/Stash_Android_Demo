package com.markshoe.stashapp.fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.markshoe.stashapp.CustomViews.HighlightedDrawable;
import com.markshoe.stashapp.CustomViews.ItemFragmentGridLayout;
import com.markshoe.stashapp.R;
import com.markshoe.stashapp.data.BagContract;

/**
 * Created by shoe on 15-07-17.
 */
public class BackpackItemsGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener{
    static final int ITEMS_LOADER = 3809119;
    ScrollView mScrollView;
    ItemFragmentGridLayout mGridView;
    BackpackItemsGridFragmentAdapter itemGridAdapter;
    long bagId = 0;

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity(),(String) (Long.toString((Long)v.getTag(R.id.cast_notification_id))),Toast.LENGTH_SHORT).show();
    }

    /**
     * Only really used for loader instatiation
     * @param savedIntanceState
     */
    public void onActivityCreated(Bundle savedIntanceState) {
        getLoaderManager().initLoader(ITEMS_LOADER, null, this);
        super.onActivityCreated(savedIntanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle!= null)
            bagId = bundle.getLong("BACKPACK_ID");
        mScrollView = (ScrollView) inflater.inflate(R.layout.fragment_backpack_grid_view, container,false);
        mGridView = (ItemFragmentGridLayout) mScrollView.findViewById(R.id.grid_layout);
        itemGridAdapter = new BackpackItemsGridFragmentAdapter(getActivity(),null,mGridView,this);
        return mScrollView;
    }

    public void itemCallback(long itemId){
        Toast.makeText(getActivity(),String.valueOf(itemId),Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = BagContract.BagEntry.buildBagItemsUriById(bagId);
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        itemGridAdapter.updateCursor(data);
        getLoaderManager().destroyLoader(ITEMS_LOADER);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        itemGridAdapter.updateCursor(null);
    }


    public class BackpackItemsGridFragmentAdapter {
        public boolean mSetup;
        Context mContext;
        LayoutInflater mInflater;
        ViewGroup mContainer;
        View.OnClickListener mClickListener;

        public BackpackItemsGridFragmentAdapter(Context context, Cursor data, ViewGroup container, View.OnClickListener cl) {
            this.mContext = context;
            mClickListener = cl;
            mInflater = LayoutInflater.from(context);
            mContainer = container;
            mContainer.removeAllViews();
            updateCursor(data);
        }

        public void updateCursor(Cursor cursor) {
            // item_res_name[7] item_color[6] date_description[5]
            // current_bag_key[4] item_description[3] item_name[2] tag_id[1] _id[0]
            if (cursor == null || !cursor.moveToFirst()) {
                mGridView.removeAllViews();
                return;
            }
            cursor.moveToFirst();
            do  {
                String resName = cursor.getString(7);
                int color = cursor.getInt(6);
                long itemId = cursor.getLong(0);
                int resId = mContext.getResources().getIdentifier(resName, "drawable", mContext.getPackageName());
                if (resId == 0){
                    Log.e("bp-ad", "Error could not find a resourse for: " + resName +
                            " defaulting to backpack icon");
                    resId = R.drawable.bag_icon;
                }
                HighlightedDrawable hd= new HighlightedDrawable(getActivity());
                hd.setTag(R.id.cast_notification_id, itemId);
                hd.setProperties(resId, color);
                hd.setOnClickListener(mClickListener);
                mContainer.addView(hd);
            }while (cursor.moveToNext());
        }
    }
}
