package com.andrasmuller.yamba;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by andras on 2014.08.05..
 */
public class TimeLineFragment extends ListFragment
                              implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = TimeLineFragment.class.getSimpleName();
    private static final String[] FROM = { StatusContract.Column.USER,
                                           StatusContract.Column.MESSAGE,
                                           StatusContract.Column.CREATED_AT/*,
                                           StatusContract.Column.CREATED_AT*/ };
    private static final int[] TO = { R.id.list_item_text_user, R.id.list_item_text_message,
                                      R.id.list_item_text_created_at/*,
                                      R.id.list_item_freshness*/};
    private static final int LOADER_ID = 42;
    private SimpleCursorAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null, FROM, TO, 0);
        mAdapter.setViewBinder(new TimeLineViewBinder());

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    class TimeLineViewBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (view.getId() != R.id.list_item_text_created_at)
                return false;

            long timeStamp = cursor.getLong(columnIndex);
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(timeStamp);
            ((TextView) view).setText(relativeTime);

            return true;
        }
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        DetailsFragment fragment = (DetailsFragment) getFragmentManager()
                                    .findFragmentById(R.id.fragment_details);

        if (fragment != null && fragment.isVisible()) {
            fragment.updateView(id);
        } else {
            startActivity(new Intent(getActivity(), DetailsActivity.class)
                    .putExtra(StatusContract.Column.ID, id));
        }
    }

    // >>>> LOADER CALLBACKS >>>>

    // Executed on NON-UI thread

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID != id)
            return null;

        Log.d(TAG, "onCreateLoader");
        return new CursorLoader(getActivity(), StatusContract.CONTENT_URI, null, null, null,
                                StatusContract.DEFAULT_SORT); // Runtime error: invalid uri or
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished with cursor: " +  data.getCount());
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
