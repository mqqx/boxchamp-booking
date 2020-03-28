package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BoxChampHttpClientTest {
    private static final String URL = "https://boxchamp.io/path";
    private static final String PATH = "path";
    BoxChampHttpClient boxChampHttpClient;

    @Mock
    RestTemplate restTemplate;

    @Mock
    BoxChampUriBuilderFactory boxChampUriBuilderFactory;

    @BeforeEach
    void setUp() {
        boxChampHttpClient = new BoxChampHttpClient(restTemplate, boxChampUriBuilderFactory);
    }

    @Test
    @DisplayName("Should make a get request to the given path")
    void get() throws URISyntaxException {
        URI uri = new URI(URL);
        when(boxChampUriBuilderFactory.buildUriWithPath(PATH)).thenReturn(uri);
        HttpEntity<HttpHeaders> authorizedEntity = new HttpEntity<>(new HttpHeaders());

        boxChampHttpClient.get(PATH, authorizedEntity);

        verify(restTemplate).exchange(uri, HttpMethod.GET, authorizedEntity, String.class);
    }

    @Test
    @DisplayName("Should make a post request to the given path")
    void post() throws URISyntaxException {
        URI uri = new URI(URL);
        when(boxChampUriBuilderFactory.buildUriWithPath(PATH)).thenReturn(uri);

        boxChampHttpClient.post(PATH, null);

        verify(restTemplate).postForEntity(uri, null, String.class);
    }
}
