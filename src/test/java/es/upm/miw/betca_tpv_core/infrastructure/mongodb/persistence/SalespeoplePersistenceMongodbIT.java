package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
public class SalespeoplePersistenceMongodbIT {

    @Autowired
    private SalespeoplePersistenceMongodb salespeoplePersistenceMongodb;

    @Test
    void testFindBySalespersonAndSalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MAY, 1);
        LocalDate dateEnd = LocalDate.of(2021, Month.MAY, 15);
        StepVerifier
                .create(this.salespeoplePersistenceMongodb.findBySalespersonAndSalesDateBetween("admin", dateBegin, dateEnd))
                .expectNextMatches(salespeople -> {
                    assertTrue(salespeople.getSalesDate().isAfter(dateBegin) && salespeople.getSalesDate().isBefore(dateEnd));
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindBySalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MAY, 1);
        LocalDate dateEnd = LocalDate.of(2021, Month.JUNE, 1);
        StepVerifier
                .create(this.salespeoplePersistenceMongodb.findBySalesDateBetween(dateBegin, dateEnd))
                .expectNextMatches(salespeople -> {
                    assertTrue(salespeople.getSalesDate().isAfter(dateBegin) && salespeople.getSalesDate().isBefore(dateEnd));
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
