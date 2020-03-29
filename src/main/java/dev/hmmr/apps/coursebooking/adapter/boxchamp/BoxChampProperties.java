package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties("box-champ")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BoxChampProperties {
    String host;
    String scheme;

    String listRegex;
    String listPath;
    String bookingPath;
    String loginPath;
    int weeksInAdvance;

    String queryParamEmail;
    String queryParamPassword;
}
