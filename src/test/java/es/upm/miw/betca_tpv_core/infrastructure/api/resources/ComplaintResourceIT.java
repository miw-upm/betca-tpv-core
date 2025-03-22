package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.model.ComplaintState;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ComplaintCreationDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

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
    void testReadByTrackingCode_UserNotLogged(){
        this.webTestClient
                .get()
                .uri(COMPLAINTS+"/dasd")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }
    @Test
    void testReadByTrackingCode_UserIsAdmin(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(COMPLAINTS+"/9B83A8")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Complaint.class)
                .value(Assertions::assertNotNull)
                .value(complaint -> assertEquals("Éxito en readByTrackingCode","66", complaint.getUserMobile().toString()));
    }

    @Test
    void testReadByTrackingCode_WhenCustomerIsNotOwnerOfComplaint(){
        this.restClientTestService.loginCustomer(webTestClient)
                .get()
                .uri(COMPLAINTS+"/6A6867")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void testReadByTrackingCode_WhenCustomerIsOwnerOfComplaint(){
        this.restClientTestService.loginCustomer(webTestClient)
                .get()
                .uri(COMPLAINTS+"/9B83A8")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Complaint.class)
                .value(Assertions::assertNotNull)
                .value(complaint -> assertEquals("Éxito en readByTrackingCode","66", complaint.getUserMobile().toString()));
    }

    @Test
    void testReadByTrackingCode_NotFound(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(COMPLAINTS+"/sdnieufsudn843")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testCreateComplaintResource_ForbidenWhileTryingToCreateAComplainForOtherUser(){
        ComplaintCreationDto complaintCreationDto = ComplaintCreationDto.builder()
                .barcode("8400000000100")
                .userMobile("666666005")
                .description("Nueva descripcion de test resource")
                .build();
        this.restClientTestService.loginCustomer(webTestClient)
                .post()
                .uri(COMPLAINTS)
                .body(Mono.just(complaintCreationDto),ComplaintCreationDto.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void testCreateComplaintResource_NotAuthorizeAsOperator(){
        ComplaintCreationDto complaintCreationDto = ComplaintCreationDto.builder()
                .barcode("8400000000100").userMobile("666666005").description("Nueva descripcion de test resource").build();
        this.restClientTestService.loginOperator(webTestClient)
                .post()
                .uri(COMPLAINTS)
                .body(Mono.just(complaintCreationDto),ComplaintCreationDto.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }
    @Test
    void testCreateComplaintResource_NotAuthorizeAsManager(){
        ComplaintCreationDto complaintCreationDto = ComplaintCreationDto.builder()
                .barcode("8400000000100").userMobile("666666005").description("Nueva descripcion de test resource").build();
        this.restClientTestService.loginManager(webTestClient)
                .post()
                .uri(COMPLAINTS)
                .body(Mono.just(complaintCreationDto),ComplaintCreationDto.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }
    @Test
    void testCreateComplaintResource_NotAuthorizeAsAdmin(){
        ComplaintCreationDto complaintCreationDto = ComplaintCreationDto.builder()
                .barcode("8400000000100").userMobile("666666005").description("Nueva descripcion de test resource").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(COMPLAINTS)
                .body(Mono.just(complaintCreationDto),ComplaintCreationDto.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void testCreateComplaintResource_NotFoundBarcode(){
        ComplaintCreationDto complaintCreationDto = ComplaintCreationDto.builder()
                .barcode("fr8h3nif4hu8n ru8eucn938cj3")
                .userMobile("66")
                .description("Nueva descripcion de test resource")
                .build();
        this.restClientTestService.loginCustomer(webTestClient)
                .post()
                .uri(COMPLAINTS)
                .body(Mono.just(complaintCreationDto),ComplaintCreationDto.class)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
    @Test
    void testCreateComplaintResource_AlreadyExistsComplaintWith(){
        ComplaintCreationDto complaintCreationDto = ComplaintCreationDto.builder()
                .barcode("8400000000017")
                .userMobile("66")
                .description("Nueva descripcion de test resource")
                .build();
        this.restClientTestService.loginCustomer(webTestClient)
                .post()
                .uri(COMPLAINTS)
                .body(Mono.just(complaintCreationDto),ComplaintCreationDto.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT);
    }

    private String getIdOfNewComplaintByBarcode(String barcode){
        return this.restClientTestService.loginOtherCustomer(webTestClient)
                .post()
                .uri(COMPLAINTS)
                .bodyValue(ComplaintCreationDto.builder().description("Descripcion nueva prueba test")
                        .barcode(barcode)
                        .userMobile("666666004")
                        .build()
                )
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Complaint.class)
                .getResponseBody()
                .blockFirst()
                .getTrackingCode();
    }

    @Test
    void testDeleteComplaintAsAdmin_Successful(){
        String complaintId=this.getIdOfNewComplaintByBarcode("8400000000017");
        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(COMPLAINTS+"/"+complaintId)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void testDeleteComplaintAsCustomer_Forbidden(){
        this.restClientTestService.loginCustomer(webTestClient)
                .delete()
                .uri(COMPLAINTS+"/frieourfncw0")
                .exchange()
                .expectStatus()
                .isForbidden();
    }
    @Test
    void testDeleteComplaintAsCustomer_ComplaintIsClosed(){
        this.restClientTestService.loginExtraCustomer(webTestClient)
                .delete()
                .uri(COMPLAINTS+"/fdfsdfsdfgfgdfgdfcer")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }
    @Test
    void testDeleteComplaintAsManager_UnAuthorized(){
        this.restClientTestService.loginManager(webTestClient)
                .delete()
                .uri(COMPLAINTS+"/4r34f54")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }
    @Test
    void testDeleteComplaintAsOperator_UnAuthorized(){
        this.restClientTestService.loginOperator(webTestClient)
                .delete()
                .uri(COMPLAINTS+"/4r34f54")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

}
