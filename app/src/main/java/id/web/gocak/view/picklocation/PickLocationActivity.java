package id.web.gocak.view.picklocation;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.web.gocak.R;
import id.web.gocak.service.GPSTracker;
import id.web.gocak.util.ApiConstant;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PickLocationActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int RC_SMS_LOCATION = 122;
    private static final String LOG_TAG = "PickLocationActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    //TextView tvLocation;
    GoogleMap googleMap;

    @Bind(R.id.tv_location)  TextView tvLocation;
    @Bind(R.id.toolbar) Toolbar mToolbarView;
    @Bind(R.id.btnLocation) TextView btnLocation;
    @Bind(R.id.progress)  ProgressBar progress;

    private String address, lat, longi;
    private LatLng currentLocation;
    View.OnClickListener btnLocationClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (lat == null || longi == null || address == null) {
                Toast.makeText(PickLocationActivity.this, R.string.please_try_again, Toast.LENGTH_LONG).show();
            } else {
                Log.e("Result PICK MENU", currentLocation.latitude + " - " + currentLocation.longitude + " - ");

                Intent myIntent = new Intent();
                myIntent.putExtra(ApiConstant.latitude, lat);
                myIntent.putExtra(ApiConstant.longitude, longi);
                myIntent.putExtra(ApiConstant.address, address);
                setResult(RESULT_OK, myIntent);
                finish();
                overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
                Log.e("Result PICK Marker", lat + " - " + longi + " - " + address);
            }


        }
    };
    private Geocoder geocoder;
    GoogleMap.OnCameraChangeListener cameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(final CameraPosition cameraPosition) {

            LatLng centerLatLng = cameraPosition.target;
            Log.d("Center Latitude", String.valueOf(centerLatLng.latitude));
            Log.d("Center Longitude", String.valueOf(centerLatLng.longitude));

            currentLocation = centerLatLng;

            lat = String.valueOf(currentLocation.latitude);
            longi = String.valueOf(currentLocation.longitude);

            progress.setVisibility(View.VISIBLE);
            tvLocation.setVisibility(View.INVISIBLE);
            if (centerLatLng.latitude != 0 && centerLatLng.longitude != 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<Address> addresses = geocoder.getFromLocation(
                                    currentLocation.latitude, currentLocation.longitude, 1);

                            currentLocation = cameraPosition.target;

                            if (addresses != null) {
                                Address returnedAddress = addresses.get(0);
                                address = returnedAddress.getAddressLine(0);
                                progress.setVisibility(View.GONE);
                                tvLocation.setVisibility(View.VISIBLE);
                                tvLocation.setText(address);

                            } else {
                                tvLocation.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.GONE);
                                // tvLocation.setText("No Address returned!");
                                tvLocation.setText("Latitude:" + lat
                                        + ", Longitude:" + longi);
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            tvLocation.setText(" ");
                            tvLocation.setVisibility(View.VISIBLE);
                            Toast.makeText(PickLocationActivity.this, R.string.please_try_again, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(PickLocationActivity.this, R.string.please_try_again, Toast.LENGTH_LONG).show();
            }

        }

    };
    private AutoCompleteTextView mAutocompleteTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private String TAG = "PERMISSION";
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);

            Log.e("LATLON", place.getLatLng() + "");

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

            // Zoom in the Google Map
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    };
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnLocation.setOnClickListener(btnLocationClick);

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getBaseContext());

        geocoder = new Geocoder(this, Locale.ENGLISH);

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are
            // not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
                    requestCode);
            dialog.show();

        } else { // Google Play Services are available

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            // Getting a reference to the map
            googleMap = supportMapFragment.getMap();
//            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setOnCameraChangeListener(cameraChangeListener);

            // Getting LocationManager object from System Service
            // LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            askLocationTask();

            GPSTracker gps = new GPSTracker(this);
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();

                // Getting longitude of the current location
                double longitude = gps.getLongitude();

                // Creating a LatLng object for the current location
                LatLng latLng = new LatLng(latitude, longitude);

                // Showing the current location in Google Map
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                // Zoom in the Google Map
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            } else {
                gps.showSettingsAlert();
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(PickLocationActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
    }

    @Override
    public void onPermissionsGranted(List<String> perms) {
        Log.d(TAG, "permission granted");
    }

    @Override
    public void onPermissionsDenied(List<String> perms) {
        Log.d(TAG, "permission denied");
    }

    @AfterPermissionGranted(RC_SMS_LOCATION)
    private void askLocationTask() {
        if (EasyPermissions.hasPermissions(PickLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            startLocationUpdates();
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, "This app needs access to location",
                    RC_SMS_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }
}
