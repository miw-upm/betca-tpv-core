package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

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
}
