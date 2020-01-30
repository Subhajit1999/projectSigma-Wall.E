package com.subhajitkar.projectsigma.hydra.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.fragments.ImagesFragment;
import com.subhajitkar.projectsigma.hydra.utils.ImageDifferentSize;
import com.subhajitkar.projectsigma.hydra.utils.ImagesItem;
import com.subhajitkar.projectsigma.hydra.utils.RecyclerAdapter;
import com.subhajitkar.projectsigma.hydra.utils.StaticUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.subhajitkar.projectsigma.hydra.utils.StaticUtils.permissions;

public class DetailActivity extends AppCompatActivity implements RecyclerAdapter.OnItemClickListener{
    private static final String TAG = "DetailActivity";

    private AppBarLayout appBarLayout;
    private DownloadManager downloadManager;
    private Toolbar toolbar;
    private boolean appBarExpanded,saved=false;
    private Context mContext = DetailActivity.this;
    private ImageView pexels,saveIcon;
    private ImageView imageView;
    private int imagePos,listId,fragId;
    private String urlRecommended,search_term,File_Name,dimen,imageUrl;
    private RecyclerAdapter adapter;
    private CoordinatorLayout coordinatorLayout;
    private BroadcastReceiver broadcastReceiver;
    private TextView photographer,saveText;
    private ImagesItem image;
    private Bitmap downloadedBitmap;
    private int orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: gets called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        StaticUtils.recommendedImagesList = new ArrayList<>();  //initializing the arraylist
        //setting up to get the data
        fragId = getIntent().getIntExtra(StaticUtils.KEY_SAVED_FRAG_ID, 0);
        if (fragId != 2) {
            search_term = getIntent().getStringExtra(StaticUtils.KEY_SEARCH_TERM);
        } else {
            int randInt = new Random().nextInt(16);
            search_term = StaticUtils.categoryList.get(randInt).getmTitle();
        }
        checkNetworkAvailabilty(search_term);
        coordinatorLayout = findViewById(R.id.root_coordinator);
        photographer = findViewById(R.id.textAttrPhotographer);
        pexels = findViewById(R.id.roundedPexels);

