package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ComplaintPersistenceMongodbIT {
    @Autowired
    private ComplaintPersistenceMongodb complaintPersistenceMongodb;

    @Test
    void testFindByUserMobileNullSafe_MobileWithComplaintsAssociated(){
        StepVerifier
                .create(this.complaintPersistenceMongodb.findByUserMobileNullSafe("66"))
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
                .create(this.complaintPersistenceMongodb.findByUserMobileNullSafe("6"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testFindByUserMobileNullSafe_NullMobile(){
        StepVerifier
                .create(this.complaintPersistenceMongodb.findByUserMobileNullSafe(null))
                .assertNext(complaint -> assertNotNull(complaint.getDescription()))
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUserMobileAndBarcode_NotExistsBarcode(){
        StepVerifier
                .create(this.complaintPersistenceMongodb.findByUserMobileAndBarcode("66","y"))
                .verifyComplete();
    }

    @Test
    void testFindByUserMobileAndBarcode_ExistsComplaint(){
        StepVerifier
                .create(this.complaintPersistenceMongodb.findByUserMobileAndBarcode("66","8400000000017"))
                .assertNext(complaint -> assertTrue(complaint.getDescription().contains("Queja aleatoria")))
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUserMobileAndBarcode_NotExistsComplaint(){
        StepVerifier
                .create(this.complaintPersistenceMongodb.findByUserMobileAndBarcode("66","8400000000100"))
                .verifyComplete();
    }

    @Test
    void testCreateComplaint_NotExistsBarcode(){
        StepVerifier
                .create(this.complaintPersistenceMongodb.create(new Complaint()))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testCreateComplaint_Successful(){
        Complaint complaint = Complaint.builder().description("Queja En test").reply("").state("OPEN")
                .barcode("8400000000100").userMobile("666666005")
                .registrationDate(LocalDateTime.of(2025, Month.JANUARY, 1, 20, 56))
                .build();
        StepVerifier
                .create(this.complaintPersistenceMongodb.create(complaint))
                .assertNext(complaint1 -> complaint1.getDescription().contains("Queja En test"))
                .thenCancel()
                .verify();
    }
}
