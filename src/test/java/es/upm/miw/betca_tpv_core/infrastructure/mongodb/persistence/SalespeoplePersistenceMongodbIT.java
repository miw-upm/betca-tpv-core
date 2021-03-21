package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class SalespeoplePersistenceMongodbIT {

    @Autowired
    private SalespeoplePersistenceMongodb salespeoplePersistenceMongodb;

    @Test
    void findBySalespersonAndSalesDate() {
        LocalDate localDate = LocalDate.of(2021, Month.APRIL, 1);
        StepVerifier
                .create(this.salespeoplePersistenceMongodb.findBySalespersonAndSalesDate("Rosaria", localDate))
                .expectNextMatches(salespeople -> {
                    assertEquals(2, salespeople.getNumArticle());
                    assertEquals(new BigDecimal(23), salespeople.getFinalValue());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void findBySalesDate() {
        LocalDate localDate = LocalDate.of(2021, Month.APRIL, 2);
        StepVerifier
                .create(this.salespeoplePersistenceMongodb.findBySalesDate(localDate))
                .expectNextMatches(salespeople -> {
                    assertEquals(5, salespeople.getNumArticle());
                    assertEquals(new BigDecimal(25.3), salespeople.getFinalValue());
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
