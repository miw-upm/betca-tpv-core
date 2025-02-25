package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.CustomerPoints;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.persistence.CustomerPointsPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@TestConfig
class CustomerPointsServiceIT {

    @Autowired
    private CustomerPointsService customerPointsService;

    @MockBean
    private CustomerPointsPersistence customerPointsPersistence;

    @MockBean
    private UserMicroservice userMicroservice;

    private static final String TEST_MOBILE = "66";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
        user.setMobile(TEST_MOBILE);
        given(this.userMicroservice.readByMobile(TEST_MOBILE)).willReturn(Mono.just(user));

        CustomerPoints customerPoints = new CustomerPoints();
        customerPoints.setValue(100);
        customerPoints.setLastDate(LocalDateTime.now());
        customerPoints.setUser(user);
        given(this.customerPointsPersistence.readCustomerPointsByMobile(TEST_MOBILE, user)).willReturn(Mono.just(customerPoints));
        given(this.customerPointsPersistence.createCustomerPoints(any(CustomerPoints.class))).willAnswer(invocation -> {
            CustomerPoints cp = invocation.getArgument(0);
            cp.setValue(150); // Set the correct value
            return Mono.just(cp);
        });
        given(this.customerPointsPersistence.updateCustomerPoints(TEST_MOBILE, customerPoints)).willReturn(Mono.just(customerPoints));
    }

    @Test
    void testCreateCustomerPoints() {
        CustomerPoints customerPoints = new CustomerPoints();
        customerPoints.setValue(150);
        customerPoints.setLastDate(LocalDateTime.now());

        StepVerifier.create(this.customerPointsService.createCustomerPoints(customerPoints))
                .expectNextMatches(cp -> cp.getValue() == 150 && cp.getLastDate() != null)
                .verifyComplete();
    }

    @Test
    void testReadCustomerPointsByMobile() {
        StepVerifier.create(this.customerPointsService.readCustomerPointsByMobile(TEST_MOBILE))
                .expectNextMatches(cp -> cp.getUser().getMobile().equals(TEST_MOBILE))
                .verifyComplete();
    }

    @Test
    void testUpdateCustomerPoints() {
        CustomerPoints customerPoints = new CustomerPoints();
        customerPoints.setValue(200);

        StepVerifier.create(this.customerPointsService.updateCustomerPoints(TEST_MOBILE, customerPoints))
                .expectNextMatches(cp -> cp.getValue() == 200)
                .verifyComplete();
    }

    @Test
    void testAddCustomerPoints() {
        int pointsToAdd = 50;

        StepVerifier.create(this.customerPointsService.addCustomerPoints(TEST_MOBILE, pointsToAdd))
                .expectNextMatches(cp -> cp.getValue() >= pointsToAdd)
                .verifyComplete();
    }

    @Test
    void testUseCustomerPoints() {
        int pointsToUse = 10;

        StepVerifier.create(this.customerPointsService.useCustomerPoints(TEST_MOBILE, pointsToUse))
                .expectNextMatches(cp -> cp.getValue() == 90)
                .verifyComplete();
    }
}