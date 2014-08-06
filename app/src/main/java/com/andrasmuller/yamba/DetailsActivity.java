package com.andrasmuller.yamba;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by andras on 2014.08.06..
 */
public class DetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            DetailsFragment fragment = new DetailsFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, fragment,
                            fragment.getClass().getSimpleName()).commit();
        }
    }
}
