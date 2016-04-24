package id.web.go_cak.sewa.service;

import android.content.Context;

import id.web.go_cak.sewa.model.Login;
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
        @GET("operator/index.php/Welcome/loginPelanggan")
        Call<Login> getLogin(
                @Query("name") String name,
                @Query("password") String password);
    }

    private Context context;

    public ServiceLogin(Context context) {
        this.context = context;
    }

    public void fetchLogin(String name, String password, final LoginCallBack callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginUrl service = retrofit.create(LoginUrl.class);
        Call<Login> listCall = service.getLogin(name, password);
        listCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call,
                                   Response<Login> response) {

                if (response.isSuccessful()) {
                    Login login = response.body();

                    if (login.success == 1) {
                        callback.onSuccess(login);
                    } else {
                        callback.onFailure("Terjadi Kesalahan Login, Periksa No Telp dan Password Anda.");
                    }
                } else {
                    callback.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Login> call,
                                  Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public interface LoginCallBack {
        void onSuccess(Login login);

        void onFailure(String message);
    }

}
