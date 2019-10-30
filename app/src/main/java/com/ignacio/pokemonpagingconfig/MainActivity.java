package com.ignacio.pokemonpagingconfig;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ignacio.pokemonpagingconfig.api.PokemonDownloadWorker;
import com.ignacio.pokemonpagingconfig.data.ResponseState;
import com.ignacio.pokemonpagingconfig.databinding.ActivityMainBinding;
import com.ignacio.pokemonpagingconfig.ui.CustomAdapter;
import com.ignacio.pokemonpagingconfig.ui.RetroPokemonViewModel;
import com.ignacio.pokemonpagingconfig.utils.PepeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity  {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progressDoalog;
    private CustomAdapter adapter;
    private RetroPokemonViewModel viewModel;
    private ActivityMainBinding dataBinding;
    //public static int index = -1;
    //public static int top = -1;
    private Parcelable layoutState;
    private int lastKey;

    public static final String WORK_NAME = "work_name";
    public static boolean activityIsActive;

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    public static final int NOTIFICATION_ID = 0;
    private boolean justDownloaded;

    /**
     * CHANGE ONLY FOR TESTING. IF YOU CHANGE IT, IN ORDER TO SEE THE EFFECTS YOU NEED TO DELETE ALL
     * FROM THE DATABASE AND RESET THE lastRefresh VARIABLE. ORIGINAL VALUE IS 897 FOR THE LITTLE SPRITES
     * AND 720 FOR THE ARTWORK IMAGES.
     */
    public static int DOWNLOAD_SIZE = 720;//897;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Uses DataBinding to set the content view
        dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Loading....");
        //viewModel = ViewModelProviders new ViewModelFactory(RetroPokemonRepository.getInstance(this))

        if(savedInstanceState != null) {
            layoutState = savedInstanceState.getParcelable("layoutState");
            lastKey = savedInstanceState.getInt("lastKey",-1);
            Log.d(LOG_TAG, "last key retrieving: " + lastKey);
        }

        viewModel = ViewModelProviders.of(
                this).get(RetroPokemonViewModel.class);

        generateDataList();
        updatePokemons();
        /*if(savedInstanceState != null) {
            dataBinding.customRecyclerView.scrollToPosition(savedInstanceState.getInt("recyclerViewPosition"));
        }*/
        dataBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dataBinding.customRecyclerView.scrollToPosition(0);
                Log.d(LOG_TAG,"called with "+ s.toString().trim());
                viewModel.triggerSearchPokemons(s.toString().trim());
            }
        });

        dataBinding.customRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(lastKey != -1) {
                    //adapter.submitList(thelist);
                    dataBinding.customRecyclerView.scrollToPosition(lastKey);
                    Log.d(LOG_TAG, "list restored; last list position is: " + ((Integer)adapter.getCurrentList().getLastKey()));
                    lastKey = -1;
                    if(layoutState != null) {
                        dataBinding.customRecyclerView.getLayoutManager().onRestoreInstanceState(layoutState);
                        layoutState = null;
                        //dataBinding.customRecyclerView.scrollToPosition(lastKey);
                        Log.d(LOG_TAG, "last key using: " + lastKey);
                    }
                }
                Log.d(LOG_TAG, "last key scrolling: " + adapter.getCurrentList().getLastKey());
            }

            //public abstract boolean isLoading();

        });
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList() {
        //Add dividers between RecyclerView's row items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dataBinding.customRecyclerView.addItemDecoration(dividerItemDecoration);
        //Initializing Adapter
        adapter = new CustomAdapter(this);
        dataBinding.customRecyclerView.setAdapter(adapter);
        //private Parcelable state;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        dataBinding.customRecyclerView.setHasFixedSize(true);
        dataBinding.customRecyclerView.setLayoutManager(linearLayoutManager);

    }


    private void log(String msg) {
        Log.d(LOG_TAG,msg);
    }

    private void updatePokemons() {
        progressDoalog.show();

        viewModel.triggerSearchPokemons(dataBinding.editText.getText().toString().trim());
        viewModel.getPokemons().observe(this, list -> {
            if (list != null && !list.isEmpty()) {
                Log.d(LOG_TAG, "photo list size: " + list.size());
                if(list.get(0) != null)
                Log.d(LOG_TAG, "first pokemon: " + list.get(0).getName());
                else {
                    Log.d(LOG_TAG, "first pokemon is null");
                }
                //int lastKey = (Integer)(adapter.getCurrentList().getLastKey());
                //Log.d(LOG_TAG, "last key: " + lastKey);
                if(justDownloaded) {
                    justDownloaded = false;
                    //dataBinding.customRecyclerView.scrollToPosition(lastKey);
                    //lastKey = -1;
                    getImage(list.get((Integer)list.getLastKey()).getId());
                }
            }

            adapter.submitList(list);
            progressDoalog.dismiss();

        });


        viewModel.getNetworkErrors().observe(this, responseState -> {
            String errorMsg = "";
            switch (responseState.value) {
                case ResponseState.NO_NETWORK:
                    scheduleDownload();
                    lastKey = (Integer)adapter.getCurrentList().getLastKey();
                    layoutState = dataBinding.customRecyclerView.getLayoutManager().onSaveInstanceState();
                case ResponseState.DOWNLOAD_FAILURE:
                case ResponseState.DOWNLOAD_UNSUCCESSFUL:
                case ResponseState.EMPTY_DATA:
                    errorMsg = getString(R.string.download_failed);
                    break;

            }

            if (!errorMsg.isEmpty())
                Toast.makeText(this, "\uD83D\uDE28 Wooops " + errorMsg, Toast.LENGTH_LONG).show();
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(adapter != null) {
            lastKey = (Integer)adapter.getCurrentList().getLastKey();
            Log.d(LOG_TAG, "last key saving the list: " + lastKey);
            outState.putInt("lastKey",lastKey);
            outState.putParcelable("layoutState",dataBinding.customRecyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    private void scheduleDownload() {
        //if(!downloadScheduled) {
        Log.d(LOG_TAG,"scheduling download");
            WorkManager mWorkManager = WorkManager.getInstance(this);
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(PokemonDownloadWorker.class)
                    .setConstraints(constraints)
                    //.setInputData(new Data.Builder().putString("name",name)
                            //.put("mainActivity",this)
                     //       .build())

                    //.addTag(WORK_TAG)
                    //.setInitialDelay()
                    //.addTag(TAG_OUTPUT)
                    .build();
            //mWorkManager.enqueue(mRequest);
            mWorkManager.enqueueUniqueWork (WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    mRequest);

            //downloadScheduled = true;
        //}

    }

    @Override
    public void onPause() {
        super.onPause();
        getApplicationContext().getSharedPreferences("preferences", MODE_MULTI_PROCESS).edit().putBoolean("isActive", false).commit();;
        activityIsActive = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getApplicationContext().getSharedPreferences("preferences", MODE_MULTI_PROCESS).edit().putBoolean("isActive", false).commit();
        activityIsActive = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getApplicationContext().getSharedPreferences("preferences", MODE_MULTI_PROCESS).edit().putBoolean("isActive", true).commit();
        activityIsActive = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPepeEvent(PepeEvent event) {
        viewModel.triggerSearchPokemons(dataBinding.editText.getText().toString().trim());
        //getImage(1);
        justDownloaded = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void showNotification(Bitmap resource) {

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        createNotificationChannel(manager);
        Intent contentIntent = new Intent(getApplicationContext(), MainActivity.class);
        //contentIntent.putExtra("id", pokId);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                getApplicationContext(),NOTIFICATION_ID,contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),PRIMARY_CHANNEL_ID)
                .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(resource)//.bigLargeIcon(resource)
                        /*.setBigContentTitle("Notification Updated!")*/)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(Color.YELLOW)
                .setLargeIcon(resource)
                .setContentTitle("Pokemon Download Finished!")
                .setContentText("Finished downloading pokemon list")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        manager.notify(NOTIFICATION_ID,builder.build());

    }

    private void getImage(int id) {
        // TODO IMPLEMENT THE REAL TASK OF THE WORKER.
        //MainActivity.scheduleDelayedWork(2*60*1000,getApplicationContext());
        String path = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other-sprites/official-artwork/"+id+".png";
        RequestOptions options = new RequestOptions()
                .centerCrop()
                //.fitCenter()
                //.placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(path)
                //.dontAnimate()
                .apply(options)
                .into(new CustomTarget<Bitmap>(475,475) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        showNotification(resource);

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public void createNotificationChannel(NotificationManager manager) {
        manager = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel= new NotificationChannel(
                    PRIMARY_CHANNEL_ID,"Test Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("notifications for a job test app");
            manager.createNotificationChannel(notificationChannel);
        }
    }
}

// TODO IMPLEMENT THE THING I WROTE FOR THE REPOSITORY IN ORDER TO DOWNLOAD THE DATA WHEN WE RECOVER INTERNET CONNECTION ETC.
// TODO NOW ITS THE TIME WE SHOULD START WITH IT NOW. SINCE WE CAN CHECK WHETHER THE DB IS EMPTY ETC.