package id.web.gocak.service;

import android.content.Context;
import android.util.Log;

import id.web.gocak.R;
import id.web.gocak.model.Order;
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
public class ServiceOrder {

    public interface OrderUrl {
        @GET("jemput")
        Call<Order> getOrder(
                @Query("ID") String ID,
                @Query("userName") String userName,
                @Query("addressdetail") String addressdetail,
                @Query("latfrom") String latfrom,
                @Query("latto") String latto,
                @Query("longfrom") String longfrom,
                @Query("longto") String longto,
                @Query("distance") String distance,
                @Query("payment") String payment
        );

    }

    private Context context;

    public ServiceOrder(Context context) {
        this.context = context;
    }

    public void fetchOrder(
            String ID, String userName, String addressdetail,
            String fromLatitude, String fromLongitude,
            String toLatitude, String toLongitude,
            String distance,
            String payment, final OrderCallBack callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OrderUrl service = retrofit.create(OrderUrl.class);
        Call<Order> listCall = service.getOrder(ID, userName, addressdetail,
                fromLatitude, toLatitude, fromLongitude, toLongitude, distance, payment);
        listCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call,
                                   Response<Order> response) {
                if (response.isSuccessful()) {
                    Order order = response.body();
                    Order.Jemput jemput = order.jemput.get(0);

                    Log.wtf("ORDER", "onResponse: " + jemput.code + jemput.result);

                    if (jemput.code.equals("1")) {
                        callback.onSuccess(jemput);
                    } else {
                        callback.onFailure(context.getString(R.string.order_gagal));
                    }
                } else {
                    callback.onFailure(context.getString(R.string.koneksi_bermasalah));
                }
            }

            @Override
            public void onFailure(Call<Order> call,
                                  Throwable t) {
                callback.onFailure(context.getString(R.string.koneksi_bermasalah));
            }
        });
    }

    public interface OrderCallBack {
        void onSuccess(Order.Jemput jemput);

        void onFailure(String message);
    }

}
