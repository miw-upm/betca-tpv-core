package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.RgpdReactive;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class RgpdPersistenceMongodbIT {

    @Autowired
    private RgpdPersistenceMongodb rgpdPersistenceMongodb;

    @Autowired
    private RgpdReactive rgpdReactive;

    @Test
    void testCreateRgpd() {

        String agreementEncoded = Base64.getEncoder().encodeToString("NewAgreement".getBytes());
        String userMobile = "600600601";

        StepVerifier
                .create(rgpdPersistenceMongodb.create(Rgpd.builder().rgpdType(RgpdType.BASIC).agreement(agreementEncoded.getBytes()).user(User.builder().mobile(userMobile).build()).build()))
                .expectNextMatches(savedRgpd -> {
                    assertNotNull(savedRgpd);
                    assertNotNull(savedRgpd.getAgreement());
                    assertNotNull(savedRgpd);
                    assertEquals(userMobile, savedRgpd.getUser().getMobile());
                    assertEquals(RgpdType.BASIC, savedRgpd.getRgpdType());
                    assertArrayEquals(agreementEncoded.getBytes(), savedRgpd.getAgreement());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testExistsRgpdByUserMobile() {

        String agreementEncoded = Base64.getEncoder().encodeToString("NewAgreement".getBytes());
        String userMobileExists = "600600602";
        String userMobileNotExists = "999999999";

        StepVerifier
                .create(rgpdPersistenceMongodb.create(Rgpd.builder().rgpdType(RgpdType.BASIC).agreement(agreementEncoded.getBytes()).user(User.builder().mobile(userMobileExists).build()).build()))
                .expectNextMatches(savedRgpd -> {
                    assertNotNull(savedRgpd);
                    return true;
                })
                .expectComplete()
                .verify();


        StepVerifier.create(rgpdPersistenceMongodb.existsRgpdByUserMobile(userMobileExists))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(rgpdPersistenceMongodb.existsRgpdByUserMobile(userMobileNotExists))
                .expectNext(false)
                .verifyComplete();
    }

}
