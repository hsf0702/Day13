package com.example.a25737;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Toast;

        import butterknife.BindView;
        import butterknife.ButterKnife;
        import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.jizhu)
    CheckBox jizhu;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        username.setBackgroundColor(Color.TRANSPARENT);
        sp = getSharedPreferences("config.config", MODE_PRIVATE);
        boolean jizhu1 = sp.getBoolean("mima", false);
        Log.e("text101", "onCreate: 11111" + jizhu1 + "");
        if (jizhu1) {
            String mima = sp.getString("jizhu", "");
            String[] split = mima.split("==");
            username.setText(split[0]);
            password.setText(split[1]);
            this.jizhu.setChecked(true);
        }

    }

    @OnClick({R.id.btn_login, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                String userName = username.getText().toString().trim();
                String passWord = password.getText().toString().trim();
                if (isEmpty(userName)) {
                    Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isEmpty(passWord)) {
                    Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserInfoDbUtils userInfoDbUtils = new UserInfoDbUtils(this);
                UserInfoBean query = userInfoDbUtils.query(userName, passWord);
                Log.e("text101", "onViewClicked: " + query.username + "=====" + query.password);
                if (!query.password.equals("")) {
                    if (query.password.equals(passWord)) {
                        if (jizhu.isChecked()) {
                            sp.edit().putBoolean("mima", true).commit();
                            sp.edit().putString("jizhu", userName + "==" + passWord).commit();
                        } else {
                            sp.edit().putBoolean("mima", false).commit();
                        }
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
            case R.id.btn_register:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                break;
        }
    }

    private boolean isEmpty(String s) {
        if (TextUtils.isEmpty(s)) {
            return true;
        }
        return false;
    }
}
