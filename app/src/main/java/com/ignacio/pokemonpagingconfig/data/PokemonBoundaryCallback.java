package com.ignacio.pokemonpagingconfig.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;
import androidx.annotation.NonNull;
import android.util.Log;

import com.ignacio.pokemonpagingconfig.model.RetroPokemon;

import java.util.List;

/**
 * PagedList.BoundaryCallback class to know when to trigger the Network request for more data
 *
 * Extremely important question: WE NEED TO HAVE DEFINED SOME PARAMETER IN ORDER TO GET X ELEMENTS FROM THE SERVER,
 * INSTEAD OF GETTING ALL OF THEM. AND SO, THIS PARAMETER WILL ALSO BE IN PokemonService METHOD, AND IT WILL BE A
 * PARAMETER TO KEEP HERE IN THIS CLASS. AS A MATTER OF FACT THIS WOULD BE THE PARAMETER "OFFSET" IN THE CASE OF THE
 * POKEMON API.(SERVER PAGE SIZE WOULD BE THE OTHER PARAMETER).
 */
public class PokemonBoundaryCallback extends PagedList.BoundaryCallback<RetroPokemon> implements RetroPokemonRepository.ApiCallback {
    //Constant used for logs
    private static final String LOG_TAG = PokemonBoundaryCallback.class.getSimpleName();
    // Constant for the Number of items in a page to be requested from the Github API
    private static final int NETWORK_PAGE_SIZE = 40;
    private String name;
    // Keep the last requested page. When the request is successful, increment the page number.
    public static int lastOffset;
    // Avoid triggering multiple requests in the same time
    private boolean isRequestInProgress = false;
    // LiveData of network errors.
    private MutableLiveData<String> networkErrors = new MutableLiveData<>();
    private RetroPokemonRepository repository;

    PokemonBoundaryCallback(String name, RetroPokemonRepository repository) {
        this.name = name;
        this.repository = repository;
    }

    LiveData<String> getNetworkErrors() {
        return networkErrors;
    }

    /**
     * Method to request data from Github API for the given search query
     * and save the results.
     *
     *
     */
    private void requestAndSaveData(String name) {
        //Exiting if the request is in progress
        if (isRequestInProgress) return;

        //Set to true as we are starting the network request
        isRequestInProgress = true;

        //Calling the client API to retrieve the Repos for the given search query
        repository.fetchPokemonsFromApi(this, name, lastOffset, NETWORK_PAGE_SIZE);

    }

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    @Override
    public void onZeroItemsLoaded() {
        Log.d(LOG_TAG, "onZeroItemsLoaded: Started");
        requestAndSaveData(name);
    }

    /**
     * Called when the item at the end of the PagedList has been loaded, and access has
     * occurred within {@link PagedList.Config#prefetchDistance} of it.
     * <p>
     * No more data will be appended to the PagedList after this item.
     *
     * @param itemAtEnd The first item of PagedList
     */
    @Override
    public void onItemAtEndLoaded(@NonNull RetroPokemon itemAtEnd) {
        Log.d(LOG_TAG, "onItemAtEndLoaded: Started");
        //lastOffset += NETWORK_PAGE_SIZE;
        requestAndSaveData(name);
    }

    /**
     * Callback invoked when the Search Repo API Call
     * completed successfully
     *
     * @param pokemons The List of Repos retrieved for the Search done
     */
    @Override
    public void onSuccess(List<RetroPokemon> pokemons) {
        //Inserting records in the database thread

        repository.insertVarious(pokemons, () -> {
            //Updating the last requested page number when the request was successful
            //and the results were inserted successfully
            lastOffset += NETWORK_PAGE_SIZE;
            Log.d(LOG_TAG,"last Offset: " + lastOffset + "; page size: " + NETWORK_PAGE_SIZE);
            //Marking the request progress as completed
            isRequestInProgress = false;
        });
    }

    /**
     * Callback invoked when the Search Repo API Call failed
     *
     * @param errorMessage The Error message captured for the API Call failed
     */
    @Override
    public void onError(String errorMessage) {
        //Update the Network error to be shown
        networkErrors.postValue(errorMessage);
        //Mark the request progress as completed
        isRequestInProgress = false;
    }
}