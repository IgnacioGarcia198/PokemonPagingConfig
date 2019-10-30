package com.ignacio.pokemonpagingconfig.api;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class ApiTest {

    private static String LOG_TAG = ApiTest.class.getSimpleName();
    private PokemonService service;// = RetrofitClientInstance.getApi();
    private Executor executor = Executors.newSingleThreadExecutor();
    Context context = InstrumentationRegistry.getTargetContext();
    private static final int ALBUM_ID = 1;


    @Test
    public void getPhotosByAlbumIdFromApiTest() {

    }
}
