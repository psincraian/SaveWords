package com.psincraian.savewords;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.UserDictionary;

import com.psincraian.savewords.data.WordsContract;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Random;

public class WordsSetClass implements Parcelable {

    private ArrayList<WordClass> mSet;
    private int mTotalWords;
    private int mActualWord;
    private int mFailures;
    private int mHits;
    private String[] mOptions;
    int mResult;
    private static final String NULL_STRING = "NULL";

    public WordsSetClass(Cursor data, int numbers) {
        mSet = new ArrayList<>(0);

        while (data.moveToNext()) {
            String word = data.getString(ExamFragment.INDEX_COLUMN_WORD);
            String translation = data.getString(ExamFragment.INDEX_COLUMN_TRANSLATION);
            long time = data.getLong(ExamFragment.INDEX_COLUMN_LAST_EXAM);
            int failures = data.getInt(ExamFragment.INDEX_COLUMN_FAILURES);
            int hits = data.getInt(ExamFragment.INDEX_COLUMN_HITS);
            int consHits = data.getInt(ExamFragment.INDEX_COLUMN_CONSECUTIVE_HITS);
            WordClass wordObject = new WordClass(word, translation, time, failures, hits, consHits);
            mSet.add(wordObject);
        }

        mTotalWords = numbers;
        mFailures = mHits = 0;
    }

    private WordsSetClass(Parcel in) {
        in.readTypedList(mSet, WordClass.CREATOR);
        mTotalWords = in.readInt();
        mActualWord = in.readInt();
        mFailures = in.readInt();
        mHits = in.readInt();
        in.readStringArray(mOptions);
        mResult = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<WordsSetClass> CREATOR = new Parcelable.Creator<WordsSetClass>(){

        @Override
        public WordsSetClass createFromParcel(Parcel source) {
            return new WordsSetClass(source);
        }

        @Override
        public WordsSetClass[] newArray(int size) {
            return new WordsSetClass[size];
        }
    };

    public void writeToParcel(Parcel out, int flags) {
        out.writeTypedList(mSet);
        out.writeInt(mTotalWords);
        out.writeInt(mActualWord);
        out.writeInt(mFailures);
        out.writeInt(mHits);
        out.writeStringArray(mOptions);
        out.writeInt(mResult);
    }

    private void sort() {
        Collections.sort(mSet, new WordClass.WordComparator());
    }

    // return a random translation where word is different rom id
    private String get_random_translation(int id, ArrayList<String> options) {
        Random random = new Random();
        int pos = random.nextInt(mSet.size());
        int max_intents = 5;
        while (max_intents > 0 && (!mSet.get(pos).different_words(mSet.get(id)) ||
                options.contains(mSet.get(pos).get_translation()))) {
            pos = random.nextInt(mSet.size());
            --max_intents;
        }

        if (max_intents == 0)
            return NULL_STRING;

        return mSet.get(pos).get_translation();
    }

    private void set_options() {
        ArrayList<String> options = new ArrayList<>();
        options.add(get_random_translation(mActualWord, options));
        options.add(get_random_translation(mActualWord, options));
        options.add(get_random_translation(mActualWord, options));
        options.add(mSet.get(mActualWord).get_translation());
        Collections.shuffle(options);

        mResult = -1;
        for (int i = 0; i < 4 && mResult == -1; ++i) {
            if (options.get(i).equals(mSet.get(mActualWord).get_translation()))
                mResult = i;
        }

        mOptions = new String[4];
        options.toArray(mOptions);
    }

    public void init_game() {
        sort();
        mFailures = mHits = mActualWord = 0;
        set_options();
    }

    public void increment_hits() {
        mSet.get(mActualWord).increment_hits();
        mSet.get(mActualWord).set_last_exam_to_now();
        ++mHits;
    }

    public void increment_failures(){
        mSet.get(mActualWord).increment_failures();
        mSet.get(mActualWord).set_last_exam_to_now();
        ++mFailures;
    }

    public int top_result() {
        return mResult + 1;
    }

    public boolean next() {
        if (mActualWord + 1 == mTotalWords)
            return false;

        ++mActualWord;
        set_options();
        return true;
    }

    public String get_word() {
        return mSet.get(mActualWord).get_word();
    }

    public String get_translation(){
        return mSet.get(mActualWord).get_translation();
    }

    public String[] get_translations() {
        return mOptions;
    }

    public int get_hits() {
        return mHits;
    }

    public int get_failures() {
        return mFailures;
    }

    public ContentValues[] wordsUpdated() {
        ContentValues[] values = new ContentValues[mTotalWords];
        for (int i = 0; i < values.length; ++i)
            values[i] = mSet.get(i).getContentValues();
        return values;
    }

    public ContentValues getExamContent() {
        ContentValues values = new ContentValues();
        values.put(WordsContract.ExamsEntry.COLUMN_DATE,
                GregorianCalendar.getInstance().getTimeInMillis());
        values.put(WordsContract.ExamsEntry.COLUMN_HITS, mHits);
        values.put(WordsContract.ExamsEntry.COLUMN_FAILURES, mFailures);
        return values;
    }
}
