package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Credit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
