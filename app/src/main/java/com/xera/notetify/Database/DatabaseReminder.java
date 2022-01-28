package com.xera.notetify.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.xera.notetify.Model.ReminderModel;

import java.util.ArrayList;

public class DatabaseReminder extends SQLiteOpenHelper {


    private static final String REMINDER_TABLE = "REMINDER_TABLE";
    private static final String COLUMN_REMINDER_ID = "REMINDER_ID";
    private static final String COLUMN_DATE_REMINDER = "DATE_REMINDER";
    private static final String COLUMN_UNIQUE_ID = "UNIQUE_ID";

    public DatabaseReminder(@Nullable Context context) {
        super(context, "notetify_reminder.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + REMINDER_TABLE + "(" +
                COLUMN_REMINDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE_REMINDER + " TEXT, " +
                COLUMN_UNIQUE_ID + " TEXT)";

        sqLiteDatabase.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addReminder(ReminderModel reminderModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_DATE_REMINDER, reminderModel.getDateReminder());
        cv.put(COLUMN_UNIQUE_ID, reminderModel.getUniqueID());

        long insert = db.insert(REMINDER_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean deleteReminder(ReminderModel reminderModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + REMINDER_TABLE + " WHERE " + COLUMN_REMINDER_ID + " = " + reminderModel.getReminderID();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            return false;
        }else{
            return true;
        }
    }

    public void updateReminder(String uniqueID, ReminderModel reminderModel){
        ArrayList<ReminderModel> reminderModelArrayList = getAllData();
        for(ReminderModel reminderModel1: reminderModelArrayList){
            if(reminderModel1.getUniqueID() == uniqueID){
                deleteReminder(reminderModel1);
            }
        }
        addReminder(reminderModel);
    }

    public boolean deleteAllReminder(){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + REMINDER_TABLE ;

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            return false;
        }else{
            return true;
        }
    }

    public ArrayList<ReminderModel> getAllData(){
        ArrayList<ReminderModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + REMINDER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        ReminderModel reminderModel;

        if(cursor.moveToFirst()){
            do{
                int reminderID = cursor.getInt(0);
                String dateReminder = cursor.getString(1);
                String uniqueID = cursor.getString(2);

                reminderModel = new ReminderModel(reminderID, dateReminder, uniqueID);
                returnList.add(reminderModel);
            }while (cursor.moveToNext());
        } else{
            //failure do not add anything to the list

        }
        cursor.close();
        db.close();
        return returnList;
    }
}
