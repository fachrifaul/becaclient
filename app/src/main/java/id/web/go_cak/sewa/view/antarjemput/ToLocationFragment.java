package id.web.go_cak.sewa.view.antarjemput;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import id.web.go_cak.sewa.R;


public class ToLocationFragment extends Fragment implements OnItemClickListener, LocationListener, OnMarkerDragListener, OnMapLongClickListener {

    //MapView mapView;
    GoogleMap mGoogleMap;
    boolean markerClicked;
    public static final String SHARED_PREFERENCES_NAME = "radi.latlong";
    public static final String FROMLATITUDE = "fromlatitude";
    public static final String FROMLONGITUDE = "fromlongitude";
    public static final String FROMFINAL = "fromfinal";
    public static final String TOLATITUDE = "tolatitude";
    public static final String TOLONGITUDE = "tolongitude";
    public static final String TOFINAL = "tofinal";

    // Bikin variable Default untuk Posisiku bila GPS mati atau susah sinyal, Latlong Universitas siliwangi
    double mLatitude = 3.555109;
    double mLongitude = 98.642043;

    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    //------------ make your specific key ------------
    //private static final String API_KEY = "AIzaSyBRuQwOy4jhyc3Xq8mrVEBZgK32bXdeIx8";

    private static final String API_KEY = "AIzaSyCptZZ0pbEkqNtBV4xURuaYNtji0oU9CO0";
    //private String API_KEY = getResources().getString(R.string.google_maps_api);

    public View view;
    private TextView titleToolbar;
    private Toolbar toolbar;
    private FragmentActivity myContext;
    private String finalAddress;

    HashMap<String, String> mMarkerPlaceLink = new HashMap<String, String>();

