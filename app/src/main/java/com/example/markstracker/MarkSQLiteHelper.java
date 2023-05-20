package com.example.markstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MarkSQLiteHelper extends SQLiteOpenHelper {

    String createSQL = "CREATE TABLE mark (subject TEXT UNIQUE, mark FLOAT(2, 2))";
    String dropSQL = "DROP TABLE IF EXISTS mark";

    public static final String DATABASE_NAME = "Mark.db";
    public static final int DATABASE_VERSION = 2;

    public MarkSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropSQL);
        onCreate(db);
    }

    public void insertMark(SQLiteDatabase db, String subject, float mark){
        ContentValues newRecord = new ContentValues();
        newRecord.put("subject", subject);
        newRecord.put("mark", mark);
        db.insert("mark", null, newRecord);
    }

    public void updateMarkBySubject(SQLiteDatabase db, String subject, float newMark) {
        ContentValues values = new ContentValues();
        values.put("mark", newMark);
        String[] args = new String[] {subject};
        db.update("mark", values, "subject=?", args);
    }

    public void deleteMarkBySubject(SQLiteDatabase db, String subject){
        String[] args = new String[] {subject};
        db.delete("mark", "subject=?", args);
    }

    public void deleteAllMarks(SQLiteDatabase db){
        db.delete("mark", null, null);
    }

    public Cursor getAllMarks(SQLiteDatabase db){
        String[] fields = new String[] {"subject", "mark"};
        Cursor c = db.query("mark", fields, null,
                null, null, null, null);
        return c;
    }

    public boolean checkSubjectExists(SQLiteDatabase db, String subject){
        String[] fields = new String[] {"subject", "mark"};
        String[] args = new String[] {subject};
        Cursor c = db.query("mark", fields, "subject=?",
                args, null, null, null);
        return c.moveToFirst();
    }

    public boolean checkTableIsEmpty(SQLiteDatabase db){
        String count = "SELECT count(*) FROM mark";
        Cursor c = db.rawQuery(count, null);
        c.moveToFirst();
        return c.getInt(0) == 0;
    }

}
