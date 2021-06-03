package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

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
        LocalDate localDate=LocalDate.of(2021,5,26);
        Salespeople salespeople= Salespeople.builder().salesDate(localDate).ticketId("5fa45e863d6e834d642689ac").userMobile("666").build();
            this.restClientTestService.loginAdmin(webTestClient)
                    .post()
                    .uri(SalespeopleResource.SALESPEOPLE)
                    .body(Mono.just(salespeople),Salespeople.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Salespeople.class)
                    .value(Assertions::assertNotNull)
                    .value(returnSalespeople->{
                        assertEquals(localDate,returnSalespeople.getSalesDate());
                        assertEquals("5fa45e863d6e834d642689ac",returnSalespeople.getTicketId());
                        assertEquals("666",returnSalespeople.getUserMobile());
                    });
    }

    @Test
    void testFindBySalespersonAndSalesDate() {
        LocalDate dateBegin = LocalDate.of(2021, Month.MAY, 1);
        LocalDate dateEnd = LocalDate.of(2021, Month.MAY, 15);
        String userMobile = "666";

        String path = new DefaultUriBuilderFactory().builder()
                .path(SalespeopleResource.SALESPEOPLE + SalespeopleResource.SEARCH_SALESPEOPLE)
                .queryParam("666", userMobile)
                .queryParam("dateBeginString", dateBegin)
                .queryParam("dateEndString", dateEnd)
                .build(dateBegin, dateEnd).toString();

        restClientTestService.loginAdmin(webTestClient)
                .get().uri(path)
                .attribute("666", userMobile)
                .attribute("dateBeginString", dateBegin)
                .attribute("dateEndString", dateEnd)
                .exchange().expectStatus().isOk()
                .expectBodyList(Salespeople.class)
                .value(salespeople -> Assertions.assertEquals(2, salespeople.size()));
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
                .value(salespeople -> Assertions.assertEquals(7, salespeople.size()));
    }
}