    /*public DeportiveActivity(TextView titleToolbar, Toolbar sendToolbar) {
        this.titleToolbar = titleToolbar;
        this.toolbar = sendToolbar;
    }

    public static Fragment newInstance(TextView titleToolbar, Toolbar sendToolbar) {
        return new DeportiveActivity(titleToolbar, sendToolbar);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_from_location, container, false);

        AutoCompleteTextView autoCompView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);

        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.item_location));
        autoCompView.setOnItemClickListener(this);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getBaseContext());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();

        } else {

            //SupportMapFragment fragment = ( SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
            //mGoogleMap = ((SupportMapFragment) getChildFragmentManager().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mGoogleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            // Getting Google Map
            //mGoogleMap = fragment.getMap();

            // Enabling MyLocation in Google Map
            //mGoogleMap.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
            if (locationManager != null) {
                // Creating a criteria object to retrieve provider
                Criteria criteria = new Criteria();
                // Getting the name of the best provider
                String provider = locationManager.getBestProvider(criteria, true);
                // Getting Current Location From GPS
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    onLocationChanged(location);
                }

                locationManager.requestLocationUpdates(provider, 20000, 0, this);

                mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker arg0) {
                        //kirim lititude

                        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, getActivity().MODE_WORLD_READABLE);
                        Editor editor = prefs.edit();
                        editor.putFloat(TOLATITUDE, (float) mLatitude);
                        editor.putFloat(TOLONGITUDE, (float) mLongitude);
                        editor.putString(TOFINAL,finalAddress);
                        editor.commit();

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                       // Fragment fragment = Transport.newInstance(titleToolbar, toolbar);
                        Fragment fragment = new TransportFragment();
                        fragmentTransaction.replace(R.id.fragment, fragment, "Transport");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });

            }

            /*StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location="+mLatitude+","+mLongitude);

            sb.append("&radius=500000");
            sb.append("&types=hospital");
            sb.append("&sensor=true");
            sb.append("&key=AIzaSyBRuQwOy4jhyc3Xq8mrVEBZgK32bXdeIx8");

            PlacesTask placesTask = new PlacesTask();
            placesTask.execute(sb.toString());*/

        }

        return view;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String location = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(getActivity(), location, Toast.LENGTH_SHORT).show();

        LatLng address = getLocationFromAddress(getActivity(), location);
        //mMap.addMarker(new MarkerOptions().position(address).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(address));

        LatLng latLng = address;
        mGoogleMap.clear();

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        MarkerOptions mp = new MarkerOptions().draggable(true);

        mp.position(address);

        mp.title("Klik Disini Jika Lokasi Benar");
        mp.snippet("Geser Pin Bila Tidak Tepat\natau Input Pencarian diatas").icon(BitmapDescriptorFactory.fromResource(R.drawable.akudisini));

        mGoogleMap.addMarker(mp).showInfoWindow();

        // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
       /* mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latLng.getLatitude(), location.getLongitude()), 16));*/
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(address));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(address.latitude,address.longitude),16));

        mLatitude = address.latitude;
        mLongitude = address.longitude;
        finalAddress = location;

        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        //listener drag
        mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setOnMarkerDragListener(this);
        // end drag

    }

    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&sensor=false");
            sb.append("&language=id");
            sb.append("&components=country:id");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);


        //get Address coy

        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(mLatitude, mLongitude, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            finalAddress = "";
            for (int i = 0; i < maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                /*String city = " Kota "+address.get(0).getSubAdminArea();
                builder.append(city);*/
                /*String country = "Negara "+address.get(0).getSubLocality();
                builder.append(country);
                String postalCode = "Kode Pos"+address.get(0).getLocality();
                builder.append(postalCode);
                String knownName = " Liannya "+address.get(0).getAdminArea();
                builder.append(knownName);*/
                builder.append(" ");
            }
            finalAddress = builder.toString();
            //Toast.makeText(getActivity(), finalAddress, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
        } catch (NullPointerException e) {
            finalAddress = "";
            //Toast.makeText(getActivity(), "LingLung", Toast.LENGTH_LONG).show();
        }

        //end

        mGoogleMap.clear();

        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        MarkerOptions mp = new MarkerOptions().draggable(true);

        mp.position(new LatLng(mLatitude, mLongitude));

        mp.title("Klik Disini Jika Tujuan Benar");
        mp.snippet("Geser Pin Bila Tidak Tepat\natau Input Pencarian diatas");

        mGoogleMap.addMarker(mp).showInfoWindow();

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));


        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        //listener drag
        mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setOnMarkerDragListener(this);
        // end drag


    }

    @Override
    public void onMapLongClick(LatLng point) {
        //Toast.makeText(getActivity(), "New marker added@" + point.toString(), Toast.LENGTH_LONG).show();

        /*mGoogleMap.addMarker(new MarkerOptions()
                .position(point)
                .draggable(true));/*/

        markerClicked = false;
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //Toast.makeText(getActivity(),"Marker " + marker.getId() + " Drag@" + marker.getPosition(),Toast.LENGTH_LONG).show();
        //tvLocInfo.setText("Marker " + marker.getId() + " Drag@" + marker.getPosition());
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Toast.makeText(getActivity(), "Finally coordinat Marker " + marker.getPosition() + " DragEnd", Toast.LENGTH_LONG).show();
        mLatitude = marker.getPosition().latitude;
        mLongitude = marker.getPosition().longitude;

        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(mLatitude, mLongitude, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i = 0; i < maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                String city = "Kota "+address.get(0).getSubAdminArea();
                builder.append(city);
                builder.append(" ");
            }
            finalAddress = builder.toString();
            //Toast.makeText(getActivity(), finalAddress, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
        } catch (NullPointerException e) {
            finalAddress = "";
            //Toast.makeText(getActivity(), "LingLung", Toast.LENGTH_LONG).show();
        }

        //tvLocInfo.setText("Marker " + marker.getId() + " DragEnd");
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        //Toast.makeText(getActivity(),"Marker " + marker.getId() + " DragStart",Toast.LENGTH_LONG).show();
        //tvLocInfo.setText("Marker " + marker.getId() + " DragStart");

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


}