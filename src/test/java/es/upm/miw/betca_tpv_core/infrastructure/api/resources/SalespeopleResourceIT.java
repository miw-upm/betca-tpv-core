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
    void testFindBySalespersonAndSalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MAY, 1);
        LocalDate dateEnd = LocalDate.of(2021, Month.MAY, 15);
        String salesperson = "admin";

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
                .value(salespeople -> Assertions.assertEquals(3, salespeople.size()));
    }

    @Test
    void testFindSalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MAY, 1);
        LocalDate dateEnd = LocalDate.of(2021, Month.JUNE, 1);

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
                .value(salespeople -> Assertions.assertEquals(6, salespeople.size()));
    }
}
