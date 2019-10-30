package com.ignacio.pokemonpagingconfig.data;


import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ignacio.pokemonpagingconfig.MainActivity;
import com.ignacio.pokemonpagingconfig.api.PokemonDownloadWorker;
import com.ignacio.pokemonpagingconfig.api.PokemonResponse;
import com.ignacio.pokemonpagingconfig.api.PokemonResult;
import com.ignacio.pokemonpagingconfig.api.PokemonService;
import com.ignacio.pokemonpagingconfig.api.RetrofitClientInstance;
import com.ignacio.pokemonpagingconfig.db.PokemonDao;
import com.ignacio.pokemonpagingconfig.db.RetroPokemonDatabase;
import com.ignacio.pokemonpagingconfig.model.PokemonSearchResult;
import com.ignacio.pokemonpagingconfig.model.RetroPokemon;
import com.ignacio.pokemonpagingconfig.utils.Globals;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *     Use the Webservice (via Retrofit) the first time user launch the app
 *     Use the Webservice instead of database when last fetching from API of user’s data was more than 3 minutes ago.
 *     Otherwise, use the database (via Room).
 */
public class RetroPokemonRepository {

    private static final String LOG_TAG = RetroPokemonRepository.class.getSimpleName();
    private final PokemonService pokemonService;
    private static RetroPokemonRepository instance; // Singleton
    private final PokemonDao pokemonDao;
    private final Executor executor;
    private final WeakReference<Context> context;
    //private final Context context;
    public static final int DATABASE_PAGE_SIZE = 20;
    private int nextId = 1;
    private ResponseState responseState;
    private static boolean downloadScheduled;
    public static final String WORK_NAME = "work_name";
    private LiveData<ResponseState> networkErrors;

    //===========================================

    private RetroPokemonRepository(Context context) {
        this.context = new WeakReference<>(context);
        this.pokemonService = RetrofitClientInstance.getApi();
        this.pokemonDao = RetroPokemonDatabase.getInstance(context).retroPhotoDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public static RetroPokemonRepository getInstance(Context context) {
        if(instance == null) {
            instance = new RetroPokemonRepository(context);
        }
        return instance;
    }

    public void insertPokemon(RetroPokemon pokemon) {
        executor.execute(() -> {
            pokemonDao.save(pokemon);
        });
    }

    public void insertVarious(List<RetroPokemon> pokemonList,InsertCallback insertCallback) {
        executor.execute(() -> {
            pokemonDao.saveVarious(pokemonList);
            insertCallback.onInsertFinished();
        });
    }

    public interface InsertCallback {
        void onInsertFinished();
    }

    /*
    private void refreshPhotosByAlbumId(int albumId) {
        executor.execute(() -> {
            boolean photosAreFresh = (pokemonDao.hasFreshPhotoInAlbum(albumId,getMaxRefreshTime(new Date())) != null);

            if(!photosAreFresh) {
                webservice.getPhotosByAlbumIdFromApi(albumId).enqueue(new Callback<List<RetroPokemon>>() {
                    @Override
                    public void onResponse(Call<List<RetroPokemon>> call, Response<List<RetroPokemon>> response) {
                        if(response.isSuccessful()) {
                            Toast.makeText(context, "Data refreshed from network !", Toast.LENGTH_LONG).show();
                            //executor.execute(() -> {
                                Log.d(LOG_TAG,"Data refreshed from network !");
                            //});

                            // insert new data in the database

                            executor.execute(() -> {
                                for (RetroPokemon photo:response.body()) {
                                    photo.setLastRefresh(new Date());
                                    pokemonDao.save(photo);
                                }
                            });
                        }
                        else {
                            Toast.makeText(context, "Refresh data from network is empty", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RetroPokemon>> call, Throwable t) {
                        Toast.makeText(context, "Error on refreshing data from network", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private Date getMaxRefreshTime(Date currentDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.MINUTE, -FRESH_TIMEOUT_IN_MINUTES);
        return cal.getTime();
    }
*/

    //=============================================================================


    /**
     * Fetching the photos from retrofit
     * @param apiCallback
     */
    public void fetchPokemonsFromApi(ApiCallback apiCallback, String name, int offset, int itemsPerPage,boolean dbEmpty) {
        Log.d(LOG_TAG, String.format("fetchPokemonsFromApi: title: %s", name));

        //Executing the API
        final List<RetroPokemon> pokemons = new ArrayList<>();
        //ResponseState state;
        pokemonService.getPokemonResponseFromApi(offset, itemsPerPage).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                Log.d(LOG_TAG, "onResponse is called");
                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        List<PokemonResult> results = response.body().getResults();
                        if(results != null && !results.isEmpty()) {
                            //pokemons = new ArrayList<>();
                            //int id = nextId;
                            for(PokemonResult result : results) {
                                if(nextId > MainActivity.DOWNLOAD_SIZE) {
                                    break;
                                }
                                pokemons.add(new RetroPokemon(nextId, result.name));
                                nextId ++;

                            }
                            Log.d(LOG_TAG, "Success");
                            responseState = new ResponseState(ResponseState.DOWNLOAD_SUCCESSFUL);
                            //apiCallback.onApiResult(pokemons,new ResponseState(ResponseState.DOWNLOAD_SUCCESSFUL,false));
                        }
                        else {
                            Log.d(LOG_TAG, "onResponse: Only results was empty");
                            //pokemons = new ArrayList<>();
                            responseState = new ResponseState(ResponseState.EMPTY_DATA);
                            //apiCallback.onApiResult(pokemons,new ResponseState(ResponseState.EMPTY_DATA,false));
                        }

                    } else {
                        Log.d(LOG_TAG, "onResponse: Response was empty or null");
                        //pokemons = new ArrayList<>();
                        responseState = new ResponseState(ResponseState.EMPTY_DATA);
                        //apiCallback.onApiResult(pokemons,new ResponseState(ResponseState.EMPTY_DATA,false));

                    }

                    /*SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("lastRefresh", Calendar.getInstance().getTimeInMillis());
                    editor.apply();*/
                    //Retrieving the pokemon list when the response is successful



                }
                else {
                    Log.d(LOG_TAG, "onResponse: Response unsuccessful");
                    //When the response is unsuccessful
                    responseState = new ResponseState(ResponseState.DOWNLOAD_UNSUCCESSFUL);
                    //apiCallback.onApiResult(pokemons,new ResponseState(ResponseState.DOWNLOAD_UNSUCCESSFUL,false));

                }

