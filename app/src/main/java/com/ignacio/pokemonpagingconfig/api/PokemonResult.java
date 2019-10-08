package com.ignacio.pokemonpagingconfig.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PokemonResult {
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("url")
    @Expose
    public String url;
}
