package com.example.a25737;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.btn_register)
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiter);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_register)
    public void onViewClicked() {
        String userName = username.getText().toString().trim();
        String passWord = this.password.getText().toString().trim();
        if(isEmpty(userName)){
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(isEmpty(passWord)){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        UserInfoDbUtils userInfoDbUtils = new UserInfoDbUtils(this);
        boolean insert = userInfoDbUtils.insert(new UserInfoBean(userName, passWord));
        if (insert) {
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    private boolean isEmpty(String s){
        if(TextUtils.isEmpty(s)){
            return true;
        }
        return false;
    }
}
