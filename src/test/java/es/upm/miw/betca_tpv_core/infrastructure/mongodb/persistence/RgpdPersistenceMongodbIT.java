package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class RgpdPersistenceMongodbIT {

    @Autowired
    private RgpdPersistenceMongodb rgpdPersistenceMongodb;

    @Test
    void testCreateRgpd() {
        byte[] agreementEncoded = "NewAgreement".getBytes();
        String userMobile = "600600603";
        String userName = "Alex";

        Rgpd rgpd = new Rgpd(RgpdType.BASIC, agreementEncoded, User.builder().mobile(userMobile).firstName(userName).build());

        StepVerifier
                .create(rgpdPersistenceMongodb.create(rgpd))
                .expectNextMatches(savedRgpd -> {
                    assertNotNull(savedRgpd);
                    assertEquals(userMobile, savedRgpd.getUser().getMobile());
                    assertEquals(RgpdType.BASIC, savedRgpd.getRgpdType());
                    assertEquals(userName, savedRgpd.getUser().getFirstName());
                    assertArrayEquals(agreementEncoded, savedRgpd.getAgreement());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindRgpdByUserMobileSuccess() {
        String userMobile = "600000001";
        StepVerifier
                .create(rgpdPersistenceMongodb.findRgpdByUserMobile(userMobile))
                .expectNextMatches(rgpd -> rgpd.getUser().getMobile().equals(userMobile))
                .verifyComplete();
    }

    @Test
    void testFindRgpdByUserMobileNotFound() {
        String userMobile = "999999999";
        StepVerifier
                .create(rgpdPersistenceMongodb.findRgpdByUserMobile(userMobile))
                .verifyComplete();
    }

    @Test
    void testFindAllRgpds() {
        StepVerifier.
                create(rgpdPersistenceMongodb.findAllRgpds())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(rgpd -> true)
                .consumeRecordedWith(rgpdList -> {
                    assertThat(rgpdList).isNotNull();
                    assertThat(rgpdList.size()).isGreaterThanOrEqualTo(3);
                })
                .verifyComplete();
    }

    @Test
    void testUpdateRgpd_Success() {
        String userMobile = "600000001";
        Rgpd updatedRgpd = Rgpd.builder()
                .rgpdType(RgpdType.ADVANCED)
                .agreement("UpdatedAgreement".getBytes())
                .user(User.builder().mobile(userMobile).firstName("Alex").build())
                .build();

        StepVerifier
                .create(rgpdPersistenceMongodb.updateRgpd(userMobile, updatedRgpd))
                .assertNext(rgpd -> {
                    assertEquals(RgpdType.ADVANCED, rgpd.getRgpdType());
                    assertArrayEquals("UpdatedAgreement".getBytes(), rgpd.getAgreement());
                    assertEquals("600000001", rgpd.getUser().getMobile());
                    assertEquals("Alex", rgpd.getUser().getFirstName());
                })
                .verifyComplete();
    }

    @Test
    void testUpdateRgpd_Failure_NotFoundException() {
        String userMobile = "9999999999";
        Rgpd updatedRgpd = Rgpd.builder()
                .rgpdType(RgpdType.ADVANCED)
                .agreement("UpdatedAgreement".getBytes())
                .user(User.builder().mobile(userMobile).firstName("Sergio").build())
                .build();

        StepVerifier.create(rgpdPersistenceMongodb.updateRgpd(userMobile, updatedRgpd))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException)
                .verify();
    }

    @Test
    void testUpdateRgpd_Failure_ConflictException() {
        String userMobile = "9999999999";
        Rgpd updatedRgpd = Rgpd.builder()
                .rgpdType(RgpdType.ADVANCED)
                .agreement("UpdatedAgreement".getBytes())
                .user(User.builder().mobile(userMobile).firstName("Sergio").build())
                .build();

        StepVerifier.create(rgpdPersistenceMongodb.updateRgpd("600000000", updatedRgpd))
                .expectErrorMatches(throwable -> throwable instanceof ConflictException)
                .verify();
    }
}
