package com.example.a25737;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    //拓展思维：
        /*
        1.给进度条设置下载进度百分比-------参考   2_多线程断点下载   成功解决-----------getProgress，getMax----setMax,setProgress

        2.①当未点击下载的时候，暂停和继续按钮都是不可点击事件
        ②当点击下载后或暂停后，下载按钮变成不可用，只能操做暂停和继续
        ③当下载完成后，所有按钮变不可用
        -------成功解决----a.但是考虑到按钮间相互调用设置状态的时候，只能用匿名内部类按钮实现监听的方法
                           b.如果按钮作用相似，且只实现单一的功能时，用click---int id = view.getId();---switch(id),case R.id.bt_down:
                           c.


       3.类比其他方式，下载的优缺点



       4.拓展项目：
       ①使用圆形进度条扫描文件或下载进度，并配以颜色
         */

    private EditText et_path;
    private ProgressBar pb;
    private boolean isStop;
    private Button bt_begin;
    private Button bt_stop;
    private Button bt_continue;
    private TextView tv_pb;
    private int length;
    private int startIndex;
    private int total;

    //更新下载进度标记
    private static final int UPDATE_PROGRESS = 0;
    //下载完毕请求
    private static  final int DOWNLOADOVER = 1;

    //加入消息处理机制
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DOWNLOADOVER:
                    Toast.makeText(MainActivity.this, "文件下载完毕", Toast.LENGTH_SHORT).show();
                    bt_stop.setEnabled(false);
                    bt_continue.setEnabled(false);
                    tv_pb.setText("下载完成 ");
                    break;
                case UPDATE_PROGRESS:
                    tv_pb.setText("当前进度："+(pb.getProgress()*100/pb.getMax())+"%");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_path =  (EditText) findViewById(R.id.et_path);
        pb =  (ProgressBar) findViewById(R.id.progressBar);
        bt_begin =   (Button) findViewById(R.id.bt_begin);
        bt_stop =   (Button) findViewById(R.id.bt_stop);
        bt_continue =   (Button) findViewById(R.id.bt_continue);
        tv_pb =  (TextView) findViewById(R.id.tv_pb);
        //初始化按钮事件
        bt_begin.setEnabled(true);
        bt_stop.setEnabled(false);
        bt_continue.setEnabled(false);

        bt_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开始下载
                bt_begin.setEnabled(false);
                bt_stop.setEnabled(true);
                bt_continue.setEnabled(true);
                startDownLoad();
            }
        });

        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //暂停下载
                bt_stop.setEnabled(false);
                bt_continue.setEnabled(true);
                isStop = true;
            }
        });

        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //继续下载
                isStop = false;
                bt_stop.setEnabled(true);
                bt_continue.setEnabled(false);
                startDownLoad();
            }
        });

    }
    private String getFileName(String path) {
        int start = path.lastIndexOf("/") + 1;
        //指定文件生成目录
        String datapath = getFilesDir().getPath();//获取当前data/data/当前包名/files
        return datapath+"/"+path.substring(start);
    }
    private void startDownLoad() {
        //为什么在此处开启线程----因为出现了网络请求
        new Thread(){
            @Override
            public void run() {
                try {
                String path = et_path.getText().toString().trim();
                URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);

                    //如果断过，记录位置，判断 .txt是否存在，如果存在就继续下载
                    File file = new File(getFileName(path) + ".txt");
                    if(file.exists()&&file.length()>0){
                        FileInputStream fis = new FileInputStream(file);
                        BufferedReader bufr = new BufferedReader((new InputStreamReader(fis)));
                        String lastDownloadPosition = bufr.readLine();
                        startIndex =  Integer.parseInt(lastDownloadPosition);
                        conn.setRequestProperty("range","bytes="+startIndex+"-");
                        bufr.close();
                    }
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    if(code==200){
                        //获取文本的大小
                        length =  conn.getContentLength();
                    }
                    if(code==200||code==206){
                        pb.setMax(length);
                        InputStream is = conn.getInputStream();
                        //根据下载路径获取文件名
                        RandomAccessFile raf = new RandomAccessFile(getFileName(path), "rw");
                        raf.seek(startIndex);

                        int len = 0;
                        total =  startIndex;
                        byte[] buf = new byte[1024];
                        while((len=is.read(buf))!=-1){

                            total+=len;
                            pb.setProgress(total);

                            if(isStop){
                                RandomAccessFile raff = new RandomAccessFile(getFileName(path)+".txt", "rw");
                                raff.write(String.valueOf(total).getBytes());
                                raff.close();
                                break;
                            }
                            raf.write(buf,0,len);

                            //更新下载进度
                            Message msg=Message.obtain();
                            msg.what=UPDATE_PROGRESS;
                            handler.sendMessage(msg);
                        }
                        is.close();
                        raf.close();
                        //判断文件是否下载完成
                        if(total == length){
                            File deleteFile = new File(getFileName(path)+".txt");
                            deleteFile.delete();
                            //更新下载进度
                            Message msg=Message.obtain();
                            msg.what=DOWNLOADOVER;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
