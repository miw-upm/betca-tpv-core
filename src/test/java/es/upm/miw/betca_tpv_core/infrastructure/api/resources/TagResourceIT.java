package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Provider;
import es.upm.miw.betca_tpv_core.domain.model.Tag;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.TagResource.TAGS;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTestConfig
public class TagResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        Tag tag = Tag.builder().name("tagTest1").group("tagGroup1").description("description").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(TAGS)
                .body(Mono.just(tag), Tag.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Tag.class)
                .value(Assertions::assertNotNull)
                .value(returnTag -> {
                    System.out.println(">>>>> Test:: returnTag:" + returnTag);
                    assertEquals("tagTest1", returnTag.getName());
                    assertEquals("tagGroup1", returnTag.getGroup());
                    assertEquals("description", returnTag.getDescription());
                });
    }
}
