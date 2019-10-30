package com.ignacio.pokemonpagingconfig.db;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.ignacio.pokemonpagingconfig.model.RetroPokemon;


import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PokemonDao {

    @Insert(onConflict = REPLACE)
    void save(RetroPokemon pokemon);

    @Insert(onConflict = REPLACE)
    void saveVarious(List<RetroPokemon> pokemons);

    @Query("SELECT * FROM pokemonTable")
    DataSource.Factory<Integer, RetroPokemon> getAllPokemonsFromRoom();

    @Query("SELECT * FROM pokemonTable WHERE name LIKE :name")
    DataSource.Factory<Integer, RetroPokemon> findPokemonsByNameInRoom(String name);

    @Query("SELECT * FROM pokemonTable WHERE id LIKE :name OR name LIKE :name")
    DataSource.Factory<Integer, RetroPokemon> findPokemonsByNameOrIdInRoom(String name);

    @Query("SELECT * FROM pokemonTable WHERE id = :pokId")
    DataSource.Factory<Integer, RetroPokemon> findPokemonsByIdInRoom(int pokId);

    @Query("SELECT * FROM pokemonTable LIMIT 1")
    RetroPokemon getFirstPokemonFromRoom();

    @Query("DELETE FROM pokemonTable")
    void deleteAll();
}
