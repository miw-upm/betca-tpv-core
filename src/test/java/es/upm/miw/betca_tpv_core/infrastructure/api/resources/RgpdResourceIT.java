package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Base64;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.RgpdResource.RGPDS;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.RgpdResource.USER_MOBILE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RestTestConfig
class RgpdResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @MockBean
    private UserMicroservice userMicroservice;

    @Test
    void testCreateRgpd() {
        String userMobile = "600600601";
        String userName = "Dario";
        String agreementEncoded = Base64.getEncoder().encodeToString("SampleAgreement".getBytes());
        RgpdDto rgpdDto = new RgpdDto(RgpdType.BASIC, agreementEncoded, userMobile, userName);

        when(userMicroservice.readByMobile(userMobile)).thenReturn(Mono.just(User.builder().mobile(userMobile).firstName(userName).build()));

        RgpdDto createdRgpd = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(RGPDS)
                .body(Mono.just(rgpdDto), RgpdDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RgpdDto.class)
                .value(Assertions::assertNotNull)
                .value(returnRgpdDto -> {
                    assertEquals(RgpdType.BASIC, returnRgpdDto.getRgpdType());
                    assertEquals(userMobile, returnRgpdDto.getUserMobile());
                    assertEquals(userName, returnRgpdDto.getUserName());
                    assertEquals(agreementEncoded, returnRgpdDto.getAgreement());
                })
                .returnResult().getResponseBody();

        assertNotNull(createdRgpd);
    }

    @Test
    void testGetAllRgpds() {
        List<RgpdDto> rgpdDtoList = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(RGPDS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RgpdDto.class)
                .value(Assertions::assertNotNull)
                .returnResult().getResponseBody();

        assertNotNull(rgpdDtoList);
        assertThat(rgpdDtoList.size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void testUpdateRgpd() {
        String userMobile = "600000001";
        RgpdDto updatedRgpdDto =  RgpdDto.builder()
                .rgpdType(RgpdType.BASIC)
                .agreement(Base64.getEncoder().encodeToString("UpdatedAgreement".getBytes()))
                .userMobile(userMobile)
                .userName("Alex")
                .build();

        RgpdDto responseRgpdDto = this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(RGPDS + "/" + userMobile)
                .bodyValue(updatedRgpdDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RgpdDto.class)
                .returnResult().getResponseBody();

        assertNotNull(responseRgpdDto);
        assertEquals(RgpdType.BASIC, responseRgpdDto.getRgpdType());
        assertEquals(Base64.getEncoder().encodeToString("UpdatedAgreement".getBytes()), responseRgpdDto.getAgreement());
        assertEquals(userMobile, responseRgpdDto.getUserMobile());
        assertEquals("Alex", responseRgpdDto.getUserName());
    }
}