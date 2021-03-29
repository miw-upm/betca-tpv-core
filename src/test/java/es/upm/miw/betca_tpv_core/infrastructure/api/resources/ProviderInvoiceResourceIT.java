package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
public class ProviderInvoiceResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testFindAll() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ProviderInvoiceResource.PROVIDER_INVOICES)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProviderInvoice.class)
                .value(Assertions::assertNotNull)
                .value(providerInvoices -> assertTrue(providerInvoices.stream().allMatch(
                        providerInvoice -> providerInvoice.getNumber() != null
                )));
    }

    @Test
    void testCreate() {
        ProviderInvoice providerInvoice = ProviderInvoice.builder()
                .number(5555)
                .creationDate(LocalDate.of(2021, 12, 1))
                .baseTax(new BigDecimal("5000"))
                .taxValue(new BigDecimal("50"))
                .providerCompany("pro1")
                .orderId("ord5")
                .build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ProviderInvoiceResource.PROVIDER_INVOICES)
                .body(Mono.just(providerInvoice), ProviderInvoice.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProviderInvoice.class)
                .value(Assertions::assertNotNull)
                .value(returnProviderInvoice -> {
                    assertEquals(5555, returnProviderInvoice.getNumber());
                    assertEquals(LocalDate.of(2021, 12, 1), returnProviderInvoice.getCreationDate());
                    assertEquals(new BigDecimal("5000"), returnProviderInvoice.getBaseTax());
                    assertEquals(new BigDecimal("50"), returnProviderInvoice.getTaxValue());
                    assertEquals("pro1", returnProviderInvoice.getProviderCompany());
                    assertEquals("ord5", returnProviderInvoice.getOrderId());
                });
    }

    @Test
    void testCreateNumberAlreadyExists() {
        ProviderInvoice providerInvoice = ProviderInvoice.builder()
                .number(1111)
                .creationDate(LocalDate.of(2021, 9, 1))
                .baseTax(new BigDecimal("9000"))
                .taxValue(new BigDecimal("90"))
                .orderId("ord9")
                .providerCompany("pro1")
                .build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ProviderInvoiceResource.PROVIDER_INVOICES)
                .body(Mono.just(providerInvoice), ProviderInvoice.class)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void testCreateProviderNotFound() {
        ProviderInvoice providerInvoice = ProviderInvoice.builder()
                .number(9999)
                .baseTax(new BigDecimal("1"))
                .taxValue(new BigDecimal("1"))
                .creationDate(LocalDate.now())
                .orderId("ord")
                .providerCompany("kk")
                .build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ProviderInvoiceResource.PROVIDER_INVOICES)
                .body(Mono.just(providerInvoice), ProviderInvoice.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testRead() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ProviderInvoiceResource.PROVIDER_INVOICES)
                        .pathSegment("1111").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProviderInvoice.class)
                .value(Assertions::assertNotNull)
                .value(providerInvoice -> assertEquals(1111, providerInvoice.getNumber()));
    }

    @Test
    void testReadNumberNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ProviderInvoiceResource.PROVIDER_INVOICES)
                        .pathSegment("9999")
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdate() {
        ProviderInvoice providerInvoice = ProviderInvoice.builder()
                .number(1111)
                .creationDate(LocalDate.of(2021, 1, 1))
                .baseTax(new BigDecimal("1000"))
                .taxValue(new BigDecimal("10"))
                .providerCompany("pro1")
                .orderId("updated ord1")
                .build();
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ProviderInvoiceResource.PROVIDER_INVOICES)
                        .pathSegment("1111")
                        .build())
                .body(Mono.just(providerInvoice), ProviderInvoice.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProviderInvoice.class)
                .value(Assertions::assertNotNull)
                .value(returnProviderInvoice -> {
                    assertEquals(1111, returnProviderInvoice.getNumber());
                    assertEquals("updated ord1", providerInvoice.getOrderId());
                });
    }

    @Test
    void testUpdateOldProviderInvoiceNotFound() {
        ProviderInvoice providerInvoice = ProviderInvoice.builder()
                .number(9999)
                .creationDate(LocalDate.of(2021, 1, 1))
                .baseTax(new BigDecimal("1000"))
                .taxValue(new BigDecimal("10"))
                .providerCompany("pro1")
                .orderId("updated ord1")
                .build();
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ProviderInvoiceResource.PROVIDER_INVOICES)
                        .pathSegment("9999")
                        .build())
                .body(Mono.just(providerInvoice), ProviderInvoice.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateProviderNotFound() {
        ProviderInvoice providerInvoice = ProviderInvoice.builder()
                .number(1111)
                .creationDate(LocalDate.of(2021, 1, 1))
                .baseTax(new BigDecimal("1000"))
                .taxValue(new BigDecimal("10"))
                .providerCompany("kk")
                .orderId("updated ord1")
                .build();
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ProviderInvoiceResource.PROVIDER_INVOICES)
                        .pathSegment("1111")
                        .build())
                .body(Mono.just(providerInvoice), ProviderInvoice.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDelete() {
        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(ProviderInvoiceResource.PROVIDER_INVOICES)
                        .pathSegment("4444")
                        .build())
                .exchange()
                .expectStatus().isOk();
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ProviderInvoiceResource.PROVIDER_INVOICES)
                        .pathSegment("4444")
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(ProviderInvoiceResource.PROVIDER_INVOICES)
                        .pathSegment("9999")
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }

}
