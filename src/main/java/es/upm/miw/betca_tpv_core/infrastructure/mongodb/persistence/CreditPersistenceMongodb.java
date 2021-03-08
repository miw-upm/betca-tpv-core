package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import es.upm.miw.betca_tpv_core.domain.persistence.CreditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CreditReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CreditSaleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditSaleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CreditPersistenceMongodb implements CreditPersistence {
    private CreditReactive creditReactive;
    private CreditSaleReactive creditSaleReactive;

    @Autowired
    public CreditPersistenceMongodb(CreditReactive creditReactive, CreditSaleReactive creditSaleReactive) {
        this.creditReactive = creditReactive;
        this.creditSaleReactive = creditSaleReactive;
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

    @Override
    public Mono<Credit> addCreditSale(String userRef, CreditSale creditSale) {
        Mono<CreditEntity> creditEntityMono = this.creditReactive.findByUserReference(userRef);
        CreditSaleEntity[] creditSaleEntitiesOld = creditEntityMono.map(CreditEntity::getCreditSaleEntities).block();
        CreditSaleEntity[] creditSaleEntityAdded = this.creditSaleReactive.findByReference(creditSale.getReference()).block().toCreditSaleArray(creditSaleEntitiesOld);

        return creditEntityMono
                .map(creditEntity -> {
                    creditEntity.setCreditSaleEntities(creditSaleEntityAdded);
                    return creditEntity;
                })
                .flatMap(this.creditReactive::save)
                .map(CreditEntity::toCredit);
    }
}
