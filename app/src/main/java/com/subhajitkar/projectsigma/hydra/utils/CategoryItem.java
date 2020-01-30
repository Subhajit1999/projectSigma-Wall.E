package com.subhajitkar.projectsigma.hydra.utils;

import android.util.Log;

public class CategoryItem {
    private static final String TAG = "CategoryItem";

    private String mTitle;
    private int mImage;

    public CategoryItem(int image, String title){
        Log.d(TAG, "CategoryItem: constructor");
        mImage = image;
        mTitle = title;
    }

    public int getmImage() {
        Log.d(TAG, "getmImage: getting the image");
        return mImage;
    }

    public String getmTitle() {
        Log.d(TAG, "getmTitle: getting the title");
        return mTitle;
    }
}
