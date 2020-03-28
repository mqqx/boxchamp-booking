package dev.hmmr.apps.coursebooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "dayOfWeek"})})
public class Booking {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    Integer id;

    @JsonIgnore
    @ManyToOne
    User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    DayOfWeek dayOfWeek;

    @NotNull
    @Enumerated(EnumType.STRING)
    Course course;

    //TODO check in db what happens with localtime object
    String startsAt;
}
