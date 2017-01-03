package com.psincraian.savewords;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.psincraian.savewords.data.WordsContract;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ExamFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = ExamFragment.class.getSimpleName();
    private static final int MAX_LOAD_ELEMS = 20;   // the number of elements to load in mWordsSet
    private static final String TAG_END_DIALOG = "end_dialog";
    private static final String ARG_END_DIALOG = "arg_dialog";
    private static final String ARG_WORD_SET = "mWordSet";
    private static final String ARG_END_EXAM = "mEndExam";

    private TextView mWordTextView;
    private Button mTranslation1;
    private Button mTranslation2;
    private Button mTranslation3;
    private Button mTranslation4;
    private WordsSetClass mWordsSet;
    private boolean mEndExam;

    public static final String[] WORDS_PROJECTION = new String[] {
            WordsContract.WordsEntry.COLUMN_WORD,
            WordsContract.WordsEntry.COLUMN_TRANSLATION,
            WordsContract.WordsEntry.COLUMN_FAILURES,
            WordsContract.WordsEntry.COLUMN_HITS,
            WordsContract.WordsEntry.COLUMN_CONSECUTIVE_HITS,
            WordsContract.WordsEntry.COLUMN_DATE_LAST_EXAM
    };
    public static final int INDEX_COLUMN_WORD = 0;
    public static final int INDEX_COLUMN_TRANSLATION = 1;
    public static final int INDEX_COLUMN_FAILURES = 2;
    public static final int INDEX_COLUMN_HITS = 3;
    public static final int INDEX_COLUMN_CONSECUTIVE_HITS = 4;
    public static final int INDEX_COLUMN_LAST_EXAM = 5;

    public ExamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(LOG_TAG, "In on onCreateView()");
        View view = inflater.inflate(R.layout.fragment_exam, container, false);
        mWordTextView = (TextView) view.findViewById(R.id.word_textView);
        mTranslation1 = (Button) view.findViewById(R.id.translation1_button);
        mTranslation1.setOnClickListener(this);
        mTranslation2 = (Button) view.findViewById(R.id.translation2_button);
        mTranslation2.setOnClickListener(this);
        mTranslation3 = (Button) view.findViewById(R.id.translation3_button);
        mTranslation3.setOnClickListener(this);
        mTranslation4 = (Button) view.findViewById(R.id.translation4_button);
        mTranslation4.setOnClickListener(this);

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(LOG_TAG, "In onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null){
            Log.d(LOG_TAG, "savedInstanceState == null");
            new GetWords().execute();
        } else {
            Log.d(LOG_TAG, "savedInstanceState != null");
            if (savedInstanceState.getBoolean(ARG_END_DIALOG))
                EndExamDialog.newInstance(mWordsSet.get_hits(),
                        mWordsSet.get_failures()).show(getFragmentManager(), TAG_END_DIALOG);

            mEndExam = savedInstanceState.getBoolean(ARG_END_EXAM);
            mWordsSet = savedInstanceState.getParcelable(ARG_WORD_SET);
            mWordTextView.setText(mWordsSet.get_word());
            mTranslation1.setText(mWordsSet.get_translations()[0]);
            mTranslation2.setText(mWordsSet.get_translations()[1]);
            mTranslation3.setText(mWordsSet.get_translations()[2]);
            mTranslation4.setText(mWordsSet.get_translations()[3]);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "In onSaveInstanceState()");
        EndExamDialog dialog = (EndExamDialog) getFragmentManager()
                .findFragmentByTag(TAG_END_DIALOG);
        if (dialog != null) {
            outState.putBoolean(ARG_END_DIALOG, dialog.isVisible());
        }
        outState.putBoolean(ARG_END_EXAM, mEndExam);
        outState.putParcelable(ARG_WORD_SET, mWordsSet);
        super.onSaveInstanceState(outState);
    }

    private void show_if_correct(int itemSelected) {
        Toast toast;
        if (mWordsSet.top_result() == itemSelected)
            toast = Toast.makeText(getActivity(), R.string.correct, Toast.LENGTH_SHORT);
        else {
            String message = String.format(getString(R.string.incorrect), mWordsSet.get_translation());
            toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        }

        toast.show();
    }

    @Override
    public void onClick(View v) {
        if (!mEndExam) {
            switch (v.getId()) {
                case R.id.translation1_button: {
                    if (1 == mWordsSet.top_result()) {
                        mWordsSet.increment_hits();
                    } else
                        mWordsSet.increment_failures();
                    show_if_correct(1);
                    break;
                }
                case R.id.translation2_button: {
                    if (2 == mWordsSet.top_result())
                        mWordsSet.increment_hits();
                    else
                        mWordsSet.increment_failures();
                    show_if_correct(2);
                    break;
                }
                case R.id.translation3_button: {
                    if (3 == mWordsSet.top_result())
                        mWordsSet.increment_hits();
                    else
                        mWordsSet.increment_failures();
                    show_if_correct(3);
                    break;
                }
                case R.id.translation4_button: {
                    if (4 == mWordsSet.top_result())
                        mWordsSet.increment_hits();
                    else
                        mWordsSet.increment_failures();
                    show_if_correct(4);
                    break;
                }
                default:
                    break;
            }
        }

        update_screen();
    }

    void update_screen() {
        if (mWordsSet.next()) {
            mWordTextView.setText(mWordsSet.get_word());
            mTranslation1.setText(mWordsSet.get_translations()[0]);
            mTranslation2.setText(mWordsSet.get_translations()[1]);
            mTranslation3.setText(mWordsSet.get_translations()[2]);
            mTranslation4.setText(mWordsSet.get_translations()[3]);
        } else {
            if (!mEndExam) {
                new UpdateWordsTask().execute();
                mEndExam = true;
            }

            EndExamDialog.newInstance(mWordsSet.get_hits(),
                    mWordsSet.get_failures()).show(getFragmentManager(), TAG_END_DIALOG);
        }
    }

    private class UpdateWordsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... aVoid) {
            ContentValues[] params = mWordsSet.wordsUpdated();
            String where = WordsContract.WordsEntry.COLUMN_WORD + " = ?" + " AND " +
                    WordsContract.WordsEntry.COLUMN_TRANSLATION + " = ?";
            for (int i = 0; i < params.length; ++i) {
                getActivity().getContentResolver().update(
                        WordsContract.WordsEntry.CONTENT_URI,
                        params[i],
                        where,
                        new String[] {params[i].getAsString(WordsContract.WordsEntry.COLUMN_WORD),
                                params[i].getAsString(WordsContract.WordsEntry.COLUMN_TRANSLATION)}
                );
            }

            // Put the new instance in the provider
            getActivity().getContentResolver().insert(
                    WordsContract.ExamsEntry.CONTENT_URI,
                    mWordsSet.getExamContent()
            );

            return null;
        }
    }

    private class GetWords extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Cursor c = getActivity().getContentResolver().query(
                    WordsContract.WordsEntry.buildLastNWordsUri(MAX_LOAD_ELEMS),
                    WORDS_PROJECTION,
                    null,
                    null,
                    WordsContract.WordsEntry.COLUMN_CONSECUTIVE_HITS + " ASC"
            );

            mWordsSet = new WordsSetClass(c, 5);
            mWordsSet.init_game();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mWordTextView.setText(mWordsSet.get_word());
            mTranslation1.setText(mWordsSet.get_translations()[0]);
            mTranslation2.setText(mWordsSet.get_translations()[1]);
            mTranslation3.setText(mWordsSet.get_translations()[2]);
            mTranslation4.setText(mWordsSet.get_translations()[3]);
        }
    }

    public static class EndExamDialog extends DialogFragment {

        public static final String ARG_HITS = "hits";
        public static final String ARG_MISS = "miss";
        private static OnExamFinished mExamFinishedCallback;

        public interface OnExamFinished {
            void onExamFinished();
        }

        static EndExamDialog newInstance(int hits, int miss) {
            EndExamDialog dialog = new EndExamDialog();

            Bundle bundle = new Bundle();
            bundle.putInt(ARG_HITS, hits);
            bundle.putInt(ARG_MISS, miss);

            dialog.setArguments(bundle);
            return dialog;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mExamFinishedCallback = (OnExamFinished)activity;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String message = String.format(
                    getResources().getString(R.string.end_exam_information),
                    getArguments().getInt(ARG_HITS),
                    getArguments().getInt(ARG_MISS));
            builder.setMessage(message);
            builder.setPositiveButton(getResources().getString(R.string.accept),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mExamFinishedCallback.onExamFinished();
                            dismiss();
                        }
                    });
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mExamFinishedCallback.onExamFinished();
                        dismiss();
                    }
                    return false;
                }
            });

            return builder.create();
        }
    }
}
