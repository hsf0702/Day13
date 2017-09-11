package com.example.a25737;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lv_news;
    private LinearLayout loading;
    private List<NewsInfo> newsInfos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_news = (ListView) findViewById(R.id.lv_news);
        loading = (LinearLayout) findViewById(R.id.loading);
        initData();
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                try {
                    String path="http://192.168.126.98:8080/newsinfo.xml";

                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");

                    int code = conn.getResponseCode();
                    if(code==200){
                        InputStream is = conn.getInputStream();
                        newsInfos = NewsInfoService.getNewsInfos(is);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lv_news.setAdapter(new MyAdapter());
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return newsInfos.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertview, ViewGroup viewGroup) {
            View view;
            if(convertview==null){
                view = View.inflate(getApplicationContext(),R.layout.news_item,null);
            }else{
                view = convertview;
            }
            SmartImageView siv = (SmartImageView) view.findViewById(R.id.siv_icon);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            TextView tv_description = (TextView) view.findViewById(R.id.tv_description);
            TextView tv_type = (TextView) view.findViewById(R.id.tv_type);

            NewsInfo newsInfo = newsInfos.get(i);
            siv.setImageUrl(newsInfo.getIconPath(), R.mipmap.ic_launcher, R.mipmap.ic_launcher);
            tv_title.setText(newsInfo.getTitle());
            tv_description.setText(newsInfo.getDescription());
            int type = newsInfo.getType(); // 1. 一般新闻 2.专题 3.live
            //不同新闻类型设置不同的颜色和不同的内容
            switch (type) {
                case 1:
                    tv_type.setText("评论:" + newsInfo.getComment());
                    break;
                case 2:
                    tv_type.setTextColor(Color.RED);
                    tv_type.setText("专题");
                    break;
                case 3:
                    tv_type.setTextColor(Color.BLUE);
                    tv_type.setText("LIVE");
                    break;
            }
            return view;
        }
    }
}
