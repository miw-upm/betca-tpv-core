package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Issue;
import es.upm.miw.betca_tpv_core.domain.rest.GitHubService;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.IssueResource.ISSUES;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

@RestTestConfig
public class IssueResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;
    @MockBean
    private GitHubService gitHubService;

    @BeforeEach
    void mocks() {
        BDDMockito.given(
                this.gitHubService.search(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())
        ).willAnswer(arguments -> Flux.just(
                Issue.builder()
                        .title(arguments.getArgument(0))
                        .body(arguments.getArgument(1))
                        .labels(arguments.getArgument(2))
                        .state(arguments.getArgument(3))
                        .milestone(arguments.getArgument(4))
                        .assignees(arguments.getArgument(5))
                        .build(),
                Issue.builder()
                        .title(arguments.getArgument(0))
                        .body("This could be improved.")
                        .labels("enhancement")
                        .state("open")
                        .milestone("v1.1")
                        .assignees("octocat")
                        .build()
        ));
    }

    @Test
    void testFindByTitleAndBodyAndLabelsAndStateAndMilestoneAndAssigneeNullSafe() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(ISSUES)
                        .queryParam("title", "This is a test Title")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Issue.class)
                .hasSize(2)
                .value(issues ->
                        assertTrue(issues.stream().allMatch(issue -> issue.getTitle().contains("This is a test Title")))
                );
    }

}
