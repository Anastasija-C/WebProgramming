package mk.ukim.finki.wp.sep2023.service;

import mk.ukim.finki.wp.sep2023.model.Album;
import mk.ukim.finki.wp.sep2023.model.Genre;
import mk.ukim.finki.wp.sep2023.model.exceptions.InvalidAlbumIdException;
import mk.ukim.finki.wp.sep2023.model.exceptions.InvalidArtistIdException;

import java.time.LocalDate;
import java.util.List;

public interface AlbumService {

    /**
     * @return List of all albums in the database
     */
    List<Album> listAllAlbums();

    /**
     * returns the album with the given id
     *
     * @param id The id of the album that we want to obtain
     * @return
     * @throws InvalidAlbumIdException when there is no album with the given id
     */
    Album findById(Long id);

    /**
     * This method is used to create a new album, and save it in the database.
     *
     * @param name
     * @param details
     * @param dateOfPublishing
     * @param genre
     * @param artist
     * @return The album that is created. The id should be generated when the album is created.
     * @throws InvalidArtistIdException when there is no party with the given id
     */
    Album create(String name, String details, LocalDate dateOfPublishing, Genre genre, Long artist);

    /**
     * This method is used to update an album, and save it in the database.
     *
     * @param id The id of the album that is being edited
     * @param name
     * @param details
     * @param dateOfPublishing
     * @param genre
     * @param artist
     * @return The album that is updated.
     * @throws InvalidAlbumIdException when there is no album with the given id
     * @throws InvalidArtistIdException when there is no party with the given id
     */
    Album update(Long id, String name, String details, LocalDate dateOfPublishing, Genre genre, Long artist);

    /**
     * Method that should delete an album. If the id is invalid, it should throw InvalidAlbumIdException.
     *
     * @param id
     * @return The album that is deleted.
     * @throws InvalidAlbumIdException when there is no album with the given id
     */
    Album delete(Long id);

    /**
     * Method that should like an album. If the id is invalid, it should throw InvalidAlbumIdException.
     *
     * @param id
     * @return The album that is liked.
     * @throws InvalidAlbumIdException when there is no album with the given id
     */
    Album like(Long id);

    /**
     * The implementation of this method should use repository implementation for the filtering.
     *
     * @param yearsMoreThan that is used to filter the albums who are older than this value.
     *                        This param can be null, and is not used for filtering in this case.
     * @param genre         Used for filtering the album's genre.
     *                        This param can be null, and is not used for filtering in this case.
     * @return The albums that meet the filtering criteria
     */
    List<Album> listAlbumsYearsMoreThanAndGenre(Integer yearsMoreThan, Genre genre);
}
