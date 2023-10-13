package mk.ukim.finki.wp.sep2023.service.impl;

import mk.ukim.finki.wp.sep2023.model.Album;
import mk.ukim.finki.wp.sep2023.model.Artist;
import mk.ukim.finki.wp.sep2023.model.Genre;
import mk.ukim.finki.wp.sep2023.model.exceptions.InvalidAlbumIdException;
import mk.ukim.finki.wp.sep2023.model.exceptions.InvalidArtistIdException;
import mk.ukim.finki.wp.sep2023.repository.AlbumRepository;
import mk.ukim.finki.wp.sep2023.repository.ArtistRepository;
import mk.ukim.finki.wp.sep2023.service.AlbumService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    public AlbumServiceImpl(AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    public List<Album> listAllAlbums() {
        return albumRepository.findAll();
    }

    @Override
    public Album findById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(InvalidAlbumIdException::new);
    }

    @Override
    public Album create(String name, String details, LocalDate dateOfPublishing, Genre genre, Long artist) {
        Artist artist1 = artistRepository.findById(artist)
                .orElseThrow(InvalidArtistIdException::new);
        return albumRepository.save(new Album(name, details, dateOfPublishing, genre, artist1));
    }

    @Override
    public Album update(Long id, String name, String details, LocalDate dateOfPublishing, Genre genre, Long artist) {
        Album album = albumRepository.findById(id)
                .orElseThrow(InvalidAlbumIdException::new);
        Artist artist1 = artistRepository.findById(artist)
                .orElseThrow(InvalidArtistIdException::new);
        album.setName(name);
        album.setDetails(details);
        album.setDateOfPublishing(dateOfPublishing);
        album.setGenre(genre);
        album.setArtist(artist1);
        albumRepository.save(album);
        return album;
    }

    @Override
    public Album delete(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(InvalidAlbumIdException::new);
        albumRepository.delete(album);
        return album;
    }

    @Override
    public Album like(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(InvalidAlbumIdException::new);
        album.setLikes(album.getLikes() + 1);
        return albumRepository.save(album);
    }

    @Override
    public List<Album> listAlbumsYearsMoreThanAndGenre(Integer yearsMoreThan, Genre genre) {
        if(yearsMoreThan!=null && genre!=null){
            return albumRepository.findAllByDateOfPublishingIsGreaterThanEqualAndGenreEquals(
                    LocalDate.now().minusYears(yearsMoreThan), genre);
        } else if (yearsMoreThan!=null){
            return albumRepository.findAllByDateOfPublishingIsGreaterThanEqual(
                    LocalDate.now().minusYears(yearsMoreThan));
        } else if (genre!=null) {
            return albumRepository.findAllByGenreEquals(genre);
        } else
            return albumRepository.findAll();
    }
}


