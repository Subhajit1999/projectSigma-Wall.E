package com.subhajitkar.projectsigma.hydra.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.fragments.ImagesFragment;
import com.subhajitkar.projectsigma.hydra.utils.ImageDifferentSize;
import com.subhajitkar.projectsigma.hydra.utils.ImagesItem;
import com.subhajitkar.projectsigma.hydra.utils.NetworkUtils;
import com.subhajitkar.projectsigma.hydra.utils.StaticUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
    private Context mContext = SecondActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: creating the layout");
        setContentView(R.layout.activity_second);

        StaticUtils.imagesList = new ArrayList<>();

        getIntentData();  //receiving intent data

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
        getSupportFragmentManager().beginTransaction().replace(R.id.second_fragment_container,
                    new ImagesFragment()).commit();
    }

    private void getIntentData(){
        Log.d(TAG, "getIntentData: getting the intent-received data");

        Intent intent = getIntent();
        frag_id = intent.getIntExtra(StaticUtils.KEY_FRAG_ID,0);
        search_term = intent.getStringExtra(StaticUtils.KEY_SEARCH_DATA);
        if (search_term==null){
            urlImage = "https://api.pexels.com/v1/curated?per_page=30";
        }else{
            int randPage;
            do{
                randPage = new Random().nextInt(20);
            }while(randPage==0);
            urlImage = "https://api.pexels.com/v1/search?query="+search_term.toLowerCase()+"&per_page=30&page="+randPage;
        }
    }

    public void run(final Context context) throws IOException{
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

                                    StaticUtils.imagesList.add(new ImagesItem(imageId, srcName, dimen, srcUrl,imageArray));
                                    ImagesFragment.adapter.notifyDataSetChanged();
                                }
                            }else{
                                Toast.makeText(context,"Sorry! No wallpapers found related to your search.",Toast.LENGTH_LONG).show();
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
}
