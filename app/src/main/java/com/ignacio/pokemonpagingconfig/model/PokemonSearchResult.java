package com.ignacio.pokemonpagingconfig.model;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.ignacio.pokemonpagingconfig.data.ResponseState;

/**
 * These values can be assigned just once! Hence the convenience. We dont need to reset or change values for
 * network errors elsewhere.
 */
public class PokemonSearchResult {
    //LiveData for Search Results
    private final LiveData<PagedList<RetroPokemon>> data;
    //LiveData for the Network Errors
    private final LiveData<ResponseState> networkErrors;

    public PokemonSearchResult(LiveData<PagedList<RetroPokemon>> data, LiveData<ResponseState> networkErrors) {
        this.data = data;
        this.networkErrors = networkErrors;
    }

    public LiveData<PagedList<RetroPokemon>> getData() {
        return data;
    }

    public LiveData<ResponseState> getNetworkErrors() {
        return networkErrors;
    }
}
