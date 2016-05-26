package id.web.gocak.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.web.gocak.R;
import id.web.gocak.model.OauthUser;
import id.web.gocak.model.User;
import id.web.gocak.service.ServiceRegister;

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.name_lengkap_edittext) EditText namLengkapEditText;
    @Bind(R.id.telp_edittext) EditText phoneEditText;
    @Bind(R.id.passwordEdittext) EditText passwordEditText;
    @Bind(R.id.nama_lengkap_tel) TextInputLayout namaLengkapTel;
    @Bind(R.id.phone_tel) TextInputLayout phoneTel;
    @Bind(R.id.password_tel) TextInputLayout passwordTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
    }

    @OnClick(R.id.daftar_textview)
    public void onClickDaftar() {
        String namalengkap = namLengkapEditText.getText().toString().trim();
        String telp = phoneEditText.getText().toString().trim().toLowerCase();
        String password = passwordEditText.getText().toString().trim();


        if (!validateName()) {
            return;
        }

        if (!validatePhone()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading ....");
        progressDialog.show();

        new ServiceRegister(this).fetchLogin(namalengkap, telp, password,
                new ServiceRegister.RegisterCallBack() {
                    @Override
                    public void onSuccess(OauthUser login) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        User user = login.users.get(0);
                        if (user != null) {
                            if(user.code.equals("12")){
                                showToast("Registrasi berhasil, silahkan login dengan akun Anda.");
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }else {
                                showSnackBar(getString(R.string.kesalahan_daftar));
                            }
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        showSnackBar(message);
                    }
                });

    }

    private void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean validateName() {
        if (TextUtils.isEmpty(namLengkapEditText.getText())) {
            namaLengkapTel.setError(getString(R.string.nama_lengkap_masih_kosong));
            return false;
        } else {
            namaLengkapTel.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePhone() {
        if (TextUtils.isEmpty(phoneEditText.getText())) {
            phoneTel.setError(getString(R.string.no_telp_masih_kosong));
            return false;
        } else {
            phoneTel.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordTel.setError(getString(R.string.password_masih_kosong));
            return false;
        } else if (passwordEditText.getText().toString().length() < 4) {
            passwordTel.setError(getString(R.string.password_minimal));
            return false;
        } else {
            passwordTel.setErrorEnabled(false);
            return true;
        }
    }

}
