package com.subhajitkar.projectsigma.hydra.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.activities.DetailActivity;
import com.subhajitkar.projectsigma.hydra.activities.SecondActivity;
import com.subhajitkar.projectsigma.hydra.utils.ImagesItem;
import com.subhajitkar.projectsigma.hydra.utils.RecyclerAdapter;
import com.subhajitkar.projectsigma.hydra.utils.StaticUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ImagesFragment extends Fragment implements RecyclerAdapter.OnItemClickListener{
    private static final String TAG = "ImagesFragment";

    @SuppressLint("StaticFieldLeak")
    public static RecyclerAdapter adapter;
    private ProgressBar progressBar;
    private int frag_id=0;
    private static RecyclerView images_list;
    private LinearLayout root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: gets called");

        if (getArguments() != null) {
            frag_id = getArguments().getInt(StaticUtils.KEY_SAVED_FRAG_ID);
            Log.d(TAG, "onCreate: arguments not null: "+frag_id);
        }
        if (frag_id != 2 && frag_id != 4) {  //if not saved & downloads action
            try {
                new SecondActivity().run(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: creating view of the fragment");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: gets called");

        progressBar = view.findViewById(R.id.progressBar);
        root = view.findViewById(R.id.root_layout);

        if (frag_id==2 && StaticUtils.savedImagesList.isEmpty()){
            Snackbar.make(root,"Looks like no images in the saved list.",Snackbar.LENGTH_SHORT).show();
        }

        if (frag_id != 2 && frag_id != 4) {
            progressBar.setVisibility(View.VISIBLE);
            new CountDownTimer(5000, 1000) {
                public void onTick(long millisecondsUntillDone) {
                    //codes will be executed everytime in given intervals
                }

                public void onFinish() {
                    //will be executed once when total time reached
                    progressBar.setVisibility(View.GONE);
                }
            }.start();
        }

        images_list = view.findViewById(R.id.images_recycler);  //setting up the recycler
        images_list.setHasFixedSize(true);
        images_list.setItemViewCacheSize(50);
        images_list.setDrawingCacheEnabled(true);
        images_list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        images_list.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        images_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (frag_id!=2 && frag_id!=4) {
                        if (!StaticUtils.imagesList.isEmpty()) {
                            Snackbar.make(root, "Loading more images.", Snackbar.LENGTH_SHORT).show();
                            loadMore();
                        }
                    }
                }
            }
        });
        if (frag_id != 2 && frag_id != 4) {  //if not saved
            adapter = new RecyclerAdapter(getContext(), root, StaticUtils.categoryList, StaticUtils.imagesList, 1);  //adapter attached
        }else if (frag_id==4){
            adapter = new RecyclerAdapter(getContext(), root, StaticUtils.categoryList, StaticUtils.imagesList, 2);  //adapter attached
        }else{
            adapter = new RecyclerAdapter(getContext(), root, StaticUtils.categoryList, StaticUtils.savedImagesList, 1);  //adapter attached
        }
        adapter.setOnItemClickListener(this);
        images_list.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: recycler click action");
        if (frag_id!=4) {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra(StaticUtils.KEY_CLICKED_IMAGE, position);
            intent.putExtra(StaticUtils.KEY_SAVED_FRAG_ID, frag_id);
            if (frag_id != 2) {
                intent.putExtra(StaticUtils.KEY_SEARCH_TERM, SecondActivity.search_term);
            }
            startActivity(intent);
        }
    }

    private void updateAdapter(){  // updates the list immediately
        Log.d(TAG, "updateAdapter: updating the list");
        adapter = null;
            if (frag_id == 2) {  //for saved
                adapter = new RecyclerAdapter(getContext(),root, StaticUtils.categoryList, StaticUtils.savedImagesList, 1);  //adapter attached
                adapter.setOnItemClickListener(this);
                if (!StaticUtils.savedImagesList.isEmpty()) {
                    images_list.setAdapter(adapter);
                }else{
                    Snackbar.make(root,"Looks like no images in the saved list.",Snackbar.LENGTH_SHORT).show();
                }
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: gets called");
        if (frag_id==2) {
            updateAdapter();
        }
    }

    private void loadMore(){
        Log.d(TAG, "loadMore: loading more images.");
        try {
            new SecondActivity().run(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }
}
