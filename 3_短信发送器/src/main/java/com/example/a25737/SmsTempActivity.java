package com.example.a25737;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SmsTempActivity extends AppCompatActivity {
    String objects[] = {"我在开会,请稍后联系","我在吃饭,请稍后联系","我在睡觉,请稍后联系","我在打代码,请稍后联系"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_temp);

        ListView lv_sms = (ListView) findViewById(R.id.lv_sms);

        lv_sms.setAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.item_sms,R.id.tv_sms,objects));
        lv_sms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String smsContent = objects[i];

                Intent intent = new Intent();
                intent.putExtra("smsContent",smsContent);

                setResult(20,intent);
                finish();
            }
        });
    }
}
