package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@TestConfig
public class VoucherReactiveIT {

    @Autowired
    private VoucherReactive voucherReactive;

    @Test
    void testReadAll() {
        StepVerifier
                .create(voucherReactive.findAll())
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByReferenceExist() {
        String reference = "6aa2b2e8-8fcb-11eb-8dcd-0242ac130003";
        StepVerifier
                .create(voucherReactive.findById(reference))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByReferenceNotExist() {
        StepVerifier
                .create(voucherReactive.findById("not_exist"))
                .expectComplete()
                .verify();
    }
}
