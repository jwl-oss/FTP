package com.example.ftp.Utils;

import android.content.Context;
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

import com.example.ftp.R;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textView = getActivity().findViewById(R.id.currentMode);
        textView.setText("当前模式为：" + getArguments().getString("mode") );

        RadioGroup rg = getActivity().findViewById(R.id.mode_group);
        RadioGroup typeGroup = getActivity().findViewById(R.id.fileType);
        RadioGroup struGroup = getActivity().findViewById(R.id.structure);
        RadioGroup fileMode = getActivity().findViewById(R.id.fileMode);
        String data_mode,type,stru,mode;

        EditText port = getActivity().findViewById(R.id.data_port);
        Button button = getActivity().findViewById(R.id.confirm);
        System.out.println("123");
        boolean flag = false;
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String mode = "";
                for(int i = 0;i<rg.getChildCount();i++){
                    RadioButton rb = (RadioButton) rg.getChildAt(i);
                    if(rb.isChecked()){
                        mode = rb.getText().toString();
                    }
                }
                switch (mode) {
                    case "PASV":
                        FtpUtil.setTM(mode,4444);
                        break;
                    case "PORT":
                        if (port.getText().toString().equals("")) {
                            Toast.makeText(getContext(), "请填写完整信息", Toast.LENGTH_LONG).show();
                        } else {
                            FtpUtil.setTM(mode,Integer.parseInt(port.getText().toString()));
                        }
                        break;
                }
                System.out.println("change1 OK");
            }
        });
        typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String mode = "";
                for(int i = 0;i<typeGroup.getChildCount();i++){
                    RadioButton rb = (RadioButton) typeGroup.getChildAt(i);
                    if(rb.isChecked()){
                        mode = rb.getText().toString();
                    }
                }
                FtpUtil.setType(mode);
                System.out.println("change2 OK");
            }
        });
        struGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String mode = "";
                for(int i = 0;i<struGroup.getChildCount();i++){
                    RadioButton rb = (RadioButton) struGroup.getChildAt(i);
                    if(rb.isChecked()){
                        mode = rb.getText().toString();
                    }
                }
                FtpUtil.setStru(mode);
                System.out.println("change3 OK");
            }
        });
        fileMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String mode = "";
                for(int i = 0;i<fileMode.getChildCount();i++){
                    RadioButton rb = (RadioButton) fileMode.getChildAt(i);
                    if(rb.isChecked()){
                        mode = rb.getText().toString();
                    }
                }FtpUtil.setMode(mode);

                System.out.println("change4 OK");
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"修改成功",Toast.LENGTH_SHORT).show();
                System.out.println("send");
                String mode = "";
                for(int i = 0;i<rg.getChildCount();i++){
                    RadioButton rb = (RadioButton) rg.getChildAt(i);
                    if(rb.isChecked()){
                        mode = rb.getText().toString();
                    }
                }
                FtpUtil.setMode(mode);
                FtpUtil.setType("current_mode");
                FtpUtil.setStru("current_mode");

            }
        });


//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String current_mode = "";
//                System.out.println("click");
//                // PASV,PORT
//                int index = rg.getCheckedRadioButtonId();
//                RadioButton choice = (RadioButton) rg.getChildAt(index);
//                current_mode = choice.getText().toString();
//                switch (current_mode) {
//                    case "PASV":
//                        FtpUtil.setTM("current_mode",4444);
//                        break;
//                    case "PORT":
//                        if (port.getText().toString().equals("")) {
//                            Toast.makeText(v.getContext(), "请填写完整信息", Toast.LENGTH_LONG).show();
//                        } else {
//                            FtpUtil.setTM("current_mode",Integer.parseInt(port.getText().toString()));
//                        }
//                        break;
//                    default:
//                        Toast.makeText(v.getContext(), "请选择一种模式后再提交", Toast.LENGTH_LONG).show();
//                        break;
//                }
//                // TYPE
//                index = typeGroup.getCheckedRadioButtonId();
//                choice = (RadioButton) typeGroup.getChildAt(index);
//                if(choice == null){
//                    Toast.makeText(v.getContext(), "请选择一种模式后再提交", Toast.LENGTH_LONG).show();
//                }else {
//                    current_mode = choice.getText().toString();
//                    FtpUtil.setType("current_mode");
//                }
//                // STRU
//                index = struGroup.getCheckedRadioButtonId();
//                choice = (RadioButton) struGroup.getChildAt(index);
//                if(choice == null){
//                    Toast.makeText(v.getContext(), "请选择一种模式后再提交", Toast.LENGTH_LONG).show();
//                }else {
//                    current_mode = choice.getText().toString();
//                    FtpUtil.setStru("current_mode");
//                }
//
//                // MODE
//                index = fileMode.getCheckedRadioButtonId();
//                choice = (RadioButton) fileMode.getChildAt(index);
//                if(choice == null){
//                    Toast.makeText(v.getContext(), "请选择一种模式后再提交", Toast.LENGTH_LONG).show();
//                }else {
//                    current_mode = choice.getText().toString();
//                    FtpUtil.setMode("current_mode");
//                    Toast.makeText(v.getContext(), "修改成功", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

    }
}