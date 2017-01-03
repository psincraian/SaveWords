package com.psincraian.savewords;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ExamsAdapter extends CursorAdapter{

    public ExamsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_exam, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView dateView = (TextView) view.findViewById(R.id.exam_textView);
        long time = cursor.getLong(MainFragment.COLUMN_EXAM_DATE);
        time = Utility.normalizeDate(time);
        dateView.setText(Utility.getNiceDayFormat(time, context));
        TextView hitsView = (TextView) view.findViewById(R.id.hits_textView);
        hitsView.setText(Integer.toString(cursor.getInt(MainFragment.COLUMN_EXAM_HITS)));
        TextView failuresView = (TextView) view.findViewById(R.id.failures_textView);
        failuresView.setText(Integer.toString(cursor.getInt(MainFragment.COLUMN_WORD_FAILURES)));
    }
}
