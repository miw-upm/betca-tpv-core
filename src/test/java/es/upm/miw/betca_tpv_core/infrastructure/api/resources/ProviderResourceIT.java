package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Provider;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ProviderCompanyDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleResource.SEARCH;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ProviderResource.*;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
class ProviderResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        Provider provider = Provider.builder().company("tpc1").nif("tpn1").phone("666666666").active(null).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(PROVIDERS)
                .body(Mono.just(provider), Provider.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Provider.class)
                .value(Assertions::assertNotNull)
                .value(returnProvider -> {
                    assertEquals("tpc1", returnProvider.getCompany());
                    assertEquals("tpn1", returnProvider.getNif());
                    assertEquals("666666666", returnProvider.getPhone());
                    assertTrue(returnProvider.getActive());
                });
    }

    @Test
    void testCreateUnauthorized() {
        Provider provider = Provider.builder().company("tpc1").nif("tpn1").phone("666666666").active(null).build();
        this.webTestClient
                .post()
                .uri(PROVIDERS)
                .body(Mono.just(provider), Provider.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testCreateJsonWithoutNull() {
        Provider providerDto = Provider.builder().company("tpc2").nif("tpn2").phone("666666666").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(PROVIDERS)
                .body(Mono.just(providerDto), Provider.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(json -> assertFalse(json.contains("note")));
    }

    @Test
    void testRead() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(PROVIDERS + COMPANY_ID, "pro1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Provider.class)
                .value(provider -> {
                    assertEquals("9166666601", provider.getPhone());
                    assertTrue(provider.getActive());
                });
    }

    @Test
    void testUpdate() {
        Provider provider = Provider.builder().company("tpc3").nif("tpn3").phone("666666666").active(true).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(PROVIDERS)
                .body(Mono.just(provider), Provider.class)
                .exchange()
                .expectStatus().isOk();
        Provider providerUpdated = Provider.builder().company("tpc3-new").nif("tpn3-other").phone("666666666").active(false).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(PROVIDERS + COMPANY_ID, "tpc3")
                .body(Mono.just(providerUpdated), Provider.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Provider.class)
                .value(Assertions::assertNotNull)
                .value(returnProvider -> {
                    assertEquals("tpc3-new", returnProvider.getCompany());
                    assertEquals("tpn3-other", returnProvider.getNif());
                    assertEquals("666666666", returnProvider.getPhone());
                    assertFalse(returnProvider.getActive());
                });
    }

    @Test
    void testFindByActive() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(PROVIDERS + COMPANY)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProviderCompanyDto.class)
                .value(companiesDto -> assertTrue(companiesDto.getCompanies().containsAll(List.of("pro1", "pro3", "pro4"))));
    }

    @Test
    void testFindByCompanyAndPhoneAndNoteNullSafe() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PROVIDERS + SEARCH)
                        .queryParam("company", "ro")
                        .queryParam("note", "3")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Provider.class)
                .value(providers -> assertTrue(providers
                        .stream().anyMatch(provider -> provider.getCompany().toLowerCase().contains("pro3"))));
    }
}
