package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.Month;

import static java.math.BigDecimal.TEN;

@TestConfig
public class OfferPersistenceMongodbIT {

    @Autowired
    private OfferPersistenceMongodb offerPersistenceMongodb;

    @Test
    void testCreateDateException() {
        StepVerifier
                .create(this.offerPersistenceMongodb.create(
                        Offer.builder()
                                .reference("to1")
                                .description("error")
                                .discount(TEN)
                                .creationDate(LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10))
                                .expiryDate(LocalDateTime.of(2018, Month.JANUARY, 12, 10, 10))
                                .build()
                        )
                )
                .expectError(BadRequestException.class)
                .verify();
    }
}
