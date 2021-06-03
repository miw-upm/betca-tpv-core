package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
public class SalespeopleReactiveIT {

    @Autowired
    private SalespeopleReactive salespeopleReactive;

    @Test
    void testFindByUserAndDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MAY, 1);
        LocalDate dateEnd = LocalDate.of(2021, Month.MAY, 15);
        StepVerifier
                .create(this.salespeopleReactive.findAndUserMobileAndSalesDateBetween("admin", dateBegin, dateEnd))
                .expectNextMatches(salespeople -> {
                    assertTrue(salespeople.getSalesDate().isAfter(dateBegin) && salespeople.getSalesDate().isBefore(dateEnd));
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void findBySalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MAY, 1);
        LocalDate dateEnd = LocalDate.of(2021, Month.JUNE, 1);
        StepVerifier
                .create(this.salespeopleReactive.findBySalesDateBetween(dateBegin, dateEnd))
                .expectNextMatches(salespeople -> {
                    assertTrue(salespeople.getSalesDate().isAfter(dateBegin) && salespeople.getSalesDate().isBefore(dateEnd));
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
