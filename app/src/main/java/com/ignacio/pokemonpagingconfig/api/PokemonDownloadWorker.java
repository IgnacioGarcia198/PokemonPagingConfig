package com.ignacio.pokemonpagingconfig.api;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ignacio.pokemonpagingconfig.MainActivity;
import com.ignacio.pokemonpagingconfig.R;
import com.ignacio.pokemonpagingconfig.data.RetroPokemonRepository;
import com.ignacio.pokemonpagingconfig.utils.PepeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class PokemonDownloadWorker extends Worker {
    private static final String WORK_RESULT = "work_result";
    //private NotificationManager notificationManager;

    //NotificationManager manager;


    public PokemonDownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        //String name = getInputData().getString("name");
        if(MainActivity.activityIsActive) {
            // do the job now!!! with an interface we
            EventBus.getDefault().post(new PepeEvent(true));
        }
        //RetroPokemonRepository.getInstance(getApplicationContext()).searchPokemons(name);


        Data outputData = new Data.Builder().putString(WORK_RESULT, "Jobs Finished").build();

        return Result.success(outputData);

    }

}
