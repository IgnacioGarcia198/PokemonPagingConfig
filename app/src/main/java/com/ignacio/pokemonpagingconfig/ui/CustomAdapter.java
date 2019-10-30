package com.ignacio.pokemonpagingconfig.ui;

import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;
import android.content.Context;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

import com.bumptech.glide.request.target.Target;
import com.ignacio.pokemonpagingconfig.model.RetroPokemon;
import com.ignacio.pokemonpagingconfig.R;
import com.ignacio.pokemonpagingconfig.databinding.CustomRowBinding;
import com.ignacio.pokemonpagingconfig.utils.Globals;

public class CustomAdapter extends PagedListAdapter<RetroPokemon,CustomAdapter.CustomViewHolder> {

    private Context context;
    private boolean errorMsgShown;
    /**
     * DiffUtil to compare the RetroPokemon data (old and new)
     * for issuing notify commands suitably to update the list
     */
    private static DiffUtil.ItemCallback<RetroPokemon> POKEMON_COMPARATOR
            = new DiffUtil.ItemCallback<RetroPokemon>() {
        @Override
        public boolean areItemsTheSame(RetroPokemon oldItem, RetroPokemon newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(RetroPokemon oldItem, RetroPokemon newItem) {
            return oldItem.equals(newItem);
        }
    };

    public CustomAdapter(Context context) {
        super(POKEMON_COMPARATOR);
        this.context = context;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CustomRowBinding dataBinding;
        //private TextView txtTitle;
        //private ImageView coverImage;

        CustomViewHolder(CustomRowBinding dataBinding) {
            super(dataBinding.getRoot());
            this.dataBinding = dataBinding;
            itemView.setOnClickListener(this);
        }

        void bind(RetroPokemon pokemon) {
            if(pokemon != null) {
                dataBinding.setRetroPokemon(pokemon);
                /*RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round);
                Glide.with(context).load( "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other-sprites/official-artwork/" + pokemon.getId()+ ".png"
                ).apply(options).into(dataBinding.coverImage);*/
//===========================================================================================
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.transparent_pokeball_padding)
                        .error(R.drawable.pokeball_supertransparent_padding);
                Glide.with(context).load(
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other-sprites/official-artwork/" +
                                pokemon.getId()+ ".png")
                        .listener(new RequestListener<Drawable>() {

                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                if(errorMsgShown) {
                                    return false;
                                }
                                if(!Globals.netWorkIsOk(context)) {
                                    Toast.makeText(context, context.getString(R.string.no_internet_notice), Toast.LENGTH_SHORT).show();
                                    // TODO WILL FINISH APP ETC? SHOULD RELATE THIS TO THE CHECK IN BOUNDARYCALLBACK.
                                    // TODO THIS IS MORE USEFUL SINCE ITS FASTER TO REACH THE USER ETC.
                                }
                                else {
                                    Toast.makeText(context, context.getString(R.string.could_not_load_some_pokemon_bad_internet), Toast.LENGTH_SHORT).show();
                                }
                                errorMsgShown = true;
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .apply(options).into(dataBinding.coverImage);

            }
            else {
                dataBinding.title.setText(R.string.not_data_yet);
                dataBinding.pokid.setText("");
                dataBinding.coverImage.setImageResource(R.mipmap.ic_launcher_round);
            }
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CustomRowBinding binding = CustomRowBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false);
        return new CustomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public int getPosition() {
        return getPosition();
    }

}
