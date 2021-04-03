package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Rgpd {

    private User user;
    private RgpdType rgpdType;
    private byte[] agreement;

    public Rgpd(String mobile, RgpdType rgpdType, byte[] agreement) {
        this.user = new User(mobile);
        this.rgpdType = rgpdType;
        this.agreement = agreement;
    }

    public String getMobile() {
        return this.user.getMobile();
    }

}
