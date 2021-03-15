package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
class CreditReactiveIT {
    @Autowired
    private CreditReactive creditReactive;

    @Test
    void testFindAll() {
        StepVerifier
                .create(this.creditReactive.findAll())
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUserReference() {
        StepVerifier
                .create(this.creditReactive.findByUserReference("53354324"))
                .expectNextMatches(credit -> {
                    assertEquals("sdgfsgfdg53", credit.getReference());
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
