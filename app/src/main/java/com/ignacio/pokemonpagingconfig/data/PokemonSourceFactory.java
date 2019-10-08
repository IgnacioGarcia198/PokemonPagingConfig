package com.ignacio.pokemonpagingconfig.data;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import com.ignacio.pokemonpagingconfig.model.RetroPokemon;

public class PokemonSourceFactory extends DataSource.Factory<Integer, RetroPokemon> {
    @NonNull
    @Override
    public DataSource<Integer, RetroPokemon> create() {
        return null;
    }
}
