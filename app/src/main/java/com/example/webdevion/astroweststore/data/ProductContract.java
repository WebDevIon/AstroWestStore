package com.example.webdevion.astroweststore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the AstroWest Store app.
 */
public final class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ProductContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.webdevion.astroweststore";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.webdevion.astroweststore/products/ is a valid path for
     * looking at product data. content://com.example.webdevion.astroweststore/store/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "store".
     */
    public static final String PATH_PRODUCTS = "products";

    /**
     * Inner class that defines constant values for the products database table.
     * Each entry in the table represents a single product.
     */
    public static abstract class ProductEntry implements BaseColumns {

        /** The content URI to access the product data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /** Name of database table for products */
        public final static String TABLE_NAME = "products";

        /**
         * Unique ID number for the products (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="name";

        /**
         * Name of the producer.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_PRODUCER ="producer";

        /**
         * Price of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";

        /**
         * Image of the product.
         *
         * Type: BLOB
         */
        public final static String COLUMN_PRODUCT_IMAGE = "image";

        /**
         * Possible values for the image of the product.
         */
        public static final int IMAGE_NONE = 0;
        public static final int IMAGE_DOBSON_REFLECTOR = 1;
        public static final int IMAGE_EQUATORIAL_REFLECTOR = 2;
        public static final int IMAGE_EQUATORIAL_REFRACTOR = 3;
        public static final int IMAGE_AZIMUTHAL_REFRACTOR = 4;
        public static final int IMAGE_REFLECTOR_TUBE_ONLY = 5;
        public static final int IMAGE_REFRACTOR_TUBE_ONLY = 6;

        /**
         * Returns whether or not the given image is {@link #IMAGE_NONE}, {@link #IMAGE_DOBSON_REFLECTOR},
         *  {@link #IMAGE_EQUATORIAL_REFLECTOR},  {@link #IMAGE_EQUATORIAL_REFRACTOR},
         *  {@link #IMAGE_AZIMUTHAL_REFRACTOR},  {@link #IMAGE_REFLECTOR_TUBE_ONLY}
         *  or {@link #IMAGE_REFRACTOR_TUBE_ONLY}.
         */
        public static boolean isValidImage(int image) {
            if (image == IMAGE_NONE || image == IMAGE_DOBSON_REFLECTOR || image == IMAGE_EQUATORIAL_REFLECTOR
                    || image == IMAGE_EQUATORIAL_REFRACTOR || image == IMAGE_AZIMUTHAL_REFRACTOR
                    || image == IMAGE_REFLECTOR_TUBE_ONLY || image == IMAGE_REFRACTOR_TUBE_ONLY) {
                return true;
            }
            return false;
        }

        /**
         * Recommended user level for the product.
         *
         * The only possible values are {@link #LEVEL_BEGINNER}, {@link #LEVEL_INTERMEDIATE},
         * or {@link #LEVEL_ADVANCED}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_LEVEL = "user_level";

        /**
         * Possible values for the level of the product.
         */
        public static final int LEVEL_NONE = 0;
        public static final int LEVEL_BEGINNER = 1;
        public static final int LEVEL_INTERMEDIATE = 2;
        public static final int LEVEL_ADVANCED = 3;

        /**
         * Returns whether or not the given level is {@link #LEVEL_BEGINNER}, {@link #LEVEL_INTERMEDIATE},
         * or {@link #LEVEL_ADVANCED}.
         */
        public static boolean isValidLevel(int level) {
            if (level == LEVEL_NONE || level == LEVEL_BEGINNER || level == LEVEL_INTERMEDIATE || level == LEVEL_ADVANCED) {
                return true;
            }
            return false;
        }

        /**
         * Optical design of the product.
         *
         * The only possible values are {@link #DESIGN_REFLECTOR} or {@link #DESIGN_REFRACTOR}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_DESIGN = "optical_design";

        /**
         * Possible values for the optical design of the product.
         */
        public static final int DESIGN_NONE = 0;
        public static final int DESIGN_REFLECTOR = 1;
        public static final int DESIGN_REFRACTOR = 2;

        /**
         * Returns whether or not the given design is{@link #DESIGN_NONE}, {@link #DESIGN_REFLECTOR},
         * or {@link #DESIGN_REFRACTOR}.
         */
        public static boolean isValidDesign(int design) {
            if (design == DESIGN_NONE || design == DESIGN_REFLECTOR || design == DESIGN_REFRACTOR) {
                return true;
            }
            return false;
        }

        /**
         * Diameter of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_DIAMETER = "diameter";

        /**
         * Focal length of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_LENGTH = "length";

        /**
         * Mount type of the product.
         *
         * The only possible values are {@link #MOUNT_NONE}, {@link #MOUNT_DOBSON},
         * {@link #MOUNT_EQUATORIAL} or {@link #MOUNT_AZIMUTHAL}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_MOUNT = "mount_type";

        /**
         * Possible values for the mount type of the product.
         */
        public static final int MOUNT_NONE = 0;
        public static final int MOUNT_DOBSON = 1;
        public static final int MOUNT_EQUATORIAL = 2;
        public static final int MOUNT_AZIMUTHAL = 3;

        /**
         * Returns whether or not the given mount is {@link #MOUNT_NONE}, {@link #MOUNT_DOBSON},
         * {@link #MOUNT_EQUATORIAL} or {@link #MOUNT_AZIMUTHAL}.
         */
        public static boolean isValidMount(int mount) {
            if (mount == MOUNT_NONE || mount == MOUNT_DOBSON || mount == MOUNT_EQUATORIAL || mount == MOUNT_AZIMUTHAL) {
                return true;
            }
            return false;
        }

        /**
         * Contact number of the producer.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_CONTACT = "contact_number";

        /**
         * Quantity of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

    }
}
