package id.web.go_cak.sewa.service;

import android.content.Context;
import android.os.Handler;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainThreadCallback implements Callback{

    private Context context;
    private MainThreadCallback.Callback callback;

    public MainThreadCallback(Context context, MainThreadCallback.Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    public void onFailure(final Call call, final IOException e) {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(call, e);
            }
        });
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        final String body = response.body().string();
        final SimpleResponse simpleResponse = new SimpleResponse(response.isSuccessful(), body);
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(call, simpleResponse);
            }
        });
    }

    public interface Callback {
        void onResponse(Call call, SimpleResponse response);
        void onFailure(Call call, IOException e);
    }

    public static class SimpleResponse {
        private boolean successful;
        private String body;
        public SimpleResponse(boolean successful, String body) {
            this.successful = successful;
            this.body = body;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public String getBody() {
            return body;
        }
    }
}
