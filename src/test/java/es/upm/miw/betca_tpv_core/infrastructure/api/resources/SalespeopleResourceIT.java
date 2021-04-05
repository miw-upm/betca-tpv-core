package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
public class SalespeopleResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        LocalDate salesDate = LocalDate.of(2021, 4, 2);
        String[] str = {"8400000000017", "8400000000048"};
        Salespeople salespeople = new Salespeople("Rosaria", salesDate,
                "5fa4603b7513a164c99677a8", str, 2, new BigDecimal(22));

        Salespeople createSalespeople = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(SalespeopleResource.SALESPEOPLE)
                .body(Mono.just(salespeople), Salespeople.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Salespeople.class)
                .value(returnSalespeople -> {
                    assertNotNull(returnSalespeople.getSalesperson());
                    assertNotNull(returnSalespeople.getTicketBarcode());
                    assertNotNull(returnSalespeople.getSalesDate());
                    assertNotNull(returnSalespeople.getArticleBarcode());
                    assertEquals(2, returnSalespeople.getAmount());
                    assertEquals(new BigDecimal(22), returnSalespeople.getTotal());
                })
                .returnResult().getResponseBody();
        assertNotNull(createSalespeople);

    }

    @Test
    void testFindBySalespersonAndSalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MARCH, 1);
        LocalDate dateEnd = LocalDate.of(2021, Month.APRIL, 1);
        String salesperson = "Rosaria";

        String path = new DefaultUriBuilderFactory().builder()
                .path(SalespeopleResource.SALESPEOPLE + SalespeopleResource.SEARCH_SALESPEOPLE)
                .queryParam("salesperson", salesperson)
                .queryParam("dateBeginString", dateBegin)
                .queryParam("dateEndString", dateEnd)
                .build(dateBegin, dateEnd).toString();

        restClientTestService.loginAdmin(webTestClient)
                .get().uri(path)
                .attribute("salesperson", salesperson)
                .attribute("dateBeginString", dateBegin)
                .attribute("dateEndString", dateEnd)
                .exchange().expectStatus().isOk()
                .expectBodyList(Salespeople.class)
                .value(salespeople -> Assertions.assertEquals(1, salespeople.size()));
    }

    @Test
    void testFindSalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MARCH, 1);
        LocalDate dateEnd = LocalDate.of(2021, Month.APRIL, 1);

        String path = new DefaultUriBuilderFactory().builder()
                .path(SalespeopleResource.SALESPEOPLE + SalespeopleResource.SEARCH_Month)
                .queryParam("dateBeginString", dateBegin)
                .queryParam("dateEndString", dateEnd)
                .build(dateBegin, dateEnd).toString();

        restClientTestService.loginAdmin(webTestClient)
                .get().uri(path)
                .attribute("dateBeginString", dateBegin)
                .attribute("dateEndString", dateEnd)
                .exchange().expectStatus().isOk()
                .expectBodyList(Salespeople.class)
                .value(salespeople -> Assertions.assertEquals(7, salespeople.size()));
    }
}
