package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.persistence.RgpdPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class RgpdService {

    private final RgpdPersistence rgpdPersistence;
    private final UserMicroservice userMicroservice;

    @Autowired
    public RgpdService(RgpdPersistence rgpdPersistence, UserMicroservice userMicroservice) {
        this.rgpdPersistence = rgpdPersistence;
        this.userMicroservice = userMicroservice;
    }

    public Mono<Rgpd> create(Rgpd rgpd) {
        String userMobile = rgpd.getUser().getMobile();

        return verifyUserExistsByMobile(userMobile)
                .then(validateUserHasRgpdSigned(userMobile))
                .then(this.rgpdPersistence.create(rgpd));
    }

    public Flux<Rgpd> findAllRgpds() {
        return rgpdPersistence.findAllRgpds();
    }

    private Mono<Void> verifyUserExistsByMobile(String userMobile) {
        return userMicroservice.readByMobile(userMobile)
                .onErrorResume(BadRequestException.class, Mono::error)
                .then();
    }

    private Mono<Void> validateUserHasRgpdSigned(String userMobile) {
        return rgpdPersistence.findRgpdByUserMobile(userMobile)
                .flatMap(existingRgpd -> Mono.error(new BadRequestException("The user with mobile number " + userMobile + " has already accepted RGPD policies.")))
                .switchIfEmpty(Mono.empty())
                .then();
    }
}