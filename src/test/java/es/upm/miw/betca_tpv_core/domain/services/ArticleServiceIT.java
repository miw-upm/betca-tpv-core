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
    void testgetPurchasedBarcodesWithoutComplaints_UserWithComplaints(){
        StepVerifier.create(this.articleService.getPurchasedBarcodesWithoutComplaints("666666004"))
                .expectNextCount(3)
                .thenConsumeWhile(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void testgetPurchasedBarcodesWithoutComplaints_UserWithoutPurchasedComplaints(){
        StepVerifier.create(this.articleService.getPurchasedBarcodesWithoutComplaints("66"))
                .expectNextCount(0)
                .verifyComplete();
    }
}
