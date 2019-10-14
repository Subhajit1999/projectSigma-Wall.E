package com.subhajitkar.projectsigma.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.fragments.HomeFragment;
import com.subhajitkar.projectsigma.hydra.fragments.ImagesFragment;
import com.subhajitkar.projectsigma.hydra.utils.StaticFinalUtils;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";
    public static ArrayList<String> imagesList;
    public static String urlImage;

    private int frag_id;
    private String search_term;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: creating the layout");
        setContentView(R.layout.activity_second);

        imagesList = new ArrayList<>();
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

    public void run(String url) throws IOException{

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .header("Authorization", StaticFinalUtils.API_KEY)
                .url(url)
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
                        Log.d(TAG, "run: integrated successfully: "+myResponse);
                    }
                });

            }
        });
    }
}
