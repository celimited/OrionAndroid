package com.orion.application;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Channel;
import com.orion.entities.Image;
import com.orion.entities.Market;
import com.orion.entities.Outlet;
import com.orion.entities.Section;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class NewOutletActivity extends AppCompatActivity
        implements Spinner.OnItemSelectedListener, View.OnClickListener {

    public final static String KEY_EDITABLE_OUTLET = "editable_outlet";
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private Location location;

    private LocationManager locationManager;
    private LocationListener locationListener;


    private ArrayList marketListArray = new ArrayList();
    private ArrayList channelListArray = new ArrayList();
    private String TAG = "Trace";
    private Button btnSave;

    private Outlet currentOutlet;
    private Section marketList;
    private ArrayList<Channel> channelList;
    private Spinner spnRout;
    private Spinner spnChannelId;
    private AlertDialog imageSourceDialog;
    private Button takeImage;
    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private EditText description;
    private EditText owner;
    private EditText address;
    private EditText contact;
    private String roudId;
    private String channelId;
    private String imagePath = "";
    private double latitude = 0f;
    private double longitude = 0f;
    private Button getLocation;
    private int selectedMarketPosition = -1;
    private TextView tvLocation;
    private String insertedOutletID = null;
    private ArrayList<Image> imageObj = null;
    private boolean EditeMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_outlet);

        if (getIntent().hasExtra(KEY_EDITABLE_OUTLET)) {
            currentOutlet = (Outlet) getIntent().getSerializableExtra(KEY_EDITABLE_OUTLET);
            if (currentOutlet != null) {
                EditeMode = true;
            }
            imageObj = DatabaseQueryUtil.getOutletImageList(NewOutletActivity.this, currentOutlet.outletId);
        } else {
            currentOutlet = new Outlet();
        }

        marketList = DatabaseQueryUtil.getSection(this);
        for (int i = 0; i < marketList.list.size(); i++) {
            Market market = marketList.list.get(i);
            marketListArray.add(market.title);
            if (currentOutlet.routeID == market.routeId) {
                selectedMarketPosition = i;
            }
        }

        channelList = DatabaseQueryUtil.getChannelList(this);
        for (int i = 0; i < channelList.size(); i++) {
            channelListArray.add(channelList.get(i).name);
        }

        ArrayAdapter<String> marketListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, marketListArray);
        spnRout = (Spinner) findViewById(R.id.spnRout);
        spnRout.setAdapter(marketListAdapter);
        spnRout.setOnItemSelectedListener(this);
        if (selectedMarketPosition >= 0) {
            spnRout.setSelection(selectedMarketPosition);
        }


        ArrayAdapter<String> channelListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, channelListArray);
        spnChannelId = (Spinner) findViewById(R.id.spnChannelId);
        spnChannelId.setAdapter(channelListAdapter);
        spnChannelId.setOnItemSelectedListener(this);

        description = (EditText) findViewById(R.id.description);
        owner = (EditText) findViewById(R.id.owner);
        address = (EditText) findViewById(R.id.address);
        contact = (EditText) findViewById(R.id.contact);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        takeImage = (Button) findViewById(R.id.takeImage);
        takeImage.setOnClickListener(this);
        getLocation = (Button) findViewById(R.id.getLocation);
        getLocation.setOnClickListener(this);
        tvLocation = (TextView) findViewById(R.id.tvLocation);

        mImageView = (ImageView) findViewById(R.id.imageOutlet);

        createImagePickDialog();
        initializeLocation();
        if (getIntent().hasExtra(KEY_EDITABLE_OUTLET)) {
            initializeForEditMode();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) return;

        Bitmap bitmap = null;
        //mImageCaptureUri = data.getData();


        if (requestCode == PICK_FROM_FILE) {
            imagePath = getRealPathFromURI(mImageCaptureUri); //from Gallery

            if (imagePath == null) {
                imagePath = mImageCaptureUri.getPath(); //from File Manager
            }

            if (imagePath != null) {
                bitmap = BitmapFactory.decodeFile(imagePath);
            }

        } else {
            imagePath = mImageCaptureUri.getPath();
            bitmap = BitmapFactory.decodeFile(imagePath);
        }

        mImageView.setImageBitmap(bitmap);

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "Selected: " + i);
        if (view.getId() == R.id.spnRout) {
            roudId = marketList.list.get(i).routeId;
        } else if (view.getId() == R.id.spnChannelId) {
            channelId = channelList.get(i).channelID;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(TAG, "Nothing Selected");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSave) {

            currentOutlet.description = description.getText().toString();
            currentOutlet.channelId = channelId;
            currentOutlet.owner = owner.getText().toString();
            currentOutlet.address = address.getText().toString();
            currentOutlet.contactNo = contact.getText().toString();
            currentOutlet.visited = 0;
            currentOutlet.imageUrls = new String[]{imagePath};
            currentOutlet.outletlatitude = Double.toString(latitude);
            currentOutlet.outletlongitude = Double.toString(longitude);

            //Validation code : Added By Zakir on 6.9.16

            if (!validate()) {
                return;
            }
            //Zakir changed this method for retrieving inserted outlet ID bcoz this outletID is needed for Image saving
            if (!EditeMode) {
                insertedOutletID = DatabaseQueryUtil.createNewOutlet(NewOutletActivity.this, currentOutlet);
            }
            if (EditeMode) {
                insertedOutletID = DatabaseQueryUtil.updateNewOutlet(NewOutletActivity.this, currentOutlet);
            }
            // TODO save new outlet image : Written by Zakir on 6.9.16
            if (insertedOutletID != null) {
                Image image = new Image();
                image.imageType = Image.IMAGE_TYPE_OUTLET_FRONT;
                image.imageUrl = imagePath;
                image.outletId = insertedOutletID;

                if (!EditeMode) {
                    DatabaseQueryUtil.addNewImage(NewOutletActivity.this, new Image[]{image});
                }
                if (EditeMode) {
                    DatabaseQueryUtil.updateNewImage(NewOutletActivity.this, image);
                }
            }

            Snackbar.make(getCurrentFocus(), R.string.save_database_success, Snackbar.LENGTH_SHORT);
                    /*
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
                    */

            setResult(RESULT_OK);
            finish();
        } else if (view.getId() == R.id.takeImage) {

            // check for storage permission
            // check the permission
            int hasStoragePermission = ContextCompat.checkSelfPermission(NewOutletActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(NewOutletActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    Snackbar.make(takeImage, R.string.prompt_permission_storage, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.action_settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showPermissionsSettings();
                                }
                            })
                            .show();
                    return;

                }
                ActivityCompat.requestPermissions(NewOutletActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }

            imageSourceDialog.show();
        } else if (view.getId() == R.id.getLocation) {
            // prompt for wait if location not done
            if (latitude == 0 || longitude == 0) {
                Snackbar.make(view, R.string.please_wait, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(view, R.string.location_done, Snackbar.LENGTH_SHORT).show();
            }

        }
    }

    public boolean validate() {
        boolean valid = true;

        String name = description.getText().toString();
        String outletOwner = owner.getText().toString();
        String outletAddress = address.getText().toString();

        if (name.isEmpty()) {
            description.setError(Html.fromHtml("<font color='red'>Enter Outlet Name</font>"));
            valid = false;
        }

        if (outletOwner.isEmpty()) {
            owner.setError(Html.fromHtml("<font color='red'>Enter Outlet Owner Name</font>"));
            valid = false;
        }
        if (outletAddress.isEmpty()) {
            address.setError(Html.fromHtml("<font color='red'>Enter Address</font>"));
            valid = false;
        }

        return valid;
    }

    private void createImagePickDialog() {
        final String[] items = new String[]{"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(),
                            "tmp_image_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);

                    try {
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        });

        imageSourceDialog = builder.create();

    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    // Handle permission for Android Marshmallow devices

    // check for multiple permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    imageSourceDialog.show();
                } else {
                    // Permission Denied
//                    Toast.makeText(NewOutletActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showPermissionsSettings() {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }

    private void showGPSSettings() {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }


    // location methods
    private void initializeLocation() {
        Log.d(TAG, "initializeLocation()");
        if (null == (locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE))) {
            Toast.makeText(NewOutletActivity.this, R.string.gps_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        int hasLocationPermission = ContextCompat.checkSelfPermission(NewOutletActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            promptForPermission();
            return;
        }

        if (!isLocationEnabled(this)) {
            Toast.makeText(NewOutletActivity.this, R.string.enable_gps, Toast.LENGTH_SHORT).show();
            showGPSSettings();
            return;
        }

        location = bestLastKnownLocation(500.0f, 360000);

        if (null != location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvLocation.setText(latitude + ", " + longitude);
        }

        locationListener = new LocationListener() {
            public void onLocationChanged(Location newLocation) {
                if (null == location || newLocation.getAccuracy() < location.getAccuracy()) {

                    location = newLocation;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    tvLocation.setText(latitude + ", " + longitude);

                    if (location.getAccuracy() < 25f) {
                        locationManager.removeUpdates(locationListener);
                    }

                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    private void initializeForEditMode() {
        description.setText(currentOutlet.description);
        channelId = currentOutlet.channelId;
        owner.setText(currentOutlet.owner);
        address.setText(currentOutlet.address);
        contact.setText(currentOutlet.contactNo);

        imagePath = imageObj.get(0).imageUrl;
        mImageView.setImageBitmap(BitmapFactory.decodeFile(imageObj.get(0).imageUrl));
        // latitude = Double.parseDouble(currentOutlet.outletlatitude);
        //longitude = Double.parseDouble(currentOutlet.outletlongitude);
    }

    private boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


        int hasLocationPermission = ContextCompat.checkSelfPermission(NewOutletActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            promptForPermission();
            return;
        }

        if (null == locationManager) return;

        if (null == location
                || location.getAccuracy() > 500.0f
                || location.getTime() < System.currentTimeMillis() - 120000) {

            // Register for network location updates
            if (null != locationManager
                    .getProvider(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 10000,
                        10.0f, locationListener);
            }


            if (null != locationManager.getProvider(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 10000,
                        10.0f, locationListener);
            }

            Executors.newScheduledThreadPool(1).schedule(new Runnable() {

                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                    }

                    locationManager.removeUpdates(locationListener);

                }
            }, 30000, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }

    }

    private Location bestLastKnownLocation(float minAccuracy, long maxAge) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestAge = Long.MIN_VALUE;

        List<String> matchingProviders = locationManager.getAllProviders();

        for (String provider : matchingProviders) {

            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if (accuracy < bestAccuracy) {
                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestAge = time;
                }
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy
                || (System.currentTimeMillis() - bestAge) > maxAge) {
            return null;
        } else {
            return bestResult;
        }
    }

    private void promptForPermission() {
        Log.d(TAG, "promptForPermission() 1");
        if (!ActivityCompat.shouldShowRequestPermissionRationale(NewOutletActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            Snackbar.make(getLocation, R.string.prompt_permission_location, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showPermissionsSettings();
                        }
                    }).show();
            return;
        }
        Log.d(TAG, "promptForPermission() 2");
        ActivityCompat.requestPermissions(NewOutletActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE_ASK_PERMISSIONS);
    }
}
