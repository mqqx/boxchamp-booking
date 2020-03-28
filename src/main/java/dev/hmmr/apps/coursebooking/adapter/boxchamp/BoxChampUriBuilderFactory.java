package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;

@RequiredArgsConstructor
@EnableConfigurationProperties(BoxChampProperties.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoxChampUriBuilderFactory extends DefaultUriBuilderFactory {
    BoxChampProperties boxChampProperties;

    public URI buildUriWithPath(String path) {
        String scheme = boxChampProperties.getScheme();
        String host = boxChampProperties.getHost();

        return super.builder()
                .scheme(scheme)
                .host(host)
                .path(path)
                .build();
    }
}
