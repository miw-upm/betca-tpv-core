package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.mongodb.assertions.Assertions.*;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ComplaintResource.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@RestTestConfig
class ComplaintResourceIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testFindByUserMobileNullSafe_MobileWithComplaintsAssociated_LoggedLikeAdmin(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(COMPLAINTS+SEARCH)
                        .queryParam("userMobile","66")
                        .build()

                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Complaint.class)
                .value(Assertions::assertNotNull)
                .value( complaints -> assertTrue( (long) complaints
                        .size() >=3)
                );
    }

    @Test
    void testFindByUserMobileNullSafe_MobileWithNoComplaintsAssociated_LoggedLikeAdmin(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(COMPLAINTS+SEARCH)
                        .queryParam("userMobile","6")
                        .build()

                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Complaint.class)
                .value(Assertions::assertNotNull)
                .value( complaints -> assertTrue( (long) complaints
                        .size() ==0)
                );
    }

    @Test
    void testFindByUserMobileNullSafe_NullMobile_LoggedLikeAdmin(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(COMPLAINTS+SEARCH)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Complaint.class)
                .value(Assertions::assertNotNull)
                .value( complaints -> assertTrue( (long) complaints
                        .size() >=3)
                );
    }

    @Test
    void testFindByUserMobileNullSafe_MobileWithComplaintsAssociated_LoggedLikeCustomer(){
        this.restClientTestService.loginCustomer(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(COMPLAINTS+SEARCH)
                        .queryParam("userMobile","66")
                        .build()

                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Complaint.class)
                .value(Assertions::assertNotNull)
                .value( complaints -> assertTrue( (long) complaints
                        .size() >=3)
                );
    }

    @Test
    void testFindByUserMobileNullSafe_Forbidden_LoggedLikeCustomer(){
        this.restClientTestService.loginCustomer(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(COMPLAINTS+SEARCH)
                        .queryParam("userMobile","6")
                        .build()

                )
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void testFindByUserMobileNullSafe_ForbiddenNull_LoggedLikeCustomer(){
        this.restClientTestService.loginCustomer(webTestClient)
                .get()
                .uri(COMPLAINTS+SEARCH)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }
    @Test
    void testReadById_UserNotLogged(){
        this.webTestClient
                .get()
                .uri(COMPLAINTS+"/dasd")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }
    @Test
    void testReadById_UserIsAdmin(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(COMPLAINTS+"/dfun8ecm9cd")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Complaint.class)
                .value(Assertions::assertNotNull)
                .value(complaint -> assertEquals("Éxito en readById","66", complaint.getUserMobile().toString()));
    }

    @Test
    void testReadById_WhenCustomerIsNotOwnerOfComplaint(){
        this.restClientTestService.loginCustomer(webTestClient)
                .get()
                .uri(COMPLAINTS+"/frieourfncw0")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void testReadById_WhenCustomerIsOwnerOfComplaint(){
        this.restClientTestService.loginCustomer(webTestClient)
                .get()
                .uri(COMPLAINTS+"/dfun8ecm9cd")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Complaint.class)
                .value(Assertions::assertNotNull)
                .value(complaint -> assertEquals("Éxito en readById","66", complaint.getUserMobile().toString()));
    }

    @Test
    void testReadById_NotFound(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(COMPLAINTS+"/sdnieufsudn843")
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
