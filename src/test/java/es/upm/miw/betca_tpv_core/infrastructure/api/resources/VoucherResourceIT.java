package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.VoucherResource.SEARCH;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.VoucherResource.REFERENCE_ID;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.VoucherResource.VOUCHERS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RestTestConfig
public class VoucherResourceIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;
    @MockBean
    private UserMicroservice userMicroservice;

    @Test
    void testCreate() {
        String userMobile = "666666000";
        String userName = "Martxel";
        Voucher voucher = Voucher.builder().value(new BigDecimal(60)).user(User.builder().mobile("666666000").firstName(userName).build()).build();

        voucher.doDefault();

        when(userMicroservice.readByMobile(userMobile)).thenReturn(Mono.just(User.builder().mobile(userMobile).firstName(userName).build()));

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(VOUCHERS)
                .body(Mono.just(voucher), Voucher.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Voucher.class)
                .value(Assertions::assertNotNull)
                .value(returnVoucher -> {
                    assertEquals(new BigDecimal(60), returnVoucher.getValue());
                });
    }

    /*@Test
    void testReadByReference() {
        Voucher voucher = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(VOUCHERS + REFERENCE_ID, "VOUCHER123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Voucher.class)
                .value(Assertions::assertNotNull)
                .value(returnVoucher -> {
                    assertEquals("VOUCHER123", returnVoucher.getReference());
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(voucher);
    }*/

    @Test
    void testReadByReferenceNotFoundException() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(VOUCHERS + REFERENCE_ID, "INVALID_REF")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testFindByReferenceAndValueNullSafe() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(VOUCHERS + SEARCH)
                        .queryParam("reference", "a8ebf3a0-158a-4d77-91d8-709a9eb9fd40")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Voucher.class)
                .value(Assertions::assertNotNull);
    }
}
