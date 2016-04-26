package id.web.go_cak.sewa.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.web.go_cak.sewa.R;
import id.web.go_cak.sewa.session.UserSessionManager;
import id.web.go_cak.sewa.view.antarjemput.TransportActivity;
import id.web.go_cak.sewa.view.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;

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

}
