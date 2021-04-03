package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdUserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTestConfig
public class DataProtectionActResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testRead() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT +
                        DataProtectionActResource.MOBILE_ID, "123456789")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RgpdUserDto.class)
                .value(Assertions::assertNotNull)
                .value(rgpdUserDto -> {
                    assertEquals(rgpdUserDto.getMobile(), "123456789");
                    assertEquals(rgpdUserDto.getRgpdType(), RgpdType.ADVANCED);
                });
    }

    @Test
    void testReadNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT +
                        DataProtectionActResource.MOBILE_ID, "999999999")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RgpdUserDto.class)
                .value(Assertions::assertNull);
    }

    @Test
    void testCreate() throws IOException {
        RgpdUserDto rgpd = new RgpdUserDto("987456321", RgpdType.MEDIUM);
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT)
                .body(BodyInserters.fromMultipartData(this.setUpBody(rgpd).build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(RgpdUserDto.class)
                .value(Assertions::assertNotNull)
                .value(rgpdUserDto -> {
                    assertEquals(rgpdUserDto.getMobile(), rgpd.getMobile());
                    assertEquals(rgpdUserDto.getRgpdType(), rgpd.getRgpdType());
                });
    }

    private MultipartBodyBuilder setUpBody(RgpdUserDto rgpd) throws IOException {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(DataProtectionActResource.USER,
                "{\"mobile\":\"" + rgpd.getMobile() + "\",\"rgpdType\":" + rgpd.getRgpdType().ordinal() + "}");
        multipartBodyBuilder.part(DataProtectionActResource.AGREEMENT,
                new UrlResource(Files.createTempFile("test", ".pdf").toUri()));
        return multipartBodyBuilder;
    }

    @Test
    void testCreateAlreadyExist() throws IOException {
        RgpdUserDto rgpd = new RgpdUserDto("123456789", RgpdType.MEDIUM);
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT)
                .body(BodyInserters.fromMultipartData(this.setUpBody(rgpd).build()))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(Error.class);
    }

    @Test
    void testUpdate() throws IOException {
        RgpdUserDto rgpd = new RgpdUserDto("987654321", RgpdType.MEDIUM);
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT +
                        DataProtectionActResource.MOBILE_ID, rgpd.getMobile())
                .body(BodyInserters.fromMultipartData(this.setUpBody(rgpd).build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(RgpdUserDto.class)
                .value(Assertions::assertNotNull)
                .value(rgpdUserDto -> {
                    assertEquals(rgpdUserDto.getMobile(), rgpd.getMobile());
                    assertEquals(rgpdUserDto.getRgpdType(), rgpd.getRgpdType());
                });
    }

    @Test
    void testUpdateNotFound() throws IOException {
        RgpdUserDto rgpd = new RgpdUserDto("789625413", RgpdType.MEDIUM);
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT +
                        DataProtectionActResource.MOBILE_ID, rgpd.getMobile())
                .body(BodyInserters.fromMultipartData(this.setUpBody(rgpd).build()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Error.class);
    }

    @Test
    void testReadAgreement() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT + DataProtectionActResource.AGREEMENT_ID +
                        DataProtectionActResource.MOBILE_ID, "123456789")
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull)
                .value(bytes ->
                        assertEquals(bytes.length, 1)
                );
    }

    @Test
    void testReadAgreementNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(DataProtectionActResource.DATA_PROTECTION_ACT + DataProtectionActResource.AGREEMENT_ID +
                        DataProtectionActResource.MOBILE_ID, "999999999")
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNull);
    }

}
