package es1819.stroam.catalog.production;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es1819.stroam.catalog.genre.Genre;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @Size(max = 4, min = 4)
    private Integer year;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.EAGER,
    mappedBy = "production")
    private Set<Genre> genres;

    @NotNull
    @Size(max = 100)
    private String director;

    @NotNull
    @Size(max = 50)
    private String type;

    private String poster;

    private String description;

    private String runtime;
}
