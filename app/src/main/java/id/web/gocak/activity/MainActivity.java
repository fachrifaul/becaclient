package id.web.gocak.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.web.gocak.R;
import id.web.gocak.adapter.MainAdapter;
import id.web.gocak.model.Jasa;
import id.web.gocak.session.UserSessionManager;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.version_text_view) TextView versionTextView;

    private UserSessionManager sessionManager;
    private MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new UserSessionManager(this);
        if (sessionManager.checkLogin()) {
            redirectLogin();
        } else {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            versionApp();

            setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            adapter = new MainAdapter(this);
            mRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Jasa item, int position) {
                    if (position != 1) {
                        Intent intent = new Intent(MainActivity.this, TransportActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
                    } else {
                        showSnackBar("Fitur ini akan segera launching");
                    }
                }
            });
        }

    }

    @OnClick(R.id.fab)
    public void onClickLogout() {
        sessionManager.userLogoutUser();
        redirectLogin();
    }

    private void redirectLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void versionApp(){
        StringBuilder appNameStringBuilder = new StringBuilder();
        appNameStringBuilder.append(getString(R.string.app_name));
        appNameStringBuilder.append(" ");
        appNameStringBuilder.append("v");
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appNameStringBuilder.append(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionTextView.setText(appNameStringBuilder.toString());
    }

}
