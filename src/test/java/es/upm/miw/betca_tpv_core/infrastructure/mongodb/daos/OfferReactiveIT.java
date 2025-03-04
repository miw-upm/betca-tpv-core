package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class OfferReactiveIT {

    @Autowired
    private OfferReactive offerReactive;

    @Test
    void testFindByReferenceAndDescriptionAndDiscountNullSafe() {
        StepVerifier
                .create(this.offerReactive.findByReferenceAndDescriptionAndDiscountNullSafe(
                        null, null, null))
                .expectNextMatches(offer -> {
                    assertNotNull(offer);
                    assertNotNull(offer.getDescription());
                    assertNotNull(offer.getReference());
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
