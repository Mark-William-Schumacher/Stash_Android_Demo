package com.markshoe.stashapp.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.markshoe.stashapp.R;

public class MapsFragment extends Fragment {

    private GoogleMap map;
    ObservableScrollView mScrollView;
    static final LatLng COMPUTER_LOCAL = new LatLng(43.772, -79.246);
    static final LatLng KEYS_LOCAL = new LatLng(43.772, -79.276);
    static final LatLng IPOD_LOCAL = new LatLng(43.769, -79.262);
    static final LatLng CHARGER_LOCAL = new LatLng(43.761, -79.258);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_maps, container,
                false);



        // Get a handle to the Map Fragment
        GoogleMap map = getMapFragment().getMap();
        Marker hamburg = map.addMarker(new MarkerOptions()
                .position(COMPUTER_LOCAL)
                .title("Macbook Pro")
                .snippet("Last seen 11/08 11:54pm")
                .icon(BitmapDescriptorFactory
                        .fromBitmap(scaleImage(getActivity().getResources().getDrawable(R.drawable.marker_computer)))));


        Marker kiel = map.addMarker(new MarkerOptions()
                .position(KEYS_LOCAL)
                .title("House Keys")
                .snippet("Last seen 12/09 2:24pm")
                .icon(BitmapDescriptorFactory
                        .fromBitmap(scaleImage(getActivity().getResources().getDrawable(R.drawable.marker_keys)))));


        Marker ipod = map.addMarker(new MarkerOptions()
                .position(IPOD_LOCAL)
                .title("Ipod")
                .snippet("Last seen 12/13 3:24pm")
                .icon(BitmapDescriptorFactory
                        .fromBitmap(scaleImage(getActivity().getResources().getDrawable(R.drawable.marker_ipod)))));

        Marker charger = map.addMarker(new MarkerOptions()
                .position(CHARGER_LOCAL)
                .title("Macbook Charger")
                .snippet("Last seen 12/13 9:24am")
                .icon(BitmapDescriptorFactory
                        .fromBitmap(scaleImage(getActivity().getResources().getDrawable(R.drawable.marker_charger)))));

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(IPOD_LOCAL, 11));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(12.5f), 2000, null);

        return rootView;
    }

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    private SupportMapFragment getMapFragment() {
        FragmentManager fm = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }
        return (SupportMapFragment) fm.findFragmentById(R.id.map);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);
        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }


    public Bitmap scaleImage (Drawable image) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return null;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int width = image.getIntrinsicWidth();
        int newWidth = 110;
        float scaleFactor = (float)newWidth/width;

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(getResources(), bitmapResized);

        return ((BitmapDrawable) image).getBitmap();

    }

}
