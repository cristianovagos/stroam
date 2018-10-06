package es1819.stroam.catalog.model;

import es1819.stroam.catalog.model.Genre;
import es1819.stroam.catalog.model.SeriesSeason;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "production")
public class Production {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 200)
    private String name;

    @NotNull
    private String releaseDate;

    @NotNull
    private String year;

    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.EAGER)
    private Set<Genre> genres;

    @NotNull
    @Size(max = 100)
    private String director;

    @NotNull
    @Size(max = 50)
    private String type;

    private String poster;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    private String runtime;

    private int seasons;

    private double price = 10.00;

    @NotNull
    private String imdbID;

    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.EAGER, mappedBy = "production")
    private List<SeriesSeason> seasonList;
}