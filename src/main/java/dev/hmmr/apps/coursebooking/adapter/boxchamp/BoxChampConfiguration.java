package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(BoxChampProperties.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoxChampConfiguration {
    BoxChampProperties boxChampProperties;

    @Bean
    public BoxChampUriBuilderFactory boxChampUriBuilderFactory() {
        return new BoxChampUriBuilderFactory(boxChampProperties);
    }

}
