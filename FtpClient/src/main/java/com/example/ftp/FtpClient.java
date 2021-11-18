package com.example.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FtpClient {
    private Socket control_sock = null;
    private Socket data_sock = null;
    private int PASV = 0;
    private int PORT = 1;
    private int connectMode = PASV;// 默认被动连接
    private InputStream control_ins;
    private InputStream data_ins;
    private OutputStream control_os;
    private OutputStream data_os;

    public boolean openConnect()throws IOException{
        boolean flag = true;
        try {
            //创建客户端 命令Socket，指定服务器地址和端口
            control_sock = new Socket("192.168.244.1", 21);
            // 当客户端与服务器建立连接后，服务器会返回 220 的响应码
            control_os = control_sock.getOutputStream();
            // 发送连接请求需要发送内容吗？

            // TODO:校验服务端返回的响应码
            control_ins = control_sock.getInputStream();
            System.out.println("连接成功");
        }catch (IOException e){
            flag = false;
        }
        return flag;
    }

    public boolean login(String username,String password) throws IOException{
        boolean flag = false;
        // 当客户端发送用户名和密码，服务器验证通过后，会返回 230 的响应码
        String command_1 = "USER " + username;
        String command_2 = "PASS " + password;
        try{
            control_os = control_sock.getOutputStream();
            control_os.write(command_1.getBytes(StandardCharsets.UTF_8));
            control_os.write(command_2.getBytes(StandardCharsets.UTF_8));
            //TODO：校验服务端返回响应码（？用特定响应码对应具体错误理由）
            control_ins = control_sock.getInputStream();
        }catch (IOException e){
            flag = false;
        }

        return flag;
    }

    public void setConnectMode(int mode)throws IOException{
        control_os = control_sock.getOutputStream();
        connectMode = mode;
        String command = "PASV ";;
        switch (connectMode){
            case 0:
                control_os.write(command.getBytes(StandardCharsets.UTF_8));
                break;
            case 1:
                command = "PORT " + (1024 + Math.random()*10);
                control_os.write(command.getBytes(StandardCharsets.UTF_8));
                break;
        }
    }

    public void uploadFile(){}

    public boolean closeConnect()throws IOException{
        boolean flag = true;
        try {
            control_ins.close();
            control_os.close();
            control_sock.close();
            data_ins.close();
            data_os.close();
            data_sock.close();
        }catch (IOException e){
            flag = false;
        }
        return flag;
    }
}
