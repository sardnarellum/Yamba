package com.andrasmuller.yamba;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

/**
 * Created by andras on 2014.08.05..
 */
public class TimeLineFragment extends ListFragment {
    private static final String TAG = TimeLineFragment.class.getSimpleName();
    private static final String[] FROM = { StatusContract.Column.USER,
            StatusContract.Column.MESSAGE, StatusContract.Column.CREATED_AT,
            StatusContract.Column.CREATED_AT };
    private static final int[] TO = {R.id.list_item_text_user,
            R.id.list_item_text_message, R.id.list_item_text_created_at,
            R.id.list_item_freshness};
    private SimpleCursorAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null, FROM, TO, 0);

        setListAdapter(mAdapter);
    }
}
