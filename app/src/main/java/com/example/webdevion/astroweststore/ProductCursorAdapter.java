package com.example.webdevion.astroweststore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webdevion.astroweststore.data.ProductContract;
import com.example.webdevion.astroweststore.data.ProductContract.ProductEntry;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    /** Content URI for the existing product */
    private Uri mCurrentProductUri;

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the list_name_text_view TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.list_name_text_view);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.list_quantity_text_view);
        TextView priceTextView = (TextView) view.findViewById(R.id.list_price_text_view);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        final long id;

        id =cursor.getLong(cursor.getColumnIndex(ProductContract.ProductEntry._ID));

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);
        String productPrice = context.getString(R.string.price_appended_text) + cursor.getString(priceColumnIndex);

        // If the product name is empty string or null, then use some default text
        // that says "Unknown telescope", so the TextView isn't blank.
        if (TextUtils.isEmpty(productName)) {
            productName = context.getString(R.string.unknown_name_text);
        }

        // If the product quantity is empty string or null, then use some default text
        // that says "Unknown quantity", so the TextView isn't blank.
        if (TextUtils.isEmpty(productQuantity)) {
            productQuantity = context.getString(R.string.unknown_quantity_text);
        }

        // If the product price is empty string or null, then use some default text
        // that says "Unknown price", so the TextView isn't blank.
        if (TextUtils.isEmpty(productPrice)) {
            productPrice = context.getString(R.string.unknown_price_text);
        }

        //TODO: update the database insetead of the TextView

        // Set a click listener on the sale button.
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialise the string variables with the value of the stored quantity in the
                // database.
                String quantityStored = quantityTextView.getText().toString().trim();
                int currentQuantity = Integer.parseInt(quantityStored);
                // Check to see if the final quantity after the decrement is bigger than 0, if not
                // show a toast message to show that the minimum quantity value is 0.
                if (currentQuantity > 0) {
                    int quantity = currentQuantity - 1;
                    mCurrentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    int rowsEffected = context.getContentResolver().update(mCurrentProductUri, values, null, null);
                } else {
                    Toast.makeText(context, R.string.editor_quantity_insuficient,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Update the TextViews with the attributes for the current product
        nameTextView.setText(productName);
        quantityTextView.setText(productQuantity);
        priceTextView.setText(productPrice);
    }
}
