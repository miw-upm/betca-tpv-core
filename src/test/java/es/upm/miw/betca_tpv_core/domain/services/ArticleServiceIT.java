package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.Objects;

@TestConfig
public class ArticleServiceIT {
    @Autowired
    private ArticleService articleService;

    @Test
    void testFindByBarcodeAndUserLoggedPurchasedBarcodesWithoutComplaints(){
        StepVerifier.create(this.articleService.findByBarcodeAndUserLoggedPurchasedArticlesWithoutComplaintsOpen(null,"666666004"))
                .expectNextCount(3)
                .thenConsumeWhile(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void testFindByBarcodeAndUserLoggedPurchasedBarcodesWithoutComplaints_withBarcodeFilter(){
        StepVerifier.create(this.articleService.findByBarcodeAndUserLoggedPurchasedArticlesWithoutComplaintsOpen("8400000000031","666666004"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testFindByBarcodeAndUserLoggedPurchasedBarcodesWithoutComplaints_UserWithAllComplaintsCreated(){
        StepVerifier.create(this.articleService.findByBarcodeAndUserLoggedPurchasedArticlesWithoutComplaintsOpen(null,"66"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testFindByBarcodeAndUserLoggedPurchasedBarcodesWithoutComplaints_WithOutPurchasedItems(){
        StepVerifier.create(this.articleService.findByBarcodeAndUserLoggedPurchasedArticlesWithoutComplaintsOpen(null,"6600001"))
                .expectNextCount(0)
                .verifyComplete();
    }
}
