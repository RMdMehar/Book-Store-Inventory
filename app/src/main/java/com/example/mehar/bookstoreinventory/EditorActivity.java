package com.example.mehar.bookstoreinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mehar.bookstoreinventory.data.InventoryContract.StockEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mTitle;
    private EditText mAuthor;
    private EditText mPrice;
    private TextView mQuantity;
    private Button mDecrementor;
    private Button mIncremetor;
    private Spinner mSupplierSpinner;
    private TextView mPhone;
    private int mSupplier = 0;
    private int mSupplierPhone = 0;
    private int quantity;

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentBookUri;
    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle("Add Book");
        } else {
            setTitle("Edit Book");
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        mTitle = (EditText) findViewById(R.id.book_title);
        mTitle.setOnTouchListener(mTouchListener);
        mAuthor = (EditText) findViewById(R.id.book_author);
        mAuthor.setOnTouchListener(mTouchListener);
        mPrice = (EditText) findViewById(R.id.price);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity = (TextView) findViewById(R.id.quantity_display);
        mQuantity.setText("0");
        mDecrementor = (Button) findViewById(R.id.quantity_decrementor);
        mDecrementor.setOnTouchListener(mTouchListener);
        mDecrementor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt(mQuantity.getText().toString().trim());
                quantity--;
                if (quantity < 0) {
                    quantity = 0;
                }
                mQuantity.setText(String.valueOf(quantity));
            }
        });
        mIncremetor = (Button) findViewById(R.id.quantity_incrementor);
        mIncremetor.setOnTouchListener(mTouchListener);
        mIncremetor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt(mQuantity.getText().toString().trim());
                quantity++;
                mQuantity.setText(String.valueOf(quantity));
            }
        });
        mSupplierSpinner = (Spinner) findViewById(R.id.spinner_supplier);
        mSupplierSpinner.setOnTouchListener(mTouchListener);
        setupSpinner();
        mPhone = (TextView) findViewById(R.id.phone);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactNumber = String.valueOf(mPhone.getText());
                if (contactNumber.equals("Number Not Known")) {

                } else {
                    String phoneUri = "tel:" + contactNumber.trim();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(phoneUri));
                    startActivity(intent);
                    finish();
                }

            }
        });
    }
    private void setupSpinner() {
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_dropdown_item_1line);
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSupplierSpinner.setAdapter(supplierSpinnerAdapter);

        mSupplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier1))) {
                        mSupplier = StockEntry.SUPPLIER_1;
                        mSupplierPhone = StockEntry.SUPPLIER_1_PHONE;
                    } else if (selection.equals(getString(R.string.supplier2))) {
                        mSupplier = StockEntry.SUPPLIER_2;
                        mSupplierPhone = StockEntry.SUPPLIER_2_PHONE;
                    } else if (selection.equals(getString(R.string.supplier3))) {
                        mSupplier = StockEntry.SUPPLIER_3;
                        mSupplierPhone = StockEntry.SUPPLIER_3_PHONE;
                    } else {
                        mSupplier = StockEntry.SUPPLIER_UNKNOWN;
                        mSupplierPhone = StockEntry.SUPPLIER_UNKNOWN_PHONE;
                    }
                }
                if (mSupplierPhone == StockEntry.SUPPLIER_UNKNOWN_PHONE) {
                    mPhone.setText(R.string.unknown_number);
                } else {
                    mPhone.setText(String.valueOf(mSupplierPhone));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplier = StockEntry.SUPPLIER_UNKNOWN;
                mSupplierPhone = StockEntry.SUPPLIER_UNKNOWN_PHONE;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveStock();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveStock(){
        String bookTitle = mTitle.getText().toString().trim();
        String bookAuthor = mAuthor.getText().toString().trim();
        String quantityData = String.valueOf(mQuantity.getText());
        int price = 0;
        try{
            price = Integer.parseInt(mPrice.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.empty_field, Toast.LENGTH_SHORT).show();
        }
        if ((TextUtils.isEmpty(bookTitle) || TextUtils.isEmpty(bookAuthor) || quantityData.equals("0") || mSupplier == StockEntry.SUPPLIER_UNKNOWN)) {
            Toast.makeText(this, R.string.empty_field, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            ContentValues insertValues = new ContentValues();
            insertValues.put(StockEntry.COLUMN_TITLE, bookTitle);
            insertValues.put(StockEntry.COLUMN_AUTHOR, bookAuthor);
            insertValues.put(StockEntry.COLUMN_PRICE, price);
            quantity = Integer.parseInt(quantityData);
            insertValues.put(StockEntry.COLUMN_QUANTITY, quantity);
            insertValues.put(StockEntry.COLUMN_SUPPLIER, mSupplier);
            insertValues.put(StockEntry.COLUMN_SUPPLIER_PHONE_NUMBER, mSupplierPhone);

            if (mCurrentBookUri == null) {
                Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, insertValues);
                if (newUri == null) {
                    Toast.makeText(this, "Error with inserting book", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Book Saved", Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentBookUri, insertValues, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, "Error with updating book",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Book updated",
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {StockEntry._ID, StockEntry.COLUMN_TITLE, StockEntry.COLUMN_AUTHOR, StockEntry.COLUMN_PRICE, StockEntry.COLUMN_QUANTITY, StockEntry.COLUMN_SUPPLIER};
        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            String bookTitle = cursor.getString(cursor.getColumnIndex(StockEntry.COLUMN_TITLE));
            String bookAuthor = cursor.getString(cursor.getColumnIndex(StockEntry.COLUMN_AUTHOR));
            int price = cursor.getInt(cursor.getColumnIndex(StockEntry.COLUMN_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndex(StockEntry.COLUMN_QUANTITY));
            int supplier = cursor.getInt(cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER));

            mTitle.setText(bookTitle);
            mAuthor.setText(bookAuthor);
            mPrice.setText(String.valueOf(price));
            mQuantity.setText(String.valueOf(quantity));

            switch (supplier) {
                case StockEntry.SUPPLIER_1:
                    mSupplierSpinner.setSelection(1);
                    break;
                case StockEntry.SUPPLIER_2:
                    mSupplierSpinner.setSelection(2);
                    break;
                case StockEntry.SUPPLIER_3:
                    mSupplierSpinner.setSelection(3);
                    break;
                default:
                    mSupplierSpinner.setSelection(0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitle.setText("");
        mAuthor.setText("");
        mPrice.setText("");
        mQuantity.setText("");
        mSupplierSpinner.setSelection(0);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_confirmation);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
        if (rowsDeleted != 0) {
            Toast.makeText(this, R.string.book_deletion_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.book_deletion_failure, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}