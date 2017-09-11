package com.example.a25737;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserInfoDbUtils {
    private DBOpenHelper userInfoSqliteOpenHelper;

    public UserInfoDbUtils(Context context) {

        userInfoSqliteOpenHelper = new DBOpenHelper(context);

    }

    public boolean insert(UserInfoBean bean) {
        SQLiteDatabase db = userInfoSqliteOpenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", bean.username);
        values.put("password", bean.password);
        long result = db.insert("user", null, values);
        db.close();

        if (result == -1) {
            return false;
        } else {
            return true;
        }


    }

    /*
        public int delete(String name){

            SQLiteDatabase	 db = userInfoSqliteOpenHelper.getReadableDatabase();

            //table :
            int result = db.delete("info", "name=?", new String[]{name});

            db.close();
            return result;

        }

        //
        public int update(UserInfoBean bean){

            SQLiteDatabase	 db = userInfoSqliteOpenHelper.getReadableDatabase();

            ContentValues values = new ContentValues();
            values.put("phone", bean.phone);

            int result = db.update("info", values, "name=?", new String[]{bean.name});

            db.close();
            return result;
        }*/
    public UserInfoBean query(String name, String pass) {
        SQLiteDatabase db = userInfoSqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("user", new String[]{"username", "password"}, "username = ?", new String[]{name}, null, null, "_id desc");
        UserInfoBean userInfoBean = new UserInfoBean();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {//????cursor
                String userName = cursor.getString(cursor.getColumnIndex("username"));
                String passWord = cursor.getString(cursor.getColumnIndex("password"));
                userInfoBean.username = userName;
                userInfoBean.password = passWord;
                System.out.println("name : " + userName + " phone:" + passWord);
            }
        }
        cursor.close();
        db.close();
        return userInfoBean;
    }

}

