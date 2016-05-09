package id.web.gocak.view.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.web.gocak.R;
import id.web.gocak.model.OauthUser;
import id.web.gocak.model.User;
import id.web.gocak.service.ServiceLogin;
import id.web.gocak.session.UserSessionManager;
import id.web.gocak.view.main.MainActivity;
import id.web.gocak.view.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.phone_edittext) EditText phoneEditText;
    @Bind(R.id.password_edittext) EditText passwordEditText;
    @Bind(R.id.phone_tel) TextInputLayout phoneTel;
    @Bind(R.id.password_tel) TextInputLayout passwordTel;

    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sessionManager = new UserSessionManager(this);
    }

    @OnClick(R.id.login_textview)
    public void onClickLogin() {
        String name = phoneEditText.getText().toString().trim().toLowerCase();
        String password = passwordEditText.getText().toString().trim().toLowerCase();

        if (!validatePhone()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading ....");
        progressDialog.show();

        new ServiceLogin(this).fetchLogin(name, password,
                new ServiceLogin.LoginCallBack() {
                    @Override
                    public void onSuccess(OauthUser oauthUser) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        User user = oauthUser.users.get(0);
                        if (user != null) {
                            sessionManager.createUserIdSession(true, user.ID, user.nama, user.telp, user.email);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
                            finish();
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

    @OnClick(R.id.daftar_textview)
    public void onClickDaftar() {
        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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
//        } else if (passwordEditText.getText().toString().length() < 4) {
//            formErrorTextview.setText(getString(R.string.password_minimal));
//            return false;
        } else {
            passwordTel.setErrorEnabled(false);
            return true;
        }
    }

}
