package id.web.go_cak.sewa;

import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.web.go_cak.sewa.R;

/**
 * Created by Radi on 12/11/15.
 */
public class Transport extends Fragment implements OnClickListener {

    public static final String LOG_TAG = Transport.class.getName();

    /*@Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.titleApp)
    TextView mTitle;*/

    public static final String SHARED_PREFERENCES_NAME = "radi.latlong";
    public static final String FROMLATITUDE = "fromlatitude";
    public static final String FROMLONGITUDE = "fromlongitude";
    public static final String FROMFINAL = "fromfinal";
    public static final String TOLATITUDE = "tolatitude";
    public static final String TOLONGITUDE = "tolongitude";
    public static final String TOFINAL = "tofinal";
    public static final String DETAIL = "detail";

    private Toolbar toolbar;
    private Button buttonNext;
    public View view;

    private TextView titleToolbar, TeLokasi,TeTujuan,TeLokasiDetail;
    private NavigationView navigationView;

    //private static final String REGISTER_URL = "http://192.168.43.49/servers/php/api/register.php";

    private Double mLatitude ,mLongitude,mLatitudeDes ,mLongitudeDes;
    private String finalAddress,finalAddressDes,finaldetail;

   /* public Transport(TextView titleToolbar,Toolbar sendToolbar) {
        this.titleToolbar = titleToolbar;
        this.toolbar = sendToolbar;
    }

    public static Fragment newInstance(TextView titleToolbar,Toolbar sendToolbar) {
        return new Transport(titleToolbar,sendToolbar);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.transport, container, false);

        TeLokasi = (TextView) view.findViewById(R.id.TeLokasi);
        TeTujuan = (TextView) view.findViewById(R.id.TeTujuan);
        TeLokasiDetail = (TextView) view.findViewById(R.id.TeLokasiDetail);
        buttonNext = (Button) view.findViewById(R.id.buttonNext);

        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, getActivity().MODE_WORLD_READABLE);
        mLatitude  = (double)prefs.getFloat(FROMLATITUDE, 0);
        mLongitude = (double)prefs.getFloat(FROMLONGITUDE, 0);
        mLatitudeDes  = (double)prefs.getFloat(TOLATITUDE, 0);
        mLongitudeDes = (double)prefs.getFloat(TOLONGITUDE, 0);
        finalAddress  = prefs.getString(FROMFINAL,"");
        finalAddressDes = prefs.getString(TOFINAL,"");
        finaldetail = prefs.getString(DETAIL,"");
        TeLokasi.setText(finalAddress);
        TeTujuan.setText(finalAddressDes);
        TeLokasiDetail.setText(DETAIL);

        /*if(getArguments() != null) {
            mLatitude = getArguments().getDouble("mLatitude");
            mLongitude = getArguments().getDouble("mLongitude");
            finalAddress = getArguments().getString("finalAddress");
            mLatitudeDes = getArguments().getDouble("mLatitudeDes");
            mLongitudeDes = getArguments().getDouble("mLongitudeDes");
            finalAddressDes = getArguments().getString("finalAddressDes");
            TeLokasi.setText(finalAddress);
            TeTujuan.setText(finalAddressDes);
        }*/


        TeLokasi.setOnClickListener(this);
        TeTujuan.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

        ButterKnife.bind(this, view);

        //titleToolbar.setText("Antar Jemput");
        //toolbar.setNavigationIcon(R.drawable.ic_back);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.TeLokasi:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Fragment fragment = GooglePlacesAutocompleteActivity.newInstance(titleToolbar,toolbar);
                Fragment fragment = new GooglePlacesAutocompleteActivity();
                fragmentTransaction.replace(R.id.fragment, fragment,"autocomplete");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.TeTujuan:
                FragmentManager fragmentManager2 = getFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                //Fragment fragment2 = DeportiveActivity.newInstance(titleToolbar,toolbar);
                Fragment fragment2 = new DeportiveActivity();
                fragmentTransaction2.replace(R.id.fragment, fragment2,"autocomplete2");
                fragmentTransaction2.addToBackStack(null);
                fragmentTransaction2.commit();
                break;
            case R.id.buttonNext:
                if(TeLokasiDetail.getText().toString().length() > 0 && TeTujuan.getText().toString().length() > 0 && TeLokasi.getText().toString().length() > 0) {

                    SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, getActivity().MODE_WORLD_READABLE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(DETAIL, TeLokasiDetail.getText().toString());
                    editor.commit();

                    FragmentManager fragmentManager3 = getFragmentManager();
                    FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
                    //Fragment fragment3 = InfoActivity.newInstance(titleToolbar, toolbar);
                    Fragment fragment3 = new InfoActivity();
                    fragmentTransaction3.replace(R.id.fragment, fragment3, "button next");
                    fragmentTransaction3.addToBackStack(null);
                    fragmentTransaction3.commit();

                } else {
                    Snackbar.make(view, "Inputan Tidak Boleh ada yang Kosong", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
        }

    }


}
