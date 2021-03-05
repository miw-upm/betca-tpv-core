package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class CreditSaleReactiveIT {

    @Autowired
    private CreditSaleReactive creditSaleReactive;

    @Test
    void testFindAll() {
        StepVerifier
                .create(this.creditSaleReactive.findAll())
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByPayed() {
        StepVerifier
                .create(this.creditSaleReactive.findByPayed(true))
                .expectNextMatches(creditSale -> {
                    assertTrue(creditSale.getPayed());
                    return true;
                })
                .thenCancel()
                .verify();
    }

}
