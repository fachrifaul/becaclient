package id.web.gocak.view.picklocation;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.web.gocak.R;
import id.web.gocak.service.GPSTracker;
import id.web.gocak.util.ApiConstant;
import id.web.gocak.util.AppUtil;

public class PickLocationActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    @Bind(R.id.tv_location) TextView tvLocation;
    @Bind(R.id.progress) ProgressBar progress;
    @Bind(R.id.autoCompleteTextView) AutoCompleteTextView mAutocompleteTextView;

    private static final String LOG_TAG = "PickLocationActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(3.5945957,98.6705942), new LatLng(3.5945957,98.6705942));

    private String address, latitude, longitude;
    private LatLng currentLocation;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }

        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
                    requestCode);
            dialog.show();

        } else {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            googleMap = supportMapFragment.getMap();
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    LatLng centerLatLng = cameraPosition.target;
                    Log.d("Center Latitude", String.valueOf(centerLatLng.latitude));
                    Log.d("Center Longitude", String.valueOf(centerLatLng.longitude));

                    currentLocation = centerLatLng;

                    latitude = String.valueOf(currentLocation.latitude);
                    longitude = String.valueOf(currentLocation.longitude);

                    progress.setVisibility(View.VISIBLE);
                    tvLocation.setVisibility(View.INVISIBLE);
                    if (centerLatLng.latitude != 0 && centerLatLng.longitude != 0) {
                        new AddressBackground().execute();
                        currentLocation = cameraPosition.target;
                    } else {
                        Toast.makeText(PickLocationActivity.this, R.string.please_try_again, Toast.LENGTH_LONG).show();
                    }
                }
            });

            GPSTracker gps = new GPSTracker(this);
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Selected: " + item.description);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            Log.e(LOG_TAG, "Place query did not complete. Error: " +
                                    places.getStatus().toString());
                            return;
                        }
                        final Place place = places.get(0);

                        Log.e("LATLON", place.getLatLng() + "");

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                });
                Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
            }
        });
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

    }

    @OnClick(R.id.btnLocation)
    public void onClickButtonLocation() {
        if (latitude == null || longitude == null || address == null) {
            Toast.makeText(PickLocationActivity.this, R.string.please_try_again, Toast.LENGTH_LONG).show();
        } else {
            Log.e("Result PICK MENU", currentLocation.latitude + " - " + currentLocation.longitude + " - ");

            Intent myIntent = new Intent();
            myIntent.putExtra(ApiConstant.latitude, latitude);
            myIntent.putExtra(ApiConstant.longitude, longitude);
            myIntent.putExtra(ApiConstant.address, address);
            setResult(RESULT_OK, myIntent);
            finish();
            overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
            Log.e("Result PICK Marker", latitude + " - " + longitude + " - " + address);
        }
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public class AddressBackground extends AsyncTask<String, String, String> {
        String addressDetail;

        @Override
        protected String doInBackground(String... arg0) {
            addressDetail = AppUtil.getCompleteAddressString(PickLocationActivity.this,
                    Double.parseDouble(latitude), Double.parseDouble(longitude));
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.wtf("AddressBackground", "onPostExecute: " + addressDetail);

            if (addressDetail.equals("")) {
                new AddressBackground().execute();
            } else {
                progress.setVisibility(View.GONE);
                tvLocation.setVisibility(View.VISIBLE);
                tvLocation.setText(addressDetail);
                address = addressDetail;
            }


        }
    }
}
