package com.psincraian.savewords;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.psincraian.savewords.data.WordsContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final String ARG_DIALOG_SHOW = "dialog_visible";
    private static final String ARG_PRESSED_WORDS = "mWordsPressed";

    private static final String URI_ARGS_KEY = "uri";
    private static final int ID_WORDS_LOADER = 0;
    private static final int ID_EXAMS_LOADER = 1;
    private static final int MAX_LIST_ELEMS = 200;   // the number of max elements in the list
    private static final int MIN_ELEMS = 5;         // the minimum elements to start an exam
    private static final String ADD_WORD_DIALOG_TAG = "add_word_tag";
    private boolean mWordsPressed = true;
    private ListView mOneList;

    /* To improve performance and the clarity of the code*/
    private static final String[] WORDS_COLUMNS = new String[] {
            WordsContract.WordsEntry._ID,
            WordsContract.WordsEntry.COLUMN_WORD,
            WordsContract.WordsEntry.COLUMN_HITS,
            WordsContract.WordsEntry.COLUMN_FAILURES
    };
    public static final int COLUMN_WORD_ID = 0;
    public static final int COLUMN_WORD_NAME = 1;
    public static final int COLUMN_WORD_HITS = 2;
    public static final int COLUMN_WORD_FAILURES = 3;

    /* To improve performance and the clarity of the code*/
    private static final String[] EXAMS_COLUMNS = new String[] {
            WordsContract.ExamsEntry._ID,
            WordsContract.ExamsEntry.COLUMN_DATE,
            WordsContract.ExamsEntry.COLUMN_HITS,
            WordsContract.ExamsEntry.COLUMN_FAILURES
    };
    public static final int COLUMN_EXAM_ID = 0;
    public static final int COLUMN_EXAM_DATE = 1;
    public static final int COLUMN_EXAM_HITS = 2;
    public static final int COLUMN_EXAM_FAILURES = 3;

    private WordsAdapter mWordsAdapter;
    private ExamsAdapter mExamsAdapter;

    public MainFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(LOG_TAG, "In onActivityCreated");
        Bundle args = new Bundle();
        args.putParcelable(URI_ARGS_KEY, WordsContract.WordsEntry.CONTENT_URI);
        getLoaderManager().initLoader(ID_WORDS_LOADER, args, this);
        args.putParcelable(URI_ARGS_KEY, WordsContract.ExamsEntry.CONTENT_URI);
        getLoaderManager().initLoader(ID_EXAMS_LOADER, args, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "In onSaveInstanceState");
        DialogFragment dialog = (DialogFragment) getFragmentManager().findFragmentByTag(ADD_WORD_DIALOG_TAG);
        if (dialog != null)
            outState.putBoolean(ARG_DIALOG_SHOW, dialog.isVisible());
        outState.putBoolean(ARG_PRESSED_WORDS, mWordsPressed);
        super.onSaveInstanceState(outState);
    }

    private void restore_state(Bundle args) {
        Log.i(LOG_TAG, "In restore_state()");
        if (args != null) {
            if (args.getBoolean(ARG_DIALOG_SHOW, false)) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag(ADD_WORD_DIALOG_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                AddWordDialog dialog = new AddWordDialog();
                dialog.show(ft, ADD_WORD_DIALOG_TAG);
            }

            mWordsPressed = args.getBoolean(ARG_PRESSED_WORDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "In onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        restore_state(savedInstanceState);

        mExamsAdapter = new ExamsAdapter(getActivity(), null, 0);
        mWordsAdapter = new WordsAdapter(getActivity(), null, 0);

        if (view.findViewById(R.id.list_words) != null) {
            ListView listWords = (ListView) view.findViewById(R.id.list_words);
            listWords.setAdapter(mWordsAdapter);

            ListView listExams = (ListView) view.findViewById(R.id.list_exams);
            listExams.setAdapter(mExamsAdapter);
        } else {
            mOneList = (ListView) view.findViewById(R.id.list);
            if (mWordsPressed)
                mOneList.setAdapter(mWordsAdapter);
            else
                mOneList.setAdapter(mExamsAdapter);

            Button wordsButton = (Button) view.findViewById(R.id.words_button);
            wordsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mWordsPressed) {
                        mWordsPressed = true;
                        mOneList.setAdapter(mWordsAdapter);
                    }
                }
            });
            final Button examsButton = (Button) view.findViewById(R.id.exams_button);
            examsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mWordsPressed) {
                        mWordsPressed = false;
                        mOneList.setAdapter(mExamsAdapter);
                    }
                }
            });
        }

        Button startExam = (Button) view.findViewById(R.id.button_startExam);
        startExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWordsAdapter.getCount() >= MIN_ELEMS) {
                    Intent intent = new Intent(getActivity(), ExamsActivity.class);
                    startActivity(intent);
                } else
                    InsufficientWordsDialog.newInstance(MIN_ELEMS).show(getFragmentManager(), null);
            }
        });

        Button addWord = (Button) view.findViewById(R.id.button_addWord);
        addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag(ADD_WORD_DIALOG_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                AddWordDialog dialog = new AddWordDialog();
                dialog.show(ft, ADD_WORD_DIALOG_TAG);
            }
        });

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "In onCreateLoader");

        String[] projection = null;
        String sort = null;
        if (id == ID_WORDS_LOADER) {
            projection = WORDS_COLUMNS;
            sort = WordsContract.WordsEntry._ID + " DESC LIMIT " + Integer.toString(MAX_LIST_ELEMS) ;
        } else if (id == ID_EXAMS_LOADER) {
            projection = EXAMS_COLUMNS;
            sort = WordsContract.ExamsEntry._ID + " DESC LIMIT " + Integer.toString(MAX_LIST_ELEMS);
        }

        return new CursorLoader(
                getActivity(),
                (Uri) args.getParcelable(URI_ARGS_KEY),
                projection,
                null,
                null,
                sort
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(LOG_TAG, "In onLoadFinished");
        if (loader.getId() == ID_WORDS_LOADER)
            mWordsAdapter.swapCursor(data);
        else if (loader.getId() == ID_EXAMS_LOADER)
            mExamsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG, "In onLoaderReset()");
        if (loader.getId() == ID_WORDS_LOADER)
            mWordsAdapter.swapCursor(null);
        else if (loader.getId() == ID_EXAMS_LOADER)
            mExamsAdapter.swapCursor(null);
    }

    public static class InsufficientWordsDialog extends DialogFragment {

        private static final String ARG_MIN_WORDS = "min_words";

        public static InsufficientWordsDialog newInstance(int minWords){
            InsufficientWordsDialog dialog = new InsufficientWordsDialog();

            Bundle args = new Bundle();
            args.putInt(ARG_MIN_WORDS, minWords);

            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String message = getResources().getString(R.string.insufficient_words_error,
                    getArguments().getInt(ARG_MIN_WORDS));
            builder.setMessage(message);
            builder.setPositiveButton(getResources().getString(R.string.accept),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });

            return builder.create();
        }
    }
}