                if(responseState.value != ResponseState.DOWNLOAD_SUCCESSFUL && responseState.value != ResponseState.NO_NEED_TO_DOWNLOAD) {
                    // TODO SCHEDULE WORK ON INTERNET ACCESSIBLE.
                    //scheduleDownload(name);

                }
                Log.d(LOG_TAG,"calling apicallback");
                apiCallback.onApiResult(pokemons,responseState != null ? responseState.setFatal(dbEmpty) : new ResponseState(ResponseState.NO_NETWORK));


            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                Log.d(LOG_TAG, "onFailure: Failed to get data");
                //Pass the error to the callback
                if(!Globals.netWorkIsOk(context.get())) {
                    responseState = new ResponseState(ResponseState.NO_NETWORK);
                    //apiCallback.onApiResult(pokemons,new ResponseState(ResponseState.NO_NETWORK,false));
                }
                else {
                    responseState = new ResponseState(ResponseState.DOWNLOAD_FAILURE);
                    //apiCallback.onApiResult(pokemons,new ResponseState(ResponseState.DOWNLOAD_FAILURE,false));
                }
                Log.d(LOG_TAG,"calling apicallback");
                if(responseState == null || (responseState.value != ResponseState.DOWNLOAD_SUCCESSFUL && responseState.value != ResponseState.NO_NEED_TO_DOWNLOAD)) {
                    // TODO SCHEDULE WORK ON INTERNET ACCESSIBLE.
                    //Log.d(LOG_TAG,"scheduling download");
                    //scheduleDownload(name);

                }
                apiCallback.onApiResult(pokemons,responseState != null ? responseState.setFatal(dbEmpty) : new ResponseState(ResponseState.NO_NETWORK).setFatal(dbEmpty));

            }

        });



            }

    public interface ApiCallback {
        void onApiResult(List<RetroPokemon> pokemons,ResponseState responseState);
    }



    //=============================================================

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
        Log.d(LOG_TAG, "search: New query: name: "+name);

        // Get data source factory from the local cache
        DataSource.Factory<Integer, RetroPokemon> pokemonsResult = fetchPokemonsFromDb(name);

        // Construct the boundary callback
        PokemonBoundaryCallback boundaryCallback = new PokemonBoundaryCallback(name,this);
        networkErrors = boundaryCallback.getNetworkErrors();

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
        return new PokemonSearchResult(data,networkErrors);
    }

    public Context getContext() {
        return context.get();
    }

    private void scheduleDownload(String name) {
        if(!downloadScheduled) {
            WorkManager mWorkManager = WorkManager.getInstance(context.get());
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(PokemonDownloadWorker.class)
                    .setConstraints(constraints)
                    .setInputData(new Data.Builder().putString("name",name).build())

                    //.addTag(WORK_TAG)
                    //.setInitialDelay()
                    //.addTag(TAG_OUTPUT)
                    .build();
            //mWorkManager.enqueue(mRequest);
            mWorkManager.enqueueUniqueWork (WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    mRequest);

            downloadScheduled = true;
        }

    }

    public LiveData<ResponseState> getNetworkErrors() {
        return networkErrors;
    }
}
