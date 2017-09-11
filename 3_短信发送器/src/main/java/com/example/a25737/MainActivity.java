package com.example.a25737;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText et_number;
    private EditText et_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_number =  (EditText) findViewById(R.id.et_number);
        et_content =  (EditText) findViewById(R.id.et_content);
    }
    public void click(View view){
        int id = view.getId();
        switch (id){
            case R.id.bt_add:
                Intent intent1 = new Intent(this,ContractActivity.class);
                startActivityForResult(intent1,1);
                break;
            case R.id.bt_insert:
                Intent intent2 = new Intent(this,SmsTempActivity.class);
                startActivityForResult(intent2,2);
                break;
            case R.id.bt_send:
                String number = et_number.getText().toString().trim();
                String content = et_content.getText().toString();
                //获取短信管理者
                SmsManager smsManager = SmsManager.getDefault();

                ArrayList<String> smss = smsManager.divideMessage(content);
                for (String sms : smss) {
                    smsManager.sendTextMessage(number,null,sms,null,null);
                }
                break;
            default:
                break;
        }

    }


    //当页面调用finish方法时执行

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==10){
            String phone = data.getStringExtra("phone");
            et_number.setText(phone);
        }else if(resultCode==20){
            String smsContent = data.getStringExtra("smsContent");
            et_content.setText(smsContent);

        }
    }
}
