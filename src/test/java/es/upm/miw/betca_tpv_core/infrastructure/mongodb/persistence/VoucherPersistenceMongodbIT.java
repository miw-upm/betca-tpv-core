package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class VoucherPersistenceMongodbIT {

    @Autowired
    private VoucherPersistenceMongodb voucherPersistenceMongodb;

    @Test
    void testCreate() {
        String userMobile = "600600610";
        String userName = "Juan";
        Voucher voucher = Voucher.builder()
                .value(new BigDecimal(50))
                .creationDate(LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10))
                .user(User.builder().mobile(userMobile).firstName(userName).build())
                .build();
        voucher.doDefault();
        StepVerifier
                .create(this.voucherPersistenceMongodb.create(voucher))
                .expectNextMatches(returnVoucher -> {
                    assertEquals(new BigDecimal(50), returnVoucher.getValue());
                    assertNotNull(returnVoucher.getCreationDate());
                    assertNull(returnVoucher.getDateOfUse());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    /*@Test
    void testReadByReference() {
        StepVerifier
                .create(this.voucherPersistenceMongodb.readByReference("VOUCHER001"))
                .expectNextMatches(returnVoucher -> {
                    assertEquals("VOUCHER001", returnVoucher.getReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }*/

    @Test
    void testReadByReferenceNotFound() {
        StepVerifier
                .create(this.voucherPersistenceMongodb.readByReference("INVALID_REF"))
                .expectError(NotFoundException.class)
                .verify();
    }
}
