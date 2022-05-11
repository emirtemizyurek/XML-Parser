package com.hugin.emirhan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {


    private static final String DB_NAME = "market.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "users";
    private static final String USER_INFO_NAME = "userInfoName";
    private static final String USER_NAME = "UserName";
    private static final String PASSWORD = "Password";


    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + USER_INFO_NAME + " TEXT,"
                + USER_NAME + " TEXT,"
                + PASSWORD + " TEXT)";

        db.execSQL(query);
    }


    public Boolean insertData(String UserInfoName, String UserName, String Password) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_INFO_NAME, UserInfoName);
        values.put(USER_NAME, UserName);
        values.put(PASSWORD, Password);

        long result = db.insert(TABLE_NAME, null, values);

        if(result==1)
            return true;
        else
            return false;

    }

    public Boolean checkUsername(String Username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =db.rawQuery("Select * from users where userName = ?",new String[]{Username});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    public Boolean checkUsernamePassword(String Username, String Password){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from users where userName = ? and Password = ? ",new String[]{Username,Password});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

