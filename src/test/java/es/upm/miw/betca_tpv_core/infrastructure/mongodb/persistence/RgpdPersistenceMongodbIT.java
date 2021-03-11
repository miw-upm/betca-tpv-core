package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
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
                    assertEquals(rgpd.getMobile(), "123456789");
                    assertEquals(rgpd.getRgpdType(), RgpdType.ADVANCED);
                    assertNull(rgpd.getAgreement());
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

}
