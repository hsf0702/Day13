package com.example.a25737;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ContractActivity extends AppCompatActivity {


    private List<Contact> ContactLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract);
        ListView lv_contract = (ListView) findViewById(R.id.lv_contract);

        ContactLists = QueryContactUtils.getContact(this);

        lv_contract.setAdapter(new MyAdapter());

        lv_contract.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String phone = ContactLists.get(i).getPhone();

                Intent intent = new Intent();
                intent.putExtra("phone",phone);
                setResult(10,intent);

                finish();
            }
        });

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return ContactLists.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertview, ViewGroup viewGroup) {
            View view;
            if(convertview==null){
                view = View.inflate(getApplicationContext(),R.layout.item_contract,null);
            }else{
                view = convertview;
            }
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
            TextView tv_email = (TextView) view.findViewById(R.id.tv_email);

            tv_name.setText(ContactLists.get(i).getName());
            tv_phone.setText(ContactLists.get(i).getPhone());
            tv_email.setText(ContactLists.get(i).getEmail());
            return view;
        }
    }
}
