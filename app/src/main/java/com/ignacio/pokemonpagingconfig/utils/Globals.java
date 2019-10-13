package com.ignacio.pokemonpagingconfig.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Clase con utilidades generales para la aplicación
 */
public class Globals {

    public static final String PREFERENCE_FILE_NAME = "customPrefs.pref"; // SharedPreferences file

    /**
     * Comprueba si tenemos conexión a internet
     * @param context contexto de a app
     * @return boolean
     */
    public static boolean netWorkIsOk(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (conMgr != null) {
            networkInfo = conMgr.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }
}
