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
    void testProviderNotFound() {
        ProviderInvoice providerInvoice = ProviderInvoice.builder()
                .number(999999)
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
}