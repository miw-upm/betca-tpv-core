package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class RgpdReactiveIT {

    @Autowired
    private RgpdReactive rgpdReactive;

    @Test
    void testFindByUserMobile() {
        StepVerifier
                .create(this.rgpdReactive.findByUserMobile("123456789"))
                .assertNext(rgpdEntity -> {
                    assertEquals("123456789", rgpdEntity.getUserMobile());
                    assertEquals(RgpdType.ADVANCED, rgpdEntity.getRgpdType());
                    assertEquals(1, rgpdEntity.getAgreement().length);
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
