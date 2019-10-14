package com.subhajitkar.projectsigma.hydra.utils;

import android.util.Log;

public class CategoryItem {
    private static final String TAG = "CategoryItem";

    String mTitle;
    int mImage;

    public CategoryItem(int image, String title){
        Log.d(TAG, "CategoryItem: constructor");
        mImage = image;
        mTitle = title;
    }

    public int getmImage() {
        Log.d(TAG, "getmImage: getting the image");
        return mImage;
    }

    public void setmImage(int mImage) {
        Log.d(TAG, "setmImage: setting the image");
        this.mImage = mImage;
    }

    public String getmTitle() {
        Log.d(TAG, "getmTitle: getting the title");
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        Log.d(TAG, "setmTitle: setting the title");
        this.mTitle = mTitle;
    }
}
