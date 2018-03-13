package com.example.cvte.helloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class LogActivity extends AppCompatActivity {
    private boolean socketStatus = false;
    String staticIP = "172.17.84.234";
    OutputStream outputStream = null;
    String data = "aaaa\0";
    Socket socket = null;
    InputStream ReadLogcatFile = null;
    InputStream ReadtraceFile = null;
    InputStream ReadtombFile = null;
    InputStream RecvMesg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_layout);
        Log.d("HelloworldActivity","onCreate execute");
        Log.e("HelloworldActivity","onCreate execute");
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                connect();
                send();
                try {
                    outputStream.close();
                    ReadLogcatFile.close();
                    ReadtombFile.close();
                    ReadtraceFile.close();
                    RecvMesg.close();
                    socket.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void connect(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                if (!socketStatus) {
                    try {
                        // ip 和端口要和服务器一致
                        socket = new Socket(staticIP,8001);
                        if(socket == null){
                        }else {
                            socketStatus = true;
                        }
                        outputStream = socket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        thread.start();
    }

    public void send(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                if(socketStatus){
                    try {
                        //读本地 /data/log.txt 的内容上传到服务器
                        int len = 0;
                        byte[] bytes = new byte[1024];
                       ReadLogcatFile = new FileInputStream("/data/log.txt");
                       ReadtraceFile = new FileInputStream("/data/anr/trace.txt");
                       ReadtombFile = new FileInputStream("/data/anr/tomb.txt");
                       RecvMesg = socket.getInputStream();
                        byte[] request = new byte[128];
                        String string = "client is going to send logdata to server\0";
                        String over = "finish\0";
                        outputStream.write(string.getBytes());
                        outputStream.flush();
                        while(true){
                            //上传logcat
                            while((len=ReadLogcatFile.read(bytes)) != -1) {
                                outputStream.write(bytes, 0, len);
                                outputStream.flush();
                                //sleep(1);
                                RecvMesg.read(bytes);
                            }
                            outputStream.write(over.getBytes());
                            outputStream.flush();
                            RecvMesg.read(bytes);
                            //读完清空logcat.txt 等待10s


                            //上传trace
                            while((len=ReadtraceFile.read(bytes)) != -1) {
                                outputStream.write(bytes, 0, len);
                                outputStream.flush();
                                //sleep(1);
                                RecvMesg.read(bytes);
                            }
                            outputStream.write(over.getBytes());
                            outputStream.flush();
                            RecvMesg.read(bytes);
                            //读完清空trace.txt 等待10s


                            //上传tomb
                            while((len=ReadtombFile.read(bytes)) != -1) {
                                outputStream.write(bytes, 0, len);
                                outputStream.flush();
                                //sleep(1);
                                RecvMesg.read(bytes);
                            }

                            outputStream.write(over.getBytes());
                            outputStream.flush();
                            RecvMesg.read(bytes);
                            //读完清空tomb.txt 等待10s
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        //outputStream.close();
    }
}
