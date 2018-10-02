package es1819.stroam.catalog.retrofit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmdbResult {
    @JsonProperty(value = "Title")
    private String title;

    @JsonProperty(value = "Year")
    private String year;

    @JsonProperty(value = "Released")
    private String released;

    @JsonProperty(value = "Runtime")
    private String runtime;

    @JsonProperty(value = "Genre")
    private String genre;

    @JsonProperty(value = "Director")
    private String director;

    @JsonProperty(value = "Actors")
    private String actors;

    @JsonProperty(value = "Plot")
    private String plot;

    @JsonProperty(value = "Poster")
    private String poster;

    @JsonProperty(value = "Type")
    private String type;

    @JsonProperty(value = "Production")
    private String production;

    @JsonProperty(value = "Language")
    private String language;

    @JsonProperty(value = "Country")
    private String country;
}
