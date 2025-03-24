package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.model.ComplaintState;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ComplaintUpdateAdminDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfig
class ComplaintServiceIT {

    @Autowired
    private ComplaintService complaintService;

    @Test
    void testFindByUserMobileNullSafe_MobileWithComplaintsAssociated(){
        StepVerifier
                .create(this.complaintService.findByUserMobileNullSafe("66"))
                .expectNextMatches( complaint ->{
                    assertTrue(complaint.getDescription().contains("Queja aleatoria"));
                    return true;
                })
                .expectNextMatches( complaint ->{
                    assertTrue(complaint.getDescription().contains("Queja MIW"));
                    assertTrue(complaint.getReply().contains("Respuesta MIW"));
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUserMobileNullSafe_MobileWithNoComplaintsAssociated(){
        StepVerifier
                .create(this.complaintService.findByUserMobileNullSafe("6"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testFindByUserMobileNullSafe_NullMobile(){
        StepVerifier
                .create(this.complaintService.findByUserMobileNullSafe(null))
                .assertNext(complaint -> assertNotNull(complaint.getDescription()))
                .thenCancel()
                .verify();
    }

    @Test
    void testCreateComplaint_ForbidenUserMobile(){
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("66");

        Complaint complaint = Complaint.builder().description("Queja de cliente enfadado").reply("").state(ComplaintState.OPEN)
                .barcode("8400000000100").userMobile("6")
                .registrationDate(LocalDateTime.of(2025, Month.JANUARY, 1, 20, 56))
                .build();

        StepVerifier
                .create(this.complaintService.create(complaint,authentication))
                .expectError(ForbiddenException.class);
    }

    @Test
    void testCreateComplaint_NotExistsBarcode(){
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("66");

        Complaint complaint = Complaint.builder().description("Queja de cliente enfadado").reply("").state(ComplaintState.OPEN)
                .barcode("gvv7v576vbvtyr5dcvtuc6e5rcvft").userMobile("66")
                .registrationDate(LocalDateTime.of(2025, Month.JANUARY, 1, 20, 56))
                .build();

        StepVerifier
                .create(this.complaintService.create(complaint,authentication))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void  testUpdateComplaintAdmin_NotExistsNewUserMobile(){
        UserMicroservice userMicroservice = mock(UserMicroservice.class);
        when(userMicroservice.readByMobile("yh8h56b87")).thenReturn(Mono.empty());

        StepVerifier
                .create(this.complaintService.updateAsAdmin("4918CC",
                        ComplaintUpdateAdminDto.builder()
                                .userMobile("yh8h56b87")
                                .build()
                        ))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testUpdateComplaintAdmin_NotExistsNewBarcode(){
        UserMicroservice userMicroservice = mock(UserMicroservice.class);
        when(userMicroservice.readByMobile("66")).thenReturn(
                Mono.just(User.builder().mobile("66").build())
        );
        StepVerifier
                .create(
                        this.complaintService.updateAsAdmin("4918CC",ComplaintUpdateAdminDto.builder()
                                .barcode("fubidfvjkdvjdk dsk")
                                .userMobile("66")
                                .state(ComplaintState.OPEN)
                                .build())
                )
                .expectError(NotFoundException.class);
    }

    @Test
    void testUpdateComplaintAdmin_Successful(){
        StepVerifier
                .create(
                        this.complaintService.updateAsAdmin("9C27C5",ComplaintUpdateAdminDto.builder()
                                .reply("Cerrado")
                                .description("Descripcion modificada por administrador")
                                .build())
                )
                .assertNext(complaint -> {
                    assertEquals("666666003", complaint.getUserMobile().toString(), "Éxito");
                    assertEquals("Cerrado", complaint.getReply().toString(), "Éxito");
                    assertEquals("Descripcion modificada por administrador", complaint.getDescription(), "Éxito");
                })
                .thenCancel()
                .verify();

    }
}
