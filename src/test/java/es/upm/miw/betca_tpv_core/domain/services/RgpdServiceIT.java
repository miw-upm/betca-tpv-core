package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class RgpdServiceIT {

    @Autowired
    private RgpdService rgpdService;

    @Test
    void testCreateRgpd_Success() {
        String userMobile = "600600603";
        String agreementEncoded = Base64.getEncoder().encodeToString("SampleAgreement".getBytes());
        RgpdDto rgpdDto = new RgpdDto(userMobile, RgpdType.BASIC, agreementEncoded);

        StepVerifier
                .create(rgpdService.create(rgpdDto))
                .expectNextMatches(savedRgpd -> {
                    assertNotNull(savedRgpd);
                    assertNotNull(savedRgpd.getUser());
                    assertNotNull(savedRgpd.getUser().getMobile());
                    assertEquals(userMobile, savedRgpd.getUser().getMobile());
                    assertEquals(RgpdType.BASIC, savedRgpd.getRgpdType());
                    assertArrayEquals("SampleAgreement".getBytes(), savedRgpd.getAgreement());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testCreateRgpd_Fail_AlreadyExists() {

        String userMobile = "600600604";
        String agreementEncoded = Base64.getEncoder().encodeToString("SampleAgreement".getBytes());
        RgpdDto rgpdDto = new RgpdDto(userMobile, RgpdType.BASIC, agreementEncoded);

        StepVerifier
                .create(rgpdService.create(rgpdDto))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier
                .create(rgpdService.create(rgpdDto))
                .expectErrorMatches(throwable -> throwable instanceof BadRequestException)
                .verify();
    }
}