package com.example.webdevion.astroweststore;

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

import com.example.webdevion.astroweststore.data.ProductContract.ProductEntry;

import static java.lang.String.valueOf;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the product data loader */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /** Content URI for the existing product (null if it's a new product) */
    private Uri mCurrentProductUri;

    /** EditText field to enter the product's name */
    private EditText mNameEditText;

    /** EditText field to enter the product's producer */
    private EditText mProducerEditText;

    /** EditText field to enter the product's price */
    private EditText mPriceEditText;

    /** Spinner field to enter the product's image */
    private Spinner mImageSpinner;

    /**
     * Image of the product. The possible valid values are in the ProductContract.java file:
     * {@link ProductEntry#IMAGE_NONE}, {@link ProductEntry#IMAGE_DOBSON_REFLECTOR},
     *  {@link ProductEntry#IMAGE_EQUATORIAL_REFLECTOR},  {@link ProductEntry#IMAGE_EQUATORIAL_REFRACTOR},
     *  {@link ProductEntry#IMAGE_AZIMUTHAL_REFRACTOR},  {@link ProductEntry#IMAGE_REFLECTOR_TUBE_ONLY}
     *  or {@link ProductEntry#IMAGE_REFRACTOR_TUBE_ONLY}
     */
    private int mImage = ProductEntry.IMAGE_NONE;

    /** Spinner field to enter the product's user level */
    private Spinner mLevelSpinner;

    /**
     * User level of the product. The possible valid values are in the ProductContract.java file:
     * {@link ProductEntry#LEVEL_BEGINNER}, {@link ProductEntry#LEVEL_INTERMEDIATE}, or
     * {@link ProductEntry#LEVEL_ADVANCED}.
     */
    private int mLevel = ProductEntry.LEVEL_BEGINNER;

    /** Spinner field to enter the product's optical design */
    private Spinner mDesignSpinner;

    /**
     * Optical design of the product. The possible valid values are in the ProductContract.java file:
     * {@link ProductEntry#DESIGN_REFLECTOR} or {@link ProductEntry#DESIGN_REFRACTOR}.
     */
    private int mDesign = ProductEntry.DESIGN_REFLECTOR;

    /** EditText field to enter the product's optical diameter */
    private EditText mDiameterEditText;

    /** EditText field to enter the product's focal length */
    private EditText mLengthEditText;

    /** Spinner field to enter the product's mount type */
    private Spinner mMountSpinner;

    /**
     * Mount type of the product. The possible valid values are in the ProductContract.java file:
     * {@link ProductEntry#MOUNT_NONE}, {@link ProductEntry#MOUNT_DOBSON},
     * {@link ProductEntry#MOUNT_EQUATORIAL}, or {@link ProductEntry#MOUNT_AZIMUTHAL}.
     */
    private int mMount = ProductEntry.MOUNT_NONE;

    /** EditText field to enter the producer's contact number */
    private EditText mContactNumberEditText;

    /** TextView field to store the product's quantity */
    private TextView mQuantityTextView;

    /** EditText field to enter the product's quantity */
    private EditText mQuantityEditText;

    /** Button to decrease the product's quantity */
    private Button mDecreaseButton;

    /** Button to increase the product's quantity */
    private Button mIncreaseButton;

    /** Button to order the product from the supplier */
    private Button mOrderButton;

    /** Button to delete the product */
    private Button mDeleteButton;

    private static final int DATA_COMPLETE = 1;

    private static final int DATA_INCOMPLETE = 0;

    private int mDataChecker = DATA_INCOMPLETE;

    /** Boolean flag that keeps track of whether the product has been edited (true) or not (false) */
    private boolean mProductHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mProductHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        Button deleteButton = (Button) findViewById(R.id.delete_product_button);
        deleteButton.setVisibility(View.VISIBLE);

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentProductUri == null) {
            // This is a new product, so change the app bar to say "Add a new Product"
            setTitle(getString(R.string.new_product_name));

            // Hide the delete button.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            deleteButton.setVisibility(View.GONE);
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_name));

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.name_text_view);
        mProducerEditText = (EditText) findViewById(R.id.producer_text_view);
        mPriceEditText = (EditText) findViewById(R.id.price_text_view);
        mImageSpinner = (Spinner) findViewById(R.id.product_image_spinner);
        mLevelSpinner = (Spinner) findViewById(R.id.spinner_user_level);
        mDesignSpinner = (Spinner) findViewById(R.id.spinner_optical_design);
        mDiameterEditText = (EditText) findViewById(R.id.edit_optical_diameter);
        mLengthEditText = (EditText) findViewById(R.id.edit_focal_length);
        mMountSpinner = (Spinner) findViewById(R.id.spinner_mount_type);
        mContactNumberEditText = (EditText) findViewById(R.id.edit_contact_information);
        mQuantityTextView = (TextView) findViewById(R.id.quantity_available);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mDecreaseButton = (Button) findViewById(R.id.decrease_quantity_button);
        mIncreaseButton = (Button) findViewById(R.id.increase_quantity_button);
        mOrderButton = (Button) findViewById(R.id.order_from_supplier_button);
        mDeleteButton = (Button) findViewById(R.id.delete_product_button);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mProducerEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mImageSpinner.setOnTouchListener(mTouchListener);
        mLevelSpinner.setOnTouchListener(mTouchListener);
        mDesignSpinner.setOnTouchListener(mTouchListener);
        mDiameterEditText.setOnTouchListener(mTouchListener);
        mLengthEditText.setOnTouchListener(mTouchListener);
        mMountSpinner.setOnTouchListener(mTouchListener);
        mContactNumberEditText.setOnTouchListener(mTouchListener);
        mQuantityTextView.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mDecreaseButton.setOnTouchListener(mTouchListener);
        mIncreaseButton.setOnTouchListener(mTouchListener);
        mOrderButton.setOnTouchListener(mTouchListener);

        // Set a click listener on the decrease quantity (-) button.
        mDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialise the string variables with the value of the stored quantity in the
                // database and the value of the EditText field for the quantity.
                String quantityStored = mQuantityTextView.getText().toString().trim();
                String quantityModifier = mQuantityEditText.getText().toString().trim();
                // Check if there is a quantity stored if there is none set it to the value of 0.
                if (quantityStored.matches("")) {
                    quantityStored = "0";
                }
                // Check if there is a value in the EditText field for quantity, and if there isn't
                // show a toast message to set a decrement
                if (!quantityModifier.matches("")) {
                    int currentQuantity = Integer.parseInt(quantityStored);
                    int quantityDecrement = Integer.parseInt(quantityModifier);
                    // Check to see if the final quantity after the decrement is bigger than 0, if not
                    // show a toast message to show that the minimum quantity value is 0.
                    if (currentQuantity > 0 && currentQuantity >= quantityDecrement) {
                        int number = currentQuantity - quantityDecrement;
                        String quantity = valueOf(number);
                        mQuantityTextView.setText(quantity);
                    } else {
                        Toast.makeText(getBaseContext(), R.string.editor_quantity_decrement_to_small,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), R.string.editor_quantity_decrement_inexistent,
                            Toast.LENGTH_SHORT).show();
                }
                }
        });

        // Set a click listener on the increase quantity (+) button.
        mIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialise the string variables with the value of the stored quantity in the
                // database and the value of the EditText field for the quantity.
                String quantityStored = mQuantityTextView.getText().toString().trim();
                String quantityModifier = mQuantityEditText.getText().toString().trim();
                // Check if there is a quantity stored if there is none set it to the value of 0.
                if (quantityStored.matches("")) {
                    quantityStored = "0";
                }
                // Check if there is a value in the EditText field for quantity, and if there isn't
                // show a toast message to set a increment
                if(!quantityModifier.matches("")){
                    int currentQuantity = Integer.parseInt(quantityStored);
                    int quantityIncrement = Integer.parseInt(quantityModifier);
                    // Check to see if the final quantity after the increment is smaller than 100, if not
                    // show a toast message to show that the maximum quantity value is 100.
                    if (currentQuantity < 100 && 100 >= currentQuantity + quantityIncrement) {
                        int number = currentQuantity + quantityIncrement;
                        String quantity = valueOf(number);
                        mQuantityTextView.setText(quantity);
                    } else {
                        Toast.makeText(getBaseContext(), R.string.editor_quantity_increment_to_big,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), R.string.editor_quantity_increment_inexistent,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set a click listener on the order from producer button.
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumberStored = mContactNumberEditText.getText().toString().trim();
                // Check to see if the phone number is valid, if not
                // show a toast message to prompt the user to input a phone number.
                if(phoneNumberStored.matches("")){
                    Toast.makeText(getBaseContext(), R.string.editor_phone_number_inexistent,
                            Toast.LENGTH_SHORT).show();
                } else if((phoneNumberStored).length() < 9) {
                    // Since the phone number is to short, we will prompt the user that he needs to update it.
                    Toast.makeText(getBaseContext(), R.string.editor_phone_number_too_short,
                            Toast.LENGTH_SHORT).show();
                } else if((phoneNumberStored).length() > 9) {
                    // Since the phone number is to short, we will prompt the user that he needs to update it.
                    Toast.makeText(getBaseContext(), R.string.editor_phone_number_too_long,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumberStored));
                    startActivity(intent);
                }
            }
        });

        // Set a click listener on the delete product button.
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
            }
        });

        setupImageSpinner();
        setupLevelSpinner();
        setupDesignSpinner();
        setupMountSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the image of the product.
     */
    private void setupImageSpinner() {

        // Create the custom adapter for the spinner.
        SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(this,
                new Integer[]{R.drawable.no_image_selected, R.drawable.dobson_reflector,
                        R.drawable.equatorial_reflector, R.drawable.equatorial_refractor,
                        R.drawable.azimuthal_refractor, R.drawable.reflector_tube_only,
                        R.drawable.refractor_tube_only});

        // Apply the adapter to the spinner
        mImageSpinner.setAdapter(adapter);

        // Set the integer mImage to the constant values
        mImageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selection = (int) parent.getItemAtPosition(position);
                if (selection >= 0) {
                    if (selection == (R.drawable.no_image_selected)) {
                        mImage = ProductEntry.IMAGE_NONE;
                    } else if (selection == (R.drawable.dobson_reflector)) {
                        mImage = ProductEntry.IMAGE_DOBSON_REFLECTOR;
                    } else if (selection == (R.drawable.equatorial_reflector)) {
                        mImage = ProductEntry.IMAGE_EQUATORIAL_REFLECTOR;
                    } else if (selection == (R.drawable.equatorial_refractor)) {
                        mImage = ProductEntry.IMAGE_EQUATORIAL_REFRACTOR;
                    } else if (selection == (R.drawable.azimuthal_refractor)) {
                        mImage = ProductEntry.IMAGE_AZIMUTHAL_REFRACTOR;
                    } else if (selection == (R.drawable.reflector_tube_only)) {
                        mImage = ProductEntry.IMAGE_REFLECTOR_TUBE_ONLY;
                    } else if (selection == (R.drawable.refractor_tube_only)) {
                        mImage = ProductEntry.IMAGE_REFRACTOR_TUBE_ONLY;
                    }

                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mImage = ProductEntry.IMAGE_NONE;
            }
        });


    }

    /**
     * Setup the dropdown spinner that allows the user to select the user level of the product.
     */
    private void setupLevelSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter levelSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_level_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        levelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mLevelSpinner.setAdapter(levelSpinnerAdapter);

        // Set the integer mLevel to the constant values
        mLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.level_none))) {
                        mLevel = ProductEntry.LEVEL_NONE;
                    } else if (selection.equals(getString(R.string.level_beginner))) {
                        mLevel = ProductEntry.LEVEL_BEGINNER;
                    } else if (selection.equals(getString(R.string.level_intermediate))) {
                        mLevel = ProductEntry.LEVEL_INTERMEDIATE;
                    } else if (selection.equals(getString(R.string.level_advanced))) {
                        mLevel = ProductEntry.LEVEL_ADVANCED;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLevel = ProductEntry.LEVEL_NONE;
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the optical design of the product.
     */
    private void setupDesignSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter designSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_design_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        designSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mDesignSpinner.setAdapter(designSpinnerAdapter);

        // Set the integer mDesign to the constant values
        mDesignSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.design_none))) {
                        mDesign = ProductEntry.DESIGN_NONE;
                    } else if (selection.equals(getString(R.string.design_reflector))) {
                        mDesign = ProductEntry.DESIGN_REFLECTOR;
                    } else if (selection.equals(getString(R.string.design_refractor))) {
                        mDesign = ProductEntry.DESIGN_REFRACTOR;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mDesign = ProductEntry.DESIGN_NONE;
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the user level of the product.
     */
    private void setupMountSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter mountSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_mount_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        mountSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mMountSpinner.setAdapter(mountSpinnerAdapter);

        // Set the integer mMount to the constant values
        mMountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.mount_none))) {
                        mMount = ProductEntry.MOUNT_NONE;
                    } else if (selection.equals(getString(R.string.mount_dobson))) {
                        mMount = ProductEntry.MOUNT_DOBSON;
                    } else if (selection.equals(getString(R.string.mount_equatorial))) {
                        mMount = ProductEntry.MOUNT_EQUATORIAL;
                    } else if (selection.equals(getString(R.string.mount_azimuthal))) {
                        mMount = ProductEntry.MOUNT_AZIMUTHAL;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMount = ProductEntry.MOUNT_NONE;
            }
        });
    }

    /**
     * Get user input from editor and save product into database.
     */
    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String producerString = mProducerEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String diameterString = mDiameterEditText.getText().toString().trim();
        String lengthString = mLengthEditText.getText().toString().trim();
        String contactString = mContactNumberEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        String quantityModifierString = mQuantityEditText.getText().toString().trim();

        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(producerString) &&
                TextUtils.isEmpty(priceString) && mImage == ProductEntry.IMAGE_NONE &&
                mLevel == ProductEntry.LEVEL_NONE && mDesign == ProductEntry.DESIGN_NONE &&
                TextUtils.isEmpty(diameterString) && TextUtils.isEmpty(lengthString) &&
                mMount == ProductEntry.MOUNT_NONE && TextUtils.isEmpty(contactString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(quantityString)) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(producerString) ||
                TextUtils.isEmpty(priceString) || TextUtils.isEmpty(diameterString) ||
                TextUtils.isEmpty(lengthString) || TextUtils.isEmpty(contactString) ||
                TextUtils.isEmpty(quantityString)) {

            mDataChecker = DATA_INCOMPLETE;
            // Since some fields were left empty, we will prompt the user that he needs to complete them all.
            Toast.makeText(getBaseContext(), R.string.editor_incomplete_details,
                    Toast.LENGTH_SHORT).show();
        } else if((contactString).length() < 9) {
            // Since the phone number is to short, we will prompt the user that he needs to update it.
            Toast.makeText(getBaseContext(), R.string.editor_phone_number_too_short,
                    Toast.LENGTH_SHORT).show();
        } else if((contactString).length() > 9) {
            // Since the phone number is to short, we will prompt the user that he needs to update it.
            Toast.makeText(getBaseContext(), R.string.editor_phone_number_too_long,
                    Toast.LENGTH_SHORT).show();
        } else {

            // Create a ContentValues object where column names are the keys,
            // and product attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(ProductEntry.COLUMN_PRODUCT_PRODUCER, producerString);
            // If the price is not provided by the user, don't try to parse the string into an
            // integer value. Use 0 by default.
            int price = 0;
            if (!TextUtils.isEmpty(priceString)) {
                price = Integer.parseInt(priceString);
            }
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
            values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, mImage);
            values.put(ProductEntry.COLUMN_PRODUCT_LEVEL, mLevel);
            values.put(ProductEntry.COLUMN_PRODUCT_DESIGN, mDesign);
            // If the optical diameter is not provided by the user, don't try to parse the string into an
            // integer value. Use 0 by default.
            int diameter = 0;
            if (!TextUtils.isEmpty(diameterString)) {
                diameter = Integer.parseInt(diameterString);
            }
            values.put(ProductEntry.COLUMN_PRODUCT_DIAMETER, diameter);
            // If the focal length is not provided by the user, don't try to parse the string into an
            // integer value. Use 0 by default.
            int length = 0;
            if (!TextUtils.isEmpty(lengthString)) {
                length = Integer.parseInt(lengthString);
            }
            values.put(ProductEntry.COLUMN_PRODUCT_LENGTH, length);
            values.put(ProductEntry.COLUMN_PRODUCT_MOUNT, mMount);
            // If the contact number is not provided by the user, don't try to parse the string into an
            // integer value. Use 0 by default.
            int contact = 0;
            if (!TextUtils.isEmpty(contactString)) {
                contact = Integer.parseInt(contactString);
            }
            values.put(ProductEntry.COLUMN_PRODUCT_CONTACT, contact);
            // If the quantity is not provided by the user, don't try to parse the string into an
            // integer value. Use 0 by default.
            int quantity = 0;
            if (!TextUtils.isEmpty(quantityString)) {
                quantity = Integer.parseInt(quantityString);
            }
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

            // Determine if this is a new or existing product by checking if mCurrentProductUri is null or not
            if (mCurrentProductUri == null) {
                // This is a NEW product, so insert a new product into the provider,
                // returning the content URI for the new product.
                Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentProductUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentProductUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }

            mDataChecker = DATA_COMPLETE;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity
                if (mDataChecker == DATA_COMPLETE) {
                    finish();
                }
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the products table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRODUCER,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductEntry.COLUMN_PRODUCT_LEVEL,
                ProductEntry.COLUMN_PRODUCT_DESIGN,
                ProductEntry.COLUMN_PRODUCT_DIAMETER,
                ProductEntry.COLUMN_PRODUCT_LENGTH,
                ProductEntry.COLUMN_PRODUCT_MOUNT,
                ProductEntry.COLUMN_PRODUCT_CONTACT,
                ProductEntry.COLUMN_PRODUCT_QUANTITY };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,     // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int producerColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRODUCER);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);
            int levelColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_LEVEL);
            int designColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DESIGN);
            int diameterColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DIAMETER);
            int lengthColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_LENGTH);
            int mountColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_MOUNT);
            int contactColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CONTACT);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String producer = cursor.getString(producerColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int image = cursor.getInt(imageColumnIndex);
            int level = cursor.getInt(levelColumnIndex);
            int design = cursor.getInt(designColumnIndex);
            int diameter = cursor.getInt(diameterColumnIndex);
            int length = cursor.getInt(lengthColumnIndex);
            int mount = cursor.getInt(mountColumnIndex);
            int contact = cursor.getInt(contactColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mProducerEditText.setText(producer);
            mPriceEditText.setText(Integer.toString(price));
            mDiameterEditText.setText(Integer.toString(diameter));
            mLengthEditText.setText(Integer.toString(length));
            mContactNumberEditText.setText(Integer.toString(contact));
            mQuantityTextView.setText(Integer.toString(quantity));

            // Image selector is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is None, 1 is Dobson Reflector, 2 is Equatorial Reflector,
            // 3 is Equatorial Refractor, 4 is Azimuthal Refractor, 5 is Reflector Tube, 6 is Refractor Tube).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (image) {
                case ProductEntry.IMAGE_DOBSON_REFLECTOR:
                    mImageSpinner.setSelection(1);
                    break;
                case ProductEntry.IMAGE_EQUATORIAL_REFLECTOR:
                    mImageSpinner.setSelection(2);
                    break;
                case ProductEntry.IMAGE_EQUATORIAL_REFRACTOR:
                    mImageSpinner.setSelection(3);
                    break;
                case ProductEntry.IMAGE_AZIMUTHAL_REFRACTOR:
                    mImageSpinner.setSelection(4);
                    break;
                case ProductEntry.IMAGE_REFLECTOR_TUBE_ONLY:
                    mImageSpinner.setSelection(5);
                    break;
                case ProductEntry.IMAGE_REFRACTOR_TUBE_ONLY:
                    mImageSpinner.setSelection(6);
                    break;
                default:
                    mLevelSpinner.setSelection(0);
                    break;
            }

            // User level is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is None, 1 is Beginner, 2 is Intermediate, 3 is advanced).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (level) {
                case ProductEntry.LEVEL_BEGINNER:
                    mLevelSpinner.setSelection(1);
                    break;
                case ProductEntry.LEVEL_INTERMEDIATE:
                    mLevelSpinner.setSelection(2);
                    break;
                case ProductEntry.LEVEL_ADVANCED:
                    mLevelSpinner.setSelection(3);
                    break;
                default:
                    mLevelSpinner.setSelection(0);
                    break;
            }

            // Optical design is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is None, 1 is Reflector, 2 is Refractor).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (design) {
                case ProductEntry.DESIGN_REFLECTOR:
                    mDesignSpinner.setSelection(1);
                    break;
                case ProductEntry.DESIGN_REFRACTOR:
                    mDesignSpinner.setSelection(2);
                    break;
                default:
                    mDesignSpinner.setSelection(0);
                    break;
            }

            // Mount type is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is None, 1 is Dobson, 2 is Equatorial, 3 is Azimuthal).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (mount) {
                case ProductEntry.MOUNT_DOBSON:
                    mMountSpinner.setSelection(1);
                    break;
                case ProductEntry.MOUNT_EQUATORIAL:
                    mMountSpinner.setSelection(2);
                    break;
                case ProductEntry.MOUNT_AZIMUTHAL:
                    mMountSpinner.setSelection(3);
                    break;
                default:
                    mMountSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mProducerEditText.setText("");
        mPriceEditText.setText("");
        mImageSpinner.setSelection(0);  // Select "None" image type
        mLevelSpinner.setSelection(0); // Select "None" user level
        mDesignSpinner.setSelection(0); // Select "None" optical design
        mDiameterEditText.setText("");
        mLengthEditText.setText("");
        mMountSpinner.setSelection(0); // Select "None" mount type
        mContactNumberEditText.setText("");
        mQuantityTextView.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
