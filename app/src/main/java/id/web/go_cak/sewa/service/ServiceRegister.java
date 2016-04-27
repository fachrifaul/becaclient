package id.web.go_cak.sewa.service;

import android.content.Context;

import id.web.go_cak.sewa.R;
import id.web.go_cak.sewa.model.OauthUser;
import id.web.go_cak.sewa.util.ApiConstant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by fachrifebrian on 4/11/16.
 */
public class ServiceRegister {

    public interface LoginUrl {
        @GET("registerpelanggan")
        Call<OauthUser> getLogin(
                @Query("namalengkap") String namalengkap,
                @Query("telp") String telp,
                @Query("password") String password
        );
    }

    private Context context;

    public ServiceRegister(Context context) {
        this.context = context;
    }

    public void fetchLogin(String namalengkap, String telp, String password, final RegisterCallBack callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginUrl service = retrofit.create(LoginUrl.class);
        Call<OauthUser> listCall = service.getLogin(namalengkap, telp, password);
        listCall.enqueue(new Callback<OauthUser>() {
            @Override
            public void onResponse(Call<OauthUser> call,
                                   Response<OauthUser> response) {

                if (response.isSuccessful()) {
                    OauthUser oauthUser = response.body();

                    if (oauthUser.success == 1) {
                        callback.onSuccess(oauthUser);
                    } else {
                        callback.onFailure(context.getString(R.string.kesalahan_daftar));
                    }
                } else {
                    callback.onFailure(context.getString(R.string.koneksi_bermasalah));
                }
            }

            @Override
            public void onFailure(Call<OauthUser> call,
                                  Throwable t) {
                callback.onFailure(context.getString(R.string.koneksi_bermasalah));
            }
        });
    }

    public interface RegisterCallBack {
        void onSuccess(OauthUser login);

        void onFailure(String message);
    }

}
