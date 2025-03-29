package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.configuration.JwtService;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tags {
    @NotBlank
    @Getter @Setter
    private String name;
    @NotBlank
    @Getter @Setter
    private String group;
    @NotBlank
    @Getter @Setter
    private String description;



}
