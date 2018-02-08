package com.example.webdevion.astroweststore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.webdevion.astroweststore.data.ProductContract.ProductEntry;

/**
 * {@link ContentProvider} for AstroWest Store app.
 */
public class ProductProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /** Database helper that will provide us access to the database */
    private ProductDbHelper mDbHelper;

    /** URI matcher code for the content URI for the products table */
    public static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single product in the products table */
    public static final int PRODUCT_ID = 101;

    /** URI matcher object to match a context URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.webdevion.astrowststore/products" will map to the
        // integer code {@link #PRODUCTS}. This URI is used to provide access to MULTIPLE rows
        // of the products table.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);

        // The content URI of the form "content://com.example.webdevion.astrowststore/produts/#" will map to the
        // integer code {@link #PRODUCTS_ID}. This URI is used to provide access to ONE single row
        // of the products table.

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.webdevion.astroweststore/products/3" matches, but
        // "content://com.example.webdevion.astrowststore/products" (without a number at the end) doesn't match.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS
                + "/#", PRODUCT_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // For the PRODUCTS code, query the products table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the products table.
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.webdevion.astroweststore/products/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the products table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the name is not null
        String producer = values.getAsString(ProductEntry.COLUMN_PRODUCT_PRODUCER);
        if (producer == null) {
            throw new IllegalArgumentException("Product requires a producer");
        }

        // Check that the price is not null
        Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Product requires a price");
        } else if(price < 0) {
            throw new IllegalArgumentException("Product requires valid price");
        }

        // Check that the image is not null
        Integer image = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Product requires a image");
        }

        // Check that the user level is valid
        Integer level = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_LEVEL);
        if (level == null || !ProductEntry.isValidLevel(level)) {
            throw new IllegalArgumentException("Product requires valid user level");
        }

        // Check that the optical design is valid
        Integer design = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_DESIGN);
        if (design == null || !ProductEntry.isValidDesign(design)) {
            throw new IllegalArgumentException("Product requires valid optical design");
        }

        // Check that the optical diameter is valid
        Integer diameter = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_DIAMETER);
        if (diameter == null) {
            throw new IllegalArgumentException("Product requires a diameter");
        } else if (diameter < 0) {
            throw new IllegalArgumentException("Product requires valid optical diameter");
        }

        // Check that the focal length is valid
        Integer length = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_LENGTH);
        if (length == null) {
            throw new IllegalArgumentException("Product requires a length");
        } else if (length < 0) {
            throw new IllegalArgumentException("Product requires valid focal length");
        }

        // Check that the mount type is valid
        Integer mount = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_MOUNT);
        if (mount == null || !ProductEntry.isValidMount(mount)) {
            throw new IllegalArgumentException("Product requires valid mount type");
        }

        // Check that the phone number is valid
        Integer number = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_CONTACT);
        if (number == null) {
            throw new IllegalArgumentException("Product requires a phone number");
        } else if (number < 0 && Integer.toString(number).length() < 9) {
            throw new IllegalArgumentException("Product requires valid phone number");
        }

        // Check that the quantity is valid
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Product requires a quantity");
        } else if (quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new product with the given values
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update products in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more products).
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link ProductEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_PRODUCER} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRODUCER)) {
            String producer = values.getAsString(ProductEntry.COLUMN_PRODUCT_PRODUCER);
            if (producer == null) {
                throw new IllegalArgumentException("Product requires a producer");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_PRICE} key is present,
        // check that the name value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Product requires a price");
            } else if(price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_IMAGE} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_IMAGE)) {
            Integer image = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Product requires a image");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_LEVEL} key is present,
        // check that the name value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_LEVEL)) {
            Integer level = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_LEVEL);
            if (level == null || !ProductEntry.isValidLevel(level)) {
                throw new IllegalArgumentException("Product requires valid user level");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_DESIGN} key is present,
        // check that the name value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_DESIGN)) {
            Integer design = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_DESIGN);
            if (design == null || !ProductEntry.isValidDesign(design)) {
                throw new IllegalArgumentException("Product requires valid optical design");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_DIAMETER} key is present,
        // check that the name value is valid and not null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_DIAMETER)) {
            Integer diameter = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_DIAMETER);
            if (diameter == null) {
                throw new IllegalArgumentException("Product requires a diameter");
            } else if (diameter < 0) {
                throw new IllegalArgumentException("Product requires valid optical diameter");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_LENGTH} key is present,
        // check that the name value is valid and not null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_LENGTH)) {
            Integer length = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_LENGTH);
            if (length == null) {
                throw new IllegalArgumentException("Product requires a length");
            } else if (length < 0) {
                throw new IllegalArgumentException("Product requires valid focal length");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_MOUNT} key is present,
        // check that the name value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_MOUNT)) {
            Integer mount = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_MOUNT);
            if (mount == null || !ProductEntry.isValidDesign(mount)) {
                throw new IllegalArgumentException("Product requires valid mount type");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_CONTACT} key is present,
        // check that the name value is valid and not null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_CONTACT)) {
            Integer number = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_CONTACT);
            if (number == null) {
                throw new IllegalArgumentException("Product requires a phone number");
            } else if (number < 0 && Integer.toString(number).length() < 9) {
                throw new IllegalArgumentException("Product requires valid phone number");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_QUANTITY} key is present,
        // check that the name value is valid and not null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Product requires a quantity");
            } else if (quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
