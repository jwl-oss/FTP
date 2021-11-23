package com.example.ftp.Utils;

import com.example.ftp.FtpClient;

import java.io.File;
import java.io.IOException;

public class FtpUtil {
    private static FtpClient client;
    private static String address;
    private static int port;
    private static String username;
    private static String password;
    private static String DTP_mode = "PASV";
    private static String message;
    private static String[] files;
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

    public static String Connect() throws InterruptedException {
        message = "false";
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    if (client.isConnected()) {
                        client.closeConnection();
                    }
                    if (client.openConnect(address, port)) {
                        if (client.user(username)) {
                            if (client.password(password)) {
                                message = "true";
                                System.out.println("login successfully");
                            } else {
                                message = "password not correctly";
                                client.closeConnection();
                            }
                        } else {
                            message = "user not exits";
                            client.closeConnection();
                        }
                        message = "true";
                    }
                    } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        };
        Thread subThread = new Thread(run);
        subThread.start();
        subThread.join(1000);
        return message;
    }

    public static boolean disconnect() throws IOException, InterruptedException {
        flag = false;
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    client.closeConnection();
                    client.data_disconnect();
                    flag = true;
                }catch (IOException e){
                }
            }
        };
        Thread subThread = new Thread(run);
        subThread.start();
        subThread.join(1000);
        return flag;
    }

    public static boolean upload(String remotePath,String localPath) throws IOException, InterruptedException {
        flag = false;
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    client.upload(remotePath, localPath);
                    flag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread subThread = new Thread(run);
        subThread.start();
        subThread.join(1000);
        return flag;
    }

    public static boolean download(String remotePath,String localPath,String filename) throws IOException, InterruptedException {
        flag = false;
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    client.download(remotePath, localPath, filename);
                    flag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread subThread = new Thread(run);
        subThread.start();
        subThread.join(1000);
        return flag;
    }

    public static String getFM(){
        return client.getTransfer_mode();
    }

    public static void setType(String type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.TYPE(type);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void setMode(String mode){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.MODE(mode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void setStru(String mode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.STRU(mode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    public static void setTM(String mode,int port) {
        client.setTransferArgs(mode,port);
    }

    public static String[] list(String remotePath) throws IOException, InterruptedException {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    files = client.LIST(remotePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread subThread = new Thread(run);
        subThread.start();
        subThread.join(1000);
        return files;
    }

    public static boolean isDirectory(String remotePath) throws IOException, InterruptedException {
        flag = false;
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    files = client.LIST(remotePath);
                    if(files.length !=1)
                        flag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread subThread = new Thread(run);
        subThread.start();
        subThread.join(1000);
        return flag;
    }

}
