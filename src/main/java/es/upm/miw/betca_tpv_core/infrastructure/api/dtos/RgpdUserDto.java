package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RgpdUserDto {
    private String mobile;
    private RgpdType rgpdType;

    public static RgpdUserDto ofRgpd(Rgpd rgpd) {
        return new RgpdUserDto(rgpd.getMobile(), rgpd.getRgpdType());
    }

}
