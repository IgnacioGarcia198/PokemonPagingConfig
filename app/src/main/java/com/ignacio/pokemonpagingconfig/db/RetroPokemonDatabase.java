package com.ignacio.pokemonpagingconfig.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import com.ignacio.pokemonpagingconfig.model.RetroPokemon;

@Database(entities = {RetroPokemon.class},version = 1,exportSchema = false)
public abstract class RetroPokemonDatabase extends RoomDatabase {

    // --- SINGLETON ---
    private static volatile RetroPokemonDatabase INSTANCE;

    // --- DAO ---
    public abstract PokemonDao retroPhotoDao();

    public static RetroPokemonDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context,
                    RetroPokemonDatabase.class, "MyDatabase.db")
                    .build();
        }
        return INSTANCE;
    }

}
