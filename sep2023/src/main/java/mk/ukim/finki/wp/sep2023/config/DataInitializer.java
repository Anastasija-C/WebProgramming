package mk.ukim.finki.wp.sep2023.config;

import mk.ukim.finki.wp.sep2023.model.Genre;
import mk.ukim.finki.wp.sep2023.service.AlbumService;
import mk.ukim.finki.wp.sep2023.service.ArtistService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Component
public class DataInitializer {

    private final ArtistService artistService;

    private final AlbumService albumService;

    public DataInitializer(ArtistService artistService, AlbumService albumService) {
        this.artistService = artistService;
        this.albumService = albumService;
    }

    private Genre randomizeGenre(int i) {
        if (i % 2 == 0) return Genre.POP;
        return Genre.ROCK;
    }

    @PostConstruct
    public void initData() {
        for (int i = 1; i < 6; i++) {
            this.artistService.create("Artist: " + i);
        }

        for (int i = 1; i < 11; i++) {
            this.albumService.create("Album: " + i, "Detail: " + i, LocalDate.now().minusYears(25 + i), this.randomizeGenre(i), this.artistService.listAll().get((i - 1) % 5).getId());
        }
    }
}
