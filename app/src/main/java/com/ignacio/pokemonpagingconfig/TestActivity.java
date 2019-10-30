package com.ignacio.pokemonpagingconfig;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.ignacio.pokemonpagingconfig.api.PokemonService;
import com.ignacio.pokemonpagingconfig.api.RetrofitClientInstance;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TestActivity extends AppCompatActivity {
    private static String LOG_TAG = TestActivity.class.getSimpleName();
    private PokemonService service = RetrofitClientInstance.getApi();
    private Executor executor = Executors.newSingleThreadExecutor();
    private static final int ALBUM_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //getPhotosTest();
    }

}
