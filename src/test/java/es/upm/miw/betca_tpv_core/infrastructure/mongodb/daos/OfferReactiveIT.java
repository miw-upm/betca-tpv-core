package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class OfferReactiveIT {

    @Autowired
    private OfferReactive offerReactive;

    @Test
    void testFindByReferenceAndDescriptionNullSafe() {
        StepVerifier
                .create(this.offerReactive.findByReferenceAndDescriptionNullSafe(
                        null, null))
                .expectNextMatches(offer -> {
                    assertTrue(offer.getDescription().contains("code"));
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
