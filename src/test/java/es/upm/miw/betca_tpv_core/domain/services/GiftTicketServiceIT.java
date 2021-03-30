package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@TestConfig
public class GiftTicketServiceIT {

    @Autowired
    private GiftTicketService giftTicketService;

    @Test
    void testReceipt() {
        StepVerifier
                .create(this.giftTicketService.readReceipt("asdhgsjd56736jdfb"))
                .expectNextCount(1)
                .verifyComplete();
    }
}
