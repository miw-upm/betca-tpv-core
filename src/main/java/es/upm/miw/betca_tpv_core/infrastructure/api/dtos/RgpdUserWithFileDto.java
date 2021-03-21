package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RgpdUserWithFileDto {
    private String mobile;
    private RgpdType rgpdType;
    private String agreement;

    public Rgpd toRgpd() {
        return new Rgpd(this.mobile, this.rgpdType, this.agreement);
    }
}
