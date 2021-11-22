package com.example.ftp.Utils;

import com.example.ftp.FtpClient;

import java.io.IOException;

public class FtpUtil {
    private static FtpClient client;
    private static String address;
    private static int port;
    private static String username;
    private static String password;
    private static String DTP_mode = "PASV";
    private static boolean flag;

    public static void init(String ip, int Port, String user, String pwd){
        address = ip;
        port = Port;
        username = user;
        password = pwd;
        if(client == null){
            client = new FtpClient();
        }
        if(client.isConnected()){
            try {
                client.closeConnection();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static boolean Connect(String userInfo){
        flag = true;
        new Thread(new Runnable() {
            @Override
            public void run(){
                try{
                    if(client.isConnected()){
                        client.closeConnection();
                    }
                    switch(userInfo) {
                        case "anonymous":
                            if (client.openConnect(address, port)) {
                                //client.ACCT(userInfo);
                                flag = true;
                            }
                            break;
                        case "user":
                            if(client.openConnect(address,port)){
                                //client.ACCT(userInfo);
                                if(client.login(username,password)){
                                    flag = true;
                                }
                            }
                            break;
                    }

                }catch (IOException e){
                    flag = false;
                }
            }
        }).start();
        return flag;
    }

    public static boolean disconnect() throws IOException {
        flag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.closeConnection();
                    client.data_disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return flag;
    }


    public static boolean upload(String remotePath,String localPath){
        flag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.upload(remotePath, localPath);
                    flag = true;
                } catch (IOException e) {
                    flag = false;
                }
            }
        }).start();
        return flag;
    }

    public static boolean download(String remotePath,String localPath,String filename){
        flag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.download(remotePath, localPath, filename);
                    flag = true;
                } catch (IOException e) {
                    flag = false;
                }
            }
        }).start();
        return flag;
    }


    public static String getFM(){
        return client.getTransfer_mode();
    }

    public static void setTM(String mode,int port){
        client.setTransferArgs(mode,port);
    }

    public static boolean getFlag(){
        return flag;
    }
}
