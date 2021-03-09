package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@TestConfig
public class OfferServiceIT {

    @Autowired
    private OfferService offerService;

    @Test
    void testPrintOffer() {
        StepVerifier
                .create(this.offerService.print("cmVmZXJlbmNlb2ZmZXIx"))
                .expectNextCount(1)
                .verifyComplete();
    }
}
