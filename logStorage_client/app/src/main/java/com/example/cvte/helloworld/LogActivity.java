package com.example.cvte.helloworld;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ServiceWorkerClient;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;

public class LogActivity extends AppCompatActivity {
    private boolean socketStatus = false;
    public EditText IP_addr ;
    public  EditText Port ;
    String staticIP;
    boolean flag = true;
    String ServerMsg = null;
    String temp = null;
    OutputStream outputStream = null;
    DataOutputStream ps;
    DataInputStream []fis = new DataInputStream[4];
    int fis_index = 0;
    int length;
    Socket socket = null;
    InputStream RecvMesg = null;
    InputStreamReader inputStreamReader = null;
    BufferedReader br = null;
    String msg;
    byte[] by = new byte[8196];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_layout);
        Log.d("HelloworldActivity", "onCreate execute");
        Log.e("HelloworldActivity", "onCreate execute");
        Button button1 = (Button) findViewById(R.id.button1);
        Button button = (Button) findViewById(R.id.button2);
        IP_addr = (EditText)findViewById(R.id.editText);
        Port = (EditText)findViewById(R.id.editText2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (socket != null)
                        socket.close();
                    if (outputStream != null)
                        outputStream.close();
                    if(ps != null) ps.close();
                    if(inputStreamReader != null)
                        inputStreamReader.close();
                    if(br != null)  br.close();
                    if (RecvMesg != null)
                        RecvMesg.close();
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connect();
            }
        });
    }


    public void connect() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                if (!socketStatus) {
                    try {
                        // ip 和端口要和服务器一致
                        staticIP = IP_addr.getText().toString();
                        //socket = new Socket(staticIP, Integer.parseInt(Port.getText().toString()));
                        socket = new Socket("172.17.84.220",8668);
                        if (socket == null) {
                        } else {
                            socketStatus = true;
                        }
                        outputStream = socket.getOutputStream();
                        ps = new DataOutputStream(socket.getOutputStream());
                        //pw = new PrintWriter(outputStream);
                        RecvMesg = socket.getInputStream();
                        inputStreamReader = new InputStreamReader(RecvMesg);
                        br = new BufferedReader(inputStreamReader);
                        if (socketStatus) {
                            try {
                                //读本地 /data/log.txt 的内容上传到服务器
                                int len = 0;
                                byte[] bytes = new byte[1024];
                                String version = Build.VERSION.RELEASE;
                                String Hardware = Build.HARDWARE;
                                String info = Hardware+"-"+version;
                                String string = "logstart"+info+"\0";
                                byte[] byte1 = new byte[8192];
                                byte[] byte2 = new byte[8192];
                                byte1 = info.getBytes();
                                String over = "logfinish\0";
                                byte2 = over.getBytes();
                                //ps.write(string.getBytes());
                                //向server发送连接请求
                                ps.writeBytes(string);
                                ps.flush();
                                //打开本地相关log文件
                                fis[0]= new DataInputStream(new BufferedInputStream(new FileInputStream("/data/log.txt")));
                                fis[1] = new DataInputStream(new BufferedInputStream(new FileInputStream("/data/anr/traces.txt")));
                                fis[2] = new DataInputStream(new BufferedInputStream(new FileInputStream("/data/tombstones/tombstone_00")));
                                fis[3] = new DataInputStream(new BufferedInputStream(new FileInputStream("/etc/event-log-tags")));

                                Log.d("HelloworldActivity", "onCreate execute666");
                                //循环接收处理server端返回信息
                                while((ServerMsg = br.readLine())!=null)
                                {
                                    Log.d("test4" , ServerMsg);
                                    //server端发送相关命令的处理
                                    if(!ServerMsg.equals("recv successfully"))
                                    {
                                        Log.d("test5" , ServerMsg);
                                        String CommandRet;
                                        //命令执行并返回结果
                                        CommandRet = new String(execRootCmd(ServerMsg));
                                        ps.writeBytes(CommandRet);
                                        ps.flush();
                                    }
                                    //向server端发送log
                                    else
                                    {
                                        if(((len = fis[fis_index].read(bytes)) != -1))
                                        {
                                            ps.write(bytes, 0, len);
                                            ps.flush();
                                        }
                                        else
                                        {
                                            if(fis_index == 3)
                                            {
                                                fis_index = 0;
                                                ps.writeBytes(over);
                                                ps.flush();
                                            }
                                            else {
                                                fis_index++;
                                                ps.writeBytes(over);
                                                ps.flush();
                                                try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                      e.printStackTrace();
                                  }
                                            }
                                        }
                                    }
                                }

                            } catch(IOException e){
                                    e.printStackTrace();
                                }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    //client启动exec执行server端命令函数
    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        BufferedReader bread = null;

        try {
            //Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            Process p = Runtime.getRuntime().exec(cmd);
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            //Log.i(TAG, cmd);
            bread = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            dos.writeBytes(cmd + "\n");
//            dos.flush();
//            dos.writeBytes("exit\n");
//            dos.flush();
            String line = null;
            while ((line = bread.readLine()) != null) {
                Log.d("result", line);
                result += line;
                result += "\n";
            }
            //p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                    try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(bread != null)
            {
                try{
                    bread.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
