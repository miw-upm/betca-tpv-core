package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ArticleReactiveIT {

    @Autowired
    private ArticleReactive articleReactive;

    @Test
    void testFindByProviderEntityIsNull() {
        StepVerifier
                .create(this.articleReactive.findByProviderEntityIsNull())
                .assertNext(article -> assertNull(article.getProviderEntity()))
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe() {
        StepVerifier
                .create(this.articleReactive.findByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe(
                        null, "falda", null, 6, null))
                .expectNextMatches(article -> {
                    assertTrue(article.getDescription().contains("Falda"));
                    assertTrue(article.getStock() < 6);
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByBarcodeLikeAndNotDiscontinuedNullSafe() {
        StepVerifier
                .create(this.articleReactive.findByBarcodeLikeAndNotDiscontinuedNullSafe("84"))
                .expectNextMatches(article -> {
                    System.out.println("article: " + article);
                    assertTrue(article.getBarcode().contains("84"));
                    assertFalse(article.getDiscontinued());
                    return true;
                })
                .thenCancel()
                .verify();

    }
}
