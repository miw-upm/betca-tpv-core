package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ArticleLoss;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.CashierLastDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockAuditDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.StockAuditResource.*;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
public class StockAuditResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @BeforeTestMethod("testClose")
    @Test
    void testCreateAlreadyOpened() {
        List<String> barcodesWithoutAudit = Arrays.asList("8400000000017", "8400000000024", "8400000000031");
        this.restClientTestService.loginAdmin(webTestClient)
                .post().uri(AUDITS)
                .body(Mono.just(barcodesWithoutAudit), List.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testFindSingleOpenedAudit() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get().uri(AUDITS + OPENED)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAuditDto.class)
                .value(Assertions::assertNotNull)
                .value(stockAudit -> assertNotNull(stockAudit.getId()));
    }

    @BeforeTestMethod("testClose")
    @Test
    void testUpdate() {
        StockAuditDto stockAuditDto = new StockAuditDto();
        List<String> barcodesWithoutAudit = Arrays.asList("8400000000017");
        List<ArticleLoss> losses = Arrays.asList(new ArticleLoss("8400000000024", 3));
        stockAuditDto.setBarcodesWithoutAudit(barcodesWithoutAudit);
        stockAuditDto.setLosses(losses);
        this.restClientTestService.loginAdmin(webTestClient)
                .put().uri(AUDITS + "/1")
                .body(Mono.just(stockAuditDto), StockAuditDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAuditDto.class)
                .value(Assertions::assertNotNull)
                .value(stockAudit -> assertNotNull(stockAudit.getId()))
                .value(stockAudit -> assertNotNull(stockAudit.getBarcodesWithoutAudit()))
                .value(stockAudit -> assertNotNull(stockAudit.getLosses()))
                .value(stockAudit -> assertEquals("8400000000017", stockAudit.getBarcodesWithoutAudit().get(0)))
                .value(stockAudit -> assertEquals("8400000000024", stockAudit.getLosses().get(0).getBarcode()))
                .value(stockAudit -> assertEquals(3, stockAudit.getLosses().get(0).getAmount()));
   }

    @Test
    void testClose() {
        StockAuditDto stockAuditDto = new StockAuditDto();
        List<ArticleLoss> losses = Arrays.asList(new ArticleLoss("8400000000024", 1));
        stockAuditDto.setBarcodesWithoutAudit(Arrays.asList());
        stockAuditDto.setLosses(losses);
        this.restClientTestService.loginAdmin(webTestClient)
                .put().uri(AUDITS + CLOSE + "/1")
                .body(Mono.just(stockAuditDto), StockAuditDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAuditDto.class)
                .value(Assertions::assertNotNull)
                .value(stockAudit -> assertNotNull(stockAudit.getId()))
                .value(stockAudit -> assertNotNull(stockAudit.getBarcodesWithoutAudit()))
                .value(stockAudit -> assertEquals(0, stockAudit.getBarcodesWithoutAudit().size()))
                .value(stockAudit -> assertNotNull(stockAudit.getLosses()))
                .value(stockAudit -> assertEquals("8400000000024", stockAudit.getLosses().get(0).getBarcode()))
                .value(stockAudit -> assertEquals(1, stockAudit.getLosses().get(0).getAmount()));
    }

    @AfterTestMethod("testClose")
    @Test
    void testCreate() {
        List<String> barcodesWithoutAudit = Arrays.asList("8400000000017", "8400000000024", "8400000000031");
        this.restClientTestService.loginAdmin(webTestClient)
                .post().uri(AUDITS)
                .body(Mono.just(barcodesWithoutAudit), List.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAuditDto.class)
                .value(Assertions::assertNotNull)
                .value(stockAudit -> assertNotNull(stockAudit.getId()))
                .value(stockAudit -> assertNotNull(stockAudit.getBarcodesWithoutAudit()))
                .value(stockAudit -> assertNotNull(stockAudit.getLosses()))
                .value(stockAudit -> assertEquals(0, stockAudit.getLosses().size()))
                .value(stockAudit -> assertArrayEquals(barcodesWithoutAudit.toArray(), stockAudit.getBarcodesWithoutAudit().toArray()));
    }

    @Test
    void testUpdateNotFound() {
        StockAuditDto stockAuditDto = new StockAuditDto();
        List<String> barcodesWithoutAudit = Arrays.asList("8400000000017");
        List<ArticleLoss> losses = Arrays.asList(new ArticleLoss("8400000000024", 3));
        stockAuditDto.setBarcodesWithoutAudit(barcodesWithoutAudit);
        stockAuditDto.setLosses(losses);
        this.restClientTestService.loginAdmin(webTestClient)
                .put().uri(AUDITS + "/99999")
                .body(Mono.just(stockAuditDto), StockAuditDto.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCloseNotFound() {
        StockAuditDto stockAuditDto = new StockAuditDto();
        List<String> barcodesWithoutAudit = Arrays.asList("8400000000017");
        List<ArticleLoss> losses = Arrays.asList(new ArticleLoss("8400000000024", 3));
        stockAuditDto.setBarcodesWithoutAudit(barcodesWithoutAudit);
        stockAuditDto.setLosses(losses);
        this.restClientTestService.loginAdmin(webTestClient)
                .put().uri(AUDITS + CLOSE + "/99999")
                .body(Mono.just(stockAuditDto), StockAuditDto.class)
                .exchange()
                .expectStatus().isNotFound();
    }
}
