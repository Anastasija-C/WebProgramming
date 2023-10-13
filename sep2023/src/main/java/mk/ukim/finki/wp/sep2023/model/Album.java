package mk.ukim.finki.wp.sep2023.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Album {

    public Album() {
    }

    public Album(String name, String details, LocalDate dateOfPublishing, Genre genre, Artist artist) {
        this.name = name;
        this.details = details;
        this.dateOfPublishing = dateOfPublishing;
        this.genre = genre;
        this.artist = artist;
        this.likes = 0;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String details;

    private LocalDate dateOfPublishing;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToOne(fetch = FetchType.EAGER)
    private Artist artist;

    private Integer likes = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDate getDateOfPublishing() {
        return dateOfPublishing;
    }

    public void setDateOfPublishing(LocalDate dateOfPublishing) {
        this.dateOfPublishing = dateOfPublishing;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }
}
