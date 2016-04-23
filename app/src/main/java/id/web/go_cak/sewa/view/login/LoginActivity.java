package id.web.go_cak.sewa.view.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import id.web.go_cak.sewa.view.register.RegisterActivity;
//import radi.com.gocakdriver.fragment.DashboardFragment;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnLogin;
    private EditText txtUsername,txtPassword;
    private TextView warning,daftartext;

    private TextView titleToolbar;
    private String mTitle;
    private NavigationView navigationView;

    private static final String REGISTER_URL = "http://gocak.co.id/operator/index.php/Welcome/loginPelanggan";
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
        setContentView(R.layout.activity_login);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        sessionManager = new UserSessionManager(this);

        btnLogin    = (Button) findViewById(R.id.btnLogin);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        warning     = (TextView) findViewById(R.id.warning);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProcess();
            }
        });
        daftartext = (TextView) findViewById(R.id.daftartext);
        daftartext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(i);
            }
        });

    }

    private void cekLogin(String name,String password) {

        warning.setVisibility(View.INVISIBLE);

        String urlSuffix = "?name="+name+"&password="+password;

        class RegisterUser extends AsyncTask<String, Void, String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                warning.setVisibility(View.VISIBLE);
                loading = ProgressDialog.show(LoginActivity.this, "Tunggu Beberapa saat","Pengecekan database", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                warning.setVisibility(View.VISIBLE);
                //warning.setText(s);
                Log.d("hasil", s);

                if (s != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        USERS = jsonObj.getJSONArray(TAG_USERS);
                        for(int i=0; i < USERS.length(); i++) {
                            JSONObject c = USERS.getJSONObject(i);

                            if (c.getString("code").equals("12")) {
                                String cID = c.getString("ID");
                                String cname = c.getString("nama");
                                String cemail = c.getString("email");
                                String ctelp = c.getString("telp");
                               // warning.setText(c.getString("ID") + " (" + c.getString("telp") + " )"+c.getString("nama") + " (" + c.getString("email") + " )");
                               sessionManager.createUserIdSession(true,cID, cname, ctelp, cemail);
                               //sessionManager.createUserIdSession(true, "1", "radi@radi.com");


                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                warning.setText("Terjadi Kesalahan Login, Periksa Inputan Anda");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        warning.setText("Login Gagal "+e);
                    }

                } else {
                    warning.setText(s);
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

    public void loginProcess() {

        warning.setVisibility(View.INVISIBLE);

        //if(password1.getText().equals(password2.getText())) {

        String name = txtUsername.getText().toString().trim().toLowerCase();
        String password = txtPassword.getText().toString().trim().toLowerCase();

        cekLogin(name, password);

        /*} else {
            warning.setVisibility(View.VISIBLE);
            warning.setText("Pengisian Kata sandi tidak sama");
        }*/

    }

    @Override
    public void onBackPressed(){
        System.exit(0);
        finish();
    }


}
