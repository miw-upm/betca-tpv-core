package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTestConfig
class StockAuditResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testFindAll() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(StockAuditResource.STOCK_AUDIT)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StockAudit.class)
                .hasSize(5)
                .consumeWith(response -> {
                    response.getResponseBody().forEach(stockAudit -> {
                        assertNotNull(stockAudit.getId());
                        assertNotNull(stockAudit.getCreationDate());
                        assertNotNull(stockAudit.getLossValue());
                        assertNotNull(stockAudit.getCloseDate());
                        assertFalse(stockAudit.getLosses().isEmpty());
                        assertFalse(stockAudit.getArticlesWithoutAudit().isEmpty());
                    });
                });
    }

}