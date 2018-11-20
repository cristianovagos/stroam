package es1819.stroam.catalog.model.retrofit.frontend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FrontendChannelInfo {
    @JsonProperty(value = "users")
    private List<Integer> users;
}
