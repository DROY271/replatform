package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    private final PlatformTransactionManager moviesTm;
    private final PlatformTransactionManager albumsTm;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures, @Qualifier("movies") PlatformTransactionManager moviesTm, @Qualifier("albums") PlatformTransactionManager albumsTm) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.moviesTm = moviesTm;
        this.albumsTm = albumsTm;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        setupMovies();
        setupAlbums();
        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());
        return "setup";
    }

    private void setupAlbums() {
        TransactionStatus albumTx = albumsTm.getTransaction(null);
        for (Album album : albumFixtures.load()) {
            albumsBean.addAlbum(album);
        }
        albumsTm.commit(albumTx);
    }

    private void setupMovies() {
        TransactionStatus movieTx = moviesTm.getTransaction(null);
        for (Movie movie : movieFixtures.load()) {
            moviesBean.addMovie(movie);
        }
        moviesTm.commit(movieTx);
    }
}
