package com.ignacio.pokemonpagingconfig.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "pokemonTable")
public class RetroPokemon implements Parcelable {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    public RetroPokemon(int id, String name) {
        this.id = id;//(id > 807 && id < 10001 ? id+9193 : id);
        this.name = name;
        //this.lastRefresh = lastRefresh;
    }

    @Ignore
    public RetroPokemon(String name) {
        this.name = name;
    }

    //==============================================
    //getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /*public Date getLastRefresh() {
        return lastRefresh;
    }*/
    //=================================================
    //setters

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    //==================================================
    //parcelable
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
    }

    protected RetroPokemon(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<RetroPokemon> CREATOR = new Parcelable.Creator<RetroPokemon>() {
        @Override
        public RetroPokemon createFromParcel(Parcel source) {
            return new RetroPokemon(source);
        }

        @Override
        public RetroPokemon[] newArray(int size) {
            return new RetroPokemon[size];
        }
    };

    /*public void setLastRefresh(Date lastRefresh) {
        this.lastRefresh = lastRefresh;
    }*/

    //===============================================
    //parcelable

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null) {
            return false;
        }
        RetroPokemon pokemon = (RetroPokemon) obj;
        return name.equals(pokemon.name);
    }

}
