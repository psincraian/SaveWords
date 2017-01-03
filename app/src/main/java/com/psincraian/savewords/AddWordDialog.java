package com.psincraian.savewords;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.psincraian.savewords.data.WordsContract;


public class AddWordDialog extends DialogFragment {

    private static final String LOG_TAG = AddWordDialog.class.getSimpleName();

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "In onCreateDialog");
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_add_word, null);
        builder.setView(view);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText edtWord = (EditText) view.findViewById(R.id.word);
                String word = edtWord.getText().toString();
                EditText edtTranslation = (EditText) view.findViewById(R.id.translation);
                String translation = edtTranslation.getText().toString();

                if (Utility.isValidWord(word) && Utility.isValidWord(translation)) {
                    // normalize the word
                    word = Utility.normalizeWord(word);
                    translation = Utility.normalizeWord(translation);

                    ContentValues values = new ContentValues();
                    values.put(WordsContract.WordsEntry.COLUMN_WORD, word);
                    values.put(WordsContract.WordsEntry.COLUMN_TRANSLATION, translation);
                    new AddWordTasks(getActivity()).execute(values);
                } else {
                    Toast.makeText(getActivity(), R.string.word_translation_empty,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }

    private class AddWordTasks extends AsyncTask<ContentValues, Void, Integer> {

        private static final int ALL_OK = 1;
        private static final int ALREADY_EXIST = 2;
        private static final int ERROR = 3;
        private Context mContext;

        AddWordTasks(Context context) {
            mContext = context;
        }

        @Override
        protected Integer doInBackground(ContentValues... params) {
            int res = ALL_OK;

            String where = WordsContract.WordsEntry.COLUMN_WORD + " = ? AND " +
                    WordsContract.WordsEntry.COLUMN_TRANSLATION + " = ?";
            Cursor c = getActivity().getContentResolver().query(
                    WordsContract.WordsEntry.CONTENT_URI,
                    null,
                    where,
                    new String[]{params[0].getAsString(WordsContract.WordsEntry.COLUMN_WORD),
                            params[0].getAsString(WordsContract.WordsEntry.COLUMN_TRANSLATION)},
                    null
            );

            if (c.moveToFirst())
                res = ALREADY_EXIST;
            else { // insert the word
                try {
                    getActivity().getContentResolver().insert(
                            WordsContract.WordsEntry.CONTENT_URI,
                            params[0]
                    );
                } catch (SQLException se) {
                    res = ERROR;
                }
            }

            c.close();
            return res;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            switch (integer) {
                case ALL_OK: {
                    Toast.makeText(mContext, mContext.getResources().getString(
                            R.string.word_translation_added), Toast.LENGTH_SHORT).show();
                    break;
                }
                case ALREADY_EXIST: {
                    Toast.makeText(mContext, mContext.getString(R.string.word_translation_exists),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                case ERROR: {
                    Toast.makeText(mContext, mContext.getString(R.string.internal_error),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }
}
