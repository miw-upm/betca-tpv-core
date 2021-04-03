package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
class OrderLineReactiveIT {

    @Autowired
    private OrderLineReactive orderLineReactive;

    @Test
    void testFindAll() {
        StepVerifier
                .create(this.orderLineReactive.findAll())
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindById() {
        StepVerifier
                .create(this.orderLineReactive.findById("1"))
                .expectNextMatches(orderLine -> {
                    assertEquals("8400000000017", orderLine.getArticleEntity().getBarcode());
                    assertEquals(10, orderLine.getRequireAmount());
                    return true;
                })
                .thenCancel()
                .verify();
    }

}
