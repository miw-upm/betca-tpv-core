package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdUserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(DataProtectionActResource.DATA_PROTECTION_ACT)
public class DataProtectionActResource {

    public static final String DATA_PROTECTION_ACT = "/data-protection-act";
    public static final String MOBILE_ID = "/{mobile}";

    @GetMapping(DataProtectionActResource.MOBILE_ID)
    public Mono<RgpdUserDto> read(@PathVariable String mobile) {
        return Mono.just(new RgpdUserDto(mobile, RgpdType.ADVANCED));
    }

}
