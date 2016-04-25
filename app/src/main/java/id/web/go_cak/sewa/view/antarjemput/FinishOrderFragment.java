package id.web.go_cak.sewa.view.antarjemput;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.web.go_cak.sewa.R;

public class FinishOrderFragment extends Fragment {

    public static final String SHARED_PREFERENCES_NAME = "radi.latlong";
    public static final String FROMLATITUDE = "fromlatitude";
    public static final String FROMLONGITUDE = "fromlongitude";
    public static final String FROMFINAL = "fromfinal";
    public static final String TOLATITUDE = "tolatitude";
    public static final String TOLONGITUDE = "tolongitude";
    public static final String TOFINAL = "tofinal";
    public static final String DETAIL = "detail";

    private TextView titleToolbar;
    private Toolbar toolbar;
    public View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_finish_order, container, false);

        //Menghapus Cache preferences
        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, getActivity().MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        return view;

    }

}
