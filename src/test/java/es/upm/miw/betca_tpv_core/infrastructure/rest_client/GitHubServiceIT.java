package es.upm.miw.betca_tpv_core.infrastructure.rest_client;


import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.rest.GitHubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestConfig
public class GitHubServiceIT {

    @Autowired
    private GitHubService gitHubService;

    @Test
    void testSearch() {
        this.gitHubService.search(
                "Issues Endpoint Search GET /issues/search",
                null,
                null,
                null,
                null,
                null
        ).doOnNext(System.out::println);
    }
}
