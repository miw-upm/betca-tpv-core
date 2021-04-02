package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Login;
import es.upm.miw.betca_tpv_core.domain.model.LoginOrder;
import es.upm.miw.betca_tpv_core.domain.model.StaffReport;
import es.upm.miw.betca_tpv_core.domain.model.StaffTime;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.StaffResource.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTestConfig
public class StaffResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testLoginWithValidUser() {
        this.restClientTestService.loginManager(webTestClient)
                .post()
                .uri(STAFF + LOGIN)
                .exchange()
                .expectStatus().isOk()
        .expectBody(LoginOrder.class)
        .value(Assertions::assertNotNull)
        .value(login -> {
            assertEquals("666666001", login.getPhone());
        });
    }

    @Test
    void testLoginWithInvalidUser() {
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(STAFF + LOGIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginOrder.class)
                .value(Assertions::assertNull);
    }

    @Test
    void testLogout() {
        this.restClientTestService.loginManager(webTestClient)
                .post()
                .uri(STAFF + LOGIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginOrder.class)
                .value(Assertions::assertNotNull);

        this.restClientTestService.loginManager(webTestClient)
                .post()
                .uri(STAFF + LOGOUT)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Login.class)
                .value(Assertions::assertNotNull)
                .value(login -> {
                    assertEquals("666666001", login.getPhone());
                });
    }

    @Test
    void testLogoutWithInvalidUser() {
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(STAFF + LOGOUT)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Login.class)
                .value(Assertions::assertNull);
    }

    @Test
    void testFindTimeByDays() {
        performLoginAndLogoutWithManager();
        this.restClientTestService.loginManager(webTestClient)
                .get()
                .uri(STAFF + TIME + "?mobile=666666001&startDate=2000-01-01&endDate=2099-01-01&typeOfSearch=day")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StaffTime.class)
                .value(Assertions::assertNotNull)
                .value(staffTimes -> {
                    assertTrue(staffTimes.size() >= 1);
                });
    }

    @Test
    void testFindTimeByMonth() {
        performLoginAndLogoutWithManager();
        this.restClientTestService.loginManager(webTestClient)
                .get()
                .uri(STAFF + TIME + "?mobile=666666001&startDate=2000-01-01&endDate=2099-01-01&typeOfSearch=month")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StaffTime.class)
                .value(Assertions::assertNotNull)
                .value(staffTimes -> {
                    assertTrue(staffTimes.size() >= 1);
                });
    }

    @Test
    void testFindReports() {
        performLoginAndLogoutWithManager();
        performLoginAndLogoutWithCustomer();
        this.restClientTestService.loginManager(webTestClient)
                .get()
                .uri(STAFF + REPORTS + "?month=" + LocalDate.now().getMonth().name())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StaffReport.class)
                .value(Assertions::assertNotNull)
                .value(staffReports -> {
                    assertTrue(staffReports.size() >= 1);
                });
    }
    private void performLoginAndLogoutWithManager() {
        this.restClientTestService.loginManager(webTestClient)
                .post()
                .uri(STAFF + LOGIN)
                .exchange();

        this.restClientTestService.loginManager(webTestClient)
                .post()
                .uri(STAFF + LOGOUT)
                .exchange();
    }

    private void performLoginAndLogoutWithCustomer() {
        this.restClientTestService.loginOperator(webTestClient)
                .post()
                .uri(STAFF + LOGIN)
                .exchange();

        this.restClientTestService.loginManager(webTestClient)
                .post()
                .uri(STAFF + LOGOUT)
                .exchange();
    }
}
