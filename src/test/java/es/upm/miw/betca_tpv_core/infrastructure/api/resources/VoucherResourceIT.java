package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestTestConfig
public class VoucherResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testReadAll() {
        restClientTestService.loginAdmin(webTestClient)
                .get().uri(VoucherResource.VOUCHERS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Voucher.class);
    }

    @Test
    void testReadByReference() {
        String reference = "6aa2b2e8-8fcb-11eb-8dcd-0242ac130003";
        restClientTestService.loginAdmin(webTestClient)
                .get().uri(VoucherResource.VOUCHERS + "/" + reference)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Voucher.class);
    }

    @Test
    void testReadByReferenceNotExist() {
        String reference = "not_exist";
        restClientTestService.loginAdmin(webTestClient)
                .get().uri(VoucherResource.VOUCHERS + "/" + reference)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateVoucher() {
        Voucher voucher = new Voucher(null, 200, LocalDateTime.now(), null);
        restClientTestService.loginAdmin(webTestClient)
                .post().uri(VoucherResource.VOUCHERS)
                .body(Mono.just(voucher), Voucher.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Voucher.class)
                .value(v -> {
                    System.out.println(">>>>>> Voucher reference: " + v.getReference());
                });
    }
}
