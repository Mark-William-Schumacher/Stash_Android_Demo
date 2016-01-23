package com.markshoe.stashapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.markshoe.stashapp.CustomViews.ItemFragmentGridLayout;
import com.markshoe.stashapp.data.DbUtility;

import java.sql.Blob;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shoe on 15-07-08.
 */
public class BackpackCardAdapter {

    public boolean mSetup;
    Context mContext;
    LayoutInflater mInflater;
    ViewGroup mContainer ;
    private BagListener mListener;
    HashMap<Long, BackpackCard> bagIdToCardMap = new HashMap<>();
    OutOfBackpackCard outOfBackpackCard = null;


    public BackpackCardAdapter(Context context, Cursor data, ViewGroup container, BagListener b) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mContainer = container;
        updateCursor(data);
        this.mListener = b;
    }


    public void updateCursor(Cursor cursor){
        // bag_image_blob[5], bag_image_code [4] numOfItems [3] , bag_res_name [2] , bag_name[1]
        // current_bag_key[0]
        if (cursor == null || !cursor.moveToFirst()){
            return;
        }
        do  {
            Long bagId = cursor.getLong(0);
            String resName = cursor.getString(2);
            String bagName = cursor.getString(1);
            int numOfItems = cursor.getInt(3);
            if (bagId != 0L){
                if (!bagIdToCardMap.containsKey(bagId)){
                    View v = mInflater.inflate(R.layout.backpack_card,mContainer,false);
                    mContainer.addView(v);
                    bagIdToCardMap.put(bagId, new BackpackCard(v,bagId));
                }
                Bitmap b = DbUtility.getImage(cursor.getBlob(5));
                int code  = cursor.getInt(4);
                (bagIdToCardMap.get(bagId)).setBagCardInfo(resName, bagName, numOfItems,b, code);
            } else if (bagId == 0){
                if (outOfBackpackCard == null){
                    View v = mInflater.inflate(R.layout.out_of_backpack_card,mContainer,false);
                    mContainer.addView(v);
                    outOfBackpackCard = new OutOfBackpackCard(v);
                }
                outOfBackpackCard.setInfo(numOfItems);
            }
        }while (cursor.moveToNext());
    }


    /**
     * Sends the click up to the fragment
     * https://www.youtube.com/watch?v=K9F6U7yN2vI&index=23&list=PLonJJ3BVjZW6CtAMbJz1XD8ELUs1KXaTD
     */
    public interface BagListener {
        public void itemClicked(View view, long bagId, String bagName);
    }


    class OutOfBackpackCard implements View.OnClickListener{
        long bagId = 0;
        TextView itemInfo;
        TextView viewContentsButton;
        FrameLayout moreInfoButton;
        public OutOfBackpackCard(View bpCardView) {
            itemInfo = (TextView) bpCardView.findViewById(R.id.item_info_text);
            viewContentsButton = (TextView) bpCardView.findViewById(R.id.view_contents_button);
            moreInfoButton = (FrameLayout) bpCardView.findViewById(R.id.out_of_bp_overflow_button);
            moreInfoButton.setOnClickListener(this);
            viewContentsButton.setOnClickListener(this);
        }

        public void setInfo(int numOfItems){
            itemInfo.setText(String.format("Currently %d items outside of bags", numOfItems));
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            String idString = Long.toString(bagId);
            if (id == R.id.out_of_bp_overflow_button) {
                Toast toast = Toast.makeText(mContext, idString+ " Overflow pressed", Toast.LENGTH_SHORT);
                toast.show();
            }else if (id == R.id.view_contents_button){
                Toast toast = Toast.makeText(mContext, idString+ " View Contents Pressed", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


    class BackpackCard implements View.OnClickListener{
        View backpackCard ;
        long bagId;
        ImageView bagIcon;
        TextView backpackName;
        TextView connectionText;
        TextView itemInfo;
        TextView getLocationButton;
        TextView viewContentsButton;
        FrameLayout moreInfoButton;

        public BackpackCard(View bpCardView, long bagId){
            this.bagId = bagId;
            backpackCard = bpCardView;
            bagIcon = (ImageView) bpCardView.findViewById(R.id.bag_icon);
            backpackName = (TextView) bpCardView.findViewById(R.id.backpack_name);
            connectionText = (TextView) bpCardView.findViewById(R.id.connection_text);
            itemInfo = (TextView) bpCardView.findViewById(R.id.item_info_text);
            getLocationButton = (TextView) bpCardView.findViewById(R.id.get_location_button);
            viewContentsButton = (TextView) bpCardView.findViewById(R.id.view_contents_button);
            moreInfoButton = (FrameLayout) bpCardView.findViewById(R.id.bpcard_overflow_button);

            moreInfoButton.setOnClickListener(this);
            viewContentsButton.setOnClickListener(this);
            getLocationButton.setOnClickListener(this);
        }


        public void setBagCardInfo(String resName, String bagName, int numOfItems,Bitmap bitmap, int code){
            int resId = mContext.getResources().getIdentifier(resName, "drawable", mContext.getPackageName());
            if (resId == 0){
                Log.e("BackpackCardAdapter", "Error could not find a resourse for: " + resName +
                        " defaulting to backpack icon");
                resId = R.drawable.bag_icon;
            }
            if (code != 0){
                bagIcon.setImageBitmap(bitmap);
            }else{
                bagIcon.setImageDrawable(mContext.getResources().getDrawable(resId));
            }
            backpackName.setText(bagName);
            connectionText.setText("Connected");
            itemInfo.setText(String.format("Currently holding %d items", numOfItems));
        }

        /**
         * Sends the click up to the fragment
         * https://www.youtube.com/watch?v=K9F6U7yN2vI&index=23&list=PLonJJ3BVjZW6CtAMbJz1XD8ELUs1KXaTD
         * @param v
         */
        @Override
        public void onClick(View v) {
            int id = v.getId();
            String idString = Long.toString(bagId);
            if (id == R.id.get_location_button) {
                Toast toast = Toast.makeText(mContext, idString+ " Get Location Pressed", Toast.LENGTH_SHORT);
                toast.show();
            } else if (id == R.id.view_contents_button) {
                mListener.itemClicked(v, bagId, backpackName.getText().toString());
            } else if (id == R.id.bpcard_overflow_button){
                Toast toast = Toast.makeText(mContext, idString  + " Overflow Button Pressed", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
