package id.web.go_cak.sewa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import id.web.go_cak.sewa.UserSessionManager;

public class InfoActivity extends Fragment  {

    public static final String LOG_TAG = InfoActivity.class.getName();

    public static final String SHARED_PREFERENCES_NAME = "radi.latlong";
    public static final String FROMLATITUDE = "fromlatitude";
    public static final String FROMLONGITUDE = "fromlongitude";
    public static final String FROMFINAL = "fromfinal";
    public static final String TOLATITUDE = "tolatitude";
    public static final String TOLONGITUDE = "tolongitude";
    public static final String TOFINAL = "tofinal";
    public static final String DETAIL = "detail";

    private static final String REGISTER_URL = "http://gocak.co.id/operator/index.php/Welcome/jarak";
    private static final String TAG_USERS = "lokasi";
    private static final String SAVE_URL = "http://gocak.co.id/operator/index.php/Welcome/jemput";
    private static final String TAG_SAVE = "jemput";

    // contacts JSONArray
    JSONArray LOKASI = null;
    JSONArray SAVE = null;

    private Toolbar toolbar;
    private TextView mTitle;
    public View view;

    private Double mLatitude ,mLongitude,mLatitudeDes ,mLongitudeDes;
    private String finalAddress,finalAddressDes,finaldetail,meter,detik,jpayment,jshowpayment,jdistance;

    private TextView titleToolbar, TeLokasi,TeTujuan,TeAddress,distance,payment;
    private Button btnOrder;

    private UserSessionManager sessionManager;

    /*public InfoActivity(TextView titleToolbar,Toolbar sendToolbar) {
        this.titleToolbar = titleToolbar;
        this.toolbar = sendToolbar;
    }

    public static Fragment newInstance(TextView titleToolbar,Toolbar sendToolbar) {
        return new InfoActivity(titleToolbar,sendToolbar);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_info, container, false);

        // Session class instance
        sessionManager = new UserSessionManager(getActivity());

        TeLokasi = (TextView) view.findViewById(R.id.TeLokasi);
        TeTujuan = (TextView) view.findViewById(R.id.TeTujuan);
        TeAddress  = (TextView) view.findViewById(R.id.address);
        distance = (TextView) view.findViewById(R.id.distance);
        payment = (TextView) view.findViewById(R.id.payment);
        btnOrder = (Button) view.findViewById(R.id.btnOrder);

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
        TeAddress.setText(finaldetail);

        getDistance();

        btnOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                responorder();

            }

        });


        return view;
    }

    public void responorder() {

        String urlSuffix = "?ID="+sessionManager.getIdUser()+"&userName="+sessionManager.getTelp()+"&addressdetail="+finaldetail+"&payment="+jpayment+"&distance="+meter+"&latfrom="+mLatitude.toString()+"&latto="+mLatitudeDes.toString()+"&longfrom="+mLongitude.toString()+"&longto="+mLongitudeDes.toString()+"&distance="+distance.getText().toString();

        class RespondeSave extends AsyncTask<String, Void, String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Tunggu Beberapa saat","Pengiriman data ke Server", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //warning.setText(s);
                Log.d("hasil", s);

                if (s != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        SAVE = jsonObj.getJSONArray(TAG_SAVE);
                        for(int i=0; i < SAVE.length(); i++) {
                            JSONObject c = SAVE.getJSONObject(i);

                            if (c.getString("code").equals("1")) {
                                //Toast.makeText(getActivity(),"Berhasil Simpan",Toast.LENGTH_LONG).show();
                                FragmentManager fragmentManager3 = getFragmentManager();
                                FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
                                //Fragment fragment3 = NotifikasiDone.newInstance(titleToolbar, toolbar);
                                Fragment fragment3 = new NotifikasiDone();
                                fragmentTransaction3.replace(R.id.fragment, fragment3, "Notifikasi next");
                                fragmentTransaction3.addToBackStack(null);
                                fragmentTransaction3.commit();
                            } else {
                                Toast.makeText(getActivity(),"Terjadi Kesalahan",Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),"Koneksi Error "+e,Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getActivity(),"Koneksi Error "+s,Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(SAVE_URL+s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    return result;
                }catch(Exception e){
                    return null;
                }
            }

        }

        RespondeSave ru = new RespondeSave();
        ru.execute(urlSuffix);

    }

    public void getDistance() {

        String urlSuffix = "?ol1="+mLatitude+"&ol2="+mLongitude+"&dl1="+mLatitudeDes+"&dl2="+mLongitudeDes;
        Log.v("urlSuffixurlSuffix", urlSuffix);
        class SeacrhDistance extends AsyncTask<String, Void, String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Tunggu Beberapa saat","Kalkulasi jarak dan Pembayaran", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //warning.setText(s);
                Log.d("hasil", s);

                if (s != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        LOKASI = jsonObj.getJSONArray(TAG_USERS);
                        for(int i=0; i < LOKASI.length(); i++) {
                            JSONObject c = LOKASI.getJSONObject(i);

                            if (c.getString("code").equals("1")) {
                                jdistance = c.getString("distance");
                                meter = c.getString("distance_value");
                                //String jtime = c.getString("time");
                                detik = c.getString("time_value");
                                jpayment = c.getString("payment");
                                jshowpayment = c.getString("showpayment");
                                distance.setText(jdistance);
                                payment.setText("Rp. "+jshowpayment);

                            } else {
                                Toast.makeText(getActivity(),"Terjadi Kesalahan Login, Periksa Inputan Anda",Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),"Koneksi Error "+e,Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getActivity(),"Koneksi Error "+s,Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(REGISTER_URL+s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    return result;
                }catch(Exception e){
                    return null;
                }
            }

        }

        SeacrhDistance ru = new SeacrhDistance();
        ru.execute(urlSuffix);

    }

}
