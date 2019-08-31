package com.example.mehar.bookstoreinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {
    private InventoryContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.mehar.bookstoreinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_STOCKS = "stocks";

    public static final class StockEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STOCKS);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCKS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCKS;

        public static final String TABLE_NAME = "stocks";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER = "supplier";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        public static final int SUPPLIER_UNKNOWN = 0;
        public static final int SUPPLIER_1 = 1;
        public static final int SUPPLIER_2 = 2;
        public static final int SUPPLIER_3 = 3;

        public static final int SUPPLIER_UNKNOWN_PHONE = 0;
        public static final int SUPPLIER_1_PHONE = 44123411;
        public static final int SUPPLIER_2_PHONE = 43103316;
        public static final int SUPPLIER_3_PHONE = 49887962;

        public static boolean isValidSupplier(int supplier) {
            return supplier == SUPPLIER_UNKNOWN || supplier == SUPPLIER_1 || supplier == SUPPLIER_2 || supplier == SUPPLIER_3;
        }
    }
}