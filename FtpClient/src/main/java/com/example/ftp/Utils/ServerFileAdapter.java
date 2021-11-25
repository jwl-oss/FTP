package com.example.ftp.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ftp.R;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ServerFileAdapter extends RecyclerView.Adapter<fileAdapter.ViewHolder>{
    private String[] fileList;
    private String root = "/storage/emulated/0/FTP/FtpServer";
    private String rootFile = "FtpServer";
    private String currentPath = root;//没有currentFile的路径
    private String currentFile;// filename
    private View view;

    public ServerFileAdapter(String[] files){
        fileList = files;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView fileImage;
        TextView fileName;
        Button button;
        public ViewHolder(View view){
            super(view);
            fileImage = (ImageView)view.findViewById(R.id.file_image);
            fileName = (TextView) view.findViewById(R.id.file_name);
            button = (Button) view.findViewById(R.id.upload_button);
        }
    }

    public fileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item,parent,false);
        fileAdapter.ViewHolder holder = new fileAdapter.ViewHolder(view);
        Button button = (Button) view.findViewById(R.id.upload_button);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = fileList[holder.getAdapterPosition()];
                String path = currentPath + File.pathSeparator + fileName;
                try {
                    if(FtpUtil.isDirectory(path)){
                        currentFile = fileName;
                        fileList = FtpUtil.list(path);
                        notifyDataSetChanged();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前这个item的路径
                String fileName = fileList[holder.getAdapterPosition()];
                //String path = currentPath + File.pathSeparator +fileName;
                //String fileName = file.getName();
                System.out.println(currentPath);
                Toast.makeText(view.getContext(),currentFile,Toast.LENGTH_SHORT).show();
                //产生弹框，获取上传到服务器的路径
                EditText input = new EditText(view.getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("请输入下载客户端的路径");
                builder.setView(input);
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String remotePath = input.getText().toString();
                        if(remotePath!=null&&!remotePath.isEmpty()){
                            System.out.println(remotePath);
                            try {
                                FtpUtil.download(remotePath,currentPath,fileName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(v.getContext(),"路径为空",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });
        return  holder;
    }

    public void onBindViewHolder(@NonNull fileAdapter.ViewHolder holder, int position) {
        String fileName = fileList[position];
        holder.fileName.setText(fileName);
        holder.button.setText("download");
        String path = currentPath + File.separator + fileName;
        try {
            if(FtpUtil.isDirectory(path)){
                holder.fileImage.setImageResource(R.drawable.directory);
            }else{
                holder.fileImage.setImageResource(R.drawable.file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return fileList.length;
    }

    public void lastFile() throws IOException, InterruptedException {
        if(!currentFile.equals(rootFile)){
            currentFile = getParent(currentPath);
            currentPath = getParentPath(currentPath);
            fileList = FtpUtil.list(currentPath);
            notifyDataSetChanged();
        }else{
            Toast.makeText(view.getContext(),"已是根目录",Toast.LENGTH_SHORT).show();
        }
    }

    private String getParent(String path){
        String[] dirs = path.split("/");
        return dirs[dirs.length];
    }

    private String getParentPath(String path){
        String[] dirs = path.split("/");
        String parentPath = "";
        for(int i =0;i< dirs.length-1;i++){
            parentPath = parentPath + "/"+dirs[i];
        }
        return parentPath;
    }
}
