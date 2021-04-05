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
    void testCreate() {
        LocalDate salesDate = LocalDate.of(2021, 4, 2);
        String[] str = {"8400000000017", "8400000000048"};
        Salespeople salespeople = Salespeople.builder()
                .salesperson("Rosaria")
                .salesDate(salesDate)
                .ticketBarcode("5fa4603b7513a164c99677a8")
                .articleBarcode(str)
                .amount(2)
                .total(new BigDecimal(56))
                .build();
        salespeople.doDefault();

        StepVerifier
                .create(this.salespeoplePersistenceMongodb.creat(salespeople))
                .expectNextMatches(salespeopleTest -> {
                    assertEquals("Rosaria", salespeople.getSalesperson());
                    assertEquals(salesDate, salespeople.getSalesDate());
                    assertEquals("5fa4603b7513a164c99677a8", salespeople.getTicketBarcode());
                    assertEquals(str, salespeople.getArticleBarcode());
                    assertEquals(2, salespeople.getAmount());
                    assertEquals(new BigDecimal(56), salespeople.getTotal());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindBySalespersonAndSalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MARCH, 1);
        LocalDate dateEnd = LocalDate.of(2021, Month.APRIL, 1);
        StepVerifier
                .create(this.salespeoplePersistenceMongodb.findBySalespersonAndSalesDateBetween("Luis", dateBegin, dateEnd))
                .expectNextMatches(salespeople -> {
                    assertTrue(salespeople.getSalesDate().isAfter(dateBegin) && salespeople.getSalesDate().isBefore(dateEnd));
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindBySalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MARCH, 1);
        LocalDate dateEnd = LocalDate.of(2021, Month.APRIL, 1);
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
