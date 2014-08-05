package com.andrasmuller.yamba;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Müller András on 2014.07.31..
 */
public class StatusProvider extends ContentProvider {

    private static final String TAG = StatusProvider.class.getSimpleName();
    private DbHelper dbHelper;
    private static final UriMatcher sURImatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURImatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE, StatusContract.STATUS_DIR);
        sURImatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE + "/#", StatusContract.STATUS_ITEM);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        Log.d(TAG, "onCreated");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(StatusContract.TABLE);

        switch (sURImatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                break;
            case StatusContract.STATUS_ITEM:
                qb.appendWhere(StatusContract.Column.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        String orderBy = TextUtils.isEmpty(s2) ? StatusContract.DEFAULT_SORT : s2;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = qb.query(db, strings, s, strings2, null, null, orderBy);

        cursor.setNotificationUri(getContext().getContentResolver(), null);

        Log.d(TAG, "queried records: " + cursor.getCount());
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sURImatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                Log.d(TAG, "gotType: " + StatusContract.STATUS_TYPE_DIR);
                return StatusContract.STATUS_TYPE_DIR;
            case StatusContract.STATUS_ITEM:
                Log.d(TAG, "gotType: " + StatusContract.STATUS_TYPE_ITEM);
                return StatusContract.STATUS_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri ret = null;

        // Assert correct uri
        if (sURImatcher.match(uri) != StatusContract.STATUS_DIR)
            throw new IllegalArgumentException("Illegal uri: " + uri);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(StatusContract.TABLE, null,
                        contentValues, SQLiteDatabase.CONFLICT_IGNORE);

        // Was insert successful?
        if (-1 != rowId) {
            long id = contentValues.getAsLong(StatusContract.Column.ID);
            ret = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "inserted uri: " + ret);

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ret;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        String where;

        switch (sURImatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                where = s == null ? "1" : s;
                break;
            case StatusContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = StatusContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(s) ? "" : " and ( " + s + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.delete(StatusContract.TABLE, where, strings);

        if (ret > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        Log.d(TAG, "deleted records: " + ret);
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        String where;

        switch (sURImatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                where = s;
                break;
            case StatusContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = StatusContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(s) ? "" : " and ( "
                            + s
                            + ")"
                );
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.update(StatusContract.TABLE, contentValues, where, strings);

        if (ret > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        Log.d(TAG, "updated records: " + ret);

        return ret;
    }
}
