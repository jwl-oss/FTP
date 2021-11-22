package com.example.ftp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class transfer_fragment extends Fragment {
    private TextView textView;

    @NonNull
    public static transfer_fragment newInstance(String currentMode) {
        transfer_fragment fragment = new transfer_fragment();
        Bundle args = new Bundle();
        args.putString("mode", currentMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textView = getActivity().findViewById(R.id.currentMode);
        textView.setText("当前模式为：" + getArguments().getString("mode") );
        RadioGroup rg = getActivity().findViewById(R.id.mode_group);
        EditText port = getActivity().findViewById(R.id.data_port);
        Button button = getActivity().findViewById(R.id.confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current_mode = "";
                for(int i =0;i<rg.getChildCount();i++){
                    RadioButton rb = (RadioButton) rg.getChildAt(i);
                    if(rb.isChecked()){
                        current_mode = rb.getText().toString();
                    }
                }
                switch (current_mode) {
                    case "PASV":
                        FtpUtil.setTM(current_mode,4444);
                        Toast.makeText(v.getContext(), "修改成功", Toast.LENGTH_LONG).show();
                        break;
                    case "PORT":
                        if (port.getText().toString().equals("")) {
                            Toast.makeText(v.getContext(), "请填写完整信息", Toast.LENGTH_LONG).show();
                        } else {
                            FtpUtil.setTM(current_mode,Integer.parseInt(port.getText().toString()));
                        }
                        break;
                    default:
                        Toast.makeText(v.getContext(), "请选择一种模式后再提交", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }
}