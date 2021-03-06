package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class OfferReactiveIT {

    @Autowired
    private OfferReactive offerReactive;

    @Test
    void testFindByReference() {
        StepVerifier
                .create(this.offerReactive.findByReference("cmVmZXJlbmNlb2ZmZXIz"))
                .expectNextMatches(offer -> {
                    assertEquals("3lh67i968h3d7809l982376mn", offer.getId());
                    assertEquals("this is offer 3", offer.getDescription());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByReferenceAndDescriptionNullSafe() {
        StepVerifier
                .create(this.offerReactive.findByReferenceAndDescriptionNullSafe("cmVmZX", "3"))
                .expectNextMatches(offer -> {
                    assertEquals("3lh67i968h3d7809l982376mn", offer.getId());
                    assertEquals("cmVmZXJlbmNlb2ZmZXIz", offer.getReference());
                    assertEquals("this is offer 3", offer.getDescription());
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
