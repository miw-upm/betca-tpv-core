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
                .create(this.budgetReactive.findById("1"))
                .expectNextMatches(budget -> {
                    assertEquals("1", budget.getId());
                    return true;
                })
                .thenCancel()
                .verify();
    }
    @Test
    void testFindByReferenceNullSafe() {
        StepVerifier
                .create(this.budgetReactive.findByReferenceNullSafe(""))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }
    @Test
    void readByReference() {
        StepVerifier
                .create(this.budgetReactive.readByReference("cmVmZXJlbmNlb2ZmZXIy"))
                .expectNextMatches(budget -> {
                    assertEquals("cmVmZXJlbmNlb2ZmZXIy", budget.getReference());
                    return true;
                })
                .thenCancel()
                .verify();
    }

}
