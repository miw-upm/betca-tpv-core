package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.model.CashierClose;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

@TestConfig
class CashierServiceIT {

    @Autowired
    private CashierService cashierService;

    @Test
    void testCloseBadRequestException() {
        StepVerifier.create(this.cashierService.close(new CashierClose(BigDecimal.ZERO, BigDecimal.ZERO, "")))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void testOpenBadRequestException() {
        StepVerifier.create(this.cashierService.createOpened())
                .expectComplete()
                .verify();
        StepVerifier.create(this.cashierService.createOpened())
                .expectError(BadRequestException.class)
                .verify();
        StepVerifier.create(this.cashierService.close(new CashierClose(BigDecimal.ZERO, BigDecimal.ZERO, "")))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testFindAllByClosureDateBetween() {
        StepVerifier.create(this.cashierService.findAllByClosureDateBetween(LocalDate.of(1970, 1, 1), LocalDate.of(1970, 1, 2)))
                .expectNextMatches(cashier ->
                        cashier.getClosureDate().isAfter(LocalDate.of(1970, 1, 1).atStartOfDay()) &&
                        cashier.getClosureDate().isBefore(LocalDate.of(1970, 1, 2).atStartOfDay())
                ).expectComplete()
                .verify();
        StepVerifier.create(this.cashierService.findAllByClosureDateBetween(LocalDate.of(1970, 1, 1), LocalDate.of(1971, 1, 1)))
                .expectNextCount(7)
                .expectComplete()
                .verify();
    }
}
