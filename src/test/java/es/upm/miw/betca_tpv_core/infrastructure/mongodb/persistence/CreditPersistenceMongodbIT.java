package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class CreditPersistenceMongodbIT {

    @Autowired
    private CreditPersistenceMongodb creditPersistenceMongodb;

    @Test
    void testCreate() {
        StepVerifier
                .create(this.creditPersistenceMongodb.create(Credit.builder().reference("gdsffgd").userReference("4344354554").build()))
                .expectNextMatches(credit -> {
                    assertEquals("4344354554", credit.getUserReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByReference() {
        StepVerifier
                .create(this.creditPersistenceMongodb.findByUserReference("53354324"))
                .expectNextMatches(credit -> {
                    assertEquals("sdgfsgfdg53", credit.getReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testAddCreditSale() {
        StepVerifier
                .create(this.creditPersistenceMongodb.addCreditSale("53354324",
                        CreditSale.builder().reference("dsfdsf54fds").ticketReference("WB9-e8xQT4ejb74r1vLrCw").payed(false).build()))
                .expectNextMatches(credit -> {
                    assertEquals("sdgfsgfdg53", credit.getReference());
                    assertNotNull(credit.getCreditSales());
                    assertEquals(3, credit.getCreditSales().size());
                    assertEquals("dsfdsf54fds", credit.getCreditSales().get(2).getReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testAddCreditSaleWhenCreditSalesInCreditLineAreEmpty() {
        StepVerifier
                .create(this.creditPersistenceMongodb.addCreditSale("345436324",
                        CreditSale.builder().reference("dsfdsf54fds").ticketReference("WB9-e8xQT4ejb74r1vLrCw").payed(false).build()))
                .expectNextMatches(credit -> {
                    assertEquals("44366sgfdg53", credit.getReference());
                    assertNotNull(credit.getCreditSales());
                    assertEquals(1, credit.getCreditSales().size());
                    assertEquals("dsfdsf54fds", credit.getCreditSales().get(0).getReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindCreditSalesWithOnlyUnpaidTickets() {
        StepVerifier
                .create(this.creditPersistenceMongodb.findCreditSalesWithOnlyUnpaidTickets("53354324"))
                .expectNextMatches(ticket -> {
                    assertEquals("lpiHOlsoS_WkkEyWeFNJtg", ticket.getReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }

}
