package com.psincraian.savewords;


import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.psincraian.savewords.data.WordsContract;

import java.util.Comparator;
import java.util.GregorianCalendar;

public class WordClass implements Parcelable {

    private final static int BONUS_FAILURE = 3;
    private final static int BONUS_CONSECUTIVE_HITS = 4;
    private final static int BONUS_TOTALS = 3;
    private final static int BONUS_DATE_LAST_EXAM = 3;
    private final static int MAX_CONSECUTIVE_HITS = 3;


    private String mWord;
    private String mTranslation;
    private GregorianCalendar mLastExam;
    private int mFailures;
    private int mHits;
    private int mConsecutiveHits;

    public WordClass(String word, String translation, long time, int failures, int hits,
              int consecutiveHits) {
        mWord = word;
        mTranslation = translation;
        mLastExam = new GregorianCalendar();
        mLastExam.setTimeInMillis(time);
        mFailures = failures;
        mHits = hits;
        mConsecutiveHits = consecutiveHits;
    }

    private WordClass(Parcel in) {
        mWord = in.readString();
        mTranslation = in.readString();
        mLastExam = new GregorianCalendar();
        mLastExam.setTimeInMillis(in.readLong());
        mFailures = in.readInt();
        mHits = in.readInt();
        mConsecutiveHits = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<WordClass> CREATOR = new Parcelable.Creator<WordClass>(){

        @Override
        public WordClass createFromParcel(Parcel source) {
            return new WordClass(source);
        }

        @Override
        public WordClass[] newArray(int size) {
            return new WordClass[size];
        }
    };

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mWord);
        out.writeString(mTranslation);
        out.writeLong(mLastExam.getTimeInMillis());
        out.writeInt(mFailures);
        out.writeInt(mHits);
        out.writeInt(mConsecutiveHits);
    }


    public boolean different_words(WordClass w2) {
        return !mWord.equals(w2.mWord) && !mTranslation.equals(w2.mTranslation);
    }

    public String get_translation(){
        return mTranslation;
    }

    public String get_word() {
        return mWord;
    }

    public void increment_hits() {
        ++mHits;
        ++mConsecutiveHits;
    }

    public void increment_failures() {
        ++mFailures;
        mConsecutiveHits = 0;
    }

    public void set_last_exam_to_now() {
        mLastExam = new GregorianCalendar();
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(WordsContract.WordsEntry.COLUMN_WORD, mWord);
        values.put(WordsContract.WordsEntry.COLUMN_TRANSLATION, mTranslation);
        values.put(WordsContract.WordsEntry.COLUMN_HITS, mHits);
        values.put(WordsContract.WordsEntry.COLUMN_FAILURES, mFailures);
        values.put(WordsContract.WordsEntry.COLUMN_CONSECUTIVE_HITS, mConsecutiveHits);
        values.put(WordsContract.WordsEntry.COLUMN_DATE_LAST_EXAM, mLastExam.getTimeInMillis());
        return values;
    }

    public static class WordComparator implements Comparator<WordClass> {

        @Override
        public int compare(WordClass lhs, WordClass rhs) {
            int value1 = 0, value2 = 0;

            if (lhs.mFailures > rhs.mFailures)
                value1 += BONUS_FAILURE;
            else if (lhs.mFailures < rhs.mFailures)
                value2 += BONUS_FAILURE;

            if (lhs.mConsecutiveHits < MAX_CONSECUTIVE_HITS && rhs.mConsecutiveHits < MAX_CONSECUTIVE_HITS) {
                if (lhs.mConsecutiveHits < rhs.mConsecutiveHits)
                    value1 += BONUS_CONSECUTIVE_HITS;
                else if (lhs.mConsecutiveHits > rhs.mConsecutiveHits)
                    value2 += BONUS_CONSECUTIVE_HITS;
            }

            if (lhs.mLastExam.before(rhs.mLastExam))
                value1 += BONUS_DATE_LAST_EXAM;
            else if (rhs.mLastExam.before(lhs.mLastExam))
                value2 += BONUS_DATE_LAST_EXAM;

            return value2 - value1;
        }
    }
}
