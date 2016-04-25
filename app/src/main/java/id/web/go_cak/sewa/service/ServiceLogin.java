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
public class ServiceLogin {

    public interface LoginUrl {
        @GET("loginPelanggan")
        Call<OauthUser> getLogin(
                @Query("name") String name,
                @Query("password") String password);
    }

    private Context context;

    public ServiceLogin(Context context) {
        this.context = context;
    }

    public void fetchLogin(String name, String password, final LoginCallBack callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.AUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginUrl service = retrofit.create(LoginUrl.class);
        Call<OauthUser> listCall = service.getLogin(name, password);
        listCall.enqueue(new Callback<OauthUser>() {
            @Override
            public void onResponse(Call<OauthUser> call,
                                   Response<OauthUser> response) {

                if (response.isSuccessful()) {
                    OauthUser oauthUser = response.body();

                    if (oauthUser.success == 1) {
                        callback.onSuccess(oauthUser);
                    } else {
                        callback.onFailure(context.getString(R.string.kesalahan_login));
                    }
                } else {
                    callback.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<OauthUser> call,
                                  Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public interface LoginCallBack {
        void onSuccess(OauthUser oauthUser);

        void onFailure(String message);
    }

}
