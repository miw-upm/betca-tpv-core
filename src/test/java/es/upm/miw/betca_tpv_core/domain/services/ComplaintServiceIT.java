package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    void testCreateComplaintService_Successful(){
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("66");

        Complaint complaint = Complaint.builder().description("Queja de cliente enfadado").reply("").state("OPEN")
                .barcode("8400000000100").userMobile("666666005")
                .registrationDate(LocalDateTime.of(2025, Month.JANUARY, 1, 20, 56))
                .build();

        StepVerifier
                .create(this.complaintService.create(complaint),authentication)
                .assertNext(complaint1 -> complaint1.getDescription().contains("Queja de cliente enfadado"))
                .thenCancel()
                .verify();
    }

    @Test
    void testCreateComplaint_ForbidenUserMobile(){
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("66");

        Complaint complaint = Complaint.builder().description("Queja de cliente enfadado").reply("").state("OPEN")
                .barcode("8400000000100").userMobile("93862482342034234234324")
                .registrationDate(LocalDateTime.of(2025, Month.JANUARY, 1, 20, 56))
                .build();

        StepVerifier
                .create(this.complaintService.create(complaint,authentication))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testCreateComplaint_NotExistsBarcode(){
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("66");

        Complaint complaint = Complaint.builder().description("Queja de cliente enfadado").reply("").state("OPEN")
                .barcode("gvv7v576vbvtyr5dcvtuc6e5rcvft").userMobile("66")
                .registrationDate(LocalDateTime.of(2025, Month.JANUARY, 1, 20, 56))
                .build();

        StepVerifier
                .create(this.complaintService.create(complaint,authentication))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testCreateComplaint_AlreadyExistsComplaintWithUserMobileAndBarcodeProvided(){
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("66");

        Complaint complaint = Complaint.builder().description("Queja de cliente enfadado").reply("").state("OPEN")
                .barcode("8400000000017").userMobile("66")
                .registrationDate(LocalDateTime.of(2025, Month.JANUARY, 1, 20, 56))
                .build();

        StepVerifier
                .create(this.complaintService.create(complaint,authentication))
                .expectError(ConflictException.class)
                .verify();
    }
}
