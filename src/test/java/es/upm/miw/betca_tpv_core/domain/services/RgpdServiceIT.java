package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@TestConfig
class RgpdServiceIT {

    @MockBean
    private UserMicroservice userMicroservice;
    @Autowired
    private RgpdService rgpdService;

    @Test
    void testCreateRgpdSuccess() {
        byte[] agreementEncoded = "NewAgreement".getBytes();
        String userMobile = "600600602";
        String userName = "Sergio";

        Rgpd rgpd = new Rgpd(RgpdType.BASIC, agreementEncoded, User.builder().mobile(userMobile).firstName(userName).build());

        when(userMicroservice.readByMobile(userMobile)).thenReturn(Mono.just(User.builder().mobile(userMobile).firstName(userName).build()));

        StepVerifier.create(rgpdService.create(rgpd)).expectNextMatches(savedRgpd -> {
            assertNotNull(savedRgpd);
            assertNotNull(savedRgpd.getUser());
            assertNotNull(savedRgpd.getUser().getMobile());
            assertEquals(RgpdType.BASIC, savedRgpd.getRgpdType());
            assertEquals(userMobile, savedRgpd.getUser().getMobile());
            assertEquals(userName, savedRgpd.getUser().getFirstName());
            assertArrayEquals(agreementEncoded, savedRgpd.getAgreement());
            return true;
        }).verifyComplete();
    }

    @Test
    void testCreateRgpdUserNotFound() {
        byte[] agreementEncoded = "NewAgreement".getBytes();
        String userMobile = "600600602";
        String userName = "Juan";

        Rgpd rgpd = new Rgpd(RgpdType.BASIC, agreementEncoded, User.builder().mobile(userMobile).firstName(userName).build());

        when(userMicroservice.readByMobile(userMobile)).thenReturn(Mono.error(new BadRequestException("User not found")));

        StepVerifier.create(rgpdService.create(rgpd)).expectErrorMatches(throwable -> throwable instanceof BadRequestException).verify();
    }

    @Test
    void testCreateRgpdAlreadySigned() {
        byte[] agreementEncoded = "NewAgreement".getBytes();
        String userMobile = "600000001";
        String userName = "David";

        Rgpd newRgpd = new Rgpd(RgpdType.BASIC, agreementEncoded, User.builder().mobile(userMobile).firstName(userName).build());

        when(userMicroservice.readByMobile(userMobile)).thenReturn(Mono.just(User.builder().mobile(userMobile).firstName(userName).build()));

        StepVerifier.create(rgpdService.create(newRgpd)).expectErrorMatches(throwable -> throwable instanceof BadRequestException).verify();
    }

    @Test
    void testFindAllRgpds() {
        StepVerifier.create(rgpdService.findAllRgpds()).recordWith(ArrayList::new).thenConsumeWhile(rgpd -> true).consumeRecordedWith(rgpdList -> {
            assertThat(rgpdList).isNotNull();
            assertThat(rgpdList.size()).isGreaterThanOrEqualTo(3);
        }).verifyComplete();
    }

    @Test
    void testUpdateRgpd() {
        String userMobile = "600000001";
        Rgpd updatedRgpd = Rgpd.builder()
                .rgpdType(RgpdType.ADVANCED)
                .agreement("NewAndFinalAgreement".getBytes())
                .user(User.builder().mobile("600000001").firstName("Alex").build())
                .build();

        StepVerifier
                .create(rgpdService.updateRgpd(userMobile, updatedRgpd))
                .assertNext(rgpd -> {
                    assertEquals(RgpdType.ADVANCED, rgpd.getRgpdType());
                    assertArrayEquals("NewAndFinalAgreement".getBytes(), rgpd.getAgreement());
                    assertEquals("600000001", rgpd.getUser().getMobile());
                    assertEquals("Alex", rgpd.getUser().getFirstName());
                })
                .verifyComplete();
    }
}