package id.web.gocak.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.util.List;
import java.util.Locale;

/**
 * Created by fachrifebrian on 4/25/16.
 */
public class AppUtil {

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < (returnedAddress.getMaxAddressLineIndex() - 1); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(" ");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction ", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction ", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction ", "Canont get Address!");
        }
        return strAdd;
    }
}
