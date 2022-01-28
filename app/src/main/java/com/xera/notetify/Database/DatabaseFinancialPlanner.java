package com.xera.notetify.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.xera.notetify.Model.FinancialModel;
import com.xera.notetify.Model.NoteModel;

import java.util.ArrayList;

public class DatabaseFinancialPlanner extends SQLiteOpenHelper {

    private static final String FINANCIAL_TABLE = "FINANCIAL_TABLE";
    private static final String COLUMN_FINANCIAL_ID = "FINANCIAL_ID";
    private static final String COLUMN_DATE_CREATED = "DATE_CREATED";
    private static final String COLUMN_EVENT = "EVENT";
    private static final String COLUMN_AMOUNT = "AMOUNT";
    private static final String COLUMN_EXPENSES_CATEGORY= "EXPENSES_CATEGORY";

    public DatabaseFinancialPlanner(@Nullable Context context) {
        super(context, "notetify_financial.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createNoteTable = "CREATE TABLE " + FINANCIAL_TABLE + "(" +
                COLUMN_FINANCIAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE_CREATED + " TEXT, " +
                COLUMN_EVENT + " TEXT, " +
                COLUMN_AMOUNT + " DOUBLE, " +
                COLUMN_EXPENSES_CATEGORY + " INTEGER)";

        sqLiteDatabase.execSQL(createNoteTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }

    public boolean addNewList(FinancialModel financialModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

//        cv.put(COLUMN_NOTE_ID, noteModel.getNoteId());  **Not necessary because auto increment
        cv.put(COLUMN_DATE_CREATED, financialModel.getDateCreated());
        cv.put(COLUMN_EVENT, financialModel.getEvent());
        cv.put(COLUMN_AMOUNT, financialModel.getAmount());
        cv.put(COLUMN_EXPENSES_CATEGORY, financialModel.getExpensesCategory());

        long insert = db.insert(FINANCIAL_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean updateList(FinancialModel financialModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_DATE_CREATED, financialModel.getDateCreated());
        cv.put(COLUMN_EVENT, financialModel.getEvent());
        cv.put(COLUMN_AMOUNT, financialModel.getAmount());
        cv.put(COLUMN_EXPENSES_CATEGORY, financialModel.getExpensesCategory());

        long insert = db.update(FINANCIAL_TABLE,  cv, COLUMN_FINANCIAL_ID + " = " + financialModel.getFinancialID(), null);
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean deleteList(FinancialModel financialModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + FINANCIAL_TABLE + " WHERE " + COLUMN_FINANCIAL_ID + " = " + financialModel.getFinancialID();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            return false;
        }else{
            return true;
        }
    }

    public boolean deleteAllList(){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + FINANCIAL_TABLE ;

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            return false;
        }else{
            return true;
        }
    }

    public ArrayList<FinancialModel> getAllData(){
        ArrayList<FinancialModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + FINANCIAL_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        FinancialModel financialModel;

        if(cursor.moveToFirst()){
            do{
                int financialId = cursor.getInt(0);
                String dateCreated = cursor.getString(1);
                String event = cursor.getString(2);
                float amount = cursor.getFloat(3);
                int expenseCategory = cursor.getInt(4);
                financialModel = new FinancialModel(financialId, event, dateCreated, amount, expenseCategory);
                returnList.add(financialModel);
            }while (cursor.moveToNext());
        } else{
            //failure do not add anything to the list

        }
        cursor.close();
        db.close();
        return returnList;
    }
}
