package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Message;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
class MessengerResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        Message message = new Message("Subject", "Text message", "6", "6", false,
                LocalDate.of(2021, 9, 15));

        Message createdMessage = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(MessengerResource.MESSENGER)
                .body(Mono.just(message), Message.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Message.class)
                .value(Assertions::assertNotNull)
                .value(returnMessage -> {
                    assertNotNull(returnMessage.getSubject());
                    assertEquals("Subject", returnMessage.getSubject());
                    assertNotNull(returnMessage.getCreationDate());
                    assertEquals(new User("6").getMobile(), returnMessage.getUserTo());
                    assertEquals(new User("6").getMobile(), returnMessage.getUserFrom());
                }).returnResult().getResponseBody();
        assertNotNull(createdMessage);
    }

}
