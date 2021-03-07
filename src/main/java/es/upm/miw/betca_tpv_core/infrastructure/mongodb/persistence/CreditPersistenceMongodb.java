package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import es.upm.miw.betca_tpv_core.domain.persistence.CreditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CreditReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CreditSaleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditSaleEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CreditPersistenceMongodb implements CreditPersistence {
    private CreditReactive creditReactive;
    private CreditSaleReactive creditSaleReactive;
    private TicketReactive ticketReactive;

    @Autowired
    public CreditPersistenceMongodb(CreditReactive creditReactive, CreditSaleReactive creditSaleReactive, TicketReactive ticketReactive) {
        this.creditReactive = creditReactive;
        this.creditSaleReactive = creditSaleReactive;
        this.ticketReactive = ticketReactive;
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
    public Mono<Credit> addCreditSale(Credit credit, CreditSale creditSale) {
        Mono<CreditSaleEntity[]> creditSaleToAdd = this.creditSaleReactive.findByTicketEntity(
                this.ticketReactive.findByReference(creditSale.getTicketReference()).block())
                .map(CreditSaleEntity::toCreditSaleArray);
        Mono<CreditEntity> creditEntityMono = this.creditReactive.findByUserReference(credit.getUserReference());
        return creditEntityMono
                .flatMap(creditEntity -> {
                    BeanUtils.copyProperties(credit, creditEntity);
                    return this.creditReactive.findByUserReference((credit.getUserReference()))
                            .map(CreditEntity::getCreditSaleEntities)
                            .mergeWith(creditSaleToAdd).next()
                            .map(creditSaleEntities -> {
                                creditEntity.setCreditSaleEntities(creditSaleEntities);
                                return creditEntity;
                            });
                })
                .flatMap(this.creditReactive::save)
                .map(CreditEntity::toCredit);
    }
}
