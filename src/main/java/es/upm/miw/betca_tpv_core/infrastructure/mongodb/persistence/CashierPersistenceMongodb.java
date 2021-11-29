package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Cashier;
import es.upm.miw.betca_tpv_core.domain.persistence.CashierPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.CashierReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CashierEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CashierPersistenceMongodb implements CashierPersistence {

    private final CashierReactive cashierReactive;

    @Autowired
    public CashierPersistenceMongodb(CashierReactive cashierReactive) {
        this.cashierReactive = cashierReactive;
    }

    @Override
    public Mono< Cashier > findLast() {
        return this.cashierReactive.findFirstByOrderByOpeningDateDesc()
                .map(CashierEntity::toCashier);
    }

    @Override
    public Mono< Cashier > create(Cashier cashier) {
        return this.cashierReactive.save(new CashierEntity(cashier))
                .map(CashierEntity::toCashier);
    }

    @Override
    public Mono< Cashier > update(String id, Cashier cashier) {
        return this.cashierReactive.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Non existent cashier: " + id)))
                .map(cashierEntity -> cashierEntity.from(cashier))
                .flatMap(this.cashierReactive::save)
                .map(CashierEntity::toCashier);
    }
}
