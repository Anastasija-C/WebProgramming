package mk.ukim.finki.wp.sep2023.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class AddOrEditAlbum extends AbstractPage {

    private WebElement name;
    private WebElement details;
    private WebElement dateOfPublishing;
    private WebElement genre;
    private WebElement artist;
    private WebElement submit;

    public AddOrEditAlbum(WebDriver driver) {
        super(driver);
    }

    public static ItemsPage add(WebDriver driver, String addPath, String name, String details, String dateOfPublishing, String genre, String artist) {
        get(driver, addPath);
        assertRelativeUrl(driver, addPath);

        AddOrEditAlbum addOrEditAlbum = PageFactory.initElements(driver, AddOrEditAlbum.class);
        addOrEditAlbum.assertNoError();
        addOrEditAlbum.name.sendKeys(name);
        addOrEditAlbum.details.sendKeys(details);
        addOrEditAlbum.dateOfPublishing.sendKeys(dateOfPublishing);

        Select selectGenre = new Select(addOrEditAlbum.genre);
        selectGenre.selectByValue(genre);

        Select selectArtist = new Select(addOrEditAlbum.artist);
        selectArtist.selectByValue(artist);

        addOrEditAlbum.submit.click();
        return PageFactory.initElements(driver, ItemsPage.class);
    }

    public static ItemsPage update(WebDriver driver, WebElement editButton, String name, String details, String dateOfPublishing, String genre, String artist) {
        String href = editButton.getAttribute("href");
        System.out.println(href);
        editButton.click();
        assertAbsoluteUrl(driver, href);

        AddOrEditAlbum addOrEditAlbum = PageFactory.initElements(driver, AddOrEditAlbum.class);
        addOrEditAlbum.name.clear();
        addOrEditAlbum.name.sendKeys(name);
        addOrEditAlbum.details.clear();
        addOrEditAlbum.details.sendKeys(details);
        addOrEditAlbum.dateOfPublishing.clear();
        addOrEditAlbum.dateOfPublishing.sendKeys(dateOfPublishing);

        Select selectGenre = new Select(addOrEditAlbum.genre);
        selectGenre.selectByValue(genre);

        Select selectArtist = new Select(addOrEditAlbum.artist);
        selectArtist.selectByValue(artist);

        addOrEditAlbum.submit.click();
        return PageFactory.initElements(driver, ItemsPage.class);
    }
}