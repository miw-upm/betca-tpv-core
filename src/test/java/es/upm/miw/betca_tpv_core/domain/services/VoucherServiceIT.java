package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@TestConfig
public class VoucherServiceIT {

    @Autowired
    private VoucherService voucherService;

    @Test
    void testReadAll() {
        StepVerifier
                .create(voucherService.readAll())
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByReferenceExist() {
        String reference = "6aa2b2e8-8fcb-11eb-8dcd-0242ac130003";
        StepVerifier
                .create(voucherService.readByReference(reference))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByReferenceNotExist() {
        StepVerifier
                .create(voucherService.readByReference("not_exist"))
                .expectError()
                .verify();
    }
}
