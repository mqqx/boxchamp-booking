package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class BoxChampHttpClient {
    RestTemplate restTemplate;
    BoxChampUriBuilderFactory boxChampUriBuilderFactory;

    ResponseEntity<String> get(String path, HttpEntity<HttpHeaders> authorizedEntity) {
        URI uri = boxChampUriBuilderFactory.buildUriWithPath(path);
        return restTemplate.exchange(uri, HttpMethod.GET, authorizedEntity, String.class);
    }

    ResponseEntity<String> post(String path, Object request) {
        URI uri = boxChampUriBuilderFactory.buildUriWithPath(path);
        return restTemplate.postForEntity(uri, request, String.class);
    }
}
