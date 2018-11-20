package es1819.stroam.catalog.model.retrofit.omdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmdbSeasonResult {
    @JsonProperty(value = "Title")
    private String title;

    @JsonProperty(value = "Season")
    private int season;

    @JsonProperty(value = "totalSeasons")
    private int totalSeasons;

    @JsonProperty(value = "Episodes")
    private List<OmdbEpisodeSeasonResult> episodes;
}
