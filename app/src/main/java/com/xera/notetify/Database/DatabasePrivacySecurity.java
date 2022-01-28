package com.xera.notetify.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.xera.notetify.Model.NoteModel;
import com.xera.notetify.Model.PrivacySecurityModel;

import java.util.ArrayList;

public class DatabasePrivacySecurity extends SQLiteOpenHelper {

    private static final String TAG = "DATABASE SECURITY";
    private static final String USER_SECURITY_TABLE = "USER_SECURITY_TABLE";
    private static final String COLUMN_PRIVACY_PASSWORD = "PRIVACY_PASSWORD";
    private static final String COLUMN_SECURITY_QUESTION = "SECURITY_QUESTION";
    private static final String COLUMN_SECURITY_ANSWER = "SECURITY_ANSWER";

    public DatabasePrivacySecurity(@Nullable Context context) {
        super(context, "notetify_security.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createUserSecurityTable = "CREATE TABLE " + USER_SECURITY_TABLE + "(" +
                COLUMN_PRIVACY_PASSWORD + " TEXT, " +
                COLUMN_SECURITY_QUESTION + " INTEGER, " +
                COLUMN_SECURITY_ANSWER + " TEXT)";

        sqLiteDatabase.execSQL(createUserSecurityTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean setPrivacyPassword(PrivacySecurityModel privacySecurityModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRIVACY_PASSWORD, privacySecurityModel.getPrivacyPassword());
        cv.put(COLUMN_SECURITY_QUESTION, privacySecurityModel.getSecurityQuestion());
        cv.put(COLUMN_SECURITY_ANSWER, privacySecurityModel.getSecurityAnswer());

        long insert = db.insert(USER_SECURITY_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean deletePrivacyPassword(){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + USER_SECURITY_TABLE ;

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            return false;
        }else{
            return true;
        }
    }

    public boolean updatePrivacyPassword(int newPin){
        SQLiteDatabase db = this.getWritableDatabase();

        //ADD NEW PRIVACY PASSWORD
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRIVACY_PASSWORD, newPin);

        long insert = db.update(USER_SECURITY_TABLE, cv, "",null );
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean checkPrivacyPassword(int inputPrivacyPassword){
        String queryString = "SELECT * FROM " + USER_SECURITY_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            int getPrivacyPassword = cursor.getInt(0);
            if(getPrivacyPassword == inputPrivacyPassword){
                cursor.close();
                db.close();
                return true;
            }
        } else{
            //failure do not add anything to the list
        }

        cursor.close();
        db.close();
        return false;
    }

    public boolean checkSecurityAnswer(int securityQuestion, String securityAnswer){

        String queryString = "SELECT * FROM " + USER_SECURITY_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            int getSecurityQuestion = cursor.getInt(1);
            String getSecurityAnswer = cursor.getString(2);
            if(getSecurityQuestion == securityQuestion){
                if(getSecurityAnswer.equals(securityAnswer)){
                    cursor.close();
                    db.close();
                    return true;
                }
            }
        } else{
            //failure do not add anything to the list
        }

        cursor.close();
        db.close();
        return false;
    }

    public int getSecurityQuestion(){
        String queryString = "SELECT * FROM " + USER_SECURITY_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            int getSecurityQuestion = cursor.getInt(1);
            return getSecurityQuestion;
        } else{
            //failure do not add anything to the list
        }

        cursor.close();
        db.close();
        return -1;
    }

    public boolean checkPrivacySecurityDB(){
        String queryString = "SELECT * FROM " + USER_SECURITY_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        Boolean isDatabaseEmpty;
        if(cursor.moveToFirst()){
            isDatabaseEmpty = false;
        } else{
            //failure do not add anything to the list
            isDatabaseEmpty = true;
        }

        cursor.close();
        db.close();
        return !isDatabaseEmpty;
    }
}
