package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Rgpd {

    @NotNull
    private RgpdType rgpdType;
    @NotNull
    private byte[] agreement;
    @NotNull
    private User user;

    public RgpdDto toDto() {
        return new RgpdDto(this.rgpdType, Base64.getEncoder().encodeToString(this.agreement), this.user.getMobile(), this.user.getFirstName());
    }
}
