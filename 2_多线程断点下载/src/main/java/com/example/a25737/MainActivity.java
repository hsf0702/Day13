package com.example.a25737;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    /*
    此应用的bug:
    1.多次点击按钮更新，进度会变快-----解决办法：点击下载后，将按钮设置为不管用
    2.到54%的时候会出现负值，并减少至0
    3.无法暂停和继续----解决办法：添加相应的按钮方法----衍生出多个按钮间的相互作用的解决办法：①switch这种方法不可以，②

     */

    protected static final int threadCount = 3;
    //更新下载进度标记
    private static final int UPDATE_PROGRESS = 0;
    //下载完毕请求
    private static  final int downloadOver = 1;
    private EditText et_path;
    private ProgressBar pb;
    private TextView tv_pb;
    //当前活动下载线程数
    protected static int activeThread;
    private int curDownCount=0;

    //加入消息处理机制
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case downloadOver:
                    Toast.makeText(MainActivity.this, "文件下载完毕", Toast.LENGTH_SHORT).show();
                    bt_pause.setEnabled(false);
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
    private Button bt_down;
    private Button bt_pause;
    private Button bt_continue;
    private boolean isStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_path =  (EditText) findViewById(R.id.et_path);
        et_path.setText("http://192.168.126.98:8080/OperaNeonSetup.exe");
        pb =  (ProgressBar) findViewById(R.id.pb);
        tv_pb =  (TextView) findViewById(R.id.tv_pb);

        //按钮点击事件
        bt_down =  (Button) findViewById(R.id.bt_down);
        bt_pause =  (Button) findViewById(R.id.bt_pause);
        bt_continue =  (Button) findViewById(R.id.bt_continue);

        bt_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                bt_pause.setEnabled(true);
                bt_continue.setEnabled(true);
                //获取下载路径
                final String path = et_path.getText().toString().trim();
                //判断路径是否为空
                if(TextUtils.isEmpty(path)){
                    Toast.makeText(MainActivity.this, "请输入下载路径", Toast.LENGTH_SHORT).show();
                    return;
                }
                //开启一个线程
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(path);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setConnectTimeout(5000);
                            conn.setRequestMethod("GET");
                            int code = conn.getResponseCode();
                            if(code==200){
                                //获取请求数据的长度
                                int length = conn.getContentLength();
                                pb.setMax(length);
                                //在客户端创建一个跟服务器文件大小相同的临时文件
                                RandomAccessFile raf=new RandomAccessFile("sdcard/setup.exe", "rwd");
                                //指定临时文件的长度
                                raf.setLength(length);
                                raf.close();

                                //假设三个子线程下载，设置每个线程下载文件的大小
                                int blockSize=length/threadCount;
                                for (int threadId = 1; threadId <= threadCount; threadId++) {
                                    //当前线程下载数据的开始位置
                                    int startIndex=blockSize*(threadId-1);
                                    //当前线程下载数据的结束位置
                                    int endIndex=blockSize*threadId-1;
                                    //确定最后一个线程要下载数据的最大位置
                                    if (threadId==threadCount) {
                                        endIndex=length;
                                    }
                                    //显示下载数据的区间
                                    System.out.println("线程【"+threadId+"】开始下载："+startIndex+"---->"+endIndex);
                                    //开启下载的子线程
                                    new DownloadThread(path, threadId, startIndex, endIndex).start();
                                    //当前下载活动的线程数加1
                                    activeThread++;
                                    System.out.println("当前活动的线程数："+activeThread);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        bt_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                bt_continue.setEnabled(true);
                isStop = true;
            }
        });

        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                bt_pause.setEnabled(true);
                //继续下载
                isStop = false;
//                DownloadThread();
            }
        });
    }

//    public void click(View view){------------//很遗憾，对于解决本项目该方法行不通
//        int id = view.getId();
//        boolean b1=false,b2=false,b3=false;
//        switch (id){
//            case R.id.bt_down:
//
//                break;
//            case R.id.bt_pause:
//                view.setEnabled(b2);
//
//                break;
//            case R.id.bt_continue:
//                view.setEnabled(b3);
//                break;
//
//        }
//
//    }

    private class DownloadThread extends Thread{
        private String path;
        private int threadId;
        private int startIndex;
        private int endIndex;
        public DownloadThread(String path, int threadId, int startIndex, int endIndex) {
            this.path = path;
            this.threadId = threadId;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public void run() {
            try {
            File tempFile = new File("sdcard/"+threadId+".txt");
            //检查记录是否存在,如果存在读取数据，设置真实下载开始的位置
            if(tempFile.exists()){
                FileInputStream fis = new FileInputStream(tempFile);
                byte[] temp = new byte[2048];

                int length = fis.read(temp);
                //读取到已经下载的位置
                int downloadNewIndex = Integer.parseInt(new String(temp, 0, length));
                //计算出已经下载的数据长度
                int alreadyDown = downloadNewIndex - startIndex;
                //累加已经下载的数据量
                curDownCount+=alreadyDown;
                //设置进度条已经下载的数据量
                pb.setProgress(curDownCount);
                // //设置重新开始下载的开始位置
                startIndex=downloadNewIndex;
                fis.close();
                //显示真实下载数据的区间
                System.out.println("线程【"+threadId+"】真实开始下载数据区间："+startIndex+"---->"+endIndex);
            }

                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                //设置请求属性，请求部分资源,断点下载核心逻辑
                conn.setRequestProperty("Range","bytes="+startIndex+"-"+endIndex);
                int code = conn.getResponseCode();
                //下载部分资源，正常返回的状态码为206
                if(code==206){
                    InputStream is = conn.getInputStream();//已经设置了请求的位置，所以返回的是对应的部分资源
                    //构建随机访问文件
                    RandomAccessFile raf = new RandomAccessFile("sdcard/setup.exe", "rwd");
                    //设置 每一个线程随机写文件开始的位置
                    raf.seek(startIndex);
                    //开始写文件
                    int len=0;
                    byte[] buffer=new byte[1024];
                    //该线程已经下载数据的长度
                    int total=0;

                    while((len=is.read(buffer))!=-1) {//读取输入流
                        //记录当前线程已下载数据的长度
                        RandomAccessFile file = new RandomAccessFile("sdcard/" + threadId + ".txt", "rwd");
                        raf.write(buffer, 0, len);//写文件
                        total += len;//更新该线程已下载数据的总长度
                        System.out.println("线程【" + threadId + "】已下载数据：" + (total + startIndex));
                        //将已下载数据的位置记录写入到文件
                        file.write((startIndex + total + "").getBytes());
                        //累加已经下载的数据量
                        curDownCount += len;
                        //更新进度条【进度条的更新可以在非UI线程直接更新，具体见底层源代码】
                        pb.setProgress(curDownCount);

                        //更新下载进度
                        Message msg=Message.obtain();
                        msg.what=UPDATE_PROGRESS;
                        handler.sendMessage(msg);
                        file.close();
                    }
                    is.close();
                    raf.close();
                    //提示下载完毕
                    System.out.println("线程【"+threadId+"】下载完毕");
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("线程【"+threadId+"】下载出现异常！！");
            }finally {
                //活动的线程数减少
                activeThread--;
                if(activeThread==0){
                    for (int i = 1; i <= threadCount; i++) {
                        File tempFile=new File("sdcard/"+i+".txt");
                        tempFile.delete();
                    }
                    System.out.println("下载完毕，已清除全部临时文件");
                    //界面消息提示下载完毕
                    Message msg=new Message();
                    msg.what=downloadOver;
                    handler.sendMessage(msg);
                }
            }
        }
    }
}
