package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ArticlePersistenceMongodbIT {

    @Autowired
    private ArticlePersistenceMongodb articlePersistenceMongodb;

    @Test
    void testReadByBarcode() {
        StepVerifier
                .create(this.articlePersistenceMongodb.readByBarcode("8400000000017"))
                .expectNextMatches(article -> {
                    assertEquals("zz-falda-T2", article.getReference());
                    assertEquals("Zarzuela - Falda T2", article.getDescription());
                    assertFalse(article.getDiscontinued());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateExistingBarcode() {
        StepVerifier
                .create(this.articlePersistenceMongodb.create(
                        Article.builder().barcode("8400000000017").description("error").retailPrice(TEN).build()))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testCreateNotExistingCompany() {
        StepVerifier
                .create(this.articlePersistenceMongodb.create(
                        Article.builder().barcode("6660000000001").description("error").retailPrice(TEN)
                                .providerCompany("kk").build()))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testUpdateExistingBarcode() {
        StepVerifier
                .create(this.articlePersistenceMongodb.update("8400000000017",
                        Article.builder().barcode("8400000000024").description("error").retailPrice(TEN).build()))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testUpdate() {
        StepVerifier
                .create(this.articlePersistenceMongodb.update("8400000000093",
                        Article.builder().barcode("8400000000093").description("test").retailPrice(TEN)
                                .registrationDate(LocalDateTime.now()).discontinued(true).providerCompany("pro1")
                                .build()))
                .expectNextMatches(article -> {
                    assertEquals("test", article.getDescription());
                    assertTrue(article.getDiscontinued());
                    return true;
                })
                .verifyComplete();
    }
}
