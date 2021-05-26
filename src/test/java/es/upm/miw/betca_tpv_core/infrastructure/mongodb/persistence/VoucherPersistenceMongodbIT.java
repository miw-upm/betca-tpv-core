package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

@TestConfig
public class VoucherPersistenceMongodbIT {

    @Autowired
    private VoucherPersistenceMongodb voucherPersistenceMongodb;

    @Test
    void testReadAll() {
        StepVerifier
                .create(voucherPersistenceMongodb.readAll())
                .expectNextMatches(v -> v.getReference() != null)
                .thenCancel()
                .verify();
    }

    @Test
    void testReadByReferenceExist() {
        String reference = "6aa2b2e8-8fcb-11eb-8dcd-0242ac130003";
        StepVerifier
                .create(voucherPersistenceMongodb.readByReference(reference))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByReferenceNotExist() {
        StepVerifier
                .create(voucherPersistenceMongodb.readByReference("not_exist"))
                .expectError()
                .verify();
    }

    @Test
    void createVoucher() {
        UUID ref = UUID.randomUUID();
        Voucher voucher = new Voucher(ref, 140, null, null);
        StepVerifier
                .create(voucherPersistenceMongodb.create(voucher))
                .expectNextMatches(v -> voucher.getReference().equals(v.getReference()) &&
                        voucher.getValue().equals(v.getValue()))
                .expectComplete()
                .verify();
    }

    @Test
    void testConsume() {
        String reference = "6aa2b2e8-8fcb-11eb-8dcd-0242ac130003";
        StepVerifier
                .create(this.voucherPersistenceMongodb.consume(reference))
                .expectNextMatches(v -> v.getDateOfUse() != null)
                .expectComplete()
                .verify();
    }

    @Test
    void testConsumeVoucherNotExist() {
        String reference = "not_exists";
        StepVerifier
                .create(this.voucherPersistenceMongodb.consume(reference))
                .expectError()
                .verify();
    }

    //@Test
    void testFindUnconsumedVouchersBetweenDates() {
        LocalDateTime from = LocalDateTime.now().minusDays(3);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        StepVerifier
                .create(this.voucherPersistenceMongodb.getUnconsumedVouchersBetweenDates(from, to))
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }
}
