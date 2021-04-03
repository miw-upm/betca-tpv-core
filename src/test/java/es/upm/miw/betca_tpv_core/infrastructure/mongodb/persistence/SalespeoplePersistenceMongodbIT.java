package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
public class SalespeoplePersistenceMongodbIT {

    @Autowired
    private SalespeoplePersistenceMongodb salespeoplePersistenceMongodb;

    @Test
    void findBySalespersonAndSalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MARCH, 1);
        LocalDate dateEnd=LocalDate.of(2021,Month.APRIL,1);
        StepVerifier
                .create(this.salespeoplePersistenceMongodb.findBySalespersonAndSalesDateBetween("Pablo",dateBegin,dateEnd))
                .expectNextMatches(salespeople -> {
                    assertTrue(salespeople.getSalesDate().isAfter(dateBegin)&&salespeople.getSalesDate().isBefore(dateEnd));
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void findBySalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MARCH, 1);
        LocalDate dateEnd=LocalDate.of(2021,Month.APRIL,1);
        StepVerifier
                .create(this.salespeoplePersistenceMongodb.findBySalesDateBetween(dateBegin,dateEnd))
                .expectNextMatches(salespeople -> {
                    assertTrue(salespeople.getSalesDate().isAfter(dateBegin)&&salespeople.getSalesDate().isBefore(dateEnd));
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
