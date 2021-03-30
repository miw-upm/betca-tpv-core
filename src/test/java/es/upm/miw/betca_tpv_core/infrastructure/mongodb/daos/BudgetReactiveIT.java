package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;


@TestConfig
public class BudgetReactiveIT {

    @Autowired
    private BudgetReactive budgetReactive;

    @Test
    void testFindById(){
        StepVerifier
                .create(this.budgetReactive.findById("b600b5c9cac1"))
                .expectNextMatches(budget -> {
                    assertEquals("b600b5c9cac1", budget.getId());
                    return true;
                })
                .thenCancel()
                .verify();
    }
    @Test
    void findNullSafe() {
        StepVerifier
                .create(this.budgetReactive.findNullSafe(null))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }


}
