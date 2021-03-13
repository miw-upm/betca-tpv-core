package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class RgpdEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userMobile;
    private RgpdType rgpdType;
    private byte[] agreement;

    public RgpdEntity(Rgpd rgpd) {
        this.userMobile = rgpd.getMobile();
        this.rgpdType = rgpd.getRgpdType();
        this.agreement = rgpd.getAgreement();
    }

    public Rgpd toRgpd() {
        return new Rgpd(new User(this.userMobile), this.rgpdType, this.agreement);
    }

}
