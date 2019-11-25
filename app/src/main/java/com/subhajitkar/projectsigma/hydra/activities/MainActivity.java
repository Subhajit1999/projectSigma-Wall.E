package com.subhajitkar.projectsigma.hydra.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.fragments.HomeFragment;
import com.subhajitkar.projectsigma.hydra.fragments.SearchFragment;
import com.subhajitkar.projectsigma.hydra.utils.NetworkUtils;
import com.subhajitkar.projectsigma.hydra.utils.StaticUtils;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MaterialSearchBar.OnSearchActionListener, PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "MainActivity";

    @SuppressLint("StaticFieldLeak")
    public static MaterialSearchBar searchBar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: setting things up");

        StaticUtils.requestQueue = (RequestQueue) Volley.newRequestQueue(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //restoring recent searches list
        StaticUtils.recentSearchesList = getArrayList(StaticUtils.KEY_LIST_PREFERENCCES);
        if (StaticUtils.recentSearchesList==null){
            StaticUtils.recentSearchesList = new ArrayList<>();
        }
        searchBar = findViewById(R.id.searchToolBar);
        searchBar.setHint("Search Wallpapers");
        searchBar.setOnSearchActionListener(this);
        searchBar.hideSuggestionsList();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //sets home fragment open by default
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();

        playLogoAudio();  //to play the logo audio
        searchEvents();  //advanced search events
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: back button invoked.");

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: navigation item pressed.");

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();

        } else if (id == R.id.nav_saved) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_about) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void playLogoAudio(){
        Log.d(TAG, "playLogoAudio: playing logo audio");

        View headerView = navigationView.getHeaderView(0);
        ImageView drawerLogo = headerView.findViewById(R.id.imageLogo);
        drawerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.wall_e);
                mediaPlayer.start();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (enabled){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SearchFragment()).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Log.d(TAG, "onSearchConfirmed: confirmed search: "+text);

        Intent i = new Intent(this,SecondActivity.class);
        i.putExtra(StaticUtils.KEY_FRAG_ID,1);
        i.putExtra(StaticUtils.KEY_SEARCH_DATA,String.valueOf(text));

        if(new NetworkUtils(getApplicationContext()).checkConnection()) {  //start intent if network connected
            StaticUtils.recentSearchesList.add(String.valueOf(text));  //adds the query to the recents list

            if (StaticUtils.recentSearchesList.size()>20){
                StaticUtils.recentSearchesList.remove(0);
            }
            SearchFragment.updateAdapter(this,StaticUtils.recentSearchesList);
            searchBar.setText("");  //removes the search query
            startActivity(i);
        }
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        Log.d(TAG, "onButtonClicked: search interface button clicked: "+buttonCode);
        switch (buttonCode){
            case MaterialSearchBar.BUTTON_NAVIGATION:
                drawer.openDrawer(GravityCompat.START);
                break;
            case MaterialSearchBar.BUTTON_BACK:
                searchBar.disableSearch();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void saveArrayList(ArrayList<String> list, String key){
        Log.d(TAG, "saveArrayList: saving recent searchList data");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getArrayList(String key){
        Log.d(TAG, "getArrayList: getting saved recent searchList data");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();

        return gson.fromJson(json, type);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Saving arraylist when activity gets paused
        saveArrayList(StaticUtils.recentSearchesList,StaticUtils.KEY_LIST_PREFERENCCES);
    }

    public void searchEvents(){
        searchBar.addTextChangeListener(new TextWatcher() {
            ArrayList<String> tempList = new ArrayList<>();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: clearing the list");
                if (!tempList.isEmpty()){
                    tempList.clear();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: changing search query");
                for (String string : StaticUtils.recentSearchesList) {
                    if (s.length() > 0) {
                        if (string.matches("(?i)(" + s + ").*")) {
                            tempList.add(string);
                            SearchFragment.updateAdapter(getApplicationContext(), tempList);
                        }
                    }else{
                        SearchFragment.updateAdapter(getApplicationContext(), StaticUtils.recentSearchesList);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
