package id.web.go_cak.sewa.view.order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.ButterKnife;
import id.web.go_cak.sewa.R;
import id.web.go_cak.sewa.session.UserSessionManager;
import id.web.go_cak.sewa.util.ApiConstant;
import id.web.go_cak.sewa.view.main.MainActivity;

public class OrderActivity extends AppCompatActivity {

    private static final String REGISTER_URL = "http://gocak.co.id/operator/index.php/Welcome/jarak";
    private static final String TAG_USERS = "lokasi";
    private static final String SAVE_URL = "http://gocak.co.id/operator/index.php/Welcome/jemput";
    private static final String TAG_SAVE = "jemput";

    // contacts JSONArray
    JSONArray LOKASI = null;
    JSONArray SAVE = null;

    private Toolbar toolbar;
    private TextView mTitle;

    private String mLatitude, mLongitude, mLatitudeDes, mLongitudeDes;
    private String finalAddress, finalAddressDes, finaldetail, meter, detik, jpayment, jshowpayment, jdistance;

    private TextView titleToolbar, TeAddress, distance, payment;
    private TextView TeLokasi, TeTujuan;
    private TextView btnOrder;

    private UserSessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Session class instance
        sessionManager = new UserSessionManager(this);

        TeLokasi = (TextView) findViewById(R.id.dari_lokasi_edittext);
        TeTujuan = (TextView) findViewById(R.id.tujuan_lokasi_edittext);
        TeAddress = (TextView) findViewById(R.id.address);
        distance = (TextView) findViewById(R.id.distance);
        payment = (TextView) findViewById(R.id.payment);
        btnOrder = (TextView) findViewById(R.id.btnOrder);

        mLatitude = getIntent().getStringExtra(ApiConstant.FROMLATITUDE);
        mLongitude = getIntent().getStringExtra(ApiConstant.FROMLONGITUDE);
        mLatitudeDes = getIntent().getStringExtra(ApiConstant.TOLATITUDE);
        mLongitudeDes = getIntent().getStringExtra(ApiConstant.TOLONGITUDE);
        finalAddress = getIntent().getStringExtra(ApiConstant.FROMADDRESS);
        finalAddressDes = getIntent().getStringExtra(ApiConstant.TOADDRESS);
        finaldetail = getIntent().getStringExtra(ApiConstant.DETAIL);

        TeLokasi.setText(finalAddress);
        TeTujuan.setText(finalAddressDes);
        TeAddress.setText(finaldetail);

        getDistance();

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responorder();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
    }

    public void responorder() {

        String urlSuffix = "?ID=" + sessionManager.getIdUser() + "&userName=" + sessionManager.getTelp() + "&addressdetail=" + finaldetail + "&payment=" + jpayment + "&distance=" + meter + "&latfrom=" + mLatitude.toString() + "&latto=" + mLatitudeDes.toString() + "&longfrom=" + mLongitude.toString() + "&longto=" + mLongitudeDes.toString() + "&distance=" + distance.getText().toString();

        class RespondeSave extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OrderActivity.this, "Tunggu Beberapa saat", "Pengiriman data ke Server", true, true);
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
                        for (int i = 0; i < SAVE.length(); i++) {
                            JSONObject c = SAVE.getJSONObject(i);

                            if (c.getString("code").equals("1")) {
                                showDialog();
//                                //Toast.makeText(OrderActivity.this,"Berhasil Simpan",Toast.LENGTH_LONG).show();
//                                FragmentManager fragmentManager3 = getFragmentManager();
//                                FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
//                                //Fragment fragment3 = NotifikasiDone.newInstance(titleToolbar, toolbar);
//                                Fragment fragment3 = new FinishOrderFragment();
//                                fragmentTransaction3.replace(R.id.fragment, fragment3, "Notifikasi next");
//                                fragmentTransaction3.addToBackStack(null);
//                                fragmentTransaction3.commit();
                            } else {
                                Toast.makeText(OrderActivity.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(OrderActivity.this, "Koneksi Error " + e, Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(OrderActivity.this, "Koneksi Error " + s, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(SAVE_URL + s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    return result;
                } catch (Exception e) {
                    return null;
                }
            }

        }

        RespondeSave ru = new RespondeSave();
        ru.execute(urlSuffix);

    }

    public void getDistance() {

        String urlSuffix = "?ol1=" + mLatitude + "&ol2=" + mLongitude + "&dl1=" + mLatitudeDes + "&dl2=" + mLongitudeDes;
        Log.v("urlSuffixurlSuffix", urlSuffix);
        class SeacrhDistance extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OrderActivity.this, "Tunggu Beberapa saat", "Kalkulasi jarak dan Pembayaran", true, true);
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
                        for (int i = 0; i < LOKASI.length(); i++) {
                            JSONObject c = LOKASI.getJSONObject(i);

                            if (c.getString("code").equals("1")) {
                                jdistance = c.getString("distance");
                                meter = c.getString("distance_value");
                                //String jtime = c.getString("time");
                                detik = c.getString("time_value");
                                jpayment = c.getString("payment");
                                jshowpayment = c.getString("showpayment");
                                distance.setText(jdistance);
                                payment.setText("Rp. " + jshowpayment);

                            } else {
                                Toast.makeText(OrderActivity.this, "Terjadi Kesalahan Login, Periksa Inputan Anda", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(OrderActivity.this, "Koneksi Error " + e, Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(OrderActivity.this, "Koneksi Error " + s, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(REGISTER_URL + s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    return result;
                } catch (Exception e) {
                    return null;
                }
            }

        }

        SeacrhDistance ru = new SeacrhDistance();
        ru.execute(urlSuffix);

    }

    private void showDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Order Selesai")
                .customView(R.layout.dialog_done, true)
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .autoDismiss(false)
                .build();
        dialog.show();
    }
}
