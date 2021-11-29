package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.model.Cashier;
import es.upm.miw.betca_tpv_core.domain.model.CashierClose;
import es.upm.miw.betca_tpv_core.domain.model.CashierState;
import es.upm.miw.betca_tpv_core.domain.persistence.CashierPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.math.BigDecimal.ZERO;

@Service
public class CashierService {

    private final CashierPersistence cashierPersistence;

    @Autowired
    public CashierService(CashierPersistence cashierPersistence) {
        this.cashierPersistence = cashierPersistence;
    }


    public Mono< Void > createOpened() {
        return this.lastByOpenedAssure(false)
                .map(cashier -> Cashier.builder().initialCash(cashier.getFinalCash())
                        .openingDate(LocalDateTime.now()).cashSales(ZERO).cardSales(ZERO).usedVouchers(ZERO)
                        .deposit(ZERO).withdrawal(ZERO).comment("").build()
                )
                .flatMap(this.cashierPersistence::create)
                .then();
    }

    private Mono< Cashier > lastByOpenedAssure(boolean opened) {
        return this.cashierPersistence.findLast()
                .handle((last, sink) -> {
                    if (last.isClosed() ^ opened) {
                        sink.next(last);
                    } else {
                        String msg = opened ? "Open cashier was expected: " : "Close cashier was expected: ";
                        sink.error(new BadRequestException(msg + last.getId()));
                    }
                });
    }

    public Mono< Cashier > findLast() {
        return this.cashierPersistence.findLast();
    }

    public Mono< CashierState > findLastState() {
        return this.cashierPersistence.findLast()
                .map(CashierState::new);
    }

    public Mono< Cashier > close(CashierClose cashierClose) {
        return this.lastByOpenedAssure(true)
                .map(lastCashier -> {
                    lastCashier.close(cashierClose.getFinalCash(), cashierClose.getFinalCard(), cashierClose.getComment());
                    return lastCashier;
                })
                .flatMap(lastCashier -> this.cashierPersistence.update(lastCashier.getId(), lastCashier));
    }

    Mono< Cashier > addSale(BigDecimal cash, BigDecimal card, BigDecimal voucher) {
        return this.lastByOpenedAssure(true)
                .map(lastCashier -> {
                    lastCashier.addSale(cash, card, voucher);
                    return lastCashier;
                })
                .flatMap(lastCashier -> this.cashierPersistence.update(lastCashier.getId(), lastCashier));
    }

}
