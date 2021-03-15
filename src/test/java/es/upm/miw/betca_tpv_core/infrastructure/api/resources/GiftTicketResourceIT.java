package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.GiftTicketResource.GIFTTICKETS;
import static org.junit.jupiter.api.Assertions.*;


@RestTestConfig
public class GiftTicketResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        GiftTicket giftTicket = GiftTicket.builder().message("description of GiftTicket")
                .ticketId("5fa45e863d6e834d642689ac").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(GIFTTICKETS)
                .body(Mono.just(giftTicket), GiftTicket.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GiftTicket.class)
                .value(Assertions::assertNotNull)
                .value(returnGiftTicket -> {
                    System.out.println(">>>>> Test:: returnGiftTicket:" + returnGiftTicket);
                    assertNotNull(returnGiftTicket.getId());
                    assertEquals("description of GiftTicket", returnGiftTicket.getMessage());
                    assertEquals("5fa45e863d6e834d642689ac", returnGiftTicket.getTicketId());
                });
    }

    @Test
    void testCreateNotFoundTicketIdException() {
        GiftTicket giftTicket = GiftTicket.builder().message("description of GiftTicket")
                .ticketId("sfsfsfd").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(GIFTTICKETS)
                .body(Mono.just(giftTicket), GiftTicket.class)
                .exchange()
                .expectStatus().isNotFound();
    }

}
