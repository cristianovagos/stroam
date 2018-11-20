package es1819.stroam.catalog.model.retrofit.omdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmdbEpisodeSeasonResult {
    @JsonProperty(value = "Title")
    private String title;

    @JsonProperty(value = "Released")
    private String released;

    @JsonProperty(value = "Episode")
    private int episode;

    @JsonProperty(value = "imdbID")
    private String imdbID;
}
