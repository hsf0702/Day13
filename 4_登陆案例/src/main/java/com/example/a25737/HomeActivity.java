package com.example.a25737;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.lv)
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        requestNet();
    }

    private void requestNet() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String path = "http://192.168.0.104:8080/data.json";
                //2.访问这个路径
                URL url = null;
                try {
                    url = new URL(path);
                    //3.通过url对象获取httpurlconnection实例  该实例用于发送或者接收数据
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();                        //4.设置一下联网超时时间
                    conn.setConnectTimeout(5000);
                    //5.设置一下请求的方式
                    conn.setRequestMethod("GET"); //默认是get请求
                    //6.获取服务器返回状态码
                    int code = conn.getResponseCode();
                    //7.对code做判断
                    if (code == 200) {
                        //8.获取服务器返回的数据
                        InputStream is = conn.getInputStream();
                        String s = convertStreamToString(is);
                        boolean b = saveJSon(s);
                        if (b) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(HomeActivity.this, "写入文件成功", Toast.LENGTH_SHORT).show();

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(HomeActivity.this, "写入文件失败", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                        Gson gson = new Gson();
                        MyBean myBean = gson.fromJson(s, MyBean.class);
                        Log.e("text101", "requestNet: 22222" + myBean.getHeader());
                        requestBitMap(myBean.getHeader());
                        List<MyBean.DataBean> data = myBean.getData();
                        final MyAdapter myAdapter = new MyAdapter(data);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lv.setAdapter(myAdapter);
                            }
                        });

                        //10.把集合里面的数据 通过适配器展示出来
             /*   runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //11.更新适配器
                        lv.setAdapter(new MyAdaper());
                    }
                });*/

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("text101", "requestNet: " + e.getMessage());
                }
            }
        }.start();


    }

    private boolean saveJSon(String s) {
        //String path = "storage/sdcard/info.txt";
        //External外部的 Storage 存储 Directory目录
        //通过Environment 调用getExternalStorageDirectory 获取sd卡目录
        File file = new File(Environment.getExternalStorageDirectory(), "info2.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(s.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private void requestBitMap(final String s) {
        new Thread() {
            @Override
            public void run() {
                super.run();
//                String path = "http://192.168.126.41:8080/6666.json";
                //2.访问这个路径
                URL url = null;
                try {
                    url = new URL(s);
                    //3.通过url对象获取httpurlconnection实例  该实例用于发送或者接收数据
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();                        //4.设置一下联网超时时间
                    conn.setConnectTimeout(5000);
                    //5.设置一下请求的方式
                    conn.setRequestMethod("GET"); //默认是get请求
                    //6.获取服务器返回状态码
                    int code = conn.getResponseCode();
                    //7.对code做判断
                    if (code == 200) {
                        //8.获取服务器返回的数据
                        InputStream is = conn.getInputStream();
                        final Bitmap bitmap = BitmapFactory.decodeStream(is);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv.setBackground(new BitmapDrawable(bitmap));
                            }
                        });
                        //10.把集合里面的数据 通过适配器展示出来
             /*   runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //11.更新适配器
                        lv.setAdapter(new MyAdaper());
                    }
                });*/

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("text101", "requestNet: " + e.getMessage());
                }
            }
        }.start();
    }

    public static String convertStreamToString(InputStream is) {
        /*
 3           * To convert the InputStream to String we use the BufferedReader.readLine()
 4           * method. We iterate until the BufferedReader return null which means
 5           * there's no more data to read. Each line will appended to a StringBuilder
 6           * and returned as String.
 7           */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    @OnClick(R.id.iv)
    public void onViewClicked() {
        startActivity(new Intent(this, DemoActivity.class));
    }
}
