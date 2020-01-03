package com.subhajitkar.projectsigma.hydra.utils;


import android.Manifest;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

public class StaticUtils {
    private static final String TAG = "StaticUtils";

    public static final String API_KEY = "563492ad6f9170000100000162a17e2fcbc54c508bf6aa85bbba684b ";
    public static ArrayList<ImagesItem> imagesList;
    public static ArrayList<ImagesItem> recommendedImagesList;
    public static ArrayList<CategoryItem> categoryList;
    public static RequestQueue requestQueue;
    public static final String KEY_CLICKED_IMAGE = "CLICKED_IMAGE_ID#64297";
    public static final String KEY_SEARCH_TERM = "SEARCH_TERM#24531";
    public static final int MULTIPLE_PERMISSIONS = 10;
    public static String[] permissions = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SET_WALLPAPER
    };
    public static ArrayList<String> recentSearchesList;
    public static ArrayList<ImagesItem> savedImagesList;
    public static final String KEY_SAVED_PREFERENCES = "KEY_SAVED_PREFERENCES#36497";
    public static final String KEY_FRAG_ID = "KEY_FRAG_IDENTIFIER#98655";
    public static final String KEY_SEARCH_DATA = "KEY_SEARCH_TEXT_DATA#84788";
    public static final String NETWORK_ERROR_MESSAGE = "Oops! No Internet connection. Check & try again.";
    public static final String KEY_LIST_PREFERENCCES = "KEY_RECENT_SEARCH_LIST_PREFERENCCES#29925";
    public static final String KEY_SAVED_FRAG_ID = "KEY_SAVED_FRAG_ID#87348";
    public static final String KEY_WEBURL = "KEY_WEBURL_GENERAL#97535";
    public static final String KEY_INTENT_GENERAL = "KEY_INTENT_GENERAL#37910";
}
