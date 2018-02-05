package com.dz.test;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import java.io.IOException;
import java.util.List;

/**
 * Created by RoyDeng on 17/6/20.
 */
public class RetrofitTest {

    private static Logger logger = LoggerFactory.getLogger(RetrofitTest.class);

    @Test
    public void testRetrofit() throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                logger.debug(message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://api.newsjet.io:8080/")
                .baseUrl("http://suggestqueries.google.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build();
        retrofit.create(SuggestRequest.class)
                .suggest().enqueue(suggestCallback);
        Thread.sleep(5 * 1000);
    }

    private interface SuggestRequest {

        @GET("complete/search?client=firefox&q=akb")
        Call<List<Object>> suggest();
    }

    private Callback<List<Object>> suggestCallback = new Callback<List<Object>>() {
        @Override
        public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
            if (response.code() == 200) {
                logger.debug("onResponse url:{} body:{}", call.request().url(), response.body());
            } else {
                try {
                    logger.debug("onResponse url:{} body:{}", call.request().url(), response.errorBody().string());
                } catch (IOException e) {
                    logger.warn("onResponse parse errorBody error!", e);
                }
            }
        }

        @Override
        public void onFailure(Call<List<Object>> call, Throwable t) {
            logger.debug("onFailure", t);
        }
    };

}
