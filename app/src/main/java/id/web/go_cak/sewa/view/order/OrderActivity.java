package id.web.go_cak.sewa.view.order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.web.go_cak.sewa.R;
import id.web.go_cak.sewa.model.Distance;
import id.web.go_cak.sewa.model.Order;
import id.web.go_cak.sewa.service.ServiceDistance;
import id.web.go_cak.sewa.service.ServiceOrder;
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

    private String FROMLATITUDE, FROMLONGITUDE, TOLATITUDE, TOLONGITUDE;
    private String FROMADDRESS, TOADDRESS, DETAILADDRESS;

    private UserSessionManager sessionManager;

    @Bind(R.id.dari_lokasi_textview) TextView dariLokasiTextView;
    @Bind(R.id.tujuan_lokasi_textview) TextView tujuanLokasiTextView;

    @Bind(R.id.address_detail_textview) TextView addressDetailTextView;
    @Bind(R.id.distance_textview) TextView distanceTextView;
    @Bind(R.id.payment_textview) TextView paymentTextView;

    String paymentOrder, distanceOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new UserSessionManager(this);

        FROMLATITUDE = getIntent().getStringExtra(ApiConstant.FROMLATITUDE);
        FROMLONGITUDE = getIntent().getStringExtra(ApiConstant.FROMLONGITUDE);
        TOLATITUDE = getIntent().getStringExtra(ApiConstant.TOLATITUDE);
        TOLONGITUDE = getIntent().getStringExtra(ApiConstant.TOLONGITUDE);
        FROMADDRESS = getIntent().getStringExtra(ApiConstant.FROMADDRESS);
        TOADDRESS = getIntent().getStringExtra(ApiConstant.TOADDRESS);
        DETAILADDRESS = getIntent().getStringExtra(ApiConstant.DETAIL);

        dariLokasiTextView.setText(FROMADDRESS);
        tujuanLokasiTextView.setText(TOADDRESS);
        addressDetailTextView.setText(DETAILADDRESS);

        serviceDistance();
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

    @OnClick(R.id.order_textview)
    public void onClickOrder() {

        final ProgressDialog progressDialog = new ProgressDialog(OrderActivity.this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading ....");
        progressDialog.show();

        new ServiceOrder(this).fetchOrder(sessionManager.getIdUser(), sessionManager.getTelp(), DETAILADDRESS,
                FROMLATITUDE, FROMLONGITUDE, TOLATITUDE, TOLONGITUDE,
                distanceOrder, paymentOrder,
                new ServiceOrder.OrderCallBack() {
                    @Override
                    public void onSuccess(Order.Jemput jemput) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        showDialog();
                    }

                    @Override
                    public void onFailure(String message) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        showDialogFailed(message);
                    }
                });
    }


    private void serviceDistance() {
        new ServiceDistance(this).fetchDistance(FROMLATITUDE, FROMLONGITUDE, TOLATITUDE, TOLONGITUDE,
                new ServiceDistance.DistanceCallBack() {
                    @Override
                    public void onSuccess(Distance.Lokasi lokasi) {
                        paymentOrder = String.valueOf(lokasi.payment);
                        distanceOrder = String.valueOf(lokasi.distanceValue);
                        distanceTextView.setText(lokasi.distance);
                        paymentTextView.setText("Rp. " + lokasi.showpayment);
                    }

                    @Override
                    public void onFailure(String message) {
                        showDialogFailed(message);
                    }
                });
    }

    private void showDialogFailed(String message) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Order")
                .content(message)
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .autoDismiss(false)
                .build();
        dialog.show();
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
