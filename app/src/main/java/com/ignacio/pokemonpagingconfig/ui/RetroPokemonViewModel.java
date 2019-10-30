package com.ignacio.pokemonpagingconfig.ui;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.util.Pair;

import com.ignacio.pokemonpagingconfig.data.ResponseState;
import com.ignacio.pokemonpagingconfig.data.RetroPokemonRepository;
import com.ignacio.pokemonpagingconfig.model.PokemonSearchResult;
import com.ignacio.pokemonpagingconfig.model.RetroPokemon;

public class RetroPokemonViewModel extends AndroidViewModel {
    //private PokemonSearchResult searchResult;
    private RetroPokemonRepository repository;
    private MutableLiveData<Boolean> noNetworkLiveData;

    public RetroPokemonViewModel(Application application) {
        super(application);
        this.repository = RetroPokemonRepository.getInstance(application.getApplicationContext());
    }

    /**
     * Comprueba si tenemos conexión a internet
     * @param context contexto de a app
     * @return boolean
     */
    public LiveData<Boolean> netWorkIsOkLiveData(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (conMgr != null) {
            networkInfo = conMgr.getActiveNetworkInfo();
        }
        MutableLiveData<Boolean> returnLivedata = new MutableLiveData<>();
        returnLivedata.setValue(networkInfo != null && networkInfo.isConnected());
        return returnLivedata;
    }

    private MutableLiveData<String> query = new MutableLiveData<>();

    private LiveData<PokemonSearchResult> searchResultLiveData = Transformations.map(query,name ->
        repository.searchPokemons(name));

    private LiveData<PagedList<RetroPokemon>> pokemons = Transformations.switchMap(searchResultLiveData,
            PokemonSearchResult::getData);

    private LiveData<ResponseState> networkErrors = Transformations.switchMap(searchResultLiveData,
            PokemonSearchResult::getNetworkErrors);

    public void triggerSearchPokemons(String name) {
        query.postValue(name);
    }

    public LiveData<PagedList<RetroPokemon>> getPokemons() {
        return pokemons;
    }

    public LiveData<ResponseState> getNetworkErrors() {
        return networkErrors;
    }

    public LiveData<Boolean> getNoNetworkLiveData() {
        return noNetworkLiveData;
    }

    public void setNoNetworkLiveData(boolean noNetwork) {
        noNetworkLiveData.postValue(noNetwork);
    }
}
