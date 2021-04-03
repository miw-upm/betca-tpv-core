package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.persistence.RgpdPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.RgpdReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.RgpdEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RgpdPersistenceMongodb implements RgpdPersistence {

    private RgpdReactive rgpdReactive;

    @Autowired
    public RgpdPersistenceMongodb(RgpdReactive rgpdReactive) {
        this.rgpdReactive = rgpdReactive;
    }

    @Override
    public Mono<Rgpd> readByMobile(String mobile) {
        return this.rgpdReactive.findByUserMobile(mobile)
                .map(RgpdEntity::toRgpd);
    }

    @Override
    public Mono<Rgpd> create(Rgpd rgpd) {
        return this.rgpdReactive.findByUserMobile(rgpd.getMobile())
                .flatMap(articleEntity -> Mono.error(
                        new ConflictException("Rgpd already exists for mobile : " + rgpd.getMobile())))
                .then(Mono.just(new RgpdEntity(rgpd)))
                .flatMap(this.rgpdReactive::save)
                .map(RgpdEntity::toRgpd);
    }

    @Override
    public Mono<Rgpd> update(String mobile, Rgpd rgpd) {
        return this.rgpdReactive.findByUserMobile(mobile)
                .switchIfEmpty(Mono.error(new NotFoundException("Rgpd doesn't exists for mobile: " + mobile)))
                .flatMap(rgpdEntity -> {
                    rgpdEntity.set(rgpd);
                    return Mono.just(rgpdEntity);
                })
                .flatMap(this.rgpdReactive::save)
                .map(RgpdEntity::toRgpd);
    }

}
