package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class SalespeopleReactiveIT {

    @Autowired
    private SalespeopleReactive salespeopleReactive;
    @Test
    void testFindByUserAndDate(){
        LocalDate salespeopleTime=LocalDate.of(2021,Month.APRIL,1);
        StepVerifier
                .create(this.salespeopleReactive.findBySalespersonAndSalesDate("Rosaria",salespeopleTime))
                .expectNextMatches(salespeople->{
                    assertEquals(2,salespeople.getNumArticle());
                    assertEquals(new BigDecimal(23),salespeople.getFinalValue());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void findBySalesDate(){
        LocalDate salespeopleTime=LocalDate.of(2021,Month.APRIL,2);
        StepVerifier
                .create(this.salespeopleReactive.findBySalesDate(salespeopleTime))
                .expectNextMatches(salespeople->{
                    assertEquals(5,salespeople.getNumArticle());
                    assertEquals(new BigDecimal(25),salespeople.getFinalValue());
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
