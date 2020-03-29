package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Component;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Component
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BoxChampUriBuilderFactoryTest {
    private static final String SCHEME = "https";
    private static final String HOST = "boxchamp.io";
    private static final String PATH = "/path";

    @Mock
    BoxChampProperties boxChampProperties;

    BoxChampUriBuilderFactory sut;

    @BeforeEach
    void setUp() {
        sut = new BoxChampUriBuilderFactory(boxChampProperties);
        when(boxChampProperties.getScheme()).thenReturn(SCHEME);
        when(boxChampProperties.getHost()).thenReturn(HOST);
    }

    @Test
    @DisplayName("Should build uri with given path and preset scheme and host.")
    void buildUriWithPath() {
        URI uriWithPath = sut.buildUriWithPath(PATH);

        assertThat(uriWithPath)
                .hasScheme(SCHEME)
                .hasHost(HOST)
                .hasPath(PATH)
                .hasNoPort()
                .hasNoParameters();
    }
}
