package com.example.ftp;

import android.provider.ContactsContract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FtpClient {
    private Socket control_sock = null;
    private Socket data_sock = null;
    private String ipAddress;
    private BufferedReader control_br;
    private OutputStream data_os;
    private PrintWriter control_pw;
    private InputStream data_is;
    private boolean isConnected = false;
    private boolean data_connected = false;
    private String transfer_mode = "PASV";// 默认
    private int data_port = 4444;//默认
    private int deep = 0;
    // 根目录
    private String root = "./";


    public boolean openConnect(String host,int port)throws IOException{
        boolean flag = false;
        ipAddress = host;
        try {
            //创建客户端 命令Socket，指定服务器地址和端口
            control_sock = new Socket(host, port);
            // 当客户端与服务器建立连接后，服务器会返回 220 的响应码
            OutputStream outputStream = control_sock.getOutputStream();
            control_pw = new PrintWriter(new OutputStreamWriter(outputStream));
            // 发送命令
            sendCommand("CONN\n",control_pw);

            // 建立好连接后，从socket中获取输入流
            InputStream inputStream = control_sock.getInputStream();
            control_br = new BufferedReader(new InputStreamReader(inputStream));
            // TODO:校验服务端返回的响应码
            if(receive(control_br).equals("OK")) {
                isConnected = true;
                flag = true;
                sendCommand("successfully \n",control_pw);
                receive(control_br);
            }
        }catch (IOException e){
            flag = false;
        }
        return flag;
    }

    public boolean isConnected(){
        return isConnected;
    }

    public boolean login(String username,String password) throws IOException{
        boolean flag = false;
        // 当客户端发送用户名和密码，服务器验证通过后，会返回 230 的响应码
        sendCommand("USER "+ username +"\n",control_pw);
        if(receive(control_br).equals("OK")){
            sendCommand("PASS " + password +"\n",control_pw);
            if(receive(control_br).equals("OK")){
                sendCommand("successfully \n",control_pw);
                receive(control_br);
                flag = true;
            }
        }
        return flag;
    }

    // 上传 STOR
    public void upload(String remotePath,String localPath) throws IOException {
        if(!data_connected){
            data_connect();
        }
        String command = "STOR " + remotePath +"\n";
        sendCommand(command,control_pw);
        // 判断上传的是文件还是文件夹
        File file = new File(localPath);
        // 文件直接上传
        if(file.exists()&&file.isFile()){
            BufferedReader br = new BufferedReader(new FileReader(file));
            String data = "";
            String str;
            while((str = br.readLine()) != null){
                data = data + "\n" + str;
            }
            data_os.write(data.getBytes(StandardCharsets.UTF_8));
            br.close();
        }else if(file.isDirectory()){
            // TODO
            File[] folder = file.listFiles();

        }
        receive(control_br);
        data_disconnect();
    }

    // 下载 RSTR
    public void download(String remotePath,String filename,String localPath) throws IOException {
        if(!data_connected){
            data_connect();
        }
        // 利用deep查看是否还在递归
        deep ++;
        // TODO:遍历本地目录查询是否存在此文件，有则覆盖

        // 创建文件
        localPath = localPath + File.pathSeparator + filename;
        File file = new File(localPath);
        // 发送命令
        String command = "RSTR " + remotePath + "\n";
        sendCommand(command,control_pw);
        // 此时服务器返回文件名是目录（Dir）还是文件（File）
        String type = receive(control_br);
        // 当返回为目录时，查看当前目录内容
        if(type.equals("Dir")){
            // 创建当前目录
            if(file.mkdirs()) {
                String[] currentDir = CWD(remotePath);
                // 遍历目录下的文件
                for (int i = 0; i < currentDir.length; i++) {
                    String path = remotePath + File.pathSeparator + currentDir[i];
                    download(path, currentDir[i], localPath);
                }
            }
        }else if(type.equals("File")){
            FileWriter fw = new FileWriter(file.getName(),true);
            BufferedWriter bw = new BufferedWriter(fw);
            data_receive(data_is,bw);
            bw.close();
        }
        deep --;
        if(deep == 0){
            // 递归结束，关闭数据链路
            data_disconnect();
        }
    }

    private boolean data_pasv() throws IOException {
        boolean flag = false;
        sendCommand("PASV\n",control_pw);
        if(receive(control_br).equals("OK")){
            // 进行data_socket连接
            data_sock = new Socket(ipAddress,data_port);
            data_os = data_sock.getOutputStream();
            data_is = data_sock.getInputStream();
            flag = true;
        };
        return flag;
    }

    private boolean data_port() throws IOException{
        boolean flag = false;
        // 客户端使用自己的端口
        ServerSocket serverSocket = new ServerSocket(data_port);
        // 等待服务器接入
        data_sock = serverSocket.accept();
        // 获取客户端ip地址
        InetAddress localIP = control_sock.getLocalAddress();
        String command = "PORT " + data_port + " " + localIP + "\n";
        sendCommand(command,control_pw);
        if(receive(control_br).equals("OK")){
            data_is = data_sock.getInputStream();
            if(receive(control_br).equals("OK")) {
                System.out.println("data_connect successfully");
                data_os = data_sock.getOutputStream();
                data_is = data_sock.getInputStream();
                flag = true;
            }
        }
        return  flag;
    }

    public void sendCommand(String command,PrintWriter pw) throws IOException {
        // 发送命令给服务器
        pw.println(command);
        pw.flush();
    }

    public String receive(BufferedReader br) throws IOException {
        // 读取返回值
        String response = br.readLine();
        return response;
    }

    // 接受数据
    public void data_receive(InputStream ins,BufferedWriter bw) throws IOException{
        byte[] bytes = new byte[1024];
        int len;
        String data;
        while((len = ins.read(bytes)) != -1){
            data = new String(bytes,"UTF-8");
            bw.write(data);
        }
        bw.flush();
    }

    // 查询目录下的文件
    public String[] CWD(String remotePath) throws IOException {
        String command = "CWD " + remotePath + "\n";
        sendCommand(command,control_pw);
        // 目录名或者文件名用空格分开
        String[] dirInfo = receive(control_br).split(" ");
        return dirInfo;
    }

    public void MKD(String remotePath) throws IOException {
        String command = "MKD " + remotePath +"\n";
        sendCommand(command,control_pw);
        receive(control_br);
    }

    public void RMD(String remotePath) throws IOException {
        String command = "RMD " + remotePath +"\n";
        sendCommand(command,control_pw);
        receive(control_br);
    }

    public void TYPE(String type) throws IOException {
        String command = "RMD " + type +"\n";
        sendCommand(command,control_pw);
        receive(control_br);
    }

    public void MODE(String mode) throws IOException {
        String command = "MODE " + mode +"\n";
        sendCommand(command,control_pw);
        receive(control_br);
    }

    public void deleteFile(String remotePath) throws IOException {
        String command = "DELE " + remotePath +"\n";
        sendCommand(command,control_pw);
        receive(control_br);
    }

    public void LIST() throws IOException {
        String command = "LIST " + "\n";
        sendCommand(command,control_pw);
        String structure = receive(control_br);
    }

    public void ACCT(String accountInfo) throws IOException {
        String command = "ACCT " + accountInfo +"\n";
        sendCommand(command,control_pw);
        receive(control_br);
    }

    // 关闭数据连接
    public void data_disconnect()throws IOException{
        data_os.close();
        data_is.close();
        data_sock.close();
        data_connected = false;
    }

    private void data_connect()throws IOException{
        if(data_connected = true)
            data_disconnect();
        switch (transfer_mode){
            case "PASV":
                data_pasv();
                break;
            case "PORT":
                data_port();
                break;
        }
        data_connected = true;
    }

    public String getTransfer_mode(){
        return transfer_mode;
    }

    public void setTransferArgs(String mode,int port){
        this.data_port = port;
        this.transfer_mode = mode;
    }

    // QUIT
    public void closeConnection() throws IOException {
        sendCommand("QUIT\n",control_pw);
        control_br.close();
        control_pw.close();
        control_sock.close();
        isConnected = false;
    }

}
