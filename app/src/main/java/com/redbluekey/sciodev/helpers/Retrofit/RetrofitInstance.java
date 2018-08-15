package com.redbluekey.sciodev.helpers.Retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitInstance {

    private static Retrofit retrofit;

    private static String BASE_URL = "http://api.redbluekey.com";

    /**
     * Create an instance of Retrofit object
     * */
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .readTimeout(60, TimeUnit.SECONDS).connectionPool(new ConnectionPool(0, 1, TimeUnit.NANOSECONDS));
            OkHttpClient okHttpClient = clientBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
