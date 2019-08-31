package com.example.mehar.bookstoreinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.mehar.bookstoreinventory.data.InventoryContract.StockEntry;

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    InventoryDbHelper helper;

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_STOCKS, BOOKS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_STOCKS + "/#", BOOKS_ID);
    }

    @Override
    public boolean onCreate() {
        helper = new InventoryDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(InventoryContract.StockEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            case BOOKS_ID:
                selection = InventoryContract.StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryContract.StockEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query the unknown URI" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return StockEntry.CONTENT_LIST_TYPE;

            case BOOKS_ID:
                return StockEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri + "with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case BOOKS_ID:
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);

            case BOOKS_ID:
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        String title = values.getAsString(StockEntry.COLUMN_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book needs to have a name");
        }

        String author = values.getAsString(StockEntry.COLUMN_AUTHOR);
        if (author == null) {
            throw new IllegalArgumentException("Author name is compulsory");
        }

        Integer price = values.getAsInteger(StockEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Not a valid price");
        }

        Integer supplier = values.getAsInteger(StockEntry.COLUMN_SUPPLIER);
        if ((supplier == null) || (!StockEntry.isValidSupplier(supplier))) {
            throw new IllegalArgumentException("Not a valid supplier");
        }

        SQLiteDatabase database = helper.getWritableDatabase();
        long newId = database.insert(StockEntry.TABLE_NAME, null, values);
        if (newId == -1) {
            Log.e(LOG_TAG, "Failed to insert new row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, newId);
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(StockEntry.COLUMN_TITLE)) {
            String productName = values.getAsString(StockEntry.COLUMN_TITLE);
            if (productName == null) {
                throw new IllegalArgumentException("Book needs to have a name");
            }
        }

        if (values.containsKey(StockEntry.COLUMN_AUTHOR)) {
            String author = values.getAsString(StockEntry.COLUMN_AUTHOR);
            if (author == null) {
                throw new IllegalArgumentException("Author name is compulsory");
            }
        }

        if (values.containsKey(StockEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(StockEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Not a valid price");
            }
        }

        if (values.containsKey(StockEntry.COLUMN_SUPPLIER)) {
            Integer supplier = values.getAsInteger(StockEntry.COLUMN_SUPPLIER);
            if ((supplier == null) || (!StockEntry.isValidSupplier(supplier))) {
                throw new IllegalArgumentException("Not a valid supplier");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsUpdated = database.update(StockEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}