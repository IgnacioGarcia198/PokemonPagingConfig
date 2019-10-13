package com.ignacio.pokemonpagingconfig.api;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import static android.content.Context.NOTIFICATION_SERVICE;

public class PokemonDownloadWorker extends Worker {
    private static final String WORK_RESULT = "work_result";
    //private NotificationManager notificationManager;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    public static final int NOTIFICATION_ID = 0;
    NotificationManager manager;


    public PokemonDownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        String name = getInputData().getString("name");
        RetroPokemonRepository.getInstance(getApplicationContext()).searchPokemons(name);
        //Data taskData = getInputData();
        //String taskDataString = taskData.getString(MainActivity.MESSAGE_STATUS);
        //runTheJob();
        //showNotification("WorkManager", taskDataString != null ? taskDataString : "Message has been Sent");

        Data outputData = new Data.Builder().putString(WORK_RESULT, "Jobs Finished").build();

        return Result.success(outputData);

    }

    private void showNotification(Bitmap resource) {

        manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        createNotificationChannel();
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
                .setContentTitle("Job is finished!")
                .setContentText("Job finished execution")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        manager.notify(NOTIFICATION_ID,builder.build());
        //Log.d(LOG_TAG,"Job finished successfully");
        //jobFinished(params,false);
        //jobScheduler.cancel(MainActivity.INITIAL_JOB_ID);
        //jobScheduler =(JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);

    }


    private void runTheJob() {
        // TODO IMPLEMENT THE REAL TASK OF THE WORKER.
        //MainActivity.scheduleDelayedWork(2*60*1000,getApplicationContext());
        String path = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other-sprites/official-artwork/1.png";
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

    public void createNotificationChannel() {
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
