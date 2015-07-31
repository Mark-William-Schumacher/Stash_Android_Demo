package com.markshoe.stashapp.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.markshoe.stashapp.Transaction;
import com.markshoe.stashapp.TransactionAdapter;
import com.markshoe.stashapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by shoe
 * Check out slidenerds recyclerView tutorial for details on the code
 * https://www.youtube.com/watch?v=K9F6U7yN2vI&index=23&list=PLonJJ3BVjZW6CtAMbJz1XD8ELUs1KXaTD
 */
public class TransactionFragment extends Fragment implements TransactionAdapter.TransactionListener{

    private RecyclerView mRecyclerView;
    private RecyclerViewMaterialAdapter mAdapter;
    private List<Transaction> mContentItems = new ArrayList<>();

    public static TransactionFragment newInstance() {
        return new TransactionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.fragment_transaction, container, false);
        return vg;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        List<Transaction> t = getRandomListOfTransactions();
        TransactionAdapter adapter = new TransactionAdapter(getActivity(),  t, this);
        mAdapter = new RecyclerViewMaterialAdapter(adapter);
        mRecyclerView.setAdapter(mAdapter);

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    List<Transaction> getRandomListOfTransactions(){
        List<Transaction> lt = new ArrayList<>();
        Random rand = new Random();

        for(int i=0 ; i < 100; i++){
            int drawableSeed= rand.nextInt((bagDrawableAndName.length - 1) + 1) + 0;
            lt.add(new Transaction((int)bagDrawableAndName[drawableSeed][0],(String)bagDrawableAndName[drawableSeed][1],
                    "Thursday June 10th","23.128","21.328",createRandomItemTransactionList(1,10),1));
        }
        return lt;
    }

    Object[][] bagDrawableAndName= {
            {R.drawable.dufflebag, "Dufflebag"},
            {R.drawable.backpack,"Main Backpack"},
            {R.drawable.computerbag,"Computer bag"},
            {R.drawable.luggage,"Luggage"}
    };



    Object[][] drawableAndName= {
            {R.drawable.noun_charger_150532, "Iphone Charger"},
            {R.drawable.noun_books_21509, "Math Textbook"},
            {R.drawable.noun_ipad_4586, "Mark's Ipad"},
            {R.drawable.noun_shoes_33327, "Gym Shoes"},
            {R.drawable.noun_mouse_890, "Mouse"},
            {R.drawable.noun_keyboard_16595, "Keyboard"},
            {R.drawable.noun_charger_59725, "Macbook Charger"},
            {R.drawable.noun_laptop_3960, "Macbook"},
            {R.drawable.noun_book_62494, "Geo Textbook"},
            {R.drawable.noun_ipod_15183, "Ipod mini"}
    };
    int[] colors  = {
        R.color.tag_blue,
        R.color.tag_gold,
        R.color.tag_green,
        R.color.tag_purple,
        R.color.tag_orange
    };

    int[] direction = {1,0};

    private List<Transaction.ItemTransaction> createRandomItemTransactionList(int min, int max){
        List<Transaction.ItemTransaction> itemTransList = new ArrayList<>();
        Random rand = new Random();
        int itemTransactionAmount= rand.nextInt((max - min) + 1) + min;
        for (int i = 0 ; i < itemTransactionAmount ; i++){
            int drawableSeed= rand.nextInt((drawableAndName.length - 1) + 1) + 0;
            int drawable = (int) drawableAndName[drawableSeed][0];
            String title = (String) drawableAndName[drawableSeed][1];
            int color = colors[rand.nextInt((colors.length - 1) + 1) + 0];
            int dir = direction[rand.nextInt((direction.length - 1) + 1) + 0];
            itemTransList.add(new Transaction.ItemTransaction(drawable,title,color,dir,1));
        }
        return itemTransList;
    }


    /**
     * Getting the recyclers view on click listener
     * https://www.youtube.com/watch?v=K9F6U7yN2vI&index=23&list=PLonJJ3BVjZW6CtAMbJz1XD8ELUs1KXaTD
     * @param view
     * @param position
     */
    @Override
    public void itemClicked(View view, int position) {
        if (view.getId() == R.id.location_click){
        }

    }
}