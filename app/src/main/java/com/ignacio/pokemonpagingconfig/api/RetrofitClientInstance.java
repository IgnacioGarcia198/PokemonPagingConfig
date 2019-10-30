package com.ignacio.pokemonpagingconfig.api;

import android.util.Log;

import com.ignacio.pokemonpagingconfig.MainActivity;
import com.ignacio.pokemonpagingconfig.data.ResponseState;
import com.ignacio.pokemonpagingconfig.data.RetroPokemonRepository;
import com.ignacio.pokemonpagingconfig.model.RetroPokemon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://pokeapi.co";
    private static PokemonService serviceApi;
    private static final String LOG_TAG = RetrofitClientInstance.class.getSimpleName();
    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            //Initializing HttpLoggingInterceptor to receive the HTTP event logs
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            //Building the HTTPClient with the logger
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static PokemonService getApi() {
        if(serviceApi == null) {
            serviceApi = getRetrofitInstance().create(PokemonService.class);
        }
        return serviceApi;
    }


}
