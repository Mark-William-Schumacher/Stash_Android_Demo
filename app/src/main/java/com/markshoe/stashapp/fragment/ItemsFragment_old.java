//package com.markshoe.stashapp.fragment;
//
//import android.animation.LayoutTransition;
//import android.app.Activity;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.CursorLoader;
//import android.support.v4.content.Loader;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//
//import com.github.florent37.materialviewpager.MaterialViewPager;
//import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
//import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
//import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
//import com.markshoe.stashapp.Backpack;
//import com.markshoe.stashapp.CustomViews.HighlightedDrawable;
//import com.markshoe.stashapp.ItemDetailsActivity;
//import com.markshoe.stashapp.R;
//import com.markshoe.stashapp.data.BagContract;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by shoe on 15-06-09.
// */
//public class GridFragment extends Fragment implements com.markshoe.stashapp.InnerBackpackAdapter.BagListener, LoaderManager.LoaderCallbacks<Cursor> {
//    final String LOG_TAG = this.getClass().getSimpleName();
//    int ITEMS_LOADER = 0;
//    private RecyclerView mRecyclerView;
//    RecyclerViewMaterialAdapter mAdapter;
//    List<Backpack> listOfBps;
//    LinearLayout mBackpackContainer;
//    ObservableScrollView mScrollView;
//    com.markshoe.stashapp.InnerBackpackAdapter adapter;
//    MaterialViewPager mViewPager;
//    ArrayList<Backpack.Item> backpack1contents;
//    ArrayList<Backpack.Item> backpack2contents;
//
//
//    public static ItemsFragment_old newInstance() {
//        return new ItemsFragment_old();
//    }
//
//
//    public void onActivityCreated(Bundle savedIntanceState) {
//        getLoaderManager().initLoader(ITEMS_LOADER, null, this);
//        super.onActivityCreated(savedIntanceState);
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        mScrollView = (ObservableScrollView) inflater.inflate(R.layout.fragment_items, container, false);
//        mViewPager = (MaterialViewPager) getActivity().findViewById(R.id.materialViewPager);
//        return mScrollView;
//    }
//
//    @Override
//    public void itemClicked(View v, long bagId) {
//        if (viewId == R.id.item_in_backpack){
//            HighlightedDrawable item = (HighlightedDrawable) v.findViewById(R.id.highlighted_drawable);
//            int[] screenLocation = new int[2];
//            item.getLocationOnScreen(screenLocation);
//            int orientation = getActivity().getResources().getConfiguration().orientation;
//            Intent subActivity = new Intent(getActivity(), ItemDetailsActivity.class);
//            subActivity.putExtra("left", screenLocation[0]).
//                    putExtra("top",screenLocation[1]).
//                    putExtra("iconId",item.getIconResId()).
//                    putExtra("title",item.getTitle()).
//                    putExtra("iconWidth", item.getWidth()).
//                    putExtra("iconHeight", item.getHeight()).
//                    putExtra("color", item.getHighlightColour()).
//                    putExtra("orientation", orientation);
//            startActivityForResult(subActivity,1);
//            getActivity().overridePendingTransition(0, 0);
//        }
//        if(viewId == R.id.more_info_button){
//            adapter.setBagItems(1, backpack2contents);
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
//            mViewPager.getViewPager().setCurrentItem(1);
//        }
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mBackpackContainer = (LinearLayout) view.findViewById(R.id.backpack_container);
//
//
//        // SET ADAPTER
//        // HERE
//
//
//        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
//    }
//
//
//    /*
//                        Loader Callback function
//     */
//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        Uri uri = BagContract.BagEntry.CONTENT_URI;
//        return new CursorLoader(getActivity(),
//                uri,
//                null,
//                null,
//                null,
//                null);
//    }
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        cardAdapter.updateCursor(cursor);
////        cardAdapter.updateCursor(cursor);
//    }
//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//        adapter.swapCursor(null);
//    }
//    /*
//                        End Loader Callback function
//     */
//
//
//}
//
///*
//
//    public ArrayList<Backpack.Item> createfakebps(){
//        int blue,orange,gold,green,purple;
//        blue=getResources().getColor(R.color.tag_blue);
//        orange=getResources().getColor(R.color.tag_orange);
//        gold=getResources().getColor(R.color.tag_gold);
//        green=getResources().getColor(R.color.tag_green);
//        purple=getResources().getColor(R.color.tag_purple);
//        backpack1contents = new ArrayList<Backpack.Item>();
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_books_21509, "Bio Books", blue,1));
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_books_70129, "Learning Java", orange,2));
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_charger_150532, "Kar Buds", blue,3));
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_charger_8972, "HDMI cable", green,4));
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_energy_drink_11928, "IPad", gold,5));
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_ipad_4586, "Car Keys", orange,6));
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_ipod_15183, "Macbook", blue,7));
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_ipod_415, "Mouse", purple,8));
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_laptop_3960, "Gym Shoes", green,9));
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_keys_104114, "backpack", blue,10));
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_mouse_890, "Keyboard", green,11));
//        backpack1contents.add(new Backpack.Item(R.drawable.noun_passport_10896, "MathBook", gold,12));
//        Backpack bp1 = new Backpack("Main Backpack",R.drawable.backpack,backpack1contents,1,true,1);
//
//        backpack2contents = new ArrayList<Backpack.Item>();
//        backpack2contents.add(new Backpack.Item(R.drawable.noun_ipad_4586, "Car Keys", green,13));
//        backpack2contents.add(new Backpack.Item(R.drawable.noun_ipod_15183, "Macbook", orange,14));
//        backpack2contents.add(new Backpack.Item(R.drawable.noun_ipod_415, "Mouse", purple,15));
//        backpack2contents.add(new Backpack.Item(R.drawable.noun_keyboard_16595, "Gym Shoes", green,16));
//        backpack2contents.add(new Backpack.Item(R.drawable.noun_books_21509, "Bio Books", purple,17));
//        backpack2contents.add(new Backpack.Item(R.drawable.noun_books_70129, "Learning Java", blue,18));
//        backpack2contents.add(new Backpack.Item(R.drawable.noun_charger_150532, "Ear Buds", orange,19));
//        Backpack bp2 = new Backpack("Gym Bag",R.drawable.dufflebag,backpack2contents,1,true,2);
//
//
//        Backpack bp3 = new Backpack("Out of Bag: ", R.drawable.duffle_icon_inner, backpack1contents,0,true,0);
//        listOfBps = new ArrayList<>();
//        listOfBps.add(bp1);
//        listOfBps.add(bp2);
//        listOfBps.add(bp3);
//        return backpack1contents;
//    }
//*/