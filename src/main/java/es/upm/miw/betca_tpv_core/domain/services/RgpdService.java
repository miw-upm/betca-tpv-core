package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.persistence.RgpdPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class RgpdService {

    private final RgpdPersistence rgpdPersistence;

    @Autowired
    public RgpdService(RgpdPersistence rgpdPersistence) {
        this.rgpdPersistence = rgpdPersistence;
    }


    public Mono<Rgpd> create(RgpdDto rgpdDto) {
        String userMobile = rgpdDto.getUserMobile();
        return this.rgpdPersistence.existsRgpdByUserMobile(userMobile)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new BadRequestException("User with mobile " + userMobile + " already has a RGPD"));
                    } else {
                        Rgpd rgpd = rgpdDto.toRgpd();
                        return rgpdPersistence.create(rgpd);
                    }
                });
    }
}