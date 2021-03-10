package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdUserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

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

    //TODO Test read

}
