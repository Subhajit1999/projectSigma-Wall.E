package com.subhajitkar.projectsigma.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.fragments.ImagesFragment;
import com.subhajitkar.projectsigma.hydra.utils.ImagesItem;
import com.subhajitkar.projectsigma.hydra.utils.NetworkUtils;
import com.subhajitkar.projectsigma.hydra.utils.StaticUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";
    public static String urlImage;

    private int frag_id;
    private String search_term;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: creating the layout");
        setContentView(R.layout.activity_second);

        StaticUtils.imagesList = new ArrayList<>();

        getIntentData();  //receiving intent data

        getSupportFragmentManager().beginTransaction().replace(R.id.second_fragment_container,
                new ImagesFragment()).commit();
    }

    private void getIntentData(){
        Log.d(TAG, "getIntentData: getting the intent-received data");

        Intent intent = getIntent();
        frag_id = intent.getIntExtra("FRAG_CATEGORY_ITEMS",0);
        search_term = intent.getStringExtra("KEY_SEARCH_TERM");
        if (search_term==null){
            urlImage = "https://api.pexels.com/v1/curated";
        }else{
            urlImage = "https://api.pexels.com/v1/search?query="+search_term;
        }
    }

    public void run() throws IOException{
        Log.d(TAG, "run: up and running...");

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
                        if (!StaticUtils.imagesList.isEmpty()){
                            StaticUtils.imagesList.clear();
                        }

                        try {
                            JSONObject json = new JSONObject(myResponse);

                            JSONArray jsonArray = json.getJSONArray("photos");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                int imageId = jsonObject.getInt("id");
                                String srcName = jsonObject.getString("photographer");
                                String srcUrl = jsonObject.getString("photographer_url");

                                    JSONObject srcObject = jsonObject.getJSONObject("src");  //getting the image sources of different dimens
                                    String imgSrc = srcObject.getString("large");

                                StaticUtils.imagesList.add(new ImagesItem(imageId, imgSrc, srcName, srcUrl));
                                ImagesFragment.adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: Json Parsing data error.");
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }
}
