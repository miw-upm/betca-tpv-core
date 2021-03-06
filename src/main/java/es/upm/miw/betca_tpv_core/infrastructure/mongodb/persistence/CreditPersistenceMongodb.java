package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.persistence.CreditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CreditReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CreditPersistenceMongodb implements CreditPersistence {
    private CreditReactive creditReactive;

    @Autowired
    public CreditPersistenceMongodb(CreditReactive creditReactive) {
        this.creditReactive = creditReactive;
    }

    @Override
    public Mono<Credit> create(Credit credit) {
        return this.creditReactive.save(new CreditEntity(credit))
                .map(CreditEntity::toCredit);
    }

    @Override
    public Mono<Credit> findByUserReference(String userReference) {
        return this.creditReactive.findByUserReference(userReference)
                .map(CreditEntity::toCredit);
    }
}
