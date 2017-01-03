package com.psincraian.savewords.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class WordsContract {
    private static final String LOG_TAG = WordsContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.psincraian.savewords";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WORDS = "words";
    public static final String PATH_EXAMS = "exams";

    /* Inner class that defines de table contents of the words table */
    public static final class WordsEntry implements BaseColumns {

        public static final String TABLE_NAME = "words";

        // Column with the word
        public static final String COLUMN_WORD = "word";
        // Column with the translation of the word
        public static final String COLUMN_TRANSLATION = "translation";
        // Column with the date of the last exam
        public static final String COLUMN_DATE_LAST_EXAM = "last_exam";
        // Column with the number of failures
        public static final String COLUMN_FAILURES = "failures";
        // Column with the number of hits
        public static final String COLUMN_HITS = "hits";
        // Column with the consecutive hits of the word
        public static final String COLUMN_CONSECUTIVE_HITS = "consecutive_hits";

        // Uri to Words table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORDS).build();
        // last N words path
        public static final String LAST_WORDS_PATH = "last";
        // MIMEs
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORDS;
        public static final String CONTENT_TYPE_ITEM =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORDS;

        public static Uri buildWordUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // return a Uri that gets the last n exams
        public static Uri buildLastNWordsUri(int n){
            return CONTENT_URI.buildUpon().appendPath(LAST_WORDS_PATH)
                    .appendPath(Integer.toString(n)).build();
        }

        // return the number of words to retrieve from a Uri
        public static int getLastNWordsFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }
    }

    /* Inner class that defines the table contents of the exams table */
    public static final class ExamsEntry implements BaseColumns {

        public static final String TABLE_NAME = "exams";

        // Column with the date of the exam
        public static final String COLUMN_DATE = "date";
        // Column with the number of hits of this exam
        public static final String COLUMN_HITS = "hits";
        // Column with the number of failures of this exam
        public static final String COLUMN_FAILURES = "failures";

        // Uri to Words table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXAMS).build();
        // last N exams path, depends on the sort
        public static final String LAST_EXAMS_PATH = "last";
        // MIMEs
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXAMS;
        public static final String CONTENT_TYPE_ITEM =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXAMS;

        // return a Uri that references the id exam
        public static Uri buildExamUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // return a Uri that gets the last n exams
        public static Uri buildLastNExamsUri(int n){
            return CONTENT_URI.buildUpon().appendPath(LAST_EXAMS_PATH)
                    .appendPath(Integer.toString(n)).build();
        }

        // return the number of exams to retrieve from a Uri
        public static int getLastNExamsFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }
    }
}
