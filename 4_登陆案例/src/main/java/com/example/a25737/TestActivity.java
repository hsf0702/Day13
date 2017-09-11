package com.example.a25737;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lv;
    private ArrayAdapter<String> adapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            swipeRefreshLayout.setRefreshing(false);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshlayout);
        lv = (ListView) findViewById(R.id.lv);
        String[] strings = {"dfasf", "dsfaf", "adfaf", "sdfafasf", "dsafasf", "dsafasf", "dsafaf"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings);
        lv.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        new Thread(){
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                Message message = handler.obtainMessage();
                handler.sendMessageDelayed(message,3000);


            }
        }.start();
    }
}
