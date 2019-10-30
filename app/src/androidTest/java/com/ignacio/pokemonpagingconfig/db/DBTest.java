package com.ignacio.pokemonpagingconfig.db;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

public abstract class DBTest {
    protected RetroPokemonDatabase db;

    @Before
    public void initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                RetroPokemonDatabase.class).build();
    }

    @After
    public void closeDb() {
        db.close();
    }


}
