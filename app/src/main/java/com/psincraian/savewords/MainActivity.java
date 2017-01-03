package com.psincraian.savewords;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG_TAG, "In onCreate method");
        set_shadow();
    }

    @TargetApi(21)
    private void set_shadow() {
        if (getActionBar() != null)
            getActionBar().setElevation(R.dimen.action_bar_shadow);
    }
}
