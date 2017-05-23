package com.poponews.lite.ui;

/**
 * Created by zl on 2016/10/21.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poponews.lite.R;

public class ListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerAdapter;
    private String categoryName;

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView =
                (RecyclerView) inflater.inflate(R.layout.list_fragment, container, false);
        return mRecyclerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerAdapter = new RecyclerViewAdapter(getActivity());
        mRecyclerAdapter.setCategoryName(categoryName);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    public void refreshRecyclerView(){
        if (mRecyclerAdapter != null)
            mRecyclerAdapter.notifyDataSetChanged();
    }
}
