//package com.markshoe.stashapp.oldfiles;
//
///**
// * Created by shoe on 15-06-21.
// */
//
//import android.content.Context;
//import android.database.Cursor;
//import android.graphics.drawable.Drawable;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.markshoe.stashapp.Backpack;
//import com.markshoe.stashapp.CustomViews.HighlightedDrawable;
//import com.markshoe.stashapp.CustomViews.ItemFragmentGridLayout;
//import com.markshoe.stashapp.OnBagOverflowSelectedListener;
//import com.markshoe.stashapp.R;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * Created by shoe on 15-06-18.
// */
//public class InnerBackpackAdapter implements View.OnClickListener{
//    public boolean mSetup;
//    Context mContext;
//    LayoutInflater mInflater;
//    private BagListener mListener;
//    HashMap<Long, ViewGroup> bagIdToContainerMap = new HashMap<>();
//    HashMap<Long, Backpack> bagIdToBackpackMap = new HashMap<>();
//    public ArrayList<ViewGroup> animateLayouts = new ArrayList<>();
//
//    public InnerBackpackAdapter(Context context, List<Backpack> data, BagListener clickListener, ViewGroup container){
//        this.mContext = context;
//        mInflater = LayoutInflater.from(context);
//        mListener = clickListener;
//        animateLayouts.add(container);
//
//        // Here we are setting up the title bar for each bag, the bag's data will be stored in a hashmap
//        // and inflated upon calling refresh all bags. This implementation will allow us to switch out the
//        // bag's item data on the fly with cursors and then just call refreshAllBags
//        for (Backpack current : data) {
//            ViewGroup view;
//            ViewGroup itemContainer;
//            if (current.bagId != 0) {
//                view = (ViewGroup) mInflater.inflate(R.layout.backpack_contents, container, false);
//                BagWithTitleBar holder = new BagWithTitleBar(view, current);
//                holder.bagIcon.setImageDrawable(mContext.getResources().getDrawable(current.resId));
//                holder.backpackName.setText(current.nameOfBag);
//                holder.connectivityLight.setImageDrawable(getConnectionDrawable(current.connectionStatus));
//                //holder.zipper.setImageDrawable(getZipperDrawable(current.isExpanded));
//                itemContainer = holder.itemContainer;
//                if (!current.isExpanded)
//                    itemContainer.setVisibility(View.GONE);
//            } else {
//                view = (ViewGroup) mInflater.inflate(R.layout.out_of_backpack_items, container, false);
//                itemContainer = (ViewGroup) view.findViewById(R.id.grid_layout);
//            }
//            bagIdToContainerMap.put(current.bagId, itemContainer);
//            bagIdToBackpackMap.put(current.bagId, current);
//            container.addView(view);
//            //animateLayouts.add(view);
//        }
//        refreshAllBagItemData();
//    }
//
//    public void swapCursor(Cursor cursor){
//        // _id [0] , tag_id [1] , item_name[2], item_description[3], current_bag_key[4]
//        // date_description[5], item_color[6], item_res_name[7]
//
//        if (cursor == null){
//            return;
//        }
//        if (!cursor.moveToFirst()) return;
//        HashMap < Long , List<Backpack.Item>>  IdToItemsMap = new HashMap<>();
//        do  {
//            List<Backpack.Item> listToPopulate ;
//            long currentBagId = cursor.getLong(4);
//            String resName = cursor.getString(7);
//            int resId = mContext.getResources().getIdentifier(resName, "drawable", mContext.getPackageName());
//            if (resId == 0){
//                Log.e("bp-ad", "Error could not find a resourse for: " + resName +
//                " defaulting to backpack icon");
//                resId = R.drawable.bag_icon;
//            }
//            String itemName = cursor.getString(2);
//            int color = cursor.getInt(6);
//            int itemId = cursor.getInt(0);
//            Log.e("HELLO",itemName);
//            if (IdToItemsMap.containsKey(currentBagId)) {
//                listToPopulate = IdToItemsMap.get(currentBagId);
//            }
//            else {
//                listToPopulate = new ArrayList<>();
//                IdToItemsMap.put(currentBagId,listToPopulate);
//            }
//            listToPopulate.add(new Backpack.Item(resId,itemName,color,itemId));
//        }while (cursor.moveToNext());
//
//        Iterator it = IdToItemsMap.entrySet().iterator();
//        while (it.hasNext()) {
//            Log.e("HELLO","HELLO");
//            Map.Entry pair = (Map.Entry) it.next();
//            setBagItems((long) pair.getKey(), (List<Backpack.Item>) pair.getValue());
//        }
//    }
//
//    public void setBagItems(long bagId , List<Backpack.Item> lba){
//        Backpack b = bagIdToBackpackMap.get(bagId);
//        b.listOfItems = lba;
//        refreshBagItems(bagId, b.listOfItems);
//    }
//
//    public void refreshAllBagItemData(){
//        Iterator it = bagIdToBackpackMap.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
//            refreshBagItems((long) pair.getKey(), ((Backpack) pair.getValue()).listOfItems);
//             // avoids a ConcurrentModificationException
//        }
//    }
//    /**
//     * To be called by a cursor listening on changes in the database.
//     * @param bagID Id of the bag we want to refresh
//     * @param itemsList list of items in the bag
//     */
//    public void refreshBagItems(long bagID, List<Backpack.Item> itemsList){
//        ViewGroup v = bagIdToContainerMap.get(bagID);
//        v.removeAllViews();
//        for (int i = 0; i < itemsList.size(); i++){
//            Backpack.Item item = itemsList.get(i);
//            View singleItem = mInflater.inflate(R.layout.item, v , false);
//            ((HighlightedDrawable) singleItem.findViewById(R.id.highlighted_drawable)).setItem(item);
//            singleItem.setOnClickListener(this);
//            v.addView(singleItem);
//        }
//    }
//
//    private Drawable getConnectionDrawable(int isConnected) {
//        if (isConnected==1)
//            return mContext.getResources().getDrawable(R.drawable.green_single_light);
//        return mContext.getResources().getDrawable(R.drawable.grey_single_light);
//    }
//
//    private Drawable getZipperDrawable(boolean expanded){
//        if (expanded)
//            return mContext.getResources().getDrawable(R.drawable.zipper_open);
//        return mContext.getResources().getDrawable(R.drawable.zipper_closed);
//    }
//
//    @Override
//    public void onClick(View v) {
//        mListener.itemClicked(v); // Sends it up to fragment if we want
//    }
//
//    class BagWithTitleBar implements View.OnClickListener{
//        ViewGroup backpackContainer ;
//        ImageView bagIcon;
//        TextView backpackName;
//        ImageView connectivityLight;
//        ItemFragmentGridLayout itemContainer;
//        ImageView bagOuterIcon;
//        Backpack backpack;
//        //ImageView zipper;
//
//        public BagWithTitleBar(View itemView, Backpack backpackData){
//            backpack=backpackData;
//            backpackContainer = (ViewGroup) itemView;
//            bagIcon = (ImageView) itemView.findViewById(R.id.bag_inner_symbol);
//            backpackName = (TextView) itemView.findViewById(R.id.backpack_name);
//            bagOuterIcon = (ImageView) itemView.findViewById(R.id.bag_outer_symbol);
//            connectivityLight = (ImageView) itemView.findViewById(R.id.connectivity_light);
//            itemContainer = (ItemFragmentGridLayout) itemView.findViewById(R.id.grid_layout);
//            //zipper = (ImageView) itemView.findViewById(R.id.zipper);
//            itemView.findViewById(R.id.bag_view_overtop_symbol).setOnClickListener(this);
//            itemView.findViewById(R.id.more_info_button).setOnClickListener(new OnBagOverflowSelectedListener(mContext));
//        }
//
//        /**
//         * Sends the click up to the fragment
//         * https://www.youtube.com/watch?v=K9F6U7yN2vI&index=23&list=PLonJJ3BVjZW6CtAMbJz1XD8ELUs1KXaTD
//         * @param v
//         */
//        @Override
//        public void onClick(View v) {
//
//            int id = v.getId();
//            if (id == R.id.bag_view_overtop_symbol) {
//                Animation anim;
//                if (backpack.isExpanded) {
//                    //anim = AnimationUtils.loadAnimation(mContext, R.anim.counter_clockwise_90);
//                    itemContainer.setVisibility(View.GONE);
//                } else {
//                    //anim = AnimationUtils.loadAnimation(mContext, R.anim.clockwise_90);
//                    itemContainer.setVisibility(View.VISIBLE);
//                }
//                //bagOuterIcon.startAnimation(anim);
//                backpack.isExpanded = !backpack.isExpanded;
//                //zipper.setImageDrawable(getZipperDrawable(backpack.isExpanded));
//            } else if (id == R.id.more_info_button) {
//
//            }
//        }
//
//    }
//
//    /**
//     * Sends the click up to the fragment
//     * https://www.youtube.com/watch?v=K9F6U7yN2vI&index=23&list=PLonJJ3BVjZW6CtAMbJz1XD8ELUs1KXaTD
//     */
//    public interface BagListener {
//        public void itemClicked(View view, long bagId);
//    }
//
//
//}
