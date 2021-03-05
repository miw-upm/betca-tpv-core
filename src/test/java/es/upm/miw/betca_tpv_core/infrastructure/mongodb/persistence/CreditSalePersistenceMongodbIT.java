package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class CreditSalePersistenceMongodbIT {

    @Autowired
    private CreditSalePersistenceMongodb creditSalePersistenceMongodb;

    @Test
    void testCreate() {
        StepVerifier
                .create(this.creditSalePersistenceMongodb.create(CreditSale.builder().ticketReference("WB9-e8xQT4ejb74r1vLrCw").payed(false).build()))
                .expectNextMatches(creditSale -> {
                    assertEquals("WB9-e8xQT4ejb74r1vLrCw", creditSale.getTicketReference());
                    assertFalse(creditSale.getPayed());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testNotFoundTicket() {
        StepVerifier
                .create(this.creditSalePersistenceMongodb.create(CreditSale.builder().ticketReference("e8xQT4ejb74r1vLrCw").payed(false).build()))
                .expectError()
                .verify();
    }

    @Test
    void testFindByReference() {
        StepVerifier
                .create(this.creditSalePersistenceMongodb.findByPayed(true))
                .expectNextMatches(creditSale -> {
                    assertEquals("FGhfvfMORj6iKmzp5aERAA", creditSale.getTicketReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
