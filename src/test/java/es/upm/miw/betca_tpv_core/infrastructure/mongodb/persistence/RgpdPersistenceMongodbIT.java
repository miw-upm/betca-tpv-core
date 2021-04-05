package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestConfig
public class RgpdPersistenceMongodbIT {

    @Autowired
    private RgpdPersistenceMongodb rgpdPersistenceMongodb;

    @Test
    void testReadByMobile() {
        StepVerifier
                .create(this.rgpdPersistenceMongodb.readByMobile("123456789"))
                .assertNext(rgpd -> {
                    assertEquals("123456789", rgpd.getMobile());
                    assertEquals(RgpdType.ADVANCED, rgpd.getRgpdType());
                    assertEquals(1, rgpd.getAgreement().length);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByMobileNotFound() {
        StepVerifier
                .create(this.rgpdPersistenceMongodb.readByMobile("999999999"))
                .expectComplete()
                .verify();
    }

    @Test
    void testCreate() {
        Rgpd rgpd = new Rgpd(new User("147852369"), RgpdType.MEDIUM, null);
        StepVerifier
                .create(this.rgpdPersistenceMongodb.create(rgpd))
                .assertNext(newRgpd -> {
                    assertEquals(newRgpd.getMobile(), rgpd.getMobile());
                    assertEquals(newRgpd.getRgpdType(), rgpd.getRgpdType());
                    assertNull(newRgpd.getAgreement());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateAlreadyExist() {
        Rgpd rgpd = new Rgpd(new User("123456789"), RgpdType.MEDIUM, null);
        StepVerifier
                .create(this.rgpdPersistenceMongodb.create(rgpd))
                .expectError()
                .verify();
    }

    @Test
    void testUpdate() {
        Rgpd rgpd = new Rgpd(new User("987654321"), RgpdType.MEDIUM, null);
        StepVerifier
                .create(this.rgpdPersistenceMongodb.update(rgpd.getMobile(), rgpd))
                .assertNext(newRgpd -> {
                    assertEquals(newRgpd.getMobile(), rgpd.getMobile());
                    assertEquals(newRgpd.getRgpdType(), rgpd.getRgpdType());
                    assertNull(newRgpd.getAgreement());
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdateNotFound() {
        Rgpd rgpd = new Rgpd(new User("789625413"), RgpdType.MEDIUM, null);
        StepVerifier
                .create(this.rgpdPersistenceMongodb.update(rgpd.getMobile(), rgpd))
                .expectError()
                .verify();
    }

}
