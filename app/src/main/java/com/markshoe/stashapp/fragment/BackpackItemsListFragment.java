package com.markshoe.stashapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.markshoe.stashapp.ActivityIconChooser;
import com.markshoe.stashapp.ActivityItemDetails;
import com.markshoe.stashapp.CustomViews.HighlightedDrawable;
import com.markshoe.stashapp.R;
import com.markshoe.stashapp.data.BagContract;
import com.markshoe.stashapp.data.DbUtility;

import java.sql.Blob;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shoe on 15-07-17.
 */
public class BackpackItemsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener{
    int ITEMS_LOADER = 0;
    ScrollView mScrollView;
    LinearLayout mLinearLayout;
    long bagId = 0;
    Cursor mData;
    BackpackItemsListFragmentAdapter itemListAdapter;
    LayoutInflater mInflater;

    int GLOBAL=0;
    String[] days = {"9:52 PM", "7:45 PM" , "Yesterday", "Yesterday", "Tuesday", "Monday", "13 Jul", "12 Jul", "7 Jul", "3 Jul", "3 Jul","3 Jul","3 Jul","3 Jul","3 Jul","3 Jul", "7 Jul", "3 Jul", "3 Jul","3 Jul","3 Jul","3 Jul","3 Jul","3 Jul"};
    String[] citys = {"Ancaster, Ontario","Stauffer ,Kingston","The Arc, Kingston","Coal Harbour, Vancouver", "Distillery District, Toronto", "St. Lawrence, Toronto" ,  "Marpole, Vancouver", "Shaughnessy, Vancouver", "South Cambie, Vancouver"
                        ,"Old Ancaster, Hamilton","West End ,Vancouver","Coal Harbour, Vancouver", "False Creek, Vancouver", "Kerrisdale, Vancouver" ,  "Marpole, Vancouver", "Shaughnessy, Vancouver", "South Cambie, Vancouver",
            "Ancaster, Ontario","West End ,Vancouver","Coal Harbour, Vancouver", "False Creek, Vancouver", "Kerrisdale, Vancouver" ,  "Marpole, Vancouver", "Shaughnessy, Vancouver", "South Cambie, Vancouver"};
    String[] alarmTimes = {"N/A", "8:30am Wed","N/A", "9:30pm Tues","5:30pm Sat", "N/A", "10:30pm Friday", "N/A", "8:30am Wed","N/A", "9:30pm Tues","5:30pm Sat", "N/A", "10:30pm Friday", "N/A","5:30pm Sat", "N/A", "10:30pm Friday", "N/A", "8:30am Wed","N/A", "9:30pm Tues","5:30pm Sat", "N/A", "10:30pm Friday", "N/A"};
    int[] icons = { 2 , 1, 2, 1, 3, 1, 4, 1, 2,1, 2, 2,1,1,1,1};
    /**
     * Only really used for loader instatiation
     * @param savedIntanceState
     */
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle!= null)
            bagId = bundle.getLong("BACKPACK_ID");
        mInflater = inflater;
        mScrollView = (ScrollView) inflater.inflate(R.layout.fragment_backpack_list_view, container, false);
        mLinearLayout = (LinearLayout) mScrollView.findViewById(R.id.linear_layout);
        if (savedInstanceState == null){
            itemListAdapter = new BackpackItemsListFragmentAdapter(getActivity(),null,mLinearLayout,this);
            getLoaderManager().initLoader(ITEMS_LOADER, null, this);
        }
        return mScrollView;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // TODO change the cursors so that can share b/t grid and list
        Uri uri = BagContract.BagEntry.buildBagItemsUriById(bagId);
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && mData != data){
            mData = data;
            itemListAdapter.updateCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    public class BackpackItemsListFragmentAdapter {
        public boolean mSetup;
        Context mContext;
        LayoutInflater mInflater;
        ViewGroup mContainer;
        View.OnClickListener mClickListener;

        public BackpackItemsListFragmentAdapter(Context context, Cursor data, ViewGroup container, View.OnClickListener cl) {
            this.mContext = context;
            mClickListener = cl;
            mInflater = LayoutInflater.from(context);
            mContainer = container;
            updateCursor(data);
        }

        public void updateCursor(Cursor cursor) {
            // item_image_blob[9],item_image_code[8],item_res_name[7] item_color[6] date_description[5]
            // current_bag_key[4] item_description[3] item_name[2] tag_id[1] _id[0]
            GLOBAL = 0 ;
            mContainer.removeAllViews();
            if (cursor == null || !cursor.moveToFirst()) {
                return;
            }
            cursor.moveToFirst();
            boolean Even=true;
            do  {
                //(assuming you have a ResultSet named RS)
                byte[] blobAsBytes = cursor.getBlob(9);
                int imageCode = cursor.getInt(8);
                String resName = cursor.getString(7);
                int color = cursor.getInt(6);
                String itemName = cursor.getString(2);
                long itemId = cursor.getLong(0);
                Drawable drawable = DbUtility.getDrawableFromByteArray(getActivity(), cursor.getBlob(9));
                if (blobAsBytes == null && imageCode != BagContract.ItemEntry.NO_IMAGE){
                    imageCode = BagContract.ItemEntry.NO_IMAGE;
                }
                View v = mInflater.inflate(R.layout.fragment_backpack_list_view_item,mContainer,false);
                //if (!Even) v.setBackgroundColor(getActivity().getResources().getColor(R.color.grey_400));
                new ItemInBagListView(v,itemId,itemName,"TIME",color,mClickListener,imageCode,drawable);
                Even = !Even;
                mContainer.addView(v);
            }while (cursor.moveToNext());
        }
    }


    public class ItemInBagListView implements View.OnClickListener{
        long bagId = 0;
        TextView tramsactionInfo;
        TextView transactionTime;
        ImageView sticker;
        TextView itemName;
        TextView locationName;
        ImageView icon;
        long mItemId;
        @Override
        public void onClick(View v) {
            Intent activityItemDetailsIntent = new Intent(getActivity(), ActivityItemDetails.class);
            activityItemDetailsIntent.putExtra(ActivityItemDetails.ITEM_ID_KEY,mItemId);
            startActivity(activityItemDetailsIntent);
        }

        public ItemInBagListView(View itemInList,long itemId, String name, String time, int color,View.OnClickListener cl,int imageCode, Drawable d) {
            //icon = (ImageView) itemInList.findViewById(R.id.icon_symbol);
            //icon.setColorFilter(color);
            mItemId= itemId;
            transactionTime = (TextView) itemInList.findViewById(R.id.transaction_time);
            itemInList.setOnClickListener(this);
//            sticker = (ImageView) itemInList.findViewById(R.id.sticker_color);
//            sticker.setColorFilter(color);
//            sticker.setAlpha(0.5f);
            //setSticker(sticker, color);


            if (imageCode==BagContract.ItemEntry.NO_IMAGE){
                HighlightedDrawable hd = (HighlightedDrawable) itemInList.findViewById(R.id.highlighted_drawable);
                hd.setTitle(name);
                hd.setMode(HighlightedDrawable.LETTER_MODE);
            }
            else if (imageCode == BagContract.ItemEntry.ICON_IMAGE && d!=null){
                HighlightedDrawable hd = (HighlightedDrawable) itemInList.findViewById(R.id.highlighted_drawable);
                hd.setTitle(name);
                hd.setIconDrawable(d);
            }else if(imageCode == BagContract.ItemEntry.REAL_IMAGE && d!=null){
                FrameLayout fl = (FrameLayout) itemInList.findViewById(R.id.icon_frame);
                fl.removeViewAt(0);
                CircleImageView cv = (CircleImageView) mInflater.inflate(R.layout.circular_image,fl).findViewById(R.id.profile_image);
                cv.setImageDrawable(d);
            }else Log.e("BackpackItemsListFrag","bag image code: " +String.valueOf(imageCode));


            itemName = (TextView) itemInList.findViewById(R.id.item_name);
            locationName = (TextView)  itemInList.findViewById(R.id.location_name);
            ((TextView) itemInList.findViewById(R.id.alarm_name)).setText(alarmTimes[GLOBAL]);
            locationName.setText(citys[GLOBAL]);
            transactionTime.setText(days[GLOBAL]);
            GLOBAL++;

            itemName.setText(name);
//
//            itemInList.findViewById(R.id.get_location_button).setOnClickListener(cl);

        }

        public void setSticker(TextView v , int color){
            String colorName = "UNKNOWN colour";
            Drawable d = null;
            if (color == getActivity().getResources().getColor(R.color.tag_gold)){
                colorName = "Gold Sticker";
                d = getActivity().getResources().getDrawable(R.drawable.tag_label_bg_gold);
            }
            if (color == getActivity().getResources().getColor(R.color.tag_purple)){
                colorName = "Purple Sticker";
                d = getActivity().getResources().getDrawable(R.drawable.tag_label_bg_purple);
            }
            if (color == getActivity().getResources().getColor(R.color.tag_orange)){
                colorName = "Orange Sticker";
                d = getActivity().getResources().getDrawable(R.drawable.tag_label_bg_orange);
            }
            if (color == getActivity().getResources().getColor(R.color.tag_green)){
                colorName = "Green Sticker";
                d = getActivity().getResources().getDrawable(R.drawable.tag_label_bg_green);
            }
            if (color == getActivity().getResources().getColor(R.color.tag_blue)){
                colorName = "Blue Sticker";
                d = getActivity().getResources().getDrawable(R.drawable.tag_label_bg_blue);
            }
            v.setText(colorName);
            v.setBackground(d);
        }



    }



}