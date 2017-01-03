package com.psincraian.savewords.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.psincraian.savewords.Utility;

public class WordsProvider extends ContentProvider {
    private static final String LOG_TAG = WordsProvider.class.getSimpleName();

    private static final int WORDS = 100;
    private static final int LAST_N_WORDS = 150;
    private static final int EXAMS = 200;
    private static final int LAST_N_EXAMS = 250;

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private WordsDBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new WordsDBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i(LOG_TAG, "In on query()");

        Cursor retCursor;
        switch (mUriMatcher.match(uri)) {
            case WORDS: {
                retCursor = mDBHelper.getReadableDatabase().query(
                        WordsContract.WordsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case LAST_N_WORDS: {
                Log.v(LOG_TAG, "URI:" + uri);
                String limit = Integer.toString(WordsContract.WordsEntry.getLastNWordsFromUri(uri));
                retCursor = mDBHelper.getReadableDatabase().query(
                        WordsContract.WordsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        limit
                );
                break;
            }
            case EXAMS: {
                retCursor = mDBHelper.getReadableDatabase().query(
                        WordsContract.ExamsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case LAST_N_EXAMS: {
                String limit = Integer.toString(WordsContract.ExamsEntry.getLastNExamsFromUri(uri));
                retCursor = mDBHelper.getReadableDatabase().query(
                        WordsContract.ExamsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        limit
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        Log.i(LOG_TAG, "In on getType");
        switch (mUriMatcher.match(uri)) {
            case WORDS:
                return WordsContract.WordsEntry.CONTENT_TYPE;
            case LAST_N_WORDS:
                return WordsContract.WordsEntry.CONTENT_TYPE;
            case EXAMS:
                return WordsContract.ExamsEntry.CONTENT_TYPE;
            case LAST_N_EXAMS:
                return WordsContract.ExamsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(LOG_TAG, "In on insert()");

        Uri retUri;
        switch (mUriMatcher.match(uri)) {
            case WORDS: {
                long _id = mDBHelper.getWritableDatabase().insert(
                        WordsContract.WordsEntry.TABLE_NAME,
                        null,
                        values
                );
                if (_id > 0)
                    retUri = WordsContract.WordsEntry.buildWordUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into:" + uri);
                break;
            }
            case EXAMS: {
                long _id = mDBHelper.getWritableDatabase().insert(
                        WordsContract.ExamsEntry.TABLE_NAME,
                        null,
                        values
                );
                Log.v(LOG_TAG, "ID: " + _id);
                if (_id > 0)
                    retUri = WordsContract.ExamsEntry.buildExamUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into:" + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(LOG_TAG, "In on delete()");
        int rowsDeleted;
        switch (mUriMatcher.match(uri)) {
            case WORDS: {
                rowsDeleted = mDBHelper.getWritableDatabase().delete(
                        WordsContract.WordsEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case EXAMS: {
                rowsDeleted = mDBHelper.getWritableDatabase().delete(
                        WordsContract.ExamsEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.i(LOG_TAG, "In on update()");
        int rowsUpdated;
        switch (mUriMatcher.match(uri)) {
            case WORDS: {
                rowsUpdated = mDBHelper.getWritableDatabase().update(
                        WordsContract.WordsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case EXAMS: {
                rowsUpdated = mDBHelper.getWritableDatabase().update(
                        WordsContract.ExamsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(WordsContract.CONTENT_AUTHORITY, WordsContract.PATH_WORDS, WORDS);
        matcher.addURI( WordsContract.CONTENT_AUTHORITY,
                WordsContract.PATH_WORDS + "/" + WordsContract.WordsEntry.LAST_WORDS_PATH + "/#",
                LAST_N_WORDS);
        matcher.addURI(WordsContract.CONTENT_AUTHORITY, WordsContract.PATH_EXAMS, EXAMS);
        matcher.addURI(WordsContract.CONTENT_AUTHORITY,
                WordsContract.PATH_EXAMS + "/" + WordsContract.ExamsEntry.LAST_EXAMS_PATH + "/#",
                LAST_N_EXAMS);

        return matcher;
    }
}
