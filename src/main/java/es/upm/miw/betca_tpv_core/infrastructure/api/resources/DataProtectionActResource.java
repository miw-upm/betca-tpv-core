package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.services.DataProtectionActService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdUserDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdUserWithFileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(DataProtectionActResource.DATA_PROTECTION_ACT)
public class DataProtectionActResource {

    public static final String DATA_PROTECTION_ACT = "/data-protection-act";
    public static final String AGREEMENT = "/agreement";
    public static final String MOBILE_ID = "/{mobile}";

    private DataProtectionActService dataProtectionActService;

    @Autowired
    public DataProtectionActResource(DataProtectionActService dataProtectionActService) {
        this.dataProtectionActService = dataProtectionActService;
    }

    @GetMapping(DataProtectionActResource.MOBILE_ID)
    public Mono<RgpdUserDto> read(@PathVariable String mobile) {
        return this.dataProtectionActService.read(mobile)
                .map(RgpdUserDto::ofRgpd);
    }

    @PreAuthorize("permitAll()")
    @PostMapping(produces = {"application/json"})
    public Mono<RgpdUserDto> create(@Valid @RequestBody RgpdUserWithFileDto rgpdUserWithFileDto) {
        return this.dataProtectionActService.create(rgpdUserWithFileDto.toRgpd())
                .map(RgpdUserDto::ofRgpd);
    }

    @PutMapping(DataProtectionActResource.MOBILE_ID)
    public Mono<RgpdUserDto> update(@PathVariable String mobile, @Valid @RequestBody RgpdUserWithFileDto rgpdUserWithFileDto) {
        return this.dataProtectionActService.update(mobile, rgpdUserWithFileDto.toRgpd())
                .map(RgpdUserDto::ofRgpd);
    }

    @GetMapping(value = DataProtectionActResource.AGREEMENT + DataProtectionActResource.MOBILE_ID,
            produces = {"application/pdf", "application/json"})
    public Mono<byte[]> readAgreement(@PathVariable String mobile) {
        return this.dataProtectionActService.read(mobile)
                .map(Rgpd::getAgreement);
    }

}
