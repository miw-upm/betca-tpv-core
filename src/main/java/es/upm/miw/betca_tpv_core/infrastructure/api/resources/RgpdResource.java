package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.services.RgpdService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Rest
@RequestMapping(RgpdResource.RGPDS)
public class RgpdResource {
    public static final String RGPDS = "/rgpds";
    public static final String USER_MOBILE = "/{userMobile}";

    private final RgpdService rgpdService;

    @Autowired
    public RgpdResource(RgpdService rgpdService) {
        this.rgpdService = rgpdService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(produces = {"application/json"})
    public Mono<RgpdDto> createRgpd(@Valid @RequestBody RgpdDto creationRgpdDto) {
        return this.rgpdService.create(creationRgpdDto.toRgpd())
                .map(Rgpd::toDto);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public Flux<RgpdDto> getAllRgpds() {
        return this.rgpdService.findAllRgpds()
                .map(Rgpd::toDto);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(USER_MOBILE)
    public Mono<RgpdDto> updateRgpd(@PathVariable String userMobile, @Valid @RequestBody RgpdDto updatedRgpdDto) {
        return this.rgpdService
                .updateRgpd( userMobile, updatedRgpdDto.toRgpd())
                .map(Rgpd::toDto);
    }
}