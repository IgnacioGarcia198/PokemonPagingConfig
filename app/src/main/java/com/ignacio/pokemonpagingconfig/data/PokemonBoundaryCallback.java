package com.ignacio.pokemonpagingconfig.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;
import androidx.annotation.NonNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ignacio.pokemonpagingconfig.api.PokemonService;
import com.ignacio.pokemonpagingconfig.api.RetrofitClientInstance;
import com.ignacio.pokemonpagingconfig.model.RetroPokemon;
import com.ignacio.pokemonpagingconfig.utils.Globals;

import java.util.Calendar;
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
    private MutableLiveData<ResponseState> networkErrors = new MutableLiveData<>();
    private RetroPokemonRepository repository;

    private static final int DB_EMPTY = 0;
    private static final int OUTDATED = 1;
    private static final int UP_TO_DATE = 2;
    private static int FRESH_TIMEOUT_IN_MINUTES = 2;//43200
    //private int dbupdated;

    PokemonBoundaryCallback(String name, RetroPokemonRepository repository) {
        this.name = name;
        this.repository = repository;
        if(isdbUpToDate() == OUTDATED) {
            Log.d(LOG_TAG,"db is outdated, retrieve data from network");
            requestAndSaveData(name,false);
        }
    }

    LiveData<ResponseState> getNetworkErrors() {
        return networkErrors;
    }

    /**
     * Method to request data from Github API for the given search query
     * and save the results.
     *
     *
     */
    private void requestAndSaveData(String name,boolean dbEmpty) {
        //Exiting if the request is in progress
        if (isRequestInProgress) return;

        //Set to true as we are starting the network request
        isRequestInProgress = true;

        //Calling the client API to retrieve the Repos for the given search query
        repository.fetchPokemonsFromApi(this, name, lastOffset, NETWORK_PAGE_SIZE,dbEmpty);

    }

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    @Override
    public void onZeroItemsLoaded() {
        Log.d(LOG_TAG, "onZeroItemsLoaded: Started");
        requestAndSaveData(name,true);
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
        requestAndSaveData(name,false);
        // TODO THIS COULD NEED TO BE TRUE INSTEAD...
        // TODO CHECK THAT THE ERROR VALUES ARE UPDATING PROPERLY...
    }


    @Override
    public void onApiResult(List<RetroPokemon> pokemons, ResponseState responseState) {
        if(pokemons!= null && !pokemons.isEmpty()) {
            repository.insertVarious(pokemons, () -> {
                Log.d(LOG_TAG, "Response is good ");
                //Updating the last requested page number when the request was successful
                //and the results were inserted successfully
                lastOffset += NETWORK_PAGE_SIZE;
                Log.d(LOG_TAG, "last Offset: " + lastOffset + "; page size: " + NETWORK_PAGE_SIZE);
                // update last refresh date
                updateLastRefresh();
            });
        }
        else {
            Log.d(LOG_TAG, "Response is empty ");
        }

        //Update the Network error to be shown
        networkErrors.postValue(responseState);
        //Mark the request progress as completed
        isRequestInProgress = false;
    }

    private int isdbUpToDate() {
        SharedPreferences sharedPreferences = repository.getContext().getSharedPreferences(
                Globals.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        long lastSavedMinutes = sharedPreferences.getLong("lastRefresh",0);
        if(lastSavedMinutes == 0) {
            return DB_EMPTY;
        }
        Calendar calendar = Calendar.getInstance();
        long nowMinutes = calendar.getTimeInMillis()/60000;
        Log.d(LOG_TAG,"time difference: " + (nowMinutes - lastSavedMinutes));
        if(nowMinutes-lastSavedMinutes > FRESH_TIMEOUT_IN_MINUTES) {
            return OUTDATED;
        }
        else return UP_TO_DATE;
    }

    private void updateLastRefresh() {
        SharedPreferences.Editor editor = repository.getContext().getSharedPreferences(
                Globals.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong("lastRefresh",Calendar.getInstance().getTimeInMillis()/60000);
        editor.apply();
    }
}