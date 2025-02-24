package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.RgpdResource.RGPDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTestConfig
class RgpdResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreateRgpd() {
        String userMobile = "600600605";
        String agreementEncoded = Base64.getEncoder().encodeToString("SampleAgreement".getBytes());
        RgpdDto rgpdDto = new RgpdDto(userMobile, RgpdType.BASIC, agreementEncoded);

        Rgpd createdRgpd = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(RGPDS)
                .body(Mono.just(rgpdDto), RgpdDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Rgpd.class)
                .value(Assertions::assertNotNull)
                .value(returnRgpd -> {
                    assertNotNull(returnRgpd.getUser());
                    assertNotNull(returnRgpd.getUser().getMobile());
                    assertEquals(userMobile, returnRgpd.getUser().getMobile());
                    assertEquals(RgpdType.BASIC, returnRgpd.getRgpdType());
                    assertEquals("SampleAgreement", new String(returnRgpd.getAgreement()));
                })
                .returnResult().getResponseBody();

        assertNotNull(createdRgpd);
    }
}