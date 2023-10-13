package mk.ukim.finki.wp.sep2023.repository;

import mk.ukim.finki.wp.sep2023.model.Album;
import mk.ukim.finki.wp.sep2023.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findAllByDateOfPublishingIsGreaterThanEqualAndGenreEquals(LocalDate date, Genre genre);

    List<Album> findAllByDateOfPublishingIsGreaterThanEqual(LocalDate date);

    List<Album> findAllByGenreEquals(Genre genre);
}
