package com.subhajitkar.projectsigma.hydra.utils;

public class ImageDifferentSize {
    private static final String TAG = "ImageDifferentSize";

    private String mOriginal,mLarge,mMedium,mSmall,mPortrait,mLandscape,mTiny;

    public ImageDifferentSize(String org, String large, String mid, String small, String port, String land, String tiny){
        mOriginal = org;
        mLarge = large;
        mMedium = mid;
        mSmall = small;
        mPortrait = port;
        mLandscape = land;
        mTiny = tiny;
    }

    public String getmOriginal() {
        return mOriginal;
    }

    public String getmLarge() {
        return mLarge;
    }

    public String getmMedium() {
        return mMedium;
    }

    public String getmSmall() {
        return mSmall;
    }

    public String getmPortrait() {
        return mPortrait;
    }

    public String getmLandscape() {
        return mLandscape;
    }

    public String getmTiny() {
        return mTiny;
    }
}
