package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.model.Cashier;
import es.upm.miw.betca_tpv_core.domain.model.CashierClose;
import es.upm.miw.betca_tpv_core.domain.model.CashierMovement;
import es.upm.miw.betca_tpv_core.domain.model.CashierState;
import es.upm.miw.betca_tpv_core.domain.persistence.CashierPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CashierEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ShoppingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

@Service
public class CashierService {

    private CashierPersistence cashierPersistence;

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

    public Flux < Cashier > findCashierEntitiesByClosureDateBetween(LocalDateTime dateBegin, LocalDateTime dateEnd) {
        return this.cashierPersistence.findCashierEntitiesByClosureDateBetween(dateBegin, dateEnd);
    }

    public Flux<Cashier> findAll() {
        return this.cashierPersistence.findAll();
    }

    public Mono<Cashier> movementIn(@Valid CashierMovement cashierMovement) {
        return this.lastByOpenedAssure(true)
                .map(lastCashier -> {
                    lastCashier.movementIn(cashierMovement);
                    return lastCashier;
                })
                .flatMap(lastCashier -> this.cashierPersistence.update(lastCashier.getId(), lastCashier));
    }

    public Mono<Cashier> movementOut(@Valid CashierMovement cashierMovement) {
        return this.lastByOpenedAssure(true)
                .map(lastCashier -> {
                    lastCashier.movementOut(cashierMovement);
                    return lastCashier;
                })
                .flatMap(lastCashier -> this.cashierPersistence.update(lastCashier.getId(), lastCashier));
    }

    public Mono<Cashier> findAllTotals() {
        Cashier cashier = new Cashier();
        cashier.setId("id");
        cashier.setComment("comment");
        cashier.setOpeningDate(LocalDateTime.now());
        cashier.setClosureDate(LocalDateTime.now());
        Flux<Cashier> flux = this.cashierPersistence.findAll();
        cashier.setInitialCash(flux
                .map(Cashier -> {
                    if (Cashier.getInitialCash() == null)
                        Cashier.setInitialCash(ZERO);
                    return Cashier.getInitialCash();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setCashSales(flux
                .map(Cashier -> {
                    if (Cashier.getCashSales() == null)
                        Cashier.setCashSales(ZERO);
                    return Cashier.getCashSales();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setCardSales(flux
                .map(Cashier -> {
                    if (Cashier.getCardSales() == null)
                        Cashier.setCardSales(ZERO);
                    return Cashier.getCardSales();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setUsedVouchers(flux
                .map(Cashier -> {
                    if (Cashier.getUsedVouchers() == null)
                        Cashier.setUsedVouchers(ZERO);
                    return Cashier.getUsedVouchers();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setDeposit(flux
                .map(Cashier -> {
                    if (Cashier.getDeposit() == null)
                        Cashier.setDeposit(ZERO);
                    return Cashier.getDeposit();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setWithdrawal(flux
                .map(Cashier -> {
                    if (Cashier.getWithdrawal() == null)
                        Cashier.setWithdrawal(ZERO);
                    return Cashier.getWithdrawal();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setLostCard(flux
                .map(Cashier -> {
                    if (Cashier.getLostCard() == null)
                        Cashier.setLostCard(ZERO);
                    return Cashier.getLostCard();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setLostCash(flux
                .map(Cashier -> {
                    if (Cashier.getLostCash() == null)
                        Cashier.setLostCash(ZERO);
                    return Cashier.getLostCash();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setFinalCash(flux
                .map(Cashier -> {
                    if (Cashier.getFinalCash() == null)
                        Cashier.setFinalCash(ZERO);
                    return Cashier.getFinalCash();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        return Mono.just(cashier);

    }

    public Mono<Cashier> findCashierEntitiesByClosureDateBetweenTotals(LocalDateTime dateBegin, LocalDateTime dateEnd) {
        Cashier cashier = new Cashier();
        cashier.setId("id");
        cashier.setComment("comment");
        cashier.setOpeningDate(LocalDateTime.now());
        cashier.setClosureDate(LocalDateTime.now());
        Flux<Cashier> flux = this.cashierPersistence.findCashierEntitiesByClosureDateBetween(dateBegin, dateEnd);
        cashier.setInitialCash(flux
                .map(Cashier::getInitialCash)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setCashSales(flux
                .map(Cashier::getCashSales)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setCardSales(flux
                .map(Cashier::getCardSales)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setUsedVouchers(flux
                .map(Cashier::getUsedVouchers)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setDeposit(flux
                .map(Cashier::getDeposit)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setWithdrawal(flux
                .map(Cashier::getWithdrawal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setLostCard(flux
                .map(Cashier::getLostCard)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setLostCash(flux
                .map(Cashier::getLostCash)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        cashier.setFinalCash(flux
                .map(Cashier::getFinalCash)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .block()
        );
        return Mono.just(cashier);
    }
}
