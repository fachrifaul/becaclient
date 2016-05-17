package id.web.gocak.service;

import android.content.Context;
import android.util.Log;

import id.web.gocak.R;
import id.web.gocak.model.Distance;
import id.web.gocak.util.ApiConstant;
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
public class ServiceDistance {

    public interface DistanceUrl {
        @GET("jarak")
        Call<Distance> getDistance(
                @Query("ol1") String ol1,
                @Query("ol2") String ol2,
                @Query("dl1") String dl1,
                @Query("dl2") String dl2
        );
    }

    private Context context;

    public ServiceDistance(Context context) {
        this.context = context;
    }

    public void fetchDistance(String fromLatitude, String fromLongitude,
                              String toLatitude, String toLongitude,
                              final DistanceCallBack callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DistanceUrl service = retrofit.create(DistanceUrl.class);
        Call<Distance> listCall = service.getDistance(fromLatitude, fromLongitude, toLatitude, toLongitude);
        listCall.enqueue(new Callback<Distance>() {
            @Override
            public void onResponse(Call<Distance> call,
                                   Response<Distance> response) {

                if (response.isSuccessful()) {

                    Distance distance = response.body();
                    Distance.Lokasi lokasi = distance.lokasi.get(0);

                    Log.wtf("TAG", "onResponse: "+lokasi.distance );
                    Log.wtf("TAG", "onResponse: "+lokasi.distanceValue );

                    if (!lokasi.distance.equals("0")) {
                        callback.onSuccess(lokasi);
                    } else {
                        callback.onFailure(context.getString(R.string.cek_kembali_tujuan));
                    }
                } else {
                    callback.onFailure(context.getString(R.string.koneksi_bermasalah));
                }
            }

            @Override
            public void onFailure(Call<Distance> call,
                                  Throwable t) {
                callback.onFailure(context.getString(R.string.koneksi_bermasalah));
            }
        });
    }

    public interface DistanceCallBack {
        void onSuccess(Distance.Lokasi lokasi);

        void onFailure(String message);
    }

}
