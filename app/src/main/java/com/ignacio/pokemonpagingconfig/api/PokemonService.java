package com.ignacio.pokemonpagingconfig.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PokemonService {

    @GET("/api/v2/pokemon")
    Call<PokemonResponse> getPokemonResponseFromApi(@Query("offset") int offset, @Query("limit") int limit);

}
