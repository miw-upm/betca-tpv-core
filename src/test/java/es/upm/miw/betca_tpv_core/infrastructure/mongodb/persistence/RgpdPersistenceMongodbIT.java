package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
}
