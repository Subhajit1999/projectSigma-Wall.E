package com.subhajitkar.projectsigma.hydra.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.fragments.AboutFragment;
import com.subhajitkar.projectsigma.hydra.fragments.ImagesFragment;
import com.subhajitkar.projectsigma.hydra.utils.ImageDifferentSize;
import com.subhajitkar.projectsigma.hydra.utils.ImagesItem;
import com.subhajitkar.projectsigma.hydra.utils.NetworkUtils;
import com.subhajitkar.projectsigma.hydra.utils.StaticUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";
    public static String urlImage;

    private int frag_id;
    public static String search_term;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: creating the layout");
        setContentView(R.layout.activity_second);

        getIntentData();  //receiving intent data

        if (frag_id!=3 && frag_id!=4) {     //if not about fragment and downloads
            StaticUtils.imagesListTemp = new ArrayList<>();
            if (frag_id!=2){   //if not saved fragment too
                StaticUtils.imagesList = new ArrayList<>();
            }
            //restoring saved searches list
            StaticUtils.savedImagesList = getBookmarkArraylist(getApplicationContext());
            if (StaticUtils.savedImagesList == null) {
                StaticUtils.savedImagesList = new ArrayList<>();
            }
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

            if (search_term!=null){
                getSupportActionBar().setTitle(search_term);
            }else{
                if (frag_id==0) {
                    getSupportActionBar().setTitle("Featured");
                }
            }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {  //toolbar navigate button click
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });
            if (frag_id!=3) {  //not about fragment
                ImagesFragment fragment = new ImagesFragment();
                if (frag_id == 2||frag_id==4) {  //if saved or downloads
                    Bundle bundle = new Bundle();
                    bundle.putInt(StaticUtils.KEY_SAVED_FRAG_ID, frag_id);
                    fragment.setArguments(bundle);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.second_fragment_container,
                        fragment).commit();
            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.second_fragment_container,
                        new AboutFragment()).commit();
            }
    }

    private void getIntentData(){
        Log.d(TAG, "getIntentData: getting the intent-received data");

        Intent intent = getIntent();
        frag_id = intent.getIntExtra(StaticUtils.KEY_FRAG_ID,0);
        search_term = intent.getStringExtra(StaticUtils.KEY_SEARCH_DATA);
    }

    public void run(final Context context) throws IOException{
        Log.d(TAG, "run: up and running...");

        if (frag_id==0 || frag_id==1) {  //if home fragment & search fragment
            Log.d(TAG, "getIntentData: frag id not 2");
            if (search_term == null) {
                urlImage = "https://api.pexels.com/v1/curated";
            } else {
                int randPage;
                do {
                    randPage = new Random().nextInt(20);
                }while(randPage==0);
                urlImage = "https://api.pexels.com/v1/search?query=" + search_term.toLowerCase() + "&per_page=20&page=" + randPage;
            }
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", StaticUtils.API_KEY)
                .url(urlImage)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                final String myResponse = response.body() != null ? response.body().string() : null;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: integrated successfully: ");
                        if (!StaticUtils.imagesListTemp.isEmpty()){
                            StaticUtils.imagesListTemp.clear();
                        }

                        try {
                            JSONObject json = new JSONObject(myResponse);

                            JSONArray jsonArray = json.getJSONArray("photos");
                            if (jsonArray.length()>0){
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    int imageId = jsonObject.getInt("id");
                                    String srcName = jsonObject.getString("photographer");
                                    String srcUrl = jsonObject.getString("photographer_url");
                                    String dimen = jsonObject.getString("width")+"x"+jsonObject.getString("height");

                                    JSONObject srcObject = jsonObject.getJSONObject("src");  //getting the image sources of different dimens
                                    String imgSrcOrg = srcObject.getString("original");
                                    String imgSrcLarg = srcObject.getString("large");
                                    String imgSrcMid = srcObject.getString("medium");
                                    String imgSrcSml = srcObject.getString("small");
                                    String imgSrcPort = srcObject.getString("portrait");
                                    String imgSrcLand = srcObject.getString("landscape");
                                    String imgSrcTiny = srcObject.getString("tiny");
                                    ImageDifferentSize imageArray = new ImageDifferentSize(imgSrcOrg,imgSrcLarg,imgSrcMid,imgSrcSml,imgSrcPort,imgSrcLand,imgSrcTiny);

                                    StaticUtils.imagesListTemp.add(new ImagesItem(imageId, srcName, dimen, srcUrl,imageArray));
                                    ImagesFragment.adapter.notifyDataSetChanged();
                                }
                            }else{
                                Toast.makeText(context,"Sorry! No wallpapers found related to your search.",Toast.LENGTH_LONG).show();
                            }
                            Log.d(TAG, "run: List size: "+StaticUtils.imagesListTemp.size());
                            boolean isAddSucceed = StaticUtils.imagesList.addAll(StaticUtils.imagesListTemp);
                            Log.d(TAG, "onCreate: Copy operation succeed:"+isAddSucceed+", Final arralist size: "+StaticUtils.imagesList.size());
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: Json Parsing data error.");
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: System default back function");
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void saveBookmarkArraylist(Context mContext, ArrayList<ImagesItem> list) {
        Log.d(TAG, "saveBookmarkArraylist: saving the saved arraylist");

        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        preferences = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        editor = preferences.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(list);
        editor.putString(StaticUtils.KEY_SAVED_PREFERENCES, jsonFavorites);
        editor.commit();
    }

    public ArrayList getBookmarkArraylist(Context context) {
        Log.d(TAG, "getBookmarkArraylist: getting the arraylist");
        SharedPreferences preferences;
        ArrayList<ImagesItem> saved = new ArrayList<>();

        preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        if (preferences.contains(StaticUtils.KEY_SAVED_PREFERENCES)) {
            String jsonFavorites = preferences.getString(StaticUtils.KEY_SAVED_PREFERENCES, null);
            Gson gson = new Gson();
            ImagesItem[] favoriteItems = gson.fromJson(jsonFavorites,
                    ImagesItem[].class);

            Collections.addAll(saved,favoriteItems);
        } else
            return null;

        return (ArrayList<ImagesItem>) saved;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (frag_id!=3 && frag_id!=4) {
            saveBookmarkArraylist(getApplicationContext(), StaticUtils.savedImagesList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!StaticUtils.imagesList.isEmpty()) {
            StaticUtils.imagesList.clear();
        }
    }
}
