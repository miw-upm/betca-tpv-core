package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.CustomerPoints;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.CustomerPointsResource.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@RestTestConfig
class CustomerPointsResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;
    @MockBean
    private UserMicroservice userMicroservice;

    @BeforeEach
    void setup() {
        BDDMockito.given(this.userMicroservice.readByMobile(anyString()))
                .willAnswer(arguments ->
                        Mono.just(User.builder().mobile(arguments.getArgument(0)).firstName("mock").build()));
    }

    @Test
    void testCreateCustomerPoints() {
        CustomerPoints customerPoints = new CustomerPoints();
        customerPoints.setValue(150);
        customerPoints.setLastDate(LocalDateTime.now());
        customerPoints.setUser(User.builder().mobile("666666004").build());

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(CUSTOMER_POINTS)
                .body(Mono.just(customerPoints), CustomerPoints.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerPoints.class)
                .value(Assertions::assertNotNull)
                .value(returnCustomerPoints -> {
                    assertEquals(150, returnCustomerPoints.getValue());
                });
    }

    @Test
    void testReadCustomerPointsByMobile() {
        User mockUser = User.builder()
                .mobile("66")
                .firstName("mock")
                .build();

        BDDMockito.given(this.userMicroservice.readByMobile(anyString()))
                .willReturn(Mono.just(mockUser));

        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(CUSTOMER_POINTS + MOBILE, "66")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerPoints.class)
                .value(Assertions::assertNotNull)
                .value(returnCustomerPoints -> {
                    assertEquals(100, returnCustomerPoints.getValue());
                });
    }

    @Test
    void testUpdateCustomerPoints() {
        CustomerPoints customerPoints = new CustomerPoints();
        customerPoints.setValue(200);
        customerPoints.setLastDate(LocalDateTime.now());
        customerPoints.setUser(User.builder().mobile("666666004").build());

        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(CUSTOMER_POINTS + MOBILE, "66")
                .body(Mono.just(customerPoints), CustomerPoints.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerPoints.class)
                .value(Assertions::assertNotNull)
                .value(returnCustomerPoints -> {
                    assertEquals(200, returnCustomerPoints.getValue());
                });
    }

    @Test
    void testAddCustomerPoints() {
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(CUSTOMER_POINTS + MOBILE + ADD_POINTS + "?points=10", "666666005")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerPoints.class)
                .value(Assertions::assertNotNull)
                .value(returnCustomerPoints -> {
                    assertEquals(10, returnCustomerPoints.getValue());
                });
    }

    @Test
    void testUseCustomerPoints() {
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(CUSTOMER_POINTS + MOBILE + USE_POINTS + "?points=10", "666666003")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerPoints.class)
                .value(Assertions::assertNotNull)
                .value(returnCustomerPoints -> {
                    assertEquals(10, returnCustomerPoints.getValue());
                });
    }
}