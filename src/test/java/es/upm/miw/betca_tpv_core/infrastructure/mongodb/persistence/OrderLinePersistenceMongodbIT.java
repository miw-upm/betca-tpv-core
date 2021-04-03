package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.OrderLine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
class OrderLinePersistenceMongodbIT {

    @Autowired
    private OrderLinePersistenceMongodb orderLinePersistenceMongodb;

    @Test
    void testFindById() {
        StepVerifier
                .create(this.orderLinePersistenceMongodb.findById("1"))
                .expectNextMatches(orderLine -> {
                    assertEquals("8400000000017", orderLine.getArticleBarcode());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testNotFound() {
        StepVerifier
                .create(this.orderLinePersistenceMongodb.create(OrderLine.builder().articleBarcode("JKKKETLLOOO").build()))
                .expectError()
                .verify();
    }

    @Test
    void testCreate() {
        StepVerifier
                .create(this.orderLinePersistenceMongodb.create(OrderLine.builder().articleBarcode("8400000000017").requireAmount(10).finalAmount(15).build()))
                .expectNextMatches(orderLine -> {
                    assertEquals("8400000000017", orderLine.getArticleBarcode());
                    assertEquals(10, orderLine.getRequireAmount());
                    assertEquals(15, orderLine.getFinalAmount());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdate() {
        StepVerifier
                .create(this.orderLinePersistenceMongodb.update("8400000000017",
                        OrderLine.builder().articleBarcode("8400000000017")
                                .requireAmount(30)
                                .finalAmount(25)
                                .build()))
                .expectNextMatches(orderLine -> {
                    assertEquals("8400000000017", orderLine.getArticleBarcode());
                    assertEquals(30, orderLine.getRequireAmount());
                    assertEquals(25, orderLine.getFinalAmount());
                    return true;
                })
                .verifyComplete();
    }

}
