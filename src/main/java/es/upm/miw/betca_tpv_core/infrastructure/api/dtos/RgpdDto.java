package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.domain.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.Base64;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RgpdDto {
    @NotNull
    private RgpdType rgpdType;
    @NonNull
    private String agreement;
    @NotNull
    private String userMobile;
    @NotNull
    private String userName;

    public RgpdDto(Rgpd rgpd) {
        BeanUtils.copyProperties(rgpd, this, "user");
        this.agreement = Base64.getEncoder().encodeToString(rgpd.getAgreement());
        this.userMobile = rgpd.getUser().getMobile();
        this.userName = rgpd.getUser().getFirstName();
    }

    public Rgpd toRgpd() {
        Rgpd rgpd = new Rgpd();
        BeanUtils.copyProperties(this, rgpd, "user");

        if (this.getUserMobile() != null) {
            rgpd.setUser(User.builder().mobile(this.getUserMobile()).firstName(this.getUserName()).build());
        }
        rgpd.setAgreement(Base64.getDecoder().decode(this.getAgreement()));
        return rgpd;
    }

}