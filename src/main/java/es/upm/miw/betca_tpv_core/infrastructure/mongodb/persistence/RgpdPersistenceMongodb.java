package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

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
}
