package com.subhajitkar.projectsigma.hydra.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.activities.MainActivity;
import com.subhajitkar.projectsigma.hydra.activities.SecondActivity;
import com.subhajitkar.projectsigma.hydra.utils.NetworkUtils;
import com.subhajitkar.projectsigma.hydra.utils.StaticUtils;
import com.subhajitkar.projectsigma.hydra.utils.SuggestionListAdapter;

import java.util.ArrayList;

public class SearchFragment extends Fragment{
    private static final String TAG = "SearchFragment";

    @SuppressLint("StaticFieldLeak")
    private static ListView recentSearchList;
    @SuppressLint("StaticFieldLeak")
    private static SuggestionListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: creating view");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: initializing things");

        recentSearchList = view.findViewById(R.id.search_suggest_list);
        recentSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //list item click action
                int ActualPosition = StaticUtils.recentSearchesList.size()-position-1;
                Intent i = new Intent(getContext(), SecondActivity.class);
                i.putExtra(StaticUtils.KEY_FRAG_ID,1);
                i.putExtra(StaticUtils.KEY_SEARCH_DATA,StaticUtils.recentSearchesList.get(ActualPosition));

                if(new NetworkUtils(getContext()).checkConnection()) {  //start intent if network connected
                    MainActivity.searchBar.setText("");  //removes the search query
                    startActivity(i);
                }
            }
        });

        adapter = new SuggestionListAdapter(getContext(),StaticUtils.recentSearchesList);
        if (!StaticUtils.recentSearchesList.isEmpty()){  //if list not empty
            recentSearchList.setAdapter(adapter);
        }
    }

    public static void updateAdapter(Context mContext, ArrayList<String> list){  // updates the list immediately
        Log.d(TAG, "updateAdapter: updating the listview");
        adapter = null;
        adapter = new SuggestionListAdapter(mContext,list);
        if (!StaticUtils.recentSearchesList.isEmpty()){  //if list not empty
            recentSearchList.setAdapter(adapter);
        }
    }

}
