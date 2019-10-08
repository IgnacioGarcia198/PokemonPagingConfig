package com.ignacio.pokemonpagingconfig.api;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonResponse {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("next")
    @Expose
    @Nullable
    private String next;
    @SerializedName("previous")
    @Expose
    @Nullable
    private String previous;
    @SerializedName("results")
    @Expose
    private List<PokemonResult> results = null;

    public int getCount() {
        return count;
    }

    @Nullable
    public String getNext() {
        return next;
    }

    @Nullable
    public String getPrevious() {
        return previous;
    }

    public List<PokemonResult> getResults() {
        return results;
    }
}
