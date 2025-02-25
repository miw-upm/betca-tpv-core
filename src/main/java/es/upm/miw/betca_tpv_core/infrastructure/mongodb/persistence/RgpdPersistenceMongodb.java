package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.persistence.RgpdPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.RgpdReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.RgpdEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class RgpdPersistenceMongodb implements RgpdPersistence {

    private final RgpdReactive rgpdReactive;

    @Autowired
    public RgpdPersistenceMongodb(RgpdReactive rgpdReactive) {
        this.rgpdReactive = rgpdReactive;
    }

    @Override
    public Mono<Rgpd> create(Rgpd rgpd) {
        return this.rgpdReactive.save(new RgpdEntity(rgpd))
                .map(RgpdEntity::toRgpd);
    }

    @Override
    public Mono<Rgpd> findRgpdByUserMobile(String userMobile) {
        return this.rgpdReactive.findAll()
                .filter(rgpd -> rgpd.getUserMobile().equals(userMobile))
                .map(RgpdEntity::toRgpd)
                .next();

    }

    @Override
    public Flux<Rgpd> findAllRgpds() {
        return this.rgpdReactive.findAll()
                .map(RgpdEntity::toRgpd);
    }

    @Override
    public Mono<Rgpd> updateRgpd(String userMobile, Rgpd updatedRgpd) {
        if (!userMobile.equals(updatedRgpd.getUser().getMobile())) {
            return Mono.error(new ConflictException("Cannot change userMobile."));
        }
        return this.rgpdReactive.findByUserMobile(userMobile)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent RGPD with userMobile: " + userMobile)))
                .flatMap(existingRgpdEntity -> {
                    BeanUtils.copyProperties(updatedRgpd, existingRgpdEntity, "id", "userMobile", "userName");
                    return this.rgpdReactive.save(existingRgpdEntity);
                })
                .map(RgpdEntity::toRgpd);
    }
}