package com.psincraian.savewords;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class WordsAdapter extends CursorAdapter {

    public WordsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_word, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.word_textView);
        textView.setText(cursor.getString(MainFragment.COLUMN_WORD_NAME));
        TextView hitsView = (TextView) view.findViewById(R.id.hits_textView);
        hitsView.setText(cursor.getString(MainFragment.COLUMN_WORD_HITS));
        TextView failuresView = (TextView) view.findViewById(R.id.failures_textView);
        failuresView.setText(cursor.getString(MainFragment.COLUMN_WORD_FAILURES));
    }
}
