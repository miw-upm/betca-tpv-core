package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestConfig
public class RgpdReactiveIT {

    @Autowired
    private RgpdReactive rgpdReactive;

    @Test
    void testFindByUserMobile() {
        StepVerifier
                .create(this.rgpdReactive.findByUserMobile("123456789"))
                .assertNext(rgpdEntity -> {
                    assertEquals(rgpdEntity.getUserMobile(), "123456789");
                    assertEquals(rgpdEntity.getRgpdType(), RgpdType.ADVANCED);
                    assertEquals(rgpdEntity.getAgreement().length, 1);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByUserMobileNotFound() {
        StepVerifier
                .create(this.rgpdReactive.findByUserMobile("999999999"))
                .expectComplete()
                .verify();
    }

}
