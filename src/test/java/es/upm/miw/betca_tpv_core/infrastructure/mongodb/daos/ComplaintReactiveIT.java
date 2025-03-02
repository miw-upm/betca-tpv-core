package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ComplaintReactiveIT {
    @Autowired
    public ComplaintReactive complaintReactive;

    @Test
    void testFindByUserMobileNullSafe(){
        StepVerifier
                .create(this.complaintReactive.findByUserMobileNullSafe("6"))
                .expectNextMatches( complaint ->{
                    assertTrue(complaint.getDescription().contains("Queja aleatoria"));
                    assertEquals(ComplaintState.OPEN,complaint.getReply());
                    return true;
                })
                .expectNextMatches( complaint ->{
                    assertTrue(complaint.getDescription().contains("Queja MIW"));
                    assertTrue(complaint.getReply().contains("Respuesta MIW"));
                    assertEquals(ComplaintState.CLOSED,complaint.getReply());
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
