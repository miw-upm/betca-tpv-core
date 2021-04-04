package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
public class ArticleServiceIT {

    @Autowired
    private ArticleService articleService;

    @Test
    void testFindTop5ArticleSalesLastWeek() {
        LocalDateTime testDate = LocalDateTime.now().minusDays(7);
        StepVerifier.create(articleService.findTop5ArticleSalesLastWeek())
                    .expectNextMatches(article -> {
                        assertTrue(article.getRegistrationDate().isAfter(testDate));
                                return true;
                            })
                    .thenConsumeWhile(article -> true)
                    .expectComplete()
                    .verify();
    }
}
