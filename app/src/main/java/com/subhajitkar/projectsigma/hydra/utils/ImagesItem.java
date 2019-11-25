package com.subhajitkar.projectsigma.hydra.utils;

import android.util.Log;

public class ImagesItem {
    private static final String TAG = "ImagesItem";

    private int mImageId;
    private String mSrcName, mSrcUrl, mDimen;
    private ImageDifferentSize mImagesArray;

    public ImagesItem(int imageId, String srcName, String dimen, String srcUrl,ImageDifferentSize imagesArray){
        Log.d(TAG, "ImagesItem: constructor");

        mImageId = imageId;
        mSrcName = srcName;
        mSrcUrl = srcUrl;
        mImagesArray = imagesArray;
        mDimen = dimen;
    }

    public String getmDimen() {
        return mDimen;
    }

    public int getmImageId() {
        return mImageId;
    }

    public String getmSrcName() {
        return mSrcName;
    }

    public String getmSrcUrl() {
        return mSrcUrl;
    }

    public ImageDifferentSize getmImagesArray() {
        return mImagesArray;
    }
}
