package com.michlindev.moviedownloader.api;


import com.michlindev.moviedownloader.data.DefaultData;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private final static String BASE_URL = String.format("%s%s", DefaultData.DOMAIN,  DefaultData.API);


    private static Retrofit retrofit = null;

    public static Retrofit getInstance() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    //.client(httpClient.build())

                    .build();
        }

        return retrofit;
    }
}
