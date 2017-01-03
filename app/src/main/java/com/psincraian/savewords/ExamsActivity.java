package com.psincraian.savewords;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.psincraian.savewords.data.WordsContract;


public class ExamsActivity extends Activity implements ExamFragment.EndExamDialog.OnExamFinished {

    private static final String LOG_TAG = ExamsActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "In onCreate()");
        setContentView(R.layout.activity_exams);
    }

    @Override
    public void onExamFinished() {
        finish();
    }
}
