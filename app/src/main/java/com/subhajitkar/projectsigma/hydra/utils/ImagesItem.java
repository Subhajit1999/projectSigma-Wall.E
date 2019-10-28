package com.subhajitkar.projectsigma.hydra.utils;

import android.util.Log;

public class ImagesItem {
    private static final String TAG = "ImagesItem";

    private int mImageId;
    private String mImageUrl, mSrcName, mSrcUrl;

    public ImagesItem(int imageId, String imageUrl, String srcName, String srcUrl){
        Log.d(TAG, "ImagesItem: constructor");

        mImageId = imageId;
        mImageUrl = imageUrl;
        mSrcName = srcName;
        mSrcUrl = srcUrl;
    }

    public void setmImageId(int mImageId) {
        this.mImageId = mImageId;
    }

    public int getmImageId() {
        return mImageId;
    }

    public void setmSrcName(String mSrcName) {
        this.mSrcName = mSrcName;
    }

    public String getmSrcName() {
        return mSrcName;
    }

    public void setmSrcUrl(String mSrcUrl) {
        this.mSrcUrl = mSrcUrl;
    }

    public String getmSrcUrl() {
        return mSrcUrl;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
