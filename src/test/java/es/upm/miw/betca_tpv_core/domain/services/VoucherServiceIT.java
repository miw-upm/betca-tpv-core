package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@TestConfig
public class VoucherServiceIT {

    @MockBean
    private UserMicroservice userMicroservice;
    @Autowired
    private VoucherService voucherService;

    @Test
    void testCreateVoucher() {
        String userMobile = "666666000";
        String userName = "adm";

        Voucher voucher = Voucher.builder()
                .value(BigDecimal.TEN)
                .user(User.builder().mobile(userMobile).firstName(userName).build())
                .build();

        voucher.doDefault();

        when(userMicroservice.readByMobile(userMobile)).thenReturn(Mono.just(User.builder().mobile(userMobile).firstName(userName).build()));

        StepVerifier.create(voucherService.create(voucher)).expectNextMatches(returnVoucher -> {
            assertNotNull(returnVoucher);
            assertNotNull(returnVoucher.getUser());
            assertNotNull(returnVoucher.getUser().getMobile());
            assertEquals(voucher.getValue(),returnVoucher.getValue());
            assertEquals(userMobile, returnVoucher.getUser().getMobile());
            assertEquals(userName, returnVoucher.getUser().getFirstName());
            return true;
        }).verifyComplete();
    }

    /*@Test
    void testCreateVoucherUserNotFound() {
        String userMobile = "000000000";
        String userName = "Paco";

        Voucher voucher = Voucher.builder()
                .value(new BigDecimal(100))
                .user(User.builder().mobile(userMobile).firstName(userName).build())
                .build();

        voucher.doDefault();

        when(userMicroservice.readByMobile(userMobile)).thenReturn(Mono.error(new BadRequestException("User not found")));

        StepVerifier.create(voucherService.create(voucher)).expectErrorMatches(throwable -> throwable instanceof BadRequestException).verify();
    }*/

    /*@Test
    void testReadByReference() {
        StepVerifier
                .create(this.voucherService.read("VOUCHER001"))
                .expectNextMatches(offer -> {
                    assertEquals("VOUCHER001", offer.getReference());
                    assertEquals(new BigDecimal(50), offer.getValue());
                    return true;
                })
                .expectComplete()
                .verify();
    }*/

    @Test
    void testReadByReferenceNotFRound() {
        StepVerifier
                .create(this.voucherService.read("badreference"))
                .expectError(NotFoundException.class)
                .verify();
    }
}
