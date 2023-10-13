package mk.ukim.finki.wp.sep2023;

import com.fasterxml.jackson.core.JsonProcessingException;
import mk.ukim.finki.wp.exam.util.CodeExtractor;
import mk.ukim.finki.wp.sep2023.model.Album;
import mk.ukim.finki.wp.sep2023.model.Artist;
import mk.ukim.finki.wp.sep2023.model.Genre;
import mk.ukim.finki.wp.sep2023.selenium.AbstractPage;
import mk.ukim.finki.wp.sep2023.selenium.AddOrEditAlbum;
import mk.ukim.finki.wp.sep2023.selenium.ItemsPage;
import mk.ukim.finki.wp.sep2023.selenium.LoginPage;
import mk.ukim.finki.wp.sep2023.service.AlbumService;
import mk.ukim.finki.wp.sep2023.service.ArtistService;
import mk.ukim.finki.wp.exam.util.ExamAssert;
import mk.ukim.finki.wp.exam.util.SubmissionHelper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SeleniumScenarioTest {

    static {
        SubmissionHelper.exam = "wp-exam-sep23";
        SubmissionHelper.index = "181006";
    }

    @Autowired
    ArtistService artistService;

    @Autowired
    AlbumService albumService;

    @Order(1)
    @Test
    public void test_list_15pt() {
        SubmissionHelper.startTest("test-list-15");
        List<Album> albums = this.albumService.listAllAlbums();
        int itemNum = albums.size();

        ExamAssert.assertNotEquals("Empty db", 0, itemNum);

        ItemsPage listPage = ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");
        listPage.assertItems(itemNum);

        SubmissionHelper.endTest();
    }

    @Order(2)
    @Test
    public void test_filter_5pt() {
        SubmissionHelper.startTest("test-filter-5");
        ItemsPage listPage = ItemsPage.to(this.driver);

        listPage.filter("", "");
        listPage.assertItems(10);

        listPage.filter("30", "");
        listPage.assertItems(5);

        listPage.filter("", Genre.ROCK.name());
        listPage.assertItems(5);

        listPage.filter("30", Genre.ROCK.name());
        listPage.assertItems(3);

        SubmissionHelper.endTest();
    }

    @Order(3)
    @Test
    public void test_filter_service_5pt() {
        SubmissionHelper.startTest("test-filter-service-5");

        ExamAssert.assertEquals("without filter", 10, this.albumService.listAlbumsYearsMoreThanAndGenre(null, null).size());
        ExamAssert.assertEquals("by years more than only", 5, this.albumService.listAlbumsYearsMoreThanAndGenre(30, null).size());
        ExamAssert.assertEquals("by genre only", 5, this.albumService.listAlbumsYearsMoreThanAndGenre(null, Genre.POP).size());
        ExamAssert.assertEquals("by years more than and genre", 3, this.albumService.listAlbumsYearsMoreThanAndGenre(30, Genre.ROCK).size());

        SubmissionHelper.endTest();
    }

    @Order(4)
    @Test
    public void test_create_10pt() {
        SubmissionHelper.startTest("test-create-10");
        List<Artist> artists = this.artistService.listAll();
        List<Album> albums = this.albumService.listAllAlbums();

        int itemNum = albums.size();
        ItemsPage listPage = null;

        try {
            LoginPage loginPage = LoginPage.openLogin(this.driver);
            listPage = LoginPage.doLogin(this.driver, loginPage, admin, admin);
        } catch (Exception e) {
        }

        String date = LocalDate.now().minusYears(30).toString();

        listPage = AddOrEditAlbum.add(this.driver, ADD_URL, "testName", "testDetails", date, Genre.ROCK.name(), artists.get(0).getId().toString());
        AbstractPage.assertRelativeUrl(this.driver, LIST_URL);
        listPage.assertNoError();
        listPage.assertItems(itemNum + 1);

        SubmissionHelper.endTest();
    }

    @Order(5)
    @Test
    public void test_create_mvc_10pt() throws Exception {
        SubmissionHelper.startTest("test-create-mvc-10");
        List<Artist> artists = this.artistService.listAll();
        List<Album> albums = this.albumService.listAllAlbums();

        int itemNum = albums.size();

        MockHttpServletRequestBuilder addRequest = MockMvcRequestBuilders
                .post("/albums")
                .param("name", "testName")
                .param("details", "testDetails")
                .param("dateOfPublishing", LocalDate.now().minusDays(30).toString())
                .param("genre", Genre.ROCK.name())
                .param("artist", artists.get(0).getId().toString());

        this.mockMvc.perform(addRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(LIST_URL));

        albums = this.albumService.listAllAlbums();
        ExamAssert.assertEquals("Number of items", itemNum + 1, albums.size());

        addRequest = MockMvcRequestBuilders
                .get("/albums/add");

        this.mockMvc.perform(addRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(new ViewMatcher("form")));

        SubmissionHelper.endTest();
    }

    @Order(6)
    @Test
    public void test_edit_10pt() {
        SubmissionHelper.startTest("test-edit-10");
        List<Artist> artists = this.artistService.listAll();
        List<Album> albums = this.albumService.listAllAlbums();

        int itemNum = albums.size();

        ItemsPage listPage = null;
        try {
            LoginPage loginPage = LoginPage.openLogin(this.driver);
            listPage = LoginPage.doLogin(this.driver, loginPage, admin, admin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!LIST_URL.equals(this.driver.getCurrentUrl())) {
            System.err.println("Reloading items page");
            listPage = ItemsPage.to(this.driver);
        }

        listPage = AddOrEditAlbum.update(this.driver, listPage.getEditButtons().get(itemNum - 1), "testName", "testDetails", LocalDate.now().minusYears(30).toString(), Genre.ROCK.name(), artists.get(0).getId().toString());
        listPage.assertNoError();

        AbstractPage.assertRelativeUrl(this.driver, LIST_URL);
        if (listPage.assertItems(itemNum)) {
            ExamAssert.assertEquals("The updated album name is not as expected.", "testName", listPage.getRows().get(itemNum - 1).findElements(By.tagName("td")).get(0).getText().trim());
        }

        SubmissionHelper.endTest();
    }

    @Order(7)
    @Test
    public void test_edit_mvc_10pt() throws Exception {
        SubmissionHelper.startTest("test-edit-mvc-10");
        List<Artist> artists = this.artistService.listAll();
        List<Album> albums = this.albumService.listAllAlbums();

        int itemNum = albums.size();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/albums/" + albums.get(itemNum - 1).getId())
                .param("name", "testName")
                .param("details", "testDetails")
                .param("dateOfPublishing", LocalDate.now().minusYears(30).toString())
                .param("genre", Genre.POP.name())
                .param("artist", artists.get(0).getId().toString());

        this.mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(LIST_URL));

        albums = this.albumService.listAllAlbums();
        ExamAssert.assertEquals("Number of items", itemNum, albums.size());
        ExamAssert.assertEquals("The updated album name is not as expected.", "testName", albums.get(itemNum - 1).getName());

        request = MockMvcRequestBuilders
                .get("/albums/" + albums.get(itemNum - 1).getId() + "/edit");

        this.mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(new ViewMatcher("form")));

        SubmissionHelper.endTest();
    }

    @Order(8)
    @Test
    public void test_delete_5pt() throws Exception {
        SubmissionHelper.startTest("test-delete-5");
        List<Album> albums = this.albumService.listAllAlbums();
        int itemNum = albums.size();

        ItemsPage listPage = null;
        try {
            LoginPage loginPage = LoginPage.openLogin(this.driver);
            listPage = LoginPage.doLogin(this.driver, loginPage, admin, admin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!LIST_URL.equals(this.driver.getCurrentUrl())) {
            System.err.println("Reloading items page");
            listPage = ItemsPage.to(this.driver);
        }

        listPage.getDeleteButtons().get(itemNum - 1).click();
        listPage.assertNoError();

        AbstractPage.assertRelativeUrl(this.driver, LIST_URL);
        listPage.assertItems(itemNum - 1);

        SubmissionHelper.endTest();
    }

    @Order(9)
    @Test
    public void test_delete_mvc_5pt() throws Exception {
        SubmissionHelper.startTest("test-delete-5");
        List<Album> albums = this.albumService.listAllAlbums();
        int itemNum = albums.size();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/albums/" + albums.get(itemNum - 1).getId() + "/delete");

        this.mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(LIST_URL));

        albums = this.albumService.listAllAlbums();
        ExamAssert.assertEquals("Number of items", itemNum - 1, albums.size());

        SubmissionHelper.endTest();
    }

    @Order(10)
    @Test
    public void test_security_urls_10pt() {
        SubmissionHelper.startTest("test-security-urls-10");
        List<Album> albums = this.albumService.listAllAlbums();
        String editUrl = "/albums/" + albums.get(0).getId() + "/edit";

        ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");

        AbstractPage.get(this.driver, LIST_URL);
        AbstractPage.assertRelativeUrl(this.driver, LIST_URL);
        AbstractPage.get(this.driver, ADD_URL);
        AbstractPage.assertRelativeUrl(this.driver, LOGIN_URL);
        AbstractPage.get(this.driver, editUrl);
        AbstractPage.assertRelativeUrl(this.driver, LOGIN_URL);
        AbstractPage.get(this.driver, "/random");
        AbstractPage.assertRelativeUrl(this.driver, LOGIN_URL);

        LoginPage loginPage = LoginPage.openLogin(this.driver);
        LoginPage.doLogin(this.driver, loginPage, admin, admin);
        AbstractPage.assertRelativeUrl(this.driver, LIST_URL);

        AbstractPage.get(this.driver, LIST_URL);
        AbstractPage.assertRelativeUrl(this.driver, LIST_URL);

        AbstractPage.get(this.driver, ADD_URL);
        AbstractPage.assertRelativeUrl(this.driver, ADD_URL);

        AbstractPage.get(this.driver, editUrl);
        AbstractPage.assertRelativeUrl(this.driver, editUrl);

        LoginPage.logout(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");

        SubmissionHelper.endTest();
    }

    @Order(11)
    @Test
    public void test_security_buttons_10pt() {
        SubmissionHelper.startTest("test-security-buttons-10");
        List<Album> albums = this.albumService.listAllAlbums();
        int itemNum = albums.size();

        ItemsPage albumsPage = ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");
        albumsPage.assertButtons(0, 0, 0, 0);

        LoginPage loginPage1 = LoginPage.openLogin(this.driver);
        albumsPage = LoginPage.doLogin(this.driver, loginPage1, admin, admin);
        albumsPage.assertButtons(itemNum, itemNum, 1, 0);
        LoginPage.logout(this.driver);

        LoginPage loginPage2 = LoginPage.openLogin(this.driver);
        albumsPage = LoginPage.doLogin(this.driver, loginPage2, user, user);
        albumsPage.assertButtons(0, 0, 0, itemNum);
        LoginPage.logout(this.driver);
        SubmissionHelper.endTest();
    }

    @Order(12)
    @Test
    public void test_like_3pt() throws Exception {
        SubmissionHelper.startTest("test-like-3");
        List<Album> albums = this.albumService.listAllAlbums();

        int itemNum = albums.size();

        ItemsPage listPage = null;
        try {
            LoginPage loginPage = LoginPage.openLogin(this.driver);
            listPage = LoginPage.doLogin(this.driver, loginPage, user, user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!LIST_URL.equals(this.driver.getCurrentUrl())) {
            System.err.println("Reloading items page");
            listPage = ItemsPage.to(this.driver);
        }

        listPage.getLikeButtons().get(itemNum - 1).click();
        listPage.assertNoError();

        AbstractPage.assertRelativeUrl(this.driver, LIST_URL);
        ExamAssert.assertEquals("The updated album likes number is not as expected.", "1", listPage.getRows().get(itemNum - 1).findElements(By.tagName("td")).get(5).getText().trim());

        SubmissionHelper.endTest();
    }

    @Order(13)
    @Test
    public void test_like_mvc_2pt() throws Exception {
        SubmissionHelper.startTest("test-like-mvc-2");
        List<Album> albums = this.albumService.listAllAlbums();

        int itemNum = albums.size();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/albums/" + albums.get(0).getId() + "/like");

        this.mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(LIST_URL));

        albums = this.albumService.listAllAlbums();
        ExamAssert.assertEquals("Number of likes", albums.get(0).getLikes(), 1);

        SubmissionHelper.endTest();
    }

    private HtmlUnitDriver driver;
    private MockMvc mockMvc;

    private static String admin = "admin";
    private static String user = "user";

    @BeforeEach
    private void setup(WebApplicationContext wac) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.driver = new HtmlUnitDriver(true);
    }

    @AfterEach
    public void destroy() {
        if (this.driver != null) {
            this.driver.close();
        }
    }

    @AfterAll
    public static void finalizeAndSubmit() throws JsonProcessingException {
        CodeExtractor.submitSourcesAndLogs();
    }

    public static final String LIST_URL = "/albums";
    public static final String ADD_URL = "/albums/add";
    public static final String LOGIN_URL = "/login";

    static class ViewMatcher implements Matcher<String> {

        final String baseName;

        ViewMatcher(String baseName) {
            this.baseName = baseName;
        }

        @Override
        public boolean matches(Object o) {
            if (o instanceof String) {
                String s = (String) o;
                return s.startsWith(baseName);
            }
            return false;
        }

        @Override
        public void describeMismatch(Object o, Description description) {
        }

        @Override
        public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
        }

        @Override
        public void describeTo(Description description) {
        }
    }
}
