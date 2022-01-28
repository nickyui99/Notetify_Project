package com.xera.notetify.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.xera.notetify.Model.NoteModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseNote extends SQLiteOpenHelper {

    private static final String NOTE_TABLE = "NOTE_TABLE";
    private static final String COLUMN_NOTE_ID = "NOTE_ID";
    private static final String COLUMN_UNIQUE_ID = "UNIQUE_ID";
    private static final String COLUMN_NOTE_TITLE = "NOTE_TITLE";
    private static final String COLUMN_NOTE_CONTENT = "NOTE_CONTENT";
    private static final String COLUMN_PASSWORD_STATUS = "PASSWORD_STATUS";
    private static final String COLUMN_DATE_CREATED = "DATE_CREATED";

    public DatabaseNote(@Nullable Context context, String databaseName) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createNoteTable = "CREATE TABLE " + NOTE_TABLE + "(" +
                COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UNIQUE_ID + " TEXT, " +
                COLUMN_DATE_CREATED + " TEXT, " +
                COLUMN_NOTE_TITLE + " TEXT, " +
                COLUMN_NOTE_CONTENT + " TEXT, " +
                COLUMN_PASSWORD_STATUS + " BOOL)";

        sqLiteDatabase.execSQL(createNoteTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addNote(NoteModel noteModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

//        cv.put(COLUMN_NOTE_ID, noteModel.getNoteId());  **Not necessary because auto increment
        cv.put(COLUMN_UNIQUE_ID, noteModel.getUniqueID());
        cv.put(COLUMN_DATE_CREATED, noteModel.getDateCreated());
        cv.put(COLUMN_NOTE_TITLE, noteModel.getNoteTitle());
        cv.put(COLUMN_NOTE_CONTENT, noteModel.getNoteContent());
        cv.put(COLUMN_PASSWORD_STATUS, noteModel.getPrivacyLockStatus());

        long insert = db.insert(NOTE_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean updateNote(NoteModel noteModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_DATE_CREATED, noteModel.getDateCreated());
        cv.put(COLUMN_NOTE_TITLE, noteModel.getNoteTitle());
        cv.put(COLUMN_NOTE_CONTENT, noteModel.getNoteContent());
        cv.put(COLUMN_PASSWORD_STATUS, noteModel.getPrivacyLockStatus());

        int updateStatus = db.update(NOTE_TABLE, cv, COLUMN_NOTE_ID + "=" + noteModel.getNoteId(), null);
        if (updateStatus == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean deleteNote(NoteModel noteModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + NOTE_TABLE + " WHERE " + COLUMN_NOTE_ID + " = " + noteModel.getNoteId();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            return false;
        }else{
            return true;
        }
    }

    public boolean deleteAllNote(){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + NOTE_TABLE ;

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            return false;
        }else{
            return true;
        }
    }

    public ArrayList<NoteModel> getAllData(){
        ArrayList<NoteModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + NOTE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        NoteModel noteModel;

        if(cursor.moveToFirst()){
            do{
                int noteId = cursor.getInt(0);
                String uniqueID = cursor.getString(1);
                String dateCreated = cursor.getString(2);
                String noteTitle = cursor.getString(3);
                String noteContent = cursor.getString(4);
                Boolean passwordStatus = cursor.getInt(5)== 1 ? true: false;
                noteModel = new NoteModel(noteId, uniqueID, dateCreated, noteTitle, noteContent, passwordStatus);
                returnList.add(noteModel);
            }while (cursor.moveToNext());
        } else{
            //failure do not add anything to the list

        }
        cursor.close();
        db.close();
        return returnList;
    }

    public boolean updateContent(NoteModel noteModel){
        String queryString = "SELECT * FROM " + NOTE_TABLE + " WHERE " + COLUMN_UNIQUE_ID + " = '" + noteModel.getUniqueID() + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);


        if(cursor.moveToFirst()){
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_DATE_CREATED, noteModel.getDateCreated());
            cv.put(COLUMN_NOTE_TITLE, noteModel.getNoteTitle());
            cv.put(COLUMN_NOTE_CONTENT, noteModel.getNoteContent());

            int updateStatus = db.update(NOTE_TABLE, cv, COLUMN_UNIQUE_ID + " = '" + noteModel.getUniqueID() + "'", null);
            cursor.close();
            db.close();
            if (updateStatus == -1){
                return false;
            }
            else{
                return true;
            }
        }
        cursor.close();
        db.close();
        return false;
    }

    public boolean isDatabaseEmpty(){
        String queryString = "SELECT * FROM " + NOTE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        Boolean rowEmpty;

        if (cursor.moveToFirst()) {
            // DO SOMETHING WITH CURSOR
            rowEmpty = false;

        }
        else {
            // I AM EMPTY
            rowEmpty = true;
        }
        return rowEmpty;
    }

}
