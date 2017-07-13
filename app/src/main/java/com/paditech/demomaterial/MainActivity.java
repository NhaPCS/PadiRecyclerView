package com.paditech.demomaterial;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.paditech.padirecyclerview.LoadMoreRecyclerView;
import com.paditech.padirecyclerview.RefreshLoadmoreRecyclerView;

public class MainActivity extends AppCompatActivity {
    private RefreshLoadmoreRecyclerView mSwipeRefreshLayout;
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (RefreshLoadmoreRecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapter.setSize(20);
                    }
                }, 1000);
            }
        });

        mAdapter = new Adapter();
        mSwipeRefreshLayout.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.onLoadMoreComplete();
                        mAdapter.addSize(10);
                    }
                }, 1000);
            }
        });
        mSwipeRefreshLayout.setLayoutManager(new LinearLayoutManager(this));
        mSwipeRefreshLayout.setAdapter(mAdapter);

        mAdapter.setSize(20);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int size = 0;

        Adapter() {
        }

        void setSize(int size) {
            this.size = size;
            notifyDataSetChanged();
        }

        void addSize(int size) {
            this.size += size;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return size;
        }
    }

    private class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
