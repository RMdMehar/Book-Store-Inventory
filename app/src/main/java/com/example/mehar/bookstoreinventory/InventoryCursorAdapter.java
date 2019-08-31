package com.example.mehar.bookstoreinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mehar.bookstoreinventory.data.InventoryContract.StockEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView authorTextView = (TextView) view.findViewById(R.id.authorTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantityTextView);

        String title = cursor.getString(cursor.getColumnIndex(StockEntry.COLUMN_TITLE));
        String author = cursor.getString(cursor.getColumnIndex(StockEntry.COLUMN_AUTHOR));
        int price = cursor.getInt(cursor.getColumnIndex(StockEntry.COLUMN_PRICE));
        String priceText = "$" + String.valueOf(price);
        final int quantity = cursor.getInt(cursor.getColumnIndex(StockEntry.COLUMN_QUANTITY));
        String quantityText = context.getString(R.string.books_available_text) + String.valueOf(quantity);
        int id = cursor.getInt(cursor.getColumnIndex(StockEntry._ID));
        final Uri uri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);

        titleTextView.setText(title);
        authorTextView.setText(author);
        priceTextView.setText(priceText);
        quantityTextView.setText(quantityText);

        ImageView shop = (ImageView) view.findViewById(R.id.shopButton);
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int updatedQuantity = quantity - 1;
                if (updatedQuantity < 0) {
                    updatedQuantity = 0;
                }
                ContentValues values = new ContentValues();
                values.put(StockEntry.COLUMN_QUANTITY, updatedQuantity);
                int rowsAffected = v.getContext().getContentResolver().update(uri, values, null, null);
                if (rowsAffected != 0) {
                    Toast.makeText(v.getContext(), R.string.stock_update_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), R.string.stock_update_failure, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}