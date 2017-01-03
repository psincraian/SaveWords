package com.psincraian.savewords.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WordsDBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = WordsDBHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "words.db";

    public WordsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG_TAG, "In onCreate method");
        final String SQL_CREATE_WORDS_TABLE = "CREATE TABLE "
                + WordsContract.WordsEntry.TABLE_NAME + "( "
                + WordsContract.WordsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WordsContract.WordsEntry.COLUMN_WORD + " TEXT NOT NULL, "
                + WordsContract.WordsEntry.COLUMN_TRANSLATION + " TEXT NOT NULL, "
                // To simplifying the code and to prevent possible bugs the date cannot be null and
                // the default date is 0 (units) since a day
                + WordsContract.WordsEntry.COLUMN_DATE_LAST_EXAM + " INTEGER NOT NULL DEFAULT 0, "
                + WordsContract.WordsEntry.COLUMN_CONSECUTIVE_HITS + " INTEGER NOT NULL DEFAULT 0, "
                + WordsContract.WordsEntry.COLUMN_FAILURES + " INTEGER NOT NULL DEFAULT 0, "
                + WordsContract.WordsEntry.COLUMN_HITS + " INTEGER NOT NULL DEFAULT 0, "
                + "UNIQUE (" + WordsContract.WordsEntry.COLUMN_WORD + ", "
                + WordsContract.WordsEntry.COLUMN_TRANSLATION + ")" + ");";
        db.execSQL(SQL_CREATE_WORDS_TABLE);
        Log.v(LOG_TAG, "SQL WORDS: " + SQL_CREATE_WORDS_TABLE);

        final String SQL_CREATE_EXAMS_TABLE = "CREATE TABLE "
                + WordsContract.ExamsEntry.TABLE_NAME + "( "
                + WordsContract.ExamsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WordsContract.ExamsEntry.COLUMN_DATE + " INTEGER NOT NULL, "
                + WordsContract.ExamsEntry.COLUMN_FAILURES + " INTEGER NOT NULL, "
                + WordsContract.ExamsEntry.COLUMN_HITS + " INTEGER NOT NULL" + ");";
        db.execSQL(SQL_CREATE_EXAMS_TABLE);
        Log.v(LOG_TAG, "SQL EXAMS: " + SQL_CREATE_EXAMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LOG_TAG, "In onUpdate method");
        db.execSQL("DROP TABLE IF EXISTS " + WordsContract.WordsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WordsContract.ExamsEntry.TABLE_NAME);
        onCreate(db);    }
}
