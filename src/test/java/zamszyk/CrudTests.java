package zamszyk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles("test")
public class CrudTests {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    Bookmarks bookmarks;

    @Test
    @WithMockUser
    void successfulFormSubmissionShouldRedirectToTarget() throws Exception {
        mvc.perform(
                post("/__")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param("slug", "ex")
                        .param("target", "https://example.org")
                        .with(csrf())
        ).andExpect(redirectedUrl("https://example.org"));
    }

    @Test
    @WithMockUser
    void targetParamShouldPopulateFormAndGenerateSlug() throws Exception {
        mvc.perform(get("/__").param("target", "aHR0cHM6Ly9leGFtcGxlLm9yZy8="))
                .andExpectAll(
                        model().attribute("target", "https://example.org/"),
                        model().attribute("slug", "4ia"));
    }

    @Test
    @WithMockUser
    void databaseErrorShouldShowAnErrorOnTheForm() throws Exception {
        doThrow(UncategorizedSQLException.class).when(bookmarks).create(any(), any(), anyBoolean());
        mvc.perform(
                post("/__")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param("slug", "ex")
                        .param("target", "https://example.org")
                        .with(csrf())
        ).andExpect(model().attribute("error", "Bookmark with this slug already exists!"));
    }

    @Test
    @WithMockUser
    void loggedInUserShouldRetrieveRestrictedBookmark() throws Exception {
        when(bookmarks.findBySlug(eq(new Slug("secret")), any(Principal.class))).thenReturn(Optional.of(new Target("test://top.secret")));
        mvc.perform(get("/r/secret")).andExpect(redirectedUrl("test://top.secret"));
    }

    @Test
    @WithAnonymousUser
    void anonymousUserShouldNotRetrieveRestrictedBookmark() throws Exception {
        when(bookmarks.findBySlug(eq(new Slug("secret")), isNull(Principal.class))).thenReturn(Optional.empty());
        mvc.perform(get("/r/secret")).andExpect(status().isNotFound());
    }
}
