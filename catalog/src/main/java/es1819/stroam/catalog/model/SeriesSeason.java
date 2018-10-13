package es1819.stroam.catalog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "season")
public class SeriesSeason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int season;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "production_id")
    private Production production;

    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.LAZY, mappedBy = "season")
    private List<Episode> episodes;
}
