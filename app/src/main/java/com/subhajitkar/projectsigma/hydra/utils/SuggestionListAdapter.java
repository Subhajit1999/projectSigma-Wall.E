package com.subhajitkar.projectsigma.hydra.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.subhajitkar.projectsigma.hydra.R;

import java.util.List;

public class SuggestionListAdapter extends BaseAdapter {
    private static final String TAG = "MyItemRecyclerViewAdapt";

    private List<String> mSearchItems;
    private Context mContext;

    public SuggestionListAdapter(Context context,List<String> searchItems) {
        mSearchItems = searchItems;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mSearchItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mSearchItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView: called");

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext,R.layout.search_suggest_item,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mRecentSearch.setText(mSearchItems.get(mSearchItems.size()-position-1));
        viewHolder.mSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deletes the search item from list
                StaticUtils.recentSearchesList.remove(mSearchItems.size()-position-1);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    class ViewHolder{
        TextView mRecentSearch;
        ImageView mSearchClear;

        private ViewHolder(View view) {
            mRecentSearch = view.findViewById(R.id.tv_recent);
            mSearchClear =  view.findViewById(R.id.iv_recent_del);
        }
    }
}
