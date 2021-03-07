package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import es.upm.miw.betca_tpv_core.domain.persistence.CreditPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CreditReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CreditSaleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.TicketReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditSaleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.TicketEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Arrays;

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
        return this.creditReactive.save(new CreditEntity(credit, null))
                .map(CreditEntity::toCredit);
    }

    @Override
    public Mono<Credit> findByUserReference(String userReference) {
        return this.creditReactive.findByUserReference(userReference)
                .map(CreditEntity::toCredit);
    }

    @Override
    public Mono<Credit> addCreditSale(Credit credit, CreditSale creditSale) {
        CreditSaleEntity[] creditSaleEntityAdded = this.creditSaleReactive.findByReference(creditSale.getReference()).block().toCreditSaleArray();

        Mono<CreditSaleEntity[]> creditSaleEntityAdded2 = this.creditSaleReactive.findByReference(creditSale.getReference())
                                                        .map(CreditSaleEntity::toCreditSaleArray);
        Mono<CreditEntity> creditEntityMono = this.creditReactive.findByUserReference(credit.getUserReference());
        Mono<Credit> credit1 = creditEntityMono
                .map(creditEntity -> {
                    //TODO add the new creditSale
                    return creditEntity;
                })
                .flatMap(this.creditReactive::save)
                .map(CreditEntity::toCredit);
        Credit credit2 = credit1.block();
        return credit1;
    }
}
