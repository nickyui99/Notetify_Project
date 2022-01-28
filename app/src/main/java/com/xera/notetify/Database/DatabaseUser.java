package com.xera.notetify.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.xera.notetify.Model.UserModel;

public class DatabaseUser extends SQLiteOpenHelper {

    public static final String USER_TABLE = "USER_TABLE";
    public static final String COLUMN_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String COLUMN_ID_TOKEN = "ID_TOKEN";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_USER_EMAIL = "USER_EMAIL";

    public DatabaseUser(@Nullable Context context) {
        super(context, "notetify_user.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + USER_TABLE +
                "(" + COLUMN_ACCESS_TOKEN + " TEXT, " +
                COLUMN_ID_TOKEN + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_USER_EMAIL + " TEXT)";

        sqLiteDatabase.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertData(UserModel userModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ACCESS_TOKEN, userModel.getAccessToken());
        cv.put(COLUMN_ID_TOKEN, userModel.getIdToken());
        cv.put(COLUMN_NAME, userModel.getName());
        cv.put(COLUMN_USER_EMAIL, userModel.getEmail());

        long insert = db.insert(USER_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean deleteData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + USER_TABLE;

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }


    public UserModel getAllData(){
        String queryString = "SELECT * FROM " + USER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        UserModel userModel;

        if(cursor.moveToFirst()){
            String accessToken = cursor.getString(0);
            String idToken = cursor.getString(1);
            String userName = cursor.getString(2);
            String userEmail = cursor.getString(3);
            userModel = new UserModel(accessToken, idToken,  userName, userEmail);

        } else{
            //failure do not add anything to the list
            userModel = new UserModel();
        }
        cursor.close();
        db.close();
        return userModel;
    }

}
