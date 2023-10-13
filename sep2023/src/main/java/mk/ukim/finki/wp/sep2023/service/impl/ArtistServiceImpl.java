package mk.ukim.finki.wp.sep2023.service.impl;

import mk.ukim.finki.wp.sep2023.model.Artist;
import mk.ukim.finki.wp.sep2023.model.exceptions.InvalidArtistIdException;
import mk.ukim.finki.wp.sep2023.repository.ArtistRepository;
import mk.ukim.finki.wp.sep2023.service.ArtistService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistServiceImpl(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public Artist findById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(InvalidArtistIdException::new);
    }

    @Override
    public List<Artist> listAll() {
        return artistRepository.findAll();
    }

    @Override
    public Artist create(String name) {
        return artistRepository.save(new Artist(name));
    }
}
