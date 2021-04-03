package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Login;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class StaffPersistenceMongodbIT {

    @Autowired
    private StaffPersistenceMongodb staffPersistenceMongodb;

    @Test
    void testLogin() {
        StepVerifier
                .create(this.staffPersistenceMongodb.saveLogout(Login
                        .builder()
                        .id("1")
                        .phone("618456160")
                        .loginDate(LocalDateTime.now())
                        .logoutDate(LocalDateTime.now())
                        .build()))
                .expectNextMatches(login -> {
                    assertEquals("1", login.getId());
                    assertEquals("618456160", login.getPhone());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
