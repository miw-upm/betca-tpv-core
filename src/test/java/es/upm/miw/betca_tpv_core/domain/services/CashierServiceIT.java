package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.model.CashierClose;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

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
}
