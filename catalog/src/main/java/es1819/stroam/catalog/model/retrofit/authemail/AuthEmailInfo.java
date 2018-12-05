package es1819.stroam.catalog.model.retrofit.authemail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthEmailInfo {
    @JsonProperty("email")
    private String email;
}
