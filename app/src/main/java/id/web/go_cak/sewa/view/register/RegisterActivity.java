package id.web.go_cak.sewa.view.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import id.web.go_cak.sewa.MainActivity;
import id.web.go_cak.sewa.R;
import id.web.go_cak.sewa.UserSessionManager;

public class RegisterActivity extends AppCompatActivity {
    private EditText txttelp,txtpassword,txtnamalengkap;
    TextView btdaftar;
    private static final String REGISTER_URL = "http://gocak.co.id/operator/index.php/Welcome/registerpelanggan";
    //{"users":{"ID":"5","nama":"Radi","email":"1Radi@radi.com","telp":"1081392380333"}}
    private static final String TAG_USERS = "users";

    // contacts JSONArray
    JSONArray USERS = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;

    // Session Manager Class
    private UserSessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txttelp = (EditText) findViewById(R.id.telp);
        txtnamalengkap = (EditText) findViewById(R.id.namalengkap);
        txtpassword = (EditText) findViewById(R.id.password);
        btdaftar = (TextView) findViewById(R.id.btdaftar);
        btdaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesdaftar();
            }
        });
    }

    private void cekDaftar(String telp,String password, String namalengkap) {

        String urlSuffix = "?telp="+telp+"&password="+password+"&namalengkap="+namalengkap;

        class RegisterUser extends AsyncTask<String, Void, String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this, "Tunggu Beberapa saat","Registrasi sedang di proses", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //warning.setText(s);

                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        USERS = jsonObj.getJSONArray(TAG_USERS);
                        for(int i=0; i < USERS.length(); i++) {
                            JSONObject c = USERS.getJSONObject(i);

                            if (c.getString("code").equals("12")) {
                                Toast.makeText(getApplicationContext(), "Registrasi berhasil, silahkan login dengan akun Anda.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Maaf, Terjadi Kesalahan Daftar.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

        RegisterUser ru = new RegisterUser();
        ru.execute(urlSuffix);

    }

    public void prosesdaftar() {


        //if(password1.getText().equals(password2.getText())) {

        String telp = txttelp.getText().toString().trim().toLowerCase();
        String password = txtpassword.getText().toString().trim();
        String namalengkap = txtnamalengkap.getText().toString().trim();

        cekDaftar(telp, password, namalengkap);

        /*} else {
            warning.setVisibility(View.VISIBLE);
            warning.setText("Pengisian Kata sandi tidak sama");
        }*/

    }

}
