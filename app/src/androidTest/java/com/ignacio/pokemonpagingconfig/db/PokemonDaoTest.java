package com.ignacio.pokemonpagingconfig.db;


import androidx.lifecycle.LiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ignacio.pokemonpagingconfig.model.RetroPokemon;
import com.ignacio.pokemonpagingconfig.utils.LiveDataTestUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PokemonDaoTest extends DBTest {

    /**
     * Tests the db on memory
     * @throws
     */
    @Test
    public void insertAndLoad() throws InterruptedException {
        PokemonDao dao = db.retroPhotoDao();
        final RetroPokemon[] photos = {new RetroPokemon(101, 1, "photo1title", "url", "thumbnailUrl", new Date()),
                new RetroPokemon(101, 2, "photo2title", "url", "thumbnailUrl", new Date()),
                new RetroPokemon(101, 3, "photo3title", "url", "thumbnailUrl", new Date())};
        dao.saveVarious(photos);

        LiveData<List<RetroPokemon>> retrievedPhotos = dao.findPhotosByNameAndAlbumInRoom("pho",101);//LiveDataTestUtil.getValue(db.retroPhotoDao().findPhotosByNameAndAlbumInRoom("",101));
        assert(LiveDataTestUtil.getValue(retrievedPhotos).get(0).getTitle().compareTo("photo1title") == 0);
    }

    /*@Test
    public void makeDatabase() {
        initDb();
        //assert(db!=null);
        PokemonDao dao = db.retroPhotoDao();
        //assert(dao != null);
        final RetroPokemon[] photos = {new RetroPokemon(101, 1, "photo1title", "url", "thumbnailUrl", new Date()),
                new RetroPokemon(101, 2, "photo2title", "url", "thumbnailUrl", new Date()),
                new RetroPokemon(101, 3, "photo3title", "url", "thumbnailUrl", new Date())};
        dao.saveVarious(photos);
        LiveData<List<RetroPokemon>> liveDataResult = dao.findPhotosByNameAndAlbumInRoom("pho",101);
        //assert(liveDataResult != null);
        List<RetroPokemon> list = liveDataResult.getValue();
        //assert(list != null);
        assert(list.get(0).getTitle().compareTo("photo1title") == 0);
        closeDb();
    }*/
}
