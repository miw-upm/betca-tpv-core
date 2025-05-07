package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tags {

    private String id;  // Suponiendo que quieres un identificador para la entidad

    @NotBlank
    private String name;

    @NotBlank
    private String group;

    @NotBlank
    private String description;

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }
}
