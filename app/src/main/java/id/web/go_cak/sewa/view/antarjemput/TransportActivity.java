package id.web.go_cak.sewa.view.antarjemput;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.web.go_cak.sewa.R;
import id.web.go_cak.sewa.util.ApiConstant;
import id.web.go_cak.sewa.view.order.OrderActivity;
import id.web.go_cak.sewa.view.picklocation.PickLocationActivity;


public class TransportActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.dari_lokasi_edittext) EditText dariLokasiEditText;
    @Bind(R.id.detail_dari_lokasi_edittext) EditText detailDariLokasiEditText;
    @Bind(R.id.tujuan_lokasi_edittext) EditText tujuanLokasiEditText;

    public String FROMLATITUDE = "fromlatitude";
    public String FROMLONGITUDE = "fromlongitude";
    public String FROMADDRESS = "fromaddress";
    public String TOLATITUDE = "tolatitude";
    public String TOLONGITUDE = "tolongitude";
    public String TOADDRESS = "toaddress";
    public String DETAIL = "detail";

    private final int DARI_PICK_LOCATION_CODE = 3;
    private final int TUJUAN_PICK_LOCATION_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @OnClick(R.id.dari_lokasi_edittext)
    public void dariLokasi() {
        Intent intent = new Intent(this, PickLocationActivity.class);
        startActivityForResult(intent, DARI_PICK_LOCATION_CODE);
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
    }

    @OnClick(R.id.tujuan_lokasi_edittext)
    public void tujuanLokasi() {
        Intent intent = new Intent(this, PickLocationActivity.class);
        startActivityForResult(intent, TUJUAN_PICK_LOCATION_CODE);
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == DARI_PICK_LOCATION_CODE) {
                FROMLATITUDE = data.getStringExtra(ApiConstant.latitude);
                FROMLONGITUDE = data.getStringExtra(ApiConstant.longitude);
                FROMADDRESS = data.getStringExtra(ApiConstant.address);
                dariLokasiEditText.setText(FROMADDRESS.replaceAll("[\\t\\n\\r]+", " "));

                Log.wtf("FROMADDRESS", "onActivityResult: " + FROMLATITUDE + " - " + FROMLONGITUDE + " - " + FROMADDRESS + " - ");
            }

            if (requestCode == TUJUAN_PICK_LOCATION_CODE) {
                TOLATITUDE = data.getStringExtra(ApiConstant.latitude);
                TOLONGITUDE = data.getStringExtra(ApiConstant.longitude);
                TOADDRESS = data.getStringExtra(ApiConstant.address);
                tujuanLokasiEditText.setText(TOADDRESS.replaceAll("[\\t\\n\\r]+", " "));

                Log.wtf("FROMADDRESS", "onActivityResult: " + TOLATITUDE + " - " + TOLONGITUDE + " - " + TOADDRESS + " - ");

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.selanjutnya_textview)
    public void selanjutnya() {
        if (dariLokasiEditText.getText().toString().equals("")
                && detailDariLokasiEditText.getText().toString().equals("")
                && tujuanLokasiEditText.getText().toString().equals("")) {
            Snackbar.make(coordinatorLayout, "Inputan Tidak Boleh ada yang Kosong", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra(ApiConstant.FROMLATITUDE, FROMLATITUDE);
            intent.putExtra(ApiConstant.FROMLONGITUDE, FROMLONGITUDE);
            intent.putExtra(ApiConstant.FROMADDRESS, FROMADDRESS);
            intent.putExtra(ApiConstant.DETAIL, detailDariLokasiEditText.getText().toString());
            intent.putExtra(ApiConstant.TOLATITUDE, TOLATITUDE);
            intent.putExtra(ApiConstant.TOLONGITUDE, TOLONGITUDE);
            intent.putExtra(ApiConstant.TOADDRESS, TOADDRESS);
            startActivity(intent);
            overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
        }
    }

}
