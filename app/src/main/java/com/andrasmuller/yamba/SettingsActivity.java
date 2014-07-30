package com.andrasmuller.yamba;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by andras on 2014.07.30..
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null == savedInstanceState) {
            SettingsFragment fragment = new SettingsFragment();
            getFragmentManager().beginTransaction()
                                .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                                .commit();
        }
    }
}
