package com.example.ftp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class fileAdapter extends RecyclerView.Adapter<fileAdapter.ViewHolder> {
    private List<File> fileList;
    private File currentFile = Environment.getExternalStorageDirectory();
    private View view;

    public File getCurrentFile(){
        return currentFile;
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

    public fileAdapter(List<File> files){
        fileList = files;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        Button button = (Button) view.findViewById(R.id.upload_button);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = fileList.get(holder.getAdapterPosition());
                if(file.isDirectory()){
                    currentFile = file;
                    fileList.clear();
                    fileList.addAll(Arrays.asList(file.listFiles()));
                    notifyDataSetChanged();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前这个item的路径
                File file = fileList.get(holder.getAdapterPosition());
                String path = file.getAbsolutePath();
                //String fileName = file.getName();
                Toast.makeText(view.getContext(),path,Toast.LENGTH_SHORT).show();
                //产生弹框，获取上传到服务器的路径
                EditText input = new EditText(view.getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("请输入上传服务器的路径");
                builder.setView(input);
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String remotePath = input.getText().toString();
                        if(remotePath!=null&&!remotePath.isEmpty()){
                            FtpUtil.upload(remotePath,path);
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = fileList.get(position);
        holder.fileName.setText(file.getName());
        holder.button.setText("upload");
        if(file.isDirectory()){
            holder.fileImage.setImageResource(R.drawable.directory);
        }else{
            holder.fileImage.setImageResource(R.drawable.file);
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void lastFile(){
        if(!currentFile.equals(Environment.getExternalStorageDirectory())){
            currentFile = currentFile.getParentFile();
            fileList.clear();
            fileList.addAll(Arrays.asList(currentFile.listFiles()));
            notifyDataSetChanged();
        }else{
            Toast.makeText(view.getContext(),"已是根目录",Toast.LENGTH_SHORT).show();
        }
    }

}
