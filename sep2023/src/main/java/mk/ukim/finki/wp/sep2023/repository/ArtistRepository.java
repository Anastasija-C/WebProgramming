package mk.ukim.finki.wp.sep2023.repository;

import mk.ukim.finki.wp.sep2023.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
