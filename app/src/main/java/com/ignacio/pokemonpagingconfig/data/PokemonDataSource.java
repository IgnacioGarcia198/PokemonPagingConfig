package com.ignacio.pokemonpagingconfig.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;

import com.ignacio.pokemonpagingconfig.db.PokemonDao;
import com.ignacio.pokemonpagingconfig.model.PokemonSearchResult;
import com.ignacio.pokemonpagingconfig.model.RetroPokemon;

public class PokemonDataSource extends ItemKeyedDataSource<Integer,RetroPokemon> {
    private static final String LOG_TAG = RetroPokemonRepository.class.getSimpleName();
    private static final int DATABASE_PAGE_SIZE = 20;
    private PokemonDao pokemonDao;
    private RetroPokemonRepository repository;

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<RetroPokemon> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<RetroPokemon> callback) {

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<RetroPokemon> callback) {

    }

    @NonNull
    @Override
    public Integer getKey(@NonNull RetroPokemon item) {
        return item.getId();
    }

    /**
     * Fetching photos from database
     * @param name
     * @return
     */
    public DataSource.Factory<Integer, RetroPokemon> fetchPokemonsFromDb(String name) {

        if(name == null || name.equals("")) {
            // get by id
            return pokemonDao.getAllPokemonsFromRoom();
        }
        else {
            // get by name and id
            return pokemonDao.findPokemonsByNameInRoom("%" + name + "%");
        }

    }

    /**
     * Search photos.
     */
    public PokemonSearchResult searchPokemons(String name) {
        Log.d(LOG_TAG, "search: New query: ; name: "+name);

        // Get data source factory from the local cache
        DataSource.Factory<Integer, RetroPokemon> pokemonsResult = fetchPokemonsFromDb(name);

        // Construct the boundary callback
        PokemonBoundaryCallback boundaryCallback = new PokemonBoundaryCallback(name,repository);
        LiveData<String> networkErrors = boundaryCallback.getNetworkErrors();

        // Set the Page size for the Paged list
        PagedList.Config pagedConfig = new PagedList.Config.Builder()
                .setPageSize(DATABASE_PAGE_SIZE)
                .setInitialLoadSizeHint(DATABASE_PAGE_SIZE*3)
                //.setEnablePlaceholders(true)
                .build();

        // Get the Live Paged list
        LiveData<PagedList<RetroPokemon>> data = new LivePagedListBuilder<>(pokemonsResult, pagedConfig)
                .setBoundaryCallback(boundaryCallback)
                .build();

        // Get the Search result with the network errors exposed by the boundary callback
        return new PokemonSearchResult(data, networkErrors);
    }
}
