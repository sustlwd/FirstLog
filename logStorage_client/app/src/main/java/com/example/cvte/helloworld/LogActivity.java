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
import java.util.ArrayList;
import java.util.List;

public class LogActivity extends AppCompatActivity {
    private boolean socketStatus = false;
    public EditText IP_addr;
    public EditText Port;
    String staticIP;
    String ServerMsg = null;
    OutputStream outputStream = null;
    DataOutputStream ps;
    DataInputStream[] fis = new DataInputStream[4];
    int fis_index = 0;
    Socket socket = null;
    InputStream RecvMesg = null;
    InputStreamReader inputStreamReader = null;
    BufferedReader br = null;
    private boolean click1 = true;
    private boolean click2 = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_layout);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button = (Button) findViewById(R.id.button2);
        IP_addr = (EditText) findViewById(R.id.editText);
        Port = (EditText) findViewById(R.id.editText2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                close_apk();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(click1) {
                    start_apk();
                }
            }
        });
    }


    public void start_apk() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                if (!socketStatus) {
                        Init_Client();
                        if (socketStatus) {
                            communication();
                        }
                }
            }
        };
        thread.start();
    }

    //client启动exec执行server端命令函数
    public static String execRootCmd(String cmd) {
        String result = "";
        String ErrRet = "not found command";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        BufferedReader bread = null;
        BufferedReader errread = null;
        String line = null;
        String ErrMsg = null;
        String ErrRst = null;

        try {
            Process p = Runtime.getRuntime().exec(cmd);
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            //Log.i(TAG, cmd);
            bread = new BufferedReader(new InputStreamReader(p.getInputStream()));
            errread = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((line = bread.readLine()) != null) {
                //Log.d("result", line);
                result += line;
                result += "\n";
            }
            while ((ErrMsg = errread.readLine()) != null) {
                //Log.e("error result", ErrMsg);
                ErrRst += ErrMsg;
                ErrRst += "\n";
            }
            //if(ErrRst != null)
                //Log.e("Error", ErrRst);
           // p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bread != null) {
                try {
                    bread.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(errread != null)

                try{
                    errread.close();
                }catch (IOException e){
                    e.printStackTrace();
                }

        }
        if(ErrRst != null)
            return ErrRst;
        else if(result != null)
            return result;
        else
            return ErrRet;
    }


    //复制小段String，起始位置为f，终止位置为t
    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }

    //分割String，每段长length，一共分为size个小段
    public static List<String> getStrList(String inputString, int length, int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    //将inputString分割为每段长度为length的多个小段
    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }

// 关闭apk
    public  void close_apk(){
        if(click2){
            try {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ps.flush();
                //ps.writeBytes("client shutdown");
                //ps.flush();
                for(fis_index = 0;fis_index < 4;fis_index++)
                {
                    if(fis[fis_index] != null)
                        fis[fis_index].close();
                }
                if (RecvMesg != null)
                    RecvMesg.close();

                if (br != null) br.close();

                if (inputStreamReader != null)
                    inputStreamReader.close();

                if (ps != null) ps.close();

                if (outputStream != null)
                    outputStream.close();

                if (socket != null)
                    socket.close();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            click2 = false;
        }
    }
    //打开相关log文件
    public void Open_file(){
        try{
        fis[0] = new DataInputStream(new BufferedInputStream(new FileInputStream("/data/log.txt")));
        fis[1] = new DataInputStream(new BufferedInputStream(new FileInputStream("/data/anr/traces.txt")));
        fis[2] = new DataInputStream(new BufferedInputStream(new FileInputStream("/data/tombstones/tombstone_00")));
        fis[3] = new DataInputStream(new BufferedInputStream(new FileInputStream("/etc/event-log-tags")));
    }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //初始化client
    public void Init_Client(){
        try {
            staticIP = IP_addr.getText().toString();
            //socket = new Socket(staticIP, Integer.parseInt(Port.getText().toString()));
            socket = new Socket("172.17.84.220", 8668);
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
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //收发数据
    public void communication() {
        try {
            //读本地 /data/log.txt 的内容上传到服务器
            int len = 0;
            byte[] bytes = new byte[1024];
            String version = Build.VERSION.RELEASE;
            String Hardware = Build.HARDWARE;
            String info = Hardware + "-" + version;
            String string = "logstart" + info + "\0";
            String over = "logfinish\0";
            //ps.write(string.getBytes());
            //向server发送连接请求
            ps.writeBytes(string);
            ps.flush();

            //打开本地相关log文件
            Open_file();

            //循环接收处理server端返回信息
            while ((ServerMsg = br.readLine()) != null) {
                //Log.d("test4" , ServerMsg);
                //server端发送相关命令的处理
                if (!ServerMsg.equals("recv successfully")) {
                    //Log.d("test5", ServerMsg);
                    String CommandRet = null;
                    //命令执行并返回结果
                    CommandRet = new String(execRootCmd(ServerMsg));
                    if(CommandRet.length() <= 2048) {
                        //ps.flush();
                        ps.writeBytes(CommandRet);
                        ps.flush();
                    }
                    else
                    {
                        List<String> list = getStrList(CommandRet , 2048);
                        for(int i = 0; i < list.size(); i++)
                        {
                            ps.writeBytes(list.get(i));
                            ps.flush();
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        //list.clear();
                    }
                }
                //向server端发送log
                else {
                    if (((len = fis[fis_index].read(bytes)) != -1)) {
                        ps.write(bytes, 0, len);
                        ps.flush();
                    } else {
                        if (fis_index == 3) {
                            fis_index = 0;
                            ps.writeBytes(over);
                            ps.flush();
                        } else {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

