package com.ignacio.pokemonpagingconfig.model;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

/**
 * These values can be assigned just once! Hence the convenience. We dont need to reset or change values for
 * network errors elsewhere.
 */
public class PokemonSearchResult {
    //LiveData for Search Results
    private final LiveData<PagedList<RetroPokemon>> data;
    //LiveData for the Network Errors
    private final LiveData<String> networkErrors;

    public PokemonSearchResult(LiveData<PagedList<RetroPokemon>> data, LiveData<String> networkErrors) {
        this.data = data;
        this.networkErrors = networkErrors;
    }

    public LiveData<PagedList<RetroPokemon>> getData() {
        return data;
    }

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }
}
