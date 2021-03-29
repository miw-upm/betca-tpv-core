package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdUserDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdUserWithFileDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTestConfig
public class DataProtectionActResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testRead() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT +
                        DataProtectionActResource.MOBILE_ID, "123456789")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RgpdUserDto.class)
                .value(Assertions::assertNotNull)
                .value(rgpdUserDto -> {
                    assertEquals(rgpdUserDto.getMobile(), "123456789");
                    assertEquals(rgpdUserDto.getRgpdType(), RgpdType.ADVANCED);
                });
    }

    @Test
    void testReadNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT +
                        DataProtectionActResource.MOBILE_ID, "999999999")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RgpdUserDto.class)
                .value(Assertions::assertNull);
    }

    @Test
    void testCreate() {
        RgpdUserWithFileDto rgpd = new RgpdUserWithFileDto("987456321", RgpdType.MEDIUM, "YQ==");
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT)
                .body(Mono.just(rgpd), Rgpd.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RgpdUserDto.class)
                .value(Assertions::assertNotNull)
                .value(rgpdUserDto -> {
                    assertEquals(rgpdUserDto.getMobile(), rgpd.getMobile());
                    assertEquals(rgpdUserDto.getRgpdType(), rgpd.getRgpdType());
                });
    }

    @Test
    void testCreateAlreadyExist() {
        RgpdUserWithFileDto rgpd = new RgpdUserWithFileDto("123456789", RgpdType.MEDIUM, "YQ==");
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT)
                .body(Mono.just(rgpd), Rgpd.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(Error.class);
    }

}
