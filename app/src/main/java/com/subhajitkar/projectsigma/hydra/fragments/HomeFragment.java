package com.subhajitkar.projectsigma.hydra.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.activities.SecondActivity;
import com.subhajitkar.projectsigma.hydra.utils.CategoryItem;
import com.subhajitkar.projectsigma.hydra.utils.NetworkUtils;
import com.subhajitkar.projectsigma.hydra.utils.RecyclerAdapter;
import com.subhajitkar.projectsigma.hydra.utils.StaticUtils;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements RecyclerAdapter.OnItemClickListener {
    private static final String TAG = "HomeFragment";

    private RecyclerView mRecyclerView;
    private RecyclerAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: making the layout visible");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: view created successfully");
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.category_list);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //setting adapter
        adapter = new RecyclerAdapter(getContext(),StaticUtils.categoryList, StaticUtils.imagesList,0);  //adapter attached
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: gets called.");
        super.onCreate(savedInstanceState);

        //getting the lists ready
        StaticUtils.categoryList = new ArrayList<>();
        StaticUtils.categoryList.add(new CategoryItem(R.drawable.featured,"Featured"));
        StaticUtils.categoryList.add(new CategoryItem(R.drawable.abstruct,"Abstract"));
        StaticUtils.categoryList.add(new CategoryItem(R.drawable.foods,"Foods"));
        StaticUtils.categoryList.add(new CategoryItem(R.drawable.cars,"Cars"));
        StaticUtils.categoryList.add(new CategoryItem(R.drawable.nature,"Nature"));
        StaticUtils.categoryList.add(new CategoryItem(R.drawable.girls,"Girls"));
        StaticUtils.categoryList.add(new CategoryItem(R.drawable.technology,"Technology"));
        StaticUtils.categoryList.add(new CategoryItem(R.drawable.religious,"Religious"));
        StaticUtils.categoryList.add(new CategoryItem(R.drawable.baby,"Baby"));
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: clicked item");
        NetworkUtils network = new NetworkUtils(getContext());

        if(network.checkConnection()){      //if network connected
            Intent i = new Intent(getActivity(), SecondActivity.class);
            i.putExtra("FRAG_CATEGORY_ITEMS",0);
            if (position!=0){
                i.putExtra("KEY_SEARCH_TERM",StaticUtils.categoryList.get(position).getmTitle().toLowerCase());
            }
            startActivity(i);
        }
    }
}
