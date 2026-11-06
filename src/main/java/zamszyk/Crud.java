package zamszyk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.lang.Character.MAX_RADIX;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@Controller
@RequestMapping("/__")
public class Crud {

    private static final Logger log = LoggerFactory.getLogger(Crud.class);
    private static final BigInteger mod = BigInteger.valueOf(MAX_RADIX * MAX_RADIX * MAX_RADIX);

    private final Bookmarks bookmarks;
    private final UriComponentsBuilder baseUrl;

    public Crud(Bookmarks bookmarks, @Value("${zamszyk.base-url}") URI baseUrl) {
        this.bookmarks = bookmarks;
        this.baseUrl = UriComponentsBuilder.fromUri(baseUrl);
    }

    @ModelAttribute("revision")
    String revision(@Value("${git.commit.id:revision placeholder}") String revision) {
        return revision;
    }

    @ModelAttribute("version")
    String version(@Value("${git.build.version:version placeholder}") String version) {
        return version;
    }

    @ModelAttribute("redirect")
    UriComponents redirect(UriComponentsBuilder builder) {
        return baseUrl.cloneBuilder().path("r/%s").build();
    }

    @ModelAttribute("bookmarklet")
    String bookmarklet(UriComponentsBuilder builder) {
        return String.format(
                "javascript:window.location='%s?target='+window.btoa(window.location).replaceAll('+','-').replaceAll('/','_');",
                baseUrl.cloneBuilder().path("__").build());
    }

    @GetMapping
    public String form(UriComponentsBuilder builder, Model model, @RequestParam(required = false) String target) throws NoSuchAlgorithmException {
        model.addAttribute("slug", "");
        model.addAttribute("target", "");
        if (target != null && !target.isBlank()) {
            var decodedTarget = Base64.getUrlDecoder().decode(target);
            var sha = MessageDigest.getInstance("SHA3-224");
            var calculatedSlug = new BigInteger(1, sha.digest(decodedTarget)).mod(mod).toString(MAX_RADIX);
            model.addAttribute("slug", calculatedSlug);
            model.addAttribute("target", new String(decodedTarget, UTF_8));
        }
        return "form";
    }

    @PostMapping(consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public String create(Slug slug, Target target, boolean restricted, Model model) {
        log.info("Form submitted: '{}' -> '{}'", slug.value(), target.value());
        try {
            bookmarks.create(slug, target, restricted);
            log.info("Bookmark created successfully");
            return String.format("redirect:%s", target.value());
        } catch (DataAccessException e) {
            model.addAttribute("slug", slug.value());
            model.addAttribute("target", target.value());
            model.addAttribute("error", "Bookmark with this slug already exists!");
            log.error("Bookmark with slug '{}' already exists! Database error message: {}", slug.value(), e.getMessage());
            return "form";
        }
    }
}
