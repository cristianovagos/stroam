package es1819.stroam.catalog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "episode")
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "season_id")
    private SeriesSeason season;

    @NotNull
    private int episode;

    @NotNull
    private String title;

    @NotNull
    private String releaseDate;

    @NotNull
    private String imdbID;
}
