package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class RgpdEntity {
    @Id
    private String id;
    private RgpdType rgpdType;
    @Indexed(unique = true)
    private String userMobile;
    private String userName;
    private byte[] agreement;

    public RgpdEntity(Rgpd rgpd) {
        BeanUtils.copyProperties(rgpd, this, "user");
        if (Objects.nonNull(rgpd.getUser())) {
            this.userMobile = rgpd.getUser().getMobile();
            this.userName = rgpd.getUser().getFirstName();
        }
    }

    public Rgpd toRgpd() {
        return Rgpd.builder()
                .rgpdType(this.rgpdType)
                .agreement( this.agreement)
                .user(Objects.nonNull(this.userMobile)
                        ? User.builder().mobile(this.userMobile).firstName(this.userName).build()
                        : new User())
                .build();
    }


}
