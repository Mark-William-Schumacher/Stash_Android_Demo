package com.markshoe.stashapp;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.markshoe.stashapp.CustomViews.HighlightedDrawable;
import com.markshoe.stashapp.CustomViews.HighlightedDrawableTransaction;

import java.util.List;


/**
 * Created by shoe on 15-06-18.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    Context mContext;
    LayoutInflater mInflater;
    List<Transaction> mData;
    private TransactionListener mListener;

    public TransactionAdapter(Context context , List<Transaction> data, TransactionListener clickListener){
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = data;
        mListener = clickListener;
    }

    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.transaction_single_row, parent, false);
        TransactionViewHolder holder = new TransactionViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TransactionAdapter.TransactionViewHolder holder, int position) {
        Transaction current = mData.get(position);
        holder.bagIcon.setImageDrawable(mContext.getResources().getDrawable(current.bagIcon));
        holder.dateText.setText(current.dateText);
        holder.numItemsText.setText(getNumberOfItemsText(current.itemTransactions.size()));
        holder.latText.setText(current.latText);
        holder.lngText.setText(current.latText);
        if (current.isExpanded){
            createItemTransactionView(current.itemTransactions,holder.transactionContainer, current.bagName);
        } else {
            holder.transactionContainer.removeAllViews();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public String getNumberOfItemsText(int numOfItems){
        return String.valueOf(numOfItems).concat(" Items Exchanged");
    }

    private void createItemTransactionView(List<Transaction.ItemTransaction> listOfTransactions
            , LinearLayout container, String bagName){
        container.removeAllViews();
        for(Transaction.ItemTransaction itemTransaction:listOfTransactions){
            View singleTransaction = mInflater.inflate(R.layout.transaction_item_transactions,container,false);
            ((TextView)singleTransaction.findViewById(R.id.item_name)).setText(itemTransaction.title);
            ((TextView) singleTransaction.findViewById(R.id.transaction_desc))
                    .setText(getDescription(itemTransaction.direction, bagName));
            ((ImageView) singleTransaction.findViewById(R.id.in_out_symbol))
                    .setImageDrawable(getInOutDrawable(itemTransaction.direction));
            ((HighlightedDrawable) singleTransaction.findViewById(R.id.highlighted_drawable))
                    .setItem((int) itemTransaction.itemID, itemTransaction.resId, mContext.getResources().getColor(R.color.primary_dark), itemTransaction.title);

            container.addView(singleTransaction);
        }
    }

    private String getDescription(int direction, String bagName){
        String s = (direction == 0)?"Removed from ":"Entered ";
        s= s.concat(bagName);
        return s;
    }
    private Drawable getInOutDrawable(int dir){
        Resources r = mContext.getResources();
        return (dir == 0)?r.getDrawable(R.drawable.grey_single_light)
                :r.getDrawable(R.drawable.green_single_light);
    }


    class TransactionViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        ImageView bagIcon;
        TextView dateText;
        TextView numItemsText;
        TextView latText;
        TextView lngText;
        LinearLayout transactionContainer;

        public TransactionViewHolder(View itemView){
            super(itemView);
            bagIcon = (ImageView) itemView.findViewById(R.id.bag_icon);
            dateText = (TextView) itemView.findViewById(R.id.date_text_view);
            numItemsText = (TextView) itemView.findViewById(R.id.items_exchanged);
            latText = (TextView) itemView.findViewById(R.id.latitude);
            latText = (TextView) itemView.findViewById(R.id.latitude);
            lngText = (TextView) itemView.findViewById(R.id.longitude);
            transactionContainer = (LinearLayout) itemView.findViewById(R.id.transaction_container);
            itemView.findViewById(R.id.location_click).setOnClickListener(this);
            itemView.findViewById(R.id.bag_click).setOnClickListener(this);

        }

        /**
         * Sends the click up to the fragment
         * https://www.youtube.com/watch?v=K9F6U7yN2vI&index=23&list=PLonJJ3BVjZW6CtAMbJz1XD8ELUs1KXaTD
         * @param v
         */
        @Override
        public void onClick(View v){
            if (v.getId() == R.id.bag_click){
                Transaction clicked = mData.get(getPosition()-1);
                if (clicked.isExpanded) {
                    transactionContainer.removeAllViews();
                }
                else
                    createItemTransactionView(clicked.itemTransactions, transactionContainer, clicked.bagName);
                clicked.isExpanded= !clicked.isExpanded;
            }

            if (v.getId() == R.id.location_click){

            }
            mListener.itemClicked(v,getPosition()); // Sends it up to fragment if we want
        }

    }

    /**
     * Sends the click up to the fragment
     * https://www.youtube.com/watch?v=K9F6U7yN2vI&index=23&list=PLonJJ3BVjZW6CtAMbJz1XD8ELUs1KXaTD
     */
    public interface TransactionListener{
        public void itemClicked(View view, int position);
    }


}
