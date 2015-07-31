package com.markshoe.stashapp;

import android.content.Context;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.v7.widget.PopupMenu;

/**
 * Created by shoe on 15-07-05.
 */
public class OnBagOverflowSelectedListener implements OnClickListener {
    private Context mContext;

    public OnBagOverflowSelectedListener(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        // This is an android.support.v7.widget.PopupMenu;
        PopupMenu popupMenu = new PopupMenu(mContext, v) {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.album_overflow_delete:
                        return true;

                    case R.id.album_overflow_rename:
                        return true;

                    case R.id.album_overflow_lock:
                        return true;

                    case R.id.album_overflow_unlock:
                        return true;

                    case R.id.album_overflow_set_cover:
                        return true;

                    default:
                        return super.onMenuItemSelected(menu, item);
                }
            }
        };

        popupMenu.inflate(R.menu.bag_overflow_menu);

//        if (mAlbum.isLocked()) {
//            popupMenu.getMenu().removeItem(R.id.album_overflow_lock);
//            popupMenu.getMenu().removeItem(R.id.album_overflow_rename);
//            popupMenu.getMenu().removeItem(R.id.album_overflow_delete);
//        } else {
//            popupMenu.getMenu().removeItem(R.id.album_overflow_unlock);
//        }

        popupMenu.show();
    }
}