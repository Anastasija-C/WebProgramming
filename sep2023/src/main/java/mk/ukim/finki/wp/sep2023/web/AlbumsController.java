package mk.ukim.finki.wp.sep2023.web;

import mk.ukim.finki.wp.sep2023.model.Album;
import mk.ukim.finki.wp.sep2023.model.Genre;
import mk.ukim.finki.wp.sep2023.service.AlbumService;
import mk.ukim.finki.wp.sep2023.service.ArtistService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Controller
public class AlbumsController {

    private final AlbumService albumService;
    private final ArtistService artistService;

    public AlbumsController(AlbumService albumService, ArtistService artistService) {
        this.albumService = albumService;
        this.artistService = artistService;
    }

    /**
     * This method should use the "list.html" template to display all albums.
     * The method should be mapped on paths '/' and '/albums'.
     * The arguments that this method takes are optional and can be 'null'.
     * In the case when the arguments are not passed (both are 'null') all albums should be displayed.
     * If one, or both of the arguments are not 'null', the albums that are the result of the call
     * to the method 'listAlbumsYearsMoreThanAndGenre' from the AlbumService should be displayed.
     *
     * @param years
     * @param genre
     * @return The view "list.html".
     */
    @GetMapping(value = {"/", "/albums"})
    public String showAlbums(@RequestParam(required = false) Integer years,
                             @RequestParam(required = false) Genre genre,
                             Model model) {
        List<Album> albums = albumService.listAllAlbums();
        if (years == null && genre == null) {
            this.albumService.listAllAlbums();
        } else {
            this.albumService.listAlbumsYearsMoreThanAndGenre(years, genre);
        }
        model.addAttribute("albums", albums);
        model.addAttribute("genres", Genre.values());
        return "list";
    }

    /**
     * This method should display the "form.html" template.
     * The method should be mapped on path '/albums/add'.
     *
     * @return The view "form.html".
     */
    @GetMapping("/albums/add")
    public String showAdd(Model model) {
        //model.addAttribute(new Album());
        model.addAttribute("genres", Genre.values());
        model.addAttribute("artists", artistService.listAll());
        return "form";
    }

    /**
     * This method should display the "form.html" template.
     * However, in this case all 'input' elements should be filled with the appropriate value for the album that is updated.
     * The method should be mapped on path '/albums/[id]/edit'.
     *
     * @return The view "form.html".
     */
    @GetMapping("/albums/{id}/edit")
    public String showEdit(@PathVariable Long id, Model model) {
        Album album = this.albumService.findById(id);
        model.addAttribute("albums", album);
        model.addAttribute("genres", Genre.values());
        model.addAttribute("artist", artistService.listAll());
        return "form";
    }

    /**
     * This method should create an album given the arguments it takes.
     * The method should be mapped on path '/albums'.
     * After the album is created, all albums should be displayed.
     *
     * @return The view "list.html".
     */
    @PostMapping("/albums")
    public String create(@RequestParam String name,
                         @RequestParam String details,
                         @RequestParam @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate dateOfPublishing,
                         @RequestParam Genre genre,
                         @RequestParam Long artist) {
        this.albumService.create(name, details, dateOfPublishing, genre, artist);
        return "redirect:/albums";
    }

    /**
     * This method should update an album given the arguments it takes.
     * The method should be mapped on path '/albums/[id]'.
     * After the album is updated, all albums should be displayed.
     *
     * @return The view "list.html".
     */
    @PostMapping("/albums/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String details,
                         @RequestParam @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate dateOfPublishing,
                         @RequestParam Genre genre,
                         @RequestParam Long artist) {
        this.albumService.update(id, name, details, dateOfPublishing, genre, artist);
        return "redirect:/albums";
    }

    /**
     * This method should delete the album that has the appropriate identifier.
     * The method should be mapped on path '/albums/[id]/delete'.
     * After the album is deleted, all albums should be displayed.
     *
     * @return The view "list.html".
     */
    @PostMapping("/albums/{id}/delete")
    public String delete(@PathVariable Long id) {
        this.albumService.delete(id);
        return "redirect:/albums";
    }

    /**
     * This method should increase the number of likes of the appropriate album by 1.
     * The method should be mapped on path '/albums/[id]/like'.
     * After the operation, all albums should be displayed.
     *
     * @return The view "list.html".
     */
    @PostMapping("/albums/{id}/like")
    public String like(@PathVariable Long id) {
        this.albumService.like(id);
        return "redirect:/albums";
    }
}
