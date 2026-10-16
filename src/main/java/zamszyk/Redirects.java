package zamszyk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TEMPORARY_REDIRECT;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@RestController
@RequestMapping("/r")
public class Redirects {

    private static final Logger log = LoggerFactory.getLogger(Redirects.class);

    private static final String _404 = """
            ....................########............################............................########....
            ....................########............################............................########....
            ....................########............################............................########....
            ................############........########........########....................############....
            ................############........########........########....................############....
            ................############........########........########....................############....
            ............################........########....############................################....
            ............################........########....############................################....
            ............################........########....############................################....
            ....########........########........############....########........########........########....
            ....########........########........############....########........########........########....
            ....########........########........############....########........########........########....
            ....############################....########........########........############################
            ....############################....########........########........############################
            ....############################....########........########........############################
            ....................########........########........########........................########....
            ....................########........########........########........................########....
            ....................########........########........########........................########....
            ....................########............################............................########....
            ....................########............################............................########....
            ....................########............################............................########....
            ................................................................................................
            ................................................................................................
            ................................................................................................
            """;

    private final Bookmarks bookmarks;

    public Redirects(Bookmarks bookmarks) {
        this.bookmarks = bookmarks;
    }

    @GetMapping("/{slug:[a-z0-9]+(?:[\\-_][a-z0-9]+)*}")
    public ResponseEntity<?> query(@PathVariable Slug slug, Principal principal) {
        log.info("Querying bookmarks for '{}'", slug.value());
        return bookmarks.findBySlug(slug, principal).map(t ->  {
            log.info("Found, redirecting to '{}'", t.value());
            return ResponseEntity.status(TEMPORARY_REDIRECT).header(LOCATION, t.value()).build();
        }).orElseGet(() -> {
            log.info("Not found");
            var body = new StringBuilder();
            int j = 0;
            for(int i = 0; i < _404.length(); i++) {
                if (_404.charAt(i) == '#') {
                    body.append(slug.value().charAt(j));
                } else if (_404.charAt(i) == '.') {
                    body.append(' ');
                } else {
                    body.append(_404.charAt(i));
                }
                j = (j + 1) % slug.value().length();
            }
            return ResponseEntity.status(NOT_FOUND).contentType(TEXT_PLAIN).body(body.toString());
        });
    }
}
