package zamszyk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.client.assertj.RestTestClientResponse;

import java.util.stream.Stream;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TEMPORARY_REDIRECT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.datasource.url=jdbc:sqlite::memory:", "spring.http.clients.redirects=dont-follow"})
@AutoConfigureRestTestClient
class RedirectTests {

	@Autowired
	RestTestClient rest;

	@ParameterizedTest
	@MethodSource
	void redirectsShouldWork(String slug, String target) {
		var response = RestTestClientResponse.from(rest.get().uri(slug).exchange());
		assertThat(response).hasStatus(TEMPORARY_REDIRECT);
		assertThat(response).hasHeader(LOCATION, target);
	}

	static Stream<Arguments> redirectsShouldWork() {
		return Stream.of(
				arguments("/z/foo", "test://flight.of.opportunity"),
				arguments("/z/bar", "test://brain.access.router"),
				arguments("/z/123", "test://one.two.three"),
				arguments("/z/857620a5-79ed-4988-8439-382b912ef943", "test://undefined.unsafe.initial.design")
		);
	}

	@Test
	void httpNotFoundShouldBeReturnedForNonExistentBookmark() {
		var nonExistentBookmarkResponse = RestTestClientResponse.from(rest.get().uri("/z/" + randomUUID()).exchange());
		assertThat(nonExistentBookmarkResponse).hasStatus(NOT_FOUND);
	}

	@Test
	void httpNotFoundShouldBeReturnedForMultipleSeparatorsInQuery() {
		var multipleSeparatorsQueryResponse = RestTestClientResponse.from(rest.get().uri("/z/d-_-b").exchange());
		assertThat(multipleSeparatorsQueryResponse).hasStatus(NOT_FOUND);
	}

	@Test
	void loginLocationShouldBeReturnedForSecuredEndpoint() {
		var securedEndpointResponse = RestTestClientResponse.from(rest.get().uri("/__").exchange());
		assertThat(securedEndpointResponse).hasStatus3xxRedirection();
		assertThat(securedEndpointResponse).containsHeader(LOCATION);
		assertThat(securedEndpointResponse).headers().hasHeaderSatisfying(LOCATION, vs -> assertThat(vs).allMatch(v -> v.endsWith("/login")));
	}
}
