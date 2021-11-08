package com.example.reactiveDemo.configuration;

import com.tiket.tix.common.monitor.interceptors.OutboundRequestInterceptor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public class GitHubServiceGenerator {

    private static final String BASE_URL = "https://api.github.com/";

    @Autowired
    private OutboundRequestInterceptor outboundRequestInterceptor;

    public <S> S getService(Class<S> serviceClass) {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        if(!httpClient.interceptors().contains(outboundRequestInterceptor)) {
            httpClient.addInterceptor(outboundRequestInterceptor);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }
        return retrofit.create(serviceClass);
    }




//    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
//            .setLevel(HttpLoggingInterceptor.Level.BASIC);





//    public static <S> S createService(Class<S> serviceClass) {
////        if (!httpClient.interceptors().contains(logging)) {
////            httpClient.addInterceptor(logging);
////            builder.client(httpClient.build());
////            retrofit = builder.build();
////        }
////        return retrofit.create(serviceClass);
//
//    }

//    // Authentication
//    public static <S> S createService(Class<S> serviceClass, final String token) {
//        if ( token != null ) {
//            httpClient.interceptors().clear();
//            httpClient.addInterceptor( chain -> {
//                Request original = chain.request();
//                Request request = original.newBuilder()
//                        .header("Authorization", token)
//                        .build();
//                return chain.proceed(request);
//            });
//            builder.client(httpClient.build());
//            retrofit = builder.build();
//        }
//        return retrofit.create(serviceClass);
//    }
}
