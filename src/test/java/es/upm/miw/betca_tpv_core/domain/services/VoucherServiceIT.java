package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.UUID;

@TestConfig
public class VoucherServiceIT {

    @Autowired
    private VoucherService voucherService;

    //@Test
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

    @Test
    void testCreate() {
        Voucher voucher = new Voucher(UUID.randomUUID(), 100, null, null);
        StepVerifier
                .create(voucherService.create(voucher))
                .expectNextMatches(v -> voucher.getReference().equals(v.getReference()))
                .expectComplete()
                .verify();
    }

    @Test
    void testConsume() {
        String reference = "6aa2b2e8-8fcb-11eb-8dcd-0242ac130003";
        StepVerifier
                .create(this.voucherService.consume(reference))
                .expectNextMatches(v -> v.getDateOfUse() != null)
                .expectComplete()
                .verify();
    }

    @Test
    void testConsumeVoucherNotExist() {
        String reference = "not_exists";
        StepVerifier
                .create(this.voucherService.consume(reference))
                .expectError()
                .verify();
    }

    @Test
    void testPrintByReference() {
        String reference = "6aa2b2e8-8fcb-11eb-8dcd-0242ac130003";
        StepVerifier
                .create(this.voucherService.printByReference(reference))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testPrintByReferenceVoucherNotExist() {
        String reference = "not_exists";
        StepVerifier
                .create(this.voucherService.printByReference(reference))
                .expectError()
                .verify();
    }
}