        //system downloadManager service
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        setUpAppBar();
        setupToolBar();
        //setting up the image
        imagePos = getIntent().getIntExtra(StaticUtils.KEY_CLICKED_IMAGE, 0);
        if (fragId != 2) {
            listId = 0;
            image = StaticUtils.imagesList.get(imagePos);
        } else {
            listId = fragId;
            image = StaticUtils.savedImagesList.get(imagePos);
        }
        //setting image based on orientation
        orientation = getResources().getConfiguration().orientation;
        if (orientation==1) {
            imageUrl = image.getmImagesArray().getmPortrait();
        }else{
            imageUrl = image.getmImagesArray().getmLandscape();
        }
        loadImage(imageUrl);
        File_Name = URLUtil.guessFileName(image.getmImagesArray().getmOriginal(), null, null);
        setupRecycler();
        funcUrl(listId); //0 indicates the default imageslist from imagesFragment
        shareIntent();  //0 for default list
        manageDownload();
        manageSaveUnsave();
        manageSetWallpaper();
    }

    public void funcUrl(int identifier){
        Log.d(TAG, "funcUrl: setting up the urls");
        final String srcName,srcUrl;
        if (identifier==0){
            srcName = StaticUtils.imagesList.get(imagePos).getmSrcName();
            srcUrl = StaticUtils.imagesList.get(imagePos).getmSrcUrl();
        }else if(identifier==1){
            srcName = StaticUtils.recommendedImagesList.get(imagePos).getmSrcName();
            srcUrl = StaticUtils.recommendedImagesList.get(imagePos).getmSrcUrl();
        }else{
            srcName = StaticUtils.savedImagesList.get(imagePos).getmSrcName();
            srcUrl = StaticUtils.savedImagesList.get(imagePos).getmSrcUrl();
        }
        photographer.setText(srcName);
        photographer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opens the photographer profile on pexels
                Intent i = new Intent(DetailActivity.this,GeneralActivity.class);
                i.putExtra(StaticUtils.KEY_INTENT_GENERAL,"webView");
                i.putExtra(StaticUtils.KEY_WEBURL,srcUrl);
                startActivity(i);
            }
        });
        pexels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opens the url www.pexels.com/
                Intent i = new Intent(DetailActivity.this,GeneralActivity.class);
                i.putExtra(StaticUtils.KEY_INTENT_GENERAL,"webView");
                i.putExtra(StaticUtils.KEY_WEBURL,"http://www.pexels.com");
                startActivity(i);
            }
        });
    }

    public void setupToolBar() {
        Log.d(TAG, "setupToolBar: Setting up toolbar");
        //adding arrow back to the toolbar
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setUpAppBar() {
        Log.d(TAG, "setUpAppBar: Setting appbar to control collapsing toolbar");
        //Applying the custom toolbar background in the collapsing toolbar
        appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, int verticalOffset) {

                //  Vertical offset == 0 indicates appBar is fully expanded.
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    toolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    toolbar.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
                    invalidateOptionsMenu();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: system default back function");
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: creating the options menu");
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: click event for the menu items");
        String url="";

        switch (item.getItemId()){
            case R.id.original:
                url = image.getmImagesArray().getmOriginal();
                break;
            case R.id.large:
                url = image.getmImagesArray().getmLarge();
                break;
            case R.id.medium:
                url = image.getmImagesArray().getmMedium();
                break;
            case R.id.small:
                url = image.getmImagesArray().getmSmall();
                break;
            case R.id.portrait:
                url = image.getmImagesArray().getmPortrait();
                break;
            case R.id.landscape:
                url = image.getmImagesArray().getmLandscape();
                break;
            case R.id.tiny:
                url = image.getmImagesArray().getmTiny();
                break;
        }
        loadImage(url);
        return true;
    }

    public void loadImage(final String imageUrl){
        Log.d(TAG, "loadImage: loading image");
        imageView = findViewById(R.id.expandedImage);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d(TAG, "onBitmapLoaded: image loaded");
                imageView.setImageBitmap(bitmap);
                downloadedBitmap = bitmap;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(TAG, "onBitmapFailed: image load error");
//                        imageView.setImageDrawable(errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d(TAG, "onPrepareLoad: image loading");
//                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.circle_icon));
            }
        };
        imageView.setTag(target);

        Snackbar.make(coordinatorLayout,"Loading image...",Snackbar.LENGTH_SHORT).show();
        Picasso.with(mContext)
                .load(imageUrl)
                .placeholder(R.drawable.circle_icon)
                .centerInside()
                .resize(1000,1000)
                .into(target);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //imageView click action
                Log.d(TAG, "onClick: imageView clicked");
                ImageView mImage = new ImageView(getApplicationContext());
                if (downloadedBitmap!=null) {
                    mImage.setImageBitmap(downloadedBitmap);
                }else{
                    mImage.setImageDrawable(getResources().getDrawable(R.drawable.circle_icon));
                }
                displayFullImage(mImage);
            }
        });
    }

    private void displayFullImage(ImageView image) {
        Log.d(TAG, "displayFullImage: showing image preview dialog");

        final AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.display_image_fullscreen,
                (ViewGroup) findViewById(R.id.llFullImage));
        ImageView imageView = (ImageView) layout.findViewById(R.id.iv_fullscreen);
        imageView.setImageDrawable(image.getDrawable());
        imageDialog.setView(layout);
        imageDialog.create().show();
    }

    public void setupRecycler(){
        Log.d(TAG, "setupRecycler: setting up the recycler");

        RecyclerView recyclerView = findViewById(R.id.image_recommended_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        //setting adapter
        adapter = new RecyclerAdapter(mContext,coordinatorLayout,StaticUtils.categoryList, StaticUtils.recommendedImagesList,1);  //adapter attached
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    public void run(String search_term) throws IOException {
        Log.d(TAG, "run: up and running..."+search_term);
        int randPage;
        do{
            randPage = new Random().nextInt(20);
        }while(randPage==0);
        Log.d(TAG, "run: "+randPage);
        if (search_term == null) {
            search_term = "curated";
            urlRecommended = "https://api.pexels.com/v1/"+search_term.toLowerCase().replace(' ','+')+"?per_page=20&page="+randPage;
        }else{
            urlRecommended = "https://api.pexels.com/v1/search?query="+search_term.toLowerCase().replace(' ','+')+"&per_page=20&page="+randPage;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", StaticUtils.API_KEY)
                .url(urlRecommended)
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
                        if (!StaticUtils.recommendedImagesList.isEmpty()){
                            StaticUtils.recommendedImagesList.clear();
                        }

                        try {
                            JSONObject json = new JSONObject(myResponse);

                            JSONArray jsonArray = json.getJSONArray("photos");
                            if (jsonArray.length()>0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    int imageId = jsonObject.getInt("id");
                                    String srcName = jsonObject.getString("photographer");
                                    String srcUrl = jsonObject.getString("photographer_url");
                                    String dimen = jsonObject.getString("width") + "x" + jsonObject.getString("height");

                                    JSONObject srcObject = jsonObject.getJSONObject("src");  //getting the image sources of different dimens
                                    String imgSrcOrg = srcObject.getString("original");
                                    String imgSrcLarg = srcObject.getString("large");
                                    String imgSrcMid = srcObject.getString("medium");
                                    String imgSrcSml = srcObject.getString("small");
                                    String imgSrcPort = srcObject.getString("portrait");
                                    String imgSrcLand = srcObject.getString("landscape");
                                    String imgSrcTiny = srcObject.getString("tiny");
                                    ImageDifferentSize imageArray = new ImageDifferentSize(imgSrcOrg, imgSrcLarg, imgSrcMid, imgSrcSml, imgSrcPort, imgSrcLand, imgSrcTiny);

                                    StaticUtils.recommendedImagesList.add(new ImagesItem(imageId, srcName, dimen, srcUrl, imageArray));
                                    adapter.notifyDataSetChanged();
                                }
                            }else{
                                Snackbar.make(coordinatorLayout,"Sorry! No wallpapers found related to your search.",Snackbar.LENGTH_SHORT).show();
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

    public void checkNetworkAvailabilty(final String search_term){
        Log.d(TAG, "checkNetworkAvailability: log message");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();

                NetworkInfo info = extras.getParcelable("networkInfo");
                NetworkInfo.State state = info.getState();

                if (state == NetworkInfo.State.CONNECTED) {
                    //tasks when connection is back
                    try {
                        run(search_term);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //handle network error when connection is gone
                    Snackbar.make(coordinatorLayout,"No Internet connection.",Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        };
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: clicking from recommended image list");
        listId = 1;
        imagePos = position;
        image = StaticUtils.recommendedImagesList.get(position);
        loadImage(image.getmImagesArray().getmLarge());
        File_Name = URLUtil.guessFileName(image.getmImagesArray().getmOriginal(),null,null);
        appBarLayout.setExpanded(true,true);
        funcUrl(listId);
        if (!isImageSaved()) {  //if image doesn't exist in the saved list
            saved = false;
            saveIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_unchecked));
            saveText.setText(getResources().getString(R.string.detail_save));
        }
        checkNetworkAvailabilty(search_term);
    }

    public void shareIntent(){
        Log.d(TAG, "shareIntent: sharing the image.");

        LinearLayout shareLinear = findViewById(R.id.linear_detail_share);
        shareLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String imageUrl;
                imageUrl = image.getmImagesArray().getmOriginal();
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I found this amazing wallpaper from Wall.e, an wallpaper android app powered by pexels.com.\n\nDownload the wallpaper now from here-(link-"+imageUrl+")\n\nDownload Wall.e for more amazing wallpapers from Play Store.");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share the image..."));
            }
        });
    }

    private void manageDownload() {
        Log.d(TAG, "manageDownload: download function");

        //setting download button click action
        LinearLayout linearDownload = findViewById(R.id.linear_detail_download);
        linearDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: image download button click");
                if (MainActivity.permissionsGranted(mContext)){
                    // permissions granted.
                    downloadDialog();
                }else{
                    MainActivity.requestPermissions(DetailActivity.this);
                }
            }
        });
    }

    private void downloadDialog(){
        Log.d(TAG, "downloadDialog: showing download dialog");
        //image downloader dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Choose image size: ");
        builder.setIcon(getResources().getDrawable(R.drawable.ic_dialog_download));
        if (listId==0){
            dimen = StaticUtils.imagesList.get(imagePos).getmDimen();
        }else if (listId==1){
            dimen = StaticUtils.recommendedImagesList.get(imagePos).getmDimen();
        }else {
            dimen = StaticUtils.savedImagesList.get(imagePos).getmDimen();
        }
        final List<String> downloadSize = new ArrayList<>();
        downloadSize.add("Original  ("+dimen+")");
        downloadSize.add("Large  (650x940)");
        downloadSize.add("Medium  (__x350)");
        downloadSize.add("Small  (__x130)");
        downloadSize.add("Portrait  (1200x800)");
        downloadSize.add("Landscape  (627x1200)");
        downloadSize.add("Tiny  (200x280)");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_list_item_single_choice, downloadSize);
        builder.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: item position: "+which);
                //dialog item click action
                String url="";
                switch(which){
                    case 0:
                        url = image.getmImagesArray().getmOriginal();
                        break;
                    case 1:
                        url = image.getmImagesArray().getmLarge();
                        break;
                    case 2:
                        url = image.getmImagesArray().getmMedium();
                        break;
                    case 3:
                        url = image.getmImagesArray().getmSmall();
                        break;
                    case 4:
                        url = image.getmImagesArray().getmPortrait();
                        break;
                    case 5:
                        url = image.getmImagesArray().getmLandscape();
                        break;
                    case 6:
                        url = image.getmImagesArray().getmTiny();
                }
                downloadImage(url);
            }
        });
        //builder.setPositiveButton("Download", null);
        builder.setNegativeButton("Cancel",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void downloadImage(String ImageUrl){
        Log.d(TAG, "downloadImage: downloading image. ImageUrl: "+ImageUrl);

        Uri downloadUri = Uri.parse(ImageUrl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Wall.e");
        request.setDescription("Downloading: " + File_Name);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,"/Wall.e/"+File_Name);

        Snackbar.make(coordinatorLayout, "Downloading wallpaper...", Snackbar.LENGTH_SHORT).show();
        downloadManager.enqueue(request);
    }

    public boolean isImageSaved(){
        Log.d(TAG, "isImageSaved: checking if image is saved or not");
        for (ImagesItem item: StaticUtils.savedImagesList) {
            if (item.getmImagesArray().getmOriginal().matches("(?i)(" + image.getmImagesArray().getmOriginal() + ").*")) {
                return true;
            }
        }
        return false;
    }

    public void manageSaveUnsave(){
        Log.d(TAG, "manageSaveUnsave: implementing save and unsave feature");
        LinearLayout saveUnsave = findViewById(R.id.linear_detail_save);
        saveIcon = findViewById(R.id.iv_detail_save);
        saveText = findViewById(R.id.tv_detail_save);

        if (isImageSaved()) {
            saved = true;
            saveIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_checked));
            saveText.setText(getResources().getString(R.string.text_unsave));
        }
        saveUnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save layout click action
                if(saved){  //if image exists in the list
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Warning:");
                    builder.setIcon(getResources().getDrawable(R.drawable.ic_dialog_download));
                    builder.setMessage("Are you sure you want to delete the image from the saved list?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StaticUtils.savedImagesList.remove(image);
                            saveIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_unchecked));
                            saveText.setText(getResources().getString(R.string.detail_save));
                            saved = false;
                            Snackbar.make(coordinatorLayout,"Wallpaper removed from the saved list.",Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("No",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else{  //if doesn't exist
                    StaticUtils.savedImagesList.add(image);
                    saveIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_checked));
                    saveText.setText(getResources().getString(R.string.text_unsave));
                    saved = true;
                    Snackbar.make(coordinatorLayout,"Wallpaper added to the saved list.",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void manageSetWallpaper(){
        Log.d(TAG, "setWallpaper: setting wallpaper");
        LinearLayout linear_setWall = findViewById(R.id.linear_detail_setWall);
        linear_setWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setting wallpaper
                if (MainActivity.permissionsGranted(mContext)) {
                    WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
                    try{
                        manager.setBitmap(downloadedBitmap);
                        Snackbar.make(coordinatorLayout,"Wallpaper set successfully.",Snackbar.LENGTH_SHORT).show();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }else{
                    MainActivity.requestPermissions(DetailActivity.this);
                }
            }
        });
    }
}